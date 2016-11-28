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

import com.gitpitch.git.GRS;
import com.gitpitch.git.GRSService;
import com.gitpitch.git.GRSManager;
import com.gitpitch.utils.PitchParams;
import org.apache.commons.io.FileUtils;
import play.Configuration;
import play.Logger;
import play.libs.ws.*;

import javax.inject.*;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;

/*
 * File system storage service.
 */
@Singleton
public class DiskService {

    private final Logger.ALogger log = Logger.of(this.getClass());

    private final String storage;
    private final String decktape;
    private final String rawAuthToken;
    private final ShellService shellService;
    private final Configuration configuration;
    private final WSClient ws;

    @Inject
    public DiskService(ShellService shellService,
                       Configuration configuration,
                       WSClient ws) {

        this.shellService = shellService;
        this.configuration = configuration;
        this.ws = ws;
        this.storage = configuration.getString("gitpitch.storage.home");
        this.decktape = configuration.getString("gitpitch.decktape.home");
        this.rawAuthToken = configuration.getString("gitpitch.raw.auth.token");
    }

    /*
     * Return PitchParams branch working directory.
     */
    public Path bwd(PitchParams pp) {
        return Paths.get(storage(), pp.grs, pp.user, pp.repo, pp.branch);
    }

    /*
     * Return Path for file within PitchParams branch working directory.
     */
    public Path asPath(PitchParams pp,
                       String filename) {
        return Paths.get(storage(), pp.grs, pp.user, pp.repo, pp.branch, filename);
    }

    /*
     * Return File for file within PitchParams branch working directory.
     */
    public File asFile(PitchParams pp,
                       String filename) {
        return asPath(pp, filename).toFile();
    }

    /*
     * Ensure PitchParams branch working directory exists.
     */
    public Path ensure(PitchParams pp) {
        return ensure(bwd(pp));
    }


    /*
     * Ensure PitchParams branch working sub-directory exists.
     */
    public Path ensure(PitchParams pp, String subdir) {

        return (subdir != null) ?
                ensure(asPath(pp, subdir)) : ensure(bwd(pp));
    }

    /*
     * Ensure PitchParams branch working directory exists.
     */
    public Path ensure(Path dirPath) {

        try {
            Files.createDirectories(dirPath);
        } catch (Exception ex) {
        }
        return dirPath;
    }

    /*
     * Download file from source into bwd resulting in dest.
     */
    public int download(PitchParams pp,
                        Path wd,
                        String source,
                        String dest,
                        Map<String,String> headers) {

        int downloaded = 999;

        try {

            log.debug("download: pp={}, source={}", pp, source);

            ensure(bwd(pp));
            Path destPath = Paths.get(wd.toString(), dest);
            delete(destPath);

            final long start = System.currentTimeMillis();

            final WSRequest downloadReq = ws.url(source);

            downloadReq.setHeader(API_CACHE_CONTROL, API_NO_CACHE);

            headers.forEach((k,v) -> {
                downloadReq.setHeader(k, v);
            });

            WSResponse downloadResp =
                    downloadReq.get().toCompletableFuture().get();

            if (downloadResp.getStatus() == HttpURLConnection.HTTP_OK) {

                byte[] body = downloadResp.asByteArray();
                Files.write(destPath, body);
                downloaded = STATUS_OK;
                log.debug("download: pp={}, time-taken={} (ms) to " +
                                "write {} bytes to {} from source={}", pp,
                        (System.currentTimeMillis() - start),
                        body.length, destPath, source);

            } else {

                downloaded = downloadResp.getStatus();
                log.debug("download: pp={}, failed status={}, " +
                        "from source={}", pp, downloaded, source);
            }

        } catch (Exception dex) {
            log.warn("download: failed pp={}, from source={}, ex={}",
                    pp, source, dex);
        }

        if (downloaded != STATUS_OK) {
            log.debug("download: pp={}, from source={}, failed status={}",
                    pp, source, downloaded);
        }

        return downloaded;
    }

    public void copyDirectoryFromJar(Path jarPath, String jarDir, Path dest) {

        log.debug("copyDirectoryFromJar: jarPath={}, jarDir={}, dest={}",
                jarPath, jarDir, dest);

        try(FileSystem fs = FileSystems.newFileSystem(jarPath, null)) {

            final Path source = fs.getPath(jarDir);

            copyDirectory(source, dest);

        } catch(Exception cex) {
            log.warn("copyDirectoryFromJar: jarPath={}, jarDir={}, dest={}, ex={}",
                    jarPath, jarDir, dest, cex);
        }
    }

    public void copyDirectory(Path source, Path dest) {

        log.debug("copyDirectory: source={}, dest={}", source, dest);

        try {

            Files.walkFileTree(source, new SimpleFileVisitor<Path>() {

                public FileVisitResult preVisitDirectory(Path dir,
                        BasicFileAttributes attrs) throws IOException {

                    Path relative = source.relativize(dir);
                    Path visitPath =
                        Paths.get(dest.toString(), relative.toString());
                    ensure(visitPath);
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult visitFile(Path file,
                        BasicFileAttributes attrs) throws IOException {

                    Path copyTarget = Paths.get(dest.toString(),
                            source.relativize(file).toString());
                    if(!file.getFileName().toString().matches("\\..*") &&
                            !copyTarget.getFileName().toString().matches("\\..*")) {
                        Files.copy(file, copyTarget);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

        } catch(Exception cex) {
            log.warn("copyDirectory: source={}, dest={}, ex={}", source, dest, cex);
        }
    }

    /*
     * Delete file within PitchParams working directory.
     */
    public void delete(PitchParams pp,
                       String filename) {

        delete(asPath(pp, filename));
    }

    /*
     * Recursively delete a target directory and contents.
     */
    public void deepDelete(PitchParams pp, String target) {
        Path branchPath = asPath(pp, target);
        deepDelete(branchPath.toFile());
    }

    /*
     * Recursively delete a target directory and contents.
     */
    public void deepDelete(File directory) {

        try {
            if(directory.exists()) {
                FileUtils.deleteDirectory(directory);
            }
        } catch (IOException ioex) {
            log.warn("deepDelete: {}, ex={}", directory, ioex);
        }
    }

    /*
     * Delete file within PitchParams working directory.
     */
    public void delete(Path filePath) {

        try {
            Files.deleteIfExists(filePath);
        } catch (Exception dex) {
        }
    }

    /*
     * Return disk storage home directory.
     */
    public String storage() {
        return storage;
    }

    /*
     * Return DeckTape PDF Exporter home directory.
     */
    public String decktape() {
        return decktape;
    }

    public String rawAuthToken() {
        return rawAuthToken;
    }

    private static final Integer STATUS_OK = 0;
    private static final String API_HEADER_AUTH = "Authorization";
    private static final String API_HEADER_TOKEN = "token ";
    private static final String API_CACHE_CONTROL = "Cache-Control";
    private static final String API_NO_CACHE = "no-cache";
}
