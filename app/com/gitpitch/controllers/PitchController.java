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
package com.gitpitch.controllers;

import com.gitpitch.git.GRS;
import com.gitpitch.git.GRSManager;
import com.gitpitch.models.GitRepoModel;
import com.gitpitch.models.MarkdownModel;
import com.gitpitch.executors.FrontEndThreads;
import com.gitpitch.models.SlideshowModel;
import com.gitpitch.services.PitchService;
import com.gitpitch.policies.Dependencies;
import com.gitpitch.utils.GitRepoRenderer;
import com.gitpitch.utils.PitchParams;
import com.gitpitch.utils.RFE;
import play.Configuration;
import play.Environment;
import play.Logger;
import play.Logger.ALogger;
import play.libs.ws.*;
import play.mvc.*;
import views.html.*;

import javax.inject.*;
import java.io.File;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Front controller for GitPitch service.
 */
public class PitchController extends Controller {

    private final Logger.ALogger log = Logger.of(this.getClass());

    private final PitchService pitchService;
    private final FrontEndThreads frontEndThreads;
    private final Dependencies deps;
    private final GRSManager grsManager;
    private final Configuration cfg;
    private final WSClient ws;
    private final Environment env;
    private final String gaToken;

    @Inject
    public PitchController(PitchService pitchService,
                           FrontEndThreads frontEndThreads,
                           Dependencies deps,
                           GRSManager grsManager,
                           Configuration cfg,
                           WSClient ws,
                           Environment env) {

        this.pitchService = pitchService;
        this.frontEndThreads = frontEndThreads;
        this.deps = deps;
        this.grsManager = grsManager;
        this.cfg = cfg;
        this.ws = ws;
        this.env = env;
        this.gaToken = cfg.getString("gitpitch.google.analytics.token");
    }

    public Result test() {

        log.debug("test: begins.");

        try {

            if(env.isDev()) {

                log.debug("test: isDev.");
                File libsDir = env.getFile("public/libs");
                log.debug("test: isDev, libs exists={}", libsDir.exists());

            } else
            if(env.isProd()) {

                log.debug("test: isProd.");

            }

        } catch(Exception tex) {
            log.warn("test: text={}", tex);
        }
        return ok("Test Completed");
    }

    /*
     * Catchall redirects any URL with a trailing slash to the
     * URL minus the trailing slash. Any remaining unhandled URLs
     * are redirected to the GitPitch website.
     */
    public Result catchall(String path) {
        try {
            if (path != "/" && path.endsWith("/")) {
                return redirect("/" + path.substring(0, path.length()-1));
            } else {
                return redirect("/");
            }
        } catch(Exception ex) {
            return redirect("/");
        }
    }

    /*
     * Landing builds and renders a GitPitch repo landing page.
     */
    public CompletionStage<Result> landing(String user,
                                           String repo,
                                           String branch,
                                           String grs,
                                           String theme,
                                           String pitchme,
                                           String notes,
                                           String offline) {

        PitchParams pp =
            PitchParams.build(grsOnCall(grs),
                    user, repo, branch, theme, pitchme, notes);
        boolean isOffline =
                (offline == null) ? false : Boolean.parseBoolean(offline);
        Optional<GitRepoModel> grmo = pitchService.cachedRepo(pp);

        if (grmo.isPresent()) {

            if (isOffline)
                log.info("landing:   [ cached, offlne ] {}", pp);
            else
                log.info("landing:   [ cached, online ] {}", pp);

            GitRepoModel grm = grmo.get();
            GitRepoRenderer rndr =
                GitRepoRenderer.build(pp, grm, cfg, grsManager.listGRS());

            return CompletableFuture.completedFuture(
                    ok(com.gitpitch.views.html.Landing.render(rndr,
                            deps, isOffline, gaToken)));

        } else {

            return CompletableFuture.supplyAsync(() -> {

                return pitchService.fetchRepo(pp);

            }, frontEndThreads.POOL)
                    .thenApply(fetched -> {

                        GitRepoRenderer rndr =
                            GitRepoRenderer.build(pp, fetched, cfg,
                                    grsManager.listGRS());

                        if (rndr.isValid()) {
                            if (isOffline)
                                log.info("landing:   [ fetchd, offlne ] {}", pp);
                            else
                                log.info("landing:   [ fetchd, online ] {}", pp);
                        } else {
                            if (isOffline)
                                log.info("landing:   [ notfnd, offlne ] {}", pp);
                            else
                                log.info("landing:   [ notfnd, online ] {}", pp);
                        }

                        return ok(com.gitpitch.views.html.Landing.render(rndr,
                                deps, isOffline, gaToken));
                    });
        }

    } // landing action

