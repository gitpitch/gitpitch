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

import com.gitpitch.models.GitRepoModel;
import com.gitpitch.models.MarkdownModel;
import com.gitpitch.models.SlideshowModel;
import com.gitpitch.policies.CacheTimeout;
import com.gitpitch.utils.PitchParams;
import play.Logger;
import play.cache.*;
import play.libs.ws.*;

import javax.inject.*;
import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

/*
 * Support service for controllers.PitchController actions.
 */
@Singleton
public class PitchService {

    private final Logger.ALogger log = Logger.of(this.getClass());

    private final static String PITCHME_PDF = "PITCHME.pdf";
    private final static String PITCHME_ZIP = "PITCHME.zip";
    private final static String PITCHME_ZIP_DIR = "PITCHME";
    private final CacheApi pitchCache;
    private final DiskService diskService;
    private final GitService gitService;
    private final PrintService printService;
    private final OfflineService offlineService;
    private final CacheTimeout cacheTimeout;

    @Inject
    public PitchService(CacheApi pitchCache,
                        DiskService diskService,
                        GitService gitService,
                        PrintService printService,
                        OfflineService offlineService,
                        CacheTimeout cacheTimeout) {

        this.pitchCache = pitchCache;
        this.diskService = diskService;
        this.gitService = gitService;
        this.printService = printService;
        this.offlineService = offlineService;
        this.cacheTimeout = cacheTimeout;
    }

    /*
     * Return cached GitRepoModel, else Optional.empty.
     */
    public Optional<GitRepoModel> cachedRepo(PitchParams pp) {

        String grmKey = GitRepoModel.genKey(pp);
        log.debug("cachedRepo: grmKey={}", grmKey);
        return Optional.ofNullable(pitchCache.get(grmKey));
    }

    /*
     * Return GitRepoModel.
     */
    public GitRepoModel fetchRepo(PitchParams pp) {

        log.debug("fetchRepo: pp={}", pp);
        CountDownLatch repoLatch = gitService.fetchRepo(pp);

        try {

            /*
             * Block until GitService.repoFetch() completes.
             * On success the GitService.repoFetch() will
             * populate the pitchCache with a valid RepoModel
             * for /{user}/{repo}/{branch}. On failure, null.
             */
            repoLatch.await();
        } catch (Exception ex) {
            log.warn("fetchRepo: pp={}, repoLatch.await ex={}", pp, ex);
        }

        Optional<GitRepoModel> grmo = cachedRepo(pp);
        GitRepoModel grm = grmo.orElse(null);
        log.debug("fetchRepo: pp={}, returning grm={}", pp, grm);

        return grm;
    }

    /*
     * Return cached SlideshowModel, else Optional.empty.
     */
    public Optional<SlideshowModel> cachedYAML(PitchParams pp) {

        String ssmKey = SlideshowModel.genKey(pp);
        return Optional.ofNullable(pitchCache.get(ssmKey));
    }

    /*
     * Return SlideshowModel.
     */
    public SlideshowModel fetchYAML(PitchParams pp) {

        log.debug("fetchYAML: pp={}", pp);
        CountDownLatch slideshowLatch = gitService.fetchYAML(pp);

        try {

            /*
             * Block until GitService.fetchYAML() completes.
             * On success the GitService.fetchYAML() will
             * populate the pitchCache with a valid SlideshowModel
             * for /{user}/{repo}/{branch}. On failure, null.
             */
            slideshowLatch.await();
        } catch (Exception ex) {
            log.warn("fetchYAML: pp={}, slideshowLatch.await ex={}", pp, ex);
        }

        Optional<SlideshowModel> ssmo = cachedYAML(pp);
        SlideshowModel ssm = ssmo.orElse(null);
        log.debug("fetchYAML: pp={}, returning ssm={}", pp, ssm);

        return ssm;
    }

    /*
     * Return cached MarkdownModel, else Optional.empty.
     */
    public Optional<MarkdownModel> cachedMarkdown(PitchParams pp) {

        String mdmKey = MarkdownModel.genKey(pp);
        return Optional.ofNullable(pitchCache.get(mdmKey));
    }

