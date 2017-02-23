/*
 * MIT License
 *
 * Copyright (c) 2016 David Russell
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.gitpitch.services;

import com.gitpitch.factory.MarkdownModelFactory;
import com.gitpitch.git.*;
import com.gitpitch.models.MarkdownModel;
import com.gitpitch.models.SlideshowModel;
import com.gitpitch.executors.BackEndThreads;
import com.gitpitch.utils.PitchParams;
import com.gitpitch.utils.YAMLOptions;
import com.gitpitch.utils.MarkdownRenderer;
import org.apache.commons.io.FilenameUtils;
import play.Configuration;
import play.Environment;
import play.Logger;

import javax.inject.*;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
 * PITCHME.zip archive offline service.
 */
@Singleton
public class OfflineService {

    private final Logger.ALogger log = Logger.of(this.getClass());

    private final GRSManager grsManager;
    private final DiskService diskService;
    private final ShellService shellService;
    private final MarkdownModelFactory markdownModelFactory;
    private final BackEndThreads backEndThreads;
    private final Configuration configuration;
    private final Environment env;

    @Inject
    public OfflineService(GRSManager grsManager,
                          DiskService diskService,
                          ShellService shellService,
                          MarkdownModelFactory markdownModelFactory,
                          BackEndThreads backEndThreads,
                          Configuration configuration,
                          Environment env) {

        this.grsManager = grsManager;
        this.diskService = diskService;
        this.shellService = shellService;
        this.markdownModelFactory = markdownModelFactory;
        this.backEndThreads = backEndThreads;
        this.configuration = configuration;
        this.env = env;
    }

    /*
     * Generate and fetch PITCHME.zip.
     */
    public CountDownLatch fetchZip(PitchParams pp,
                                   Optional<SlideshowModel> ssmo) {

        log.debug("fetchZip: pp={}, ssmo.isPresent={}",
                pp, ssmo.isPresent());

        final String zipKey = MarkdownModel.genKey(pp);

        CountDownLatch freshLatch = new CountDownLatch(1);
        CountDownLatch activeLatch =
                zipLatchMap.putIfAbsent(zipKey, freshLatch);

        if (activeLatch != null) {
            /*
             * A non-null activeLatch implies a fetchZip()
             * operation is already in progress for the current
             * /{user}/{repo}?b={branch}.
             */
            log.debug("fetchZip: pp={}, already in progress, " +
                    "returning existing activeLatch={}", pp, activeLatch);
            return activeLatch;

        } else {

            CompletableFuture<Void> syncFuture =
                    CompletableFuture.supplyAsync(() -> {

                        Path branchPath = diskService.ensure(pp);
                        int zipStatus = generateZip(pp, ssmo);

                        if (zipStatus != STATUS_OK) {
                            log.warn("fetchZip: pp={}, fetch status={}", pp, zipStatus);
                        }

                /*
                 * Current operation completed, so remove latch associated
                 * with operation from zipLatchMap to permit future
                 * operations on this /{user}/{repo}?b={branch}.
                 */
                        releaseCountDownLatch(zipLatchMap, zipKey);

                /*
                 * Operation completed, valid result cached, no return required.
                 */
                        return null;

                    }, backEndThreads.POOL)
                            .handle((result, error) -> {

                                if (error != null) {

                                    log.warn("fetchZip: pp={}, fetch error={}", pp, error);
                                    releaseCountDownLatch(zipLatchMap, zipKey);
                                }

                                return null;
                            });

            return freshLatch;
        }

    }

    /*
     * Call when fetchXXX operation has completed either
     * successfully or on error. Remove latch associated with
     * operation from xxxLatchMap to permit future operations
     * on /{user}/{repo}?b={branch} associated with latchKey.
     */
    private void releaseCountDownLatch(ConcurrentHashMap<String, CountDownLatch> latchMap,
                                       String latchKey) {

        CountDownLatch completedLatch = latchMap.remove(latchKey);

        if (completedLatch != null) {
            completedLatch.countDown();
        }

    }