    /*
     * Slideshow builds and renders a GitPitch slideshow page.
     */
    public CompletionStage<Result> slideshow(String grs,
                                             String user,
                                             String repo,
                                             String branch,
                                             String theme,
                                             String pitchme,
                                             String notes,
                                             String fragments,
                                             String offline,
                                             String webprint) {

        PitchParams pp =
            PitchParams.build(grsOnCall(grs),
                    user, repo, branch, theme, pitchme, notes);
        boolean printing =
                (fragments == null) ? false : !Boolean.parseBoolean(fragments);
        boolean isOffline =
                (offline == null) ? false : Boolean.parseBoolean(offline);
        boolean webPrinting =
                (webprint == null) ? false : Boolean.parseBoolean(webprint);

        Optional<SlideshowModel> ssmo = pitchService.cachedYAML(pp);

        if (ssmo.isPresent()) {

            if (printing)
                log.info("slideshow: [ cached, printg ] {}", pp);
            else if (isOffline)
                log.info("slideshow: [ cached, offlne ] {}", pp);
            else
                log.info("slideshow: [ cached, online ] {}", pp);

            SlideshowModel ssm = ssmo.get();
            /*
             * Clone cached SlideshowModel in order to adjust for any
             * changes on the current PitchParams, such as {theme}.
             */
            ssm = ssm.clone(pp);
            return CompletableFuture.completedFuture(
                    ok(com.gitpitch.views.html.Slideshow.render(ssm,
                            deps, printing, isOffline, webPrinting)));

        } else {

            return CompletableFuture.supplyAsync(() -> {

                return pitchService.fetchYAML(pp);

            }, frontEndThreads.POOL)
                    .thenApply(fetched -> {

                        if (printing)
                            log.info("slideshow: [ fetchd, printg ] {}", pp);
                        if (isOffline)
                            log.info("slideshow: [ fetchd, offlne ] {}", pp);
                        else
                            log.info("slideshow: [ fetchd, online ] {}", pp);

                        return ok(com.gitpitch.views.html.Slideshow.render(fetched,
                                deps, printing, isOffline, webPrinting));
                    });
        }

    } // slideshow action

    /*
     * Markdown processes and renders PITCHME.md markdown.
     */
    public CompletionStage<Result> markdown(String grs,
                                            String user,
                                            String repo,
                                            String branch,
                                            String pitchme) {

        PitchParams pp =
            PitchParams.build(grsOnCall(grs),
                    user, repo, branch, null, pitchme);
        Optional<MarkdownModel> mdmo = pitchService.cachedMarkdown(pp);

        if (mdmo.isPresent()) {

            MarkdownModel mdm = mdmo.get();
            log.info("markdown:  [ cached, online ] {}", pp);
            return CompletableFuture.completedFuture(ok(mdm.produce())
                    .as("text/markdown"));

        } else {

            return CompletableFuture.supplyAsync(() -> {

                return pitchService.fetchMarkdown(pp);

            }, frontEndThreads.POOL)
                    .thenApply(fetched -> {

                        if (fetched != null) {
                            log.info("markdown:  [ fetchd, online ] {}", pp);
                            return ok(fetched.produce()).as("text/markdown");
                        }
                        if (pp.isMaster()) {
                            log.info("markdown:  [ notfnd, online ] {}", pp);
                            return ok(RFE.master(pp, grsManager.get(pp)))
                                    .as("text/markdown");
                        } else {
                            log.info("markdown:  [ notfnd, online ] {}", pp);
                            return ok(RFE.branch(pp, grsManager.get(pp)))
                                    .as("text/markdown");
                        }
                    });
        }

    } // markdown action

    /*
     * Print generates and renders PITCHME.pdf.
     */
    public CompletionStage<Result> print(String grs,
                                         String user,
                                         String repo,
                                         String branch,
                                         String theme,
                                         String pitchme,
                                         String notes) {

        PitchParams pp =
            PitchParams.build(grsOnCall(grs),
                    user, repo, branch, theme, pitchme, notes);
        Optional<File> pdfo = pitchService.cachedPDF(pp);

        if (pdfo.isPresent()) {

            log.info("print:     [ cached, printg ] {}", pp);
            File pdf = pdfo.get();
            return CompletableFuture.completedFuture(ok(pdf)
                    .as("application/pdf"));

        } else {

            return CompletableFuture.supplyAsync(() -> {

                return pitchService.fetchPDF(pp);

            }, frontEndThreads.POOL)
                    .thenApply(fetched -> {

                        if (fetched != null) {
                            log.info("print:     [ fetchd, printg ] {}", pp);
                            return ok(fetched).as("application/pdf");
                        } else {
                            log.info("print:     [ failed, printg ] {}", pp);
                            return ok(PITCHME_PRINT_ERROR);
                        }
                    });
        }

    } // print action

    /*
     * Offline generates and renders PITCHME.zip.
     */
    public CompletionStage<Result> offline(String grs,
                                           String user,
                                           String repo,
                                           String branch,
                                           String theme,
                                           String pitchme,
                                           String notes) {
                                          

        PitchParams pp =
            PitchParams.build(grsOnCall(grs),
                    user, repo, branch, theme, pitchme, notes);
        log.debug("offline: pp={}", pp);

        Optional<File> zipo = pitchService.cachedZip(pp);

        if (zipo.isPresent()) {

            log.info("offline:   [ cached, downld ] {}", pp);
            File zip = zipo.get();
            return CompletableFuture.completedFuture(ok(zip)
                    .as("application/zip"));

        } else {

            return CompletableFuture.supplyAsync(() -> {

                return pitchService.fetchZip(pp);

            }, frontEndThreads.POOL)
                    .thenApply(fetched -> {

                        if (fetched != null) {
                            log.info("offline:   [ fetchd, downld ] {}", pp);
                            return ok(fetched).as("application/zip");
                        } else {
                            log.info("offline:   [ failed, downld ] {}", pp);
                            return ok(PITCHME_OFFLINE_ERROR);
                        }
                    });
        }

    } // offline action

    /*
     * Gist generates and renders GitHub-Gist HTML for
     * embedding within slideshows.
     */
    public Result gist(String gid) {
        return ok(com.gitpitch.views.html.Gist.render(gid, deps));
    } // gist action

    /*
     * Determine GRS on call, explicitly defined or default.
     */
    private String grsOnCall(String grsParam) {
        return grsManager.getType(grsParam);
    }

    private static final String PITCHME_PRINT_ERROR =
            "GitPitch Slideshow print service temporarily unavailable.";
    private static final String PITCHME_OFFLINE_ERROR =
            "GitPitch Slideshow offline service temporarily unavailable.";
}