    /*
     * Return RepoModel.
     */
    public MarkdownModel fetchMarkdown(PitchParams pp) {

        log.debug("fetchMarkdown: pp={}", pp);
        CountDownLatch markdownLatch = gitService.fetchMarkdown(pp);

        try {

            /*
             * Block until GitService.fetchMarkdown() completes.
             * On success the GitService.fetchMarkdown() will
             * populate the pitchCache with a valid MarkdownModel
             * for /{user}/{repo}/{branch}. On failure, null.
             */
            markdownLatch.await();
        } catch (Exception ex) {
            log.warn("fetchMarkdown: pp={}, markdownLatch.await ex={}", pp, ex);
        }

        Optional<MarkdownModel> mdmo = cachedMarkdown(pp);
        MarkdownModel mdm = mdmo.orElse(null);
        log.debug("fetchMarkdown: pp={}, returning mdm={}", pp, mdm);

        return mdm;
    }

    /*
     * Return cached PITCHME.pdf, else Optional.empty.
     */
    public Optional<File> cachedPDF(PitchParams pp) {

        File pdfFile = diskService.asFile(pp, pp.PDF());
        long pdfAge = System.currentTimeMillis() - pdfFile.lastModified();

        log.debug("cachedPDF: pdfAge={}, max={}, file={}",
            pdfAge, cacheTimeout.pdf(pp), pdfFile);

        if (pdfAge > cacheTimeout.pdf(pp)) {
            diskService.delete(pp, pp.PDF());
        }

        return pdfFile.exists() ? Optional.of(pdfFile) : Optional.empty();
    }

    /*
     * Return PITCHME.pdf.
     */
    public File fetchPDF(PitchParams pp) {

        log.debug("fetchPDF: pp={}", pp);
        CountDownLatch pdfLatch = printService.fetchPDF(pp);

        try {

            /*
             * Block until PrintService.fetchPDF() completes.
             * On success the PrintService.fetchPDF() will
             * have built PITCHME.pdf on the file system
             * for /{user}/{repo}/{branch}. On failure, null.
             */
            pdfLatch.await();
        } catch (Exception ex) {
            log.warn("fetchPDF: pp={}, pdfLatch.await ex={}", pp, ex);
        }

        Optional<File> pdfo = cachedPDF(pp);
        File pdf = pdfo.orElse(null);
        log.debug("fetchPDF: pp={}, returning pdf={}", pp, pdf);

        return pdf;
    }

    /*
     * Return cached PITCHME.zip, else Optional.empty.
     */
    public Optional<File> cachedZip(PitchParams pp) {

        Path zipPath = diskService.ensure(pp, pp.pitchme);
        File zipFile = zipPath.resolve(PITCHME_ZIP).toFile();
        long zipAge = System.currentTimeMillis() - zipFile.lastModified();

        log.debug("cachedZip: zipAge={}, max={}, file={}",
            zipAge, cacheTimeout.zip(pp), zipFile);

        if (zipAge > cacheTimeout.zip(pp)) {
            diskService.delete(zipPath.resolve(PITCHME_ZIP));
            diskService.deepDelete(zipPath.resolve(PITCHME_ZIP_DIR).toFile());
            log.debug("cachedZip: deleted expired zip artifacts.");
        }

        return zipFile.exists() ? Optional.of(zipFile) : Optional.empty();
    }

    /*
     * Return PITCHME.zip.
     */
    public File fetchZip(PitchParams pp) {

        log.debug("fetchZip: pp={}", pp);
        CountDownLatch zipLatch =
            offlineService.fetchZip(pp, cachedYAML(pp));

        try {

            /*
             * Block until OfflineService.fetchZip() completes.
             * On success the OfflineService.fetchZip() will
             * have built PITCHME.zip on the file system
             * for /{user}/{repo}/{branch}. On failure, null.
             */
            zipLatch.await();
        } catch (Exception ex) {
            log.warn("fetchZip: pp={}, zipLatch.await ex={}", pp, ex);
        }

        Optional<File> zipo = cachedZip(pp);
        File zip = zipo.orElse(null);
        log.debug("fetchZip: pp={}, returning zip={}", pp, zip);

        return zip;
    }

}
