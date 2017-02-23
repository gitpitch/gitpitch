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

import com.gitpitch.models.MarkdownModel;
import com.gitpitch.executors.BackEndThreads;
import com.gitpitch.utils.PitchParams;
import play.Configuration;
import play.Logger;

import javax.inject.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/*
 * PITCHME.pdf document print service.
 */
@Singleton
public class PrintService {

    private final Logger.ALogger log = Logger.of(this.getClass());

    private DiskService diskService;
    private ShellService shellService;
    private BackEndThreads backEndThreads;
    private Configuration configuration;

    @Inject
    public PrintService(DiskService diskService,
                        ShellService shellService,
                        BackEndThreads backEndThreads,
                        Configuration configuration) {

        this.diskService = diskService;
        this.shellService = shellService;
        this.backEndThreads = backEndThreads;
        this.configuration = configuration;
    }

    /*
     * Generate and fetch PITCHME.pdf.
     */
    public CountDownLatch fetchPDF(PitchParams pp) {

        log.debug("fetchPDF: pp={}", pp);

        final String pdfKey = MarkdownModel.genKey(pp);

        CountDownLatch freshLatch = new CountDownLatch(1);
        CountDownLatch activeLatch =
                pdfLatchMap.putIfAbsent(pdfKey, freshLatch);

        if (activeLatch != null) {
            /*
             * A non-null activeLatch implies a fetchPDF()
             * operation is already in progress for the current
             * /{user}/{repo}?b={branch}.
             */
            log.debug("fetchPDF: pp={}, already in progress, " +
                    "returning existing activeLatch={}", pp, activeLatch);
            return activeLatch;

        } else {

            CompletableFuture<Void> syncFuture =
                    CompletableFuture.supplyAsync(() -> {

                        Path branchPath = diskService.ensure(pp, pp.pitchme);
                        int pdfStatus = generatePDF(pp);

                        if (pdfStatus != STATUS_OK) {
                            log.warn("fetchPDF: pp={}, fetch status={}", pp, pdfStatus);
                        }

                /*
                 * Current operation completed, so remove latch associated
                 * with operation from pdfLatchMap to permit future
                 * operations on this /{user}/{repo}?b={branch}.
                 */
                        releaseCountDownLatch(pdfLatchMap, pdfKey);

                /*
                 * Operation completed, valid result cached, no return required.
                 */
                        return null;

                    }, backEndThreads.POOL)
                            .handle((result, error) -> {

                                if (error != null) {

                                    log.warn("fetchPDF: pp={}, fetch error={}", pp, error);
                                    releaseCountDownLatch(pdfLatchMap, pdfKey);
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
     * Generate PDF using DeckTape command line service.
     */
    private int generatePDF(PitchParams pp) {

        log.debug("generatePDF: pp={}", pp);
        Path branchPath = diskService.ensure(pp);
        diskService.delete(pp, pp.PDF());
        String filePath = diskService.asFile(pp, pp.PDF()).toString();

        String slideshowUrl =
                com.gitpitch.controllers.routes.PitchController.slideshow(pp.grs,
                        pp.user,
                        pp.repo,
                        pp.branch,
                        pp.theme,
                        pp.pitchme,
                        pp.notes,
                        PRINT_NO_FRAGS,
                        null,
                        null)
                        .absoluteURL(isEncrypted(),
                                hostname());

        String[] cmd = {PDF_PHANTOM,
                PDF_DECKTAPE,
                PDF_COMMAND,
                slideshowUrl,
                filePath};
        Path dtapePath = Paths.get(diskService.decktape());

        int generated = shellService.exec(GIT_PDF, pp, dtapePath, cmd);

        if (generated != STATUS_OK) {
            log.warn("generatePDF: pp={}, generate status={}", pp, generated);
        }

        return generated;
    }

    public boolean isEncrypted() {
        return configuration.getBoolean("gitpitch.https");
    }

    public String hostname() {
        return configuration.getString("gitpitch.hostname");
    }

    private static final String PDF_PHANTOM = "./bin/phantomjs";
    private static final String PDF_DECKTAPE = "decktape.js";
    private static final String PDF_COMMAND = "reveal";
    private static final String PITCHME_PDF = "PITCHME.pdf";
    private static final String PRINT_NO_FRAGS = "false";
    private static final String GIT_PDF = "pdf";
    private static final int STATUS_OK = 0;

    private final ConcurrentHashMap<String, CountDownLatch> pdfLatchMap =
            new ConcurrentHashMap();
            
}