    /*
     * Generate offline slideshow as self-contained zip archive.
     */
    private int generateZip(PitchParams pp,
                            Optional<SlideshowModel> ssmo) {

        log.debug("generateZip: pp={}, ssmo.isPresent={}",
                pp, ssmo.isPresent());

        int status = STATUS_UNDEF;

        Path zipRoot = null;

        try {

            zipRoot = prepareZipRoot(pp);

            status = fetchOnlineMarkdown(pp, zipRoot);

            if (status == STATUS_OK)
                status = processMarkdown(pp, zipRoot, ssmo);

            if (status == STATUS_OK)
                status = fetchLandingHTML(pp, zipRoot);

            if (status == STATUS_OK)
                status = fetchSlideshowHTML(pp, zipRoot);

            if (status == STATUS_OK)
                fetchFixedDependencies(pp, zipRoot);

            if (status == STATUS_OK)
                fetchYAMLDependencies(pp, zipRoot);

            if (status == STATUS_OK)
                status = buildZip(pp, zipRoot);

        } catch (Exception zex) {
            log.warn("generateZip: pp={}, ex={}", zex);
        } finally {

            /*
             * Clean up artifacts if failed to generate zip.
             */
            if (status != STATUS_OK && zipRoot != null) {
                diskService.delete(zipRoot.resolve(PITCHME_ZIP));
                diskService.deepDelete(zipRoot.resolve(ZIP_ROOT_DIR).toFile());
            }
        }

        return status;
    }

    /*
     * Prepare zip archive root directory on disk.
     */
    private Path prepareZipRoot(PitchParams pp) {
        Path zipRoot = diskService.ensure(pp);
        if(pp.pitchme != null) {
            zipRoot = zipRoot.resolve(pp.pitchme);
        }
        zipRoot = zipRoot.resolve(ZIP_ROOT_DIR);
        return diskService.ensure(zipRoot);
    }

    /*
     * Fetch online PITCHME.md into ZIP_MD_DIR.
     */
    private int fetchOnlineMarkdown(PitchParams pp, Path zipRoot) {

        String murl =
                com.gitpitch.controllers.routes.PitchController.markdown(pp.grs,
                        pp.user,
                        pp.repo,
                        pp.branch,
                        pp.pitchme)
                        .absoluteURL(isEncrypted(), hostname());

        Path zipMdPath =
                diskService.ensure(zipRoot.resolve(ZIP_MD_DIR));
        return diskService.download(pp,
                zipMdPath, murl, PITCHME_ONLINE_MD,
                grsManager.get(pp).getHeaders());
    }

    /*
     * Fetch Landing.html.
     */
    private int fetchLandingHTML(PitchParams pp, Path zipRoot) {

        String lurl =
                com.gitpitch.controllers.routes.PitchController.landing(pp.user,
                        pp.repo,
                        pp.branch,
                        pp.grs,
                        pp.theme,
                        pp.pitchme,
                        pp.notes,
                        ENABLED)
                        .absoluteURL(isEncrypted(), hostname());

        return diskService.download(pp, zipRoot,
                lurl, INDEX_HTML, grsManager.get(pp).getHeaders());
    }

    /*
     * Fetch Slideshow.html.
     */
    private int fetchSlideshowHTML(PitchParams pp, Path zipRoot) {

        String surl =
                com.gitpitch.controllers.routes.PitchController.slideshow(pp.grs,
                        pp.user,
                        pp.repo,
                        pp.branch,
                        pp.theme,
                        pp.pitchme,
                        pp.notes,
                        ENABLED,
                        ENABLED,
                        null)
                        .absoluteURL(isEncrypted(), hostname());

        return diskService.download(pp, zipRoot,
                surl, SLIDESHOW_HTML, grsManager.get(pp).getHeaders());
    }

    /*
     * Fetch fixed JS and CSS dependencies for GitPitch slideshow.
     */
    private void fetchFixedDependencies(PitchParams pp, Path zipRoot)
            throws java.io.IOException {

        Path destPath =
            diskService.ensure(zipRoot.resolve(ZIP_ASSETS_DIR));

        if(env.isProd()) {

            Path jarPath = prodModeDependenciesJar();
            if(jarPath.toFile().exists()) {
                diskService.copyDirectoryFromJar(jarPath,
                        FIXED_ASSETS, destPath);
            } else {
                log.warn("fetchFixedDependencies: [ prod ] jar " +
                        "not found={}", jarPath);
            }

        } else {

            Path fixedAssetsPath = devModeFixedDependencies();
            if (fixedAssetsPath.toFile().exists()) {
                diskService.copyDirectory(fixedAssetsPath, destPath);
            } else {
                log.debug("fetchFixedDependencies: [ dev ] fixed assets " +
                        "not found={}", fixedAssetsPath);
            }
        }
    }

