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
import com.gitpitch.oembed.PitchEmbed;
import com.gitpitch.policies.Dependencies;
import com.gitpitch.utils.GitRepoRenderer;
import com.gitpitch.utils.PitchParams;
import com.gitpitch.utils.RFE;
import play.Configuration;
import play.Environment;
import play.Logger;
import play.Logger.ALogger;
import play.libs.Json;
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
                return ok(com.gitpitch.views.html.NotFound.render());
            }
        } catch(Exception ex) {
            return ok(com.gitpitch.views.html.NotFound.render());
        }
    }

    /*
     * Slideshow builds and renders a GitPitch presentation.
     */
    public CompletionStage<Result> slideshow(String user,
                                             String repo,
                                             String branch,
                                             String grs,
                                             String theme,
                                             String pitchme,
                                             String notes,
                                             String offline,
                                             String fragments,
                                             String webprint) {

        PitchParams pp =
            PitchParams.build(grsOnCall(grs),
                    user, repo, branch, theme, pitchme, notes);
        boolean isOffline =
                (offline == null) ? false : Boolean.parseBoolean(offline);
        boolean serverPrinting =
                (fragments == null) ? false : !Boolean.parseBoolean(fragments);
        boolean webPrinting =
                (webprint == null) ? false : Boolean.parseBoolean(webprint);

        Optional<GitRepoModel> grmo = pitchService.cachedRepo(pp);
        Optional<SlideshowModel> ssmo = pitchService.cachedYAML(pp);

        if (grmo.isPresent() && ssmo.isPresent()) {

            if (isOffline)
                log.info("slideshow: [ deps, cached, offlne ] {}", pp);
            else
                log.info("slideshow: [ deps, cached, online ] {}", pp);

            GitRepoModel grm = grmo.get();
            GitRepoRenderer rndr =
                GitRepoRenderer.build(pp, grm, cfg, grsManager.listGRS());
            SlideshowModel ssm = ssmo.get();
            /*
             * Clone cached SlideshowModel in order to adjust for any
             * changes on the current PitchParams, such as {theme}.
             */
            ssm = ssm.clone(pp);

            return CompletableFuture.completedFuture(
                    ok(com.gitpitch.views.html.Slideshow.render(ssm, rndr, deps,
                        gaToken, isOffline, serverPrinting, webPrinting)));
        } else {

            return CompletableFuture.supplyAsync(() -> {

                return pitchService.fetchRepo(pp);

            }, frontEndThreads.POOL)
                    .thenApply(repoFetched -> {

                        GitRepoRenderer rndr =
                            GitRepoRenderer.build(pp, repoFetched, cfg,
                                    grsManager.listGRS());

                        if (ssmo.isPresent()) {

                            if (webPrinting)
                                log.info("slideshow: [ repo, fetchd, printg ] {}", pp);
                            else
                            if (isOffline)
                                log.info("slideshow: [ repo, fetchd, offlne ] {}", pp);
                            else
                                log.info("slideshow: [ repo, fetchd, online ] {}", pp);

                            SlideshowModel ssm = ssmo.get();
                            /*
                             * Clone cached SlideshowModel in order to adjust for any
                             * changes on the current PitchParams, such as {theme}.
                             */
                            ssm = ssm.clone(pp);
                            return ok(com.gitpitch.views.html.Slideshow.render(ssm,
                                rndr, deps, gaToken,
                                isOffline, serverPrinting, webPrinting));

                        } else {

                            SlideshowModel ssm = pitchService.fetchYAML(pp);

                            if (webPrinting)
                                log.info("slideshow: [ yaml, fetchd, printg ] {}", pp);
                            else
                            if (isOffline)
                                log.info("slideshow: [ yaml, fetchd, offlne ] {}", pp);
                            else
                                log.info("slideshow: [ yaml, fetchd, online ] {}", pp);

                            return ok(com.gitpitch.views.html.Slideshow.render(ssm,
                                rndr, deps, gaToken,
                                isOffline, serverPrinting, webPrinting));
                        }

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
            log.info("markdown:  [ mdwn, cached, online ] {}", pp);
            return CompletableFuture.completedFuture(ok(mdm.produce())
                    .as("text/markdown"));

        } else {

            return CompletableFuture.supplyAsync(() -> {

                return pitchService.fetchMarkdown(pp);

            }, frontEndThreads.POOL)
                    .thenApply(fetched -> {

                        if (fetched != null) {
                            log.info("markdown:  [ mdwn, fetchd, online ] {}", pp);
                            return ok(fetched.produce()).as("text/markdown");
                        }
                        if (pp.isMaster()) {
                            log.info("markdown:  [ mdwn, notfnd, online ] {}", pp);
                            return ok(RFE.master(pp, grsManager.get(pp)))
                                    .as("text/markdown");
                        } else {
                            log.info("markdown:  [ mdwn, notfnd, online ] {}", pp);
                            return ok(RFE.branch(pp, grsManager.get(pp)))
                                    .as("text/markdown");
                        }
                    });
        }

    } // markdown action

    /*
     * Home generates presentation home side-panel.
     */
    public CompletionStage<Result> home(String grs,
                                        String user,
                                        String repo,
                                        String branch,
                                        String theme,
                                        String pitchme,
                                        String offline) {

        PitchParams pp =
            PitchParams.build(grsOnCall(grs),
                    user, repo, branch, theme, pitchme);

        boolean isOffline =
                (offline == null) ? false : Boolean.parseBoolean(offline);

        Optional<GitRepoModel> grmo = pitchService.cachedRepo(pp);

        GitRepoModel grm = grmo.orElse(null);
        GitRepoRenderer rndr =
                GitRepoRenderer.build(pp, grm, cfg, grsManager.listGRS());

        return CompletableFuture.completedFuture(
                ok(com.gitpitch.views.html.Home.render(rndr,
                                                       deps,
                                                       isOffline,
                                                       userAgentIsChrome())));
    } // home action

    /*
     * Git generates presentation git (grs) side-panel.
     */
    public CompletionStage<Result> git(String grs,
                                       String user,
                                       String repo,
                                       String branch,
                                       String theme,
                                       String pitchme,
                                       String offline) {

        PitchParams pp =
            PitchParams.build(grsOnCall(grs),
                    user, repo, branch, theme, pitchme);

        boolean isOffline =
                (offline == null) ? false : Boolean.parseBoolean(offline);

        Optional<GitRepoModel> grmo = pitchService.cachedRepo(pp);

        GitRepoModel grm = grmo.orElse(null);
        GitRepoRenderer rndr =
                GitRepoRenderer.build(pp, grm, cfg, grsManager.listGRS());

        return CompletableFuture.completedFuture(
                ok(com.gitpitch.views.html.Git.render(rndr, deps, isOffline)));

    } // git action

    /*
     * Themes generates presentation themes side-panel.
     */
    public CompletionStage<Result> themes(String grs,
                                          String user,
                                          String repo,
                                          String branch,
                                          String theme,
                                          String pitchme,
                                          String offline) {

        PitchParams pp =
            PitchParams.build(grsOnCall(grs),
                    user, repo, branch, theme, pitchme);

        boolean isOffline =
                (offline == null) ? false : Boolean.parseBoolean(offline);

        Optional<GitRepoModel> grmo = pitchService.cachedRepo(pp);
        Optional<SlideshowModel> ssmo = pitchService.cachedYAML(pp);

        GitRepoModel grm = grmo.orElse(null);
        GitRepoRenderer rndr =
                GitRepoRenderer.build(pp, grm, cfg, grsManager.listGRS());
        String fixedTheme = null;
        if(ssmo.isPresent()) {
          fixedTheme = ssmo.get().fixedTheme() ? ssmo.get().fetchTheme() : null;
        }

        return CompletableFuture.completedFuture(
                ok(com.gitpitch.views.html.Themes.render(rndr, deps, fixedTheme, isOffline)));

    } // themes action

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
     * oembed generates an oEmbed provider response.
     */
    public Result oembed(String url,
                         String format,
                         String width,
                         String height,
                         String maxwidth,
                         String maxheight,
                         String referrer) {

        log.info("oembed: [ {}, {}, {} ]", url, format, referrer);

        PitchEmbed pembed = PitchEmbed.build(grsManager,
                                             url,
                                             width,
                                             height,
                                             maxwidth,
                                             maxheight);
        Result result = TODO; // Indicates 501 NotImplemented
        switch(format) {
            case OEMBED_JSON:
                result = ok(Json.toJson(pembed));
                break;
            case OEMBED_XML:
                result = ok(com.gitpitch.views.xml.OEmbed.render(pembed));
                break;
        }
        return result;
    } // oembed action

    /*
     * Raw returns file content identified by path.
     */
    public CompletionStage<Result> raw(String grs,
                                       String user,
                                       String repo,
                                       String branch,
                                       String path) {

        PitchParams pp =
            PitchParams.build(grsOnCall(grs), user, repo, branch);
        log.debug("raw: pp={}, path={}", pp, path);

        String offline = request().getQueryString("offline");
        log.debug("raw: pp={}, path={}, offline={}", pp, path, offline != null);

        Optional<File> fileo = pitchService.raw(pp, path);

        if (fileo.isPresent()) {

            log.debug("raw: pp={}, path={} file is found.");
            File file = fileo.get();
            return CompletableFuture.completedFuture(ok(file));

        } else {

            log.debug("raw: pp={}, path={} file not found.");
            return CompletableFuture.completedFuture(notFound());

        }

    } // raw action

    /*
     * Determine GRS on call, explicitly defined or default.
     */
    private String grsOnCall(String grsParam) {
        return grsManager.getType(grsParam);
    }

    private boolean userAgentIsChrome() {
        boolean isChrome = false;
        try {
            String[]  userAgentHeaders =
                request().headers().get(Http.HeaderNames.USER_AGENT);
            if(userAgentHeaders.length > 0) {
                String userAgent = userAgentHeaders[0];
                isChrome = userAgent.contains("Chrome/") ||
                            userAgent.contains("Chromium/");
            }

        } catch(Exception ex) {}
        return isChrome;
    }

    private static final String PITCHME_PRINT_ERROR =
            "GitPitch Slideshow print service temporarily unavailable.";
    private static final String PITCHME_OFFLINE_ERROR =
            "GitPitch Slideshow offline service temporarily unavailable.";

    private static final String OEMBED_JSON = "json";
    private static final String OEMBED_XML  = "xml";
}