    /*
     * Convert online PITCHME.md to offline PITCHME.md.
     */
    private int processMarkdown(PitchParams pp,
                                Path zipRoot,
                                Optional<SlideshowModel> ssmo) {

        int status = STATUS_UNDEF;

        String consumed = null;
        Path mdOnlinePath = zipRoot.resolve(PITCHME_ONLINE_PATH);
        File mdOnlineFile = mdOnlinePath.toFile();

        if (mdOnlineFile.exists()) {

            GRSService grsService =
                grsManager.getService(grsManager.get(pp));

            MarkdownRenderer mrndr = 
                MarkdownRenderer.build(pp, ssmo, grsService, diskService);

            MarkdownModel markdownModel =
                (MarkdownModel) markdownModelFactory.create(mrndr);

            try (Stream<String> stream = Files.lines(mdOnlinePath)) {

                consumed = stream.map(md -> {
                    return markdownModel.offline(md);
                }).collect(Collectors.joining("\n"));

                Path mdOfflinePath =
                        zipRoot.resolve(PITCHME_OFFLINE_PATH);
                Files.write(mdOfflinePath, consumed.getBytes());

                fetchOnlineAssets(pp, zipRoot);

                status = STATUS_OK;

            } catch (Exception mex) {
                log.warn("processMarkdown: ex={}", mex);
            }

        } else {
            log.warn("processMarkdown: mdOnline not found={}", mdOnlineFile);
        }

        log.debug("processMarkdown: returning status={}", status);
        return status;
    }

    /*
     * Fetch online PITCHME.md image assets into ZIP_MD_ASSETS_DIR.
     */
    private void fetchOnlineAssets(PitchParams pp, Path zipRoot) {

        List<String> assetUrls = null;

        Path mdOnlinePath = zipRoot.resolve(PITCHME_ONLINE_PATH);
        File mdOnlineFile = mdOnlinePath.toFile();

        if (mdOnlineFile.exists()) {

            MarkdownModel markdownModel =
                (MarkdownModel) markdownModelFactory.create(null);

            try (Stream<String> stream = Files.lines(mdOnlinePath)) {

                assetUrls = stream.map(md -> {
                    return markdownModel.offlineAssets(md);
                }).collect(Collectors.toList());

                log.debug("fetchOnlineAssets: assetUrls={}", assetUrls);

                Path zipMdAssetsPath = zipRoot.resolve(ZIP_MD_ASSETS_DIR);
                zipMdAssetsPath = diskService.ensure(zipMdAssetsPath);

                List<String> fetched = new ArrayList<String>();

                for (String assetUrl : assetUrls) {
                    if (assetUrl != null &&
                            !fetched.contains(assetUrl)) {
                        diskService.download(pp, zipMdAssetsPath, assetUrl,
                            FilenameUtils.getName(assetUrl),
                            grsManager.get(pp).getHeaders());
                        fetched.add(assetUrl);
                    }
                }

            } catch (Exception mex) {
                log.warn("fetchOnlineAssets: ex={}", mex);
            }

        } else {
            log.warn("fetchOnlineAssets: mdOnline not found={}", mdOnlineFile);
        }
    }

    /*
     * Fetch PITCHME.yaml dependencies into zip archive.
     */
    private void fetchYAMLDependencies(PitchParams pp, Path zipRoot) {

        GRSService grsService =
                grsManager.getService(grsManager.get(pp));
        YAMLOptions yOpts = YAMLOptions.build(pp, grsService, diskService);
        log.debug("fetchYAMLDependencies: yOpts={}", yOpts);

        try {

            if (yOpts != null && yOpts.hasLogo()) {
                String logoUrl = yOpts.fetchLogo(pp);
                String logoName = FilenameUtils.getName(logoUrl);

                Path zipAssetsPath =
                        diskService.ensure(zipRoot.resolve(ZIP_ASSETS_DIR));
                diskService.download(pp, zipAssetsPath,
                        logoUrl, logoName, grsManager.get(pp).getHeaders());
                log.debug("fetchYAMLDependencies: downloaded logo={}", logoUrl);
            }

        } catch (Exception lex) {
            log.warn("fetchYAMLDependencies: logo ex={}", lex);
        }


        try {

            /*
             * If Math Slides not enabled within PITCHME.yaml, strip
             * Reveal.js math plugin file dependencies from zip.
             */
            if (yOpts == null || !yOpts.mathEnabled(pp)) {

                Path destPath = zipRoot.resolve(ZIP_ASSETS_DIR);
                String revealVersion =
                    configuration.getString("gitpitch.dependency.revealjs");
                Path mathPluginPath = Paths.get(destPath.toString(),
                                                "reveal.js",
                                                revealVersion,
                                                "plugin/math");
                log.debug("fetchYAMLDependencies: removing mathPlugin={}",
                        mathPluginPath);

                diskService.deepDelete(mathPluginPath.toFile());
            }

        } catch (Exception mex) {
            log.warn("fetchYAMLDependencies: math config assets ex={}", mex);
        }
    }

    /*
     * Build PITCHME.zip.
     */
    private int buildZip(PitchParams pp, Path zipRoot) {

        /*
         * Remove PITCHME_ONLINE_MD from zip directory.
         */
        Path onlinePath = zipRoot.resolve(PITCHME_ONLINE_PATH);
        diskService.delete(onlinePath);

        /*
         * CMD: zip -r PITCHME.zip PITCHME
         */
        String[] cmd = {ZIP_CMD,
                ZIP_QUIET,
                ZIP_ALL,
                PITCHME_ZIP,
                ZIP_ROOT_DIR};

        Path zipWd = diskService.ensure(pp, pp.pitchme);
        return shellService.exec(ZIP_CMD, pp, zipWd, cmd);
    }

    public boolean isEncrypted() {
        return configuration.getBoolean("gitpitch.https");
    }

    public String hostname() {
        return configuration.getString("gitpitch.hostname");
    }

    public Path devModeFixedDependencies() {
        String fixedAssets =
            configuration.getString("gitpitch.offline.dev.fixed.assets.home");
        return Paths.get(fixedAssets);
    }

    public Path prodModeDependenciesJar() {
        String jarAssets =
            configuration.getString("gitpitch.offline.prod.fixed.assets.home");
        return Paths.get(jarAssets);
    }

    private static final String ZIP_ROOT_DIR = "PITCHME";
    private static final String ZIP_MD_DIR = "assets/md";
    private static final String ZIP_MD_ASSETS_DIR = "assets/md/assets";
    private static final String ZIP_ASSETS_DIR = "assets";
    private static final String PITCHME_ONLINE_MD = "PITCHME.mdo";
    private static final String PITCHME_OFFLINE_MD = "PITCHME.md";
    private static final String PITCHME_ONLINE_PATH =
            ZIP_MD_DIR + "/" + PITCHME_ONLINE_MD;
    private static final String PITCHME_OFFLINE_PATH =
            ZIP_MD_DIR + "/" + PITCHME_OFFLINE_MD;
    private static final String PITCHME_ZIP = "PITCHME.zip";
    private static final String PITCHME_CSS = "PITCHME.css";
    private static final String INDEX_HTML  = "index.html";
    private static final String SLIDESHOW_HTML = "pitchme.html";
    private static final String FIXED_ASSETS = "/public/libs";
    private static final String FIXED_ASSETS_MATH = "/public/libs-math";
    private static final String ZIP_CMD   = "zip";
    private static final String ZIP_QUIET = "-q";
    private static final String ZIP_ALL   = "-r";
    private static final String ENABLED   = "true";
    private static final int STATUS_OK = 0;
    private static final int STATUS_UNDEF = 999;
    private final ConcurrentHashMap<String, CountDownLatch> zipLatchMap =
            new ConcurrentHashMap();
}
