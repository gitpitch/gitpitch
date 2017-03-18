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

import com.fasterxml.jackson.databind.JsonNode;
import com.gitpitch.factory.MarkdownModelFactory;
import com.gitpitch.git.*;
import com.gitpitch.models.GitRepoModel;
import com.gitpitch.models.MarkdownModel;
import com.gitpitch.models.SlideshowModel;
import com.gitpitch.policies.CacheTimeout;
import com.gitpitch.executors.BackEndThreads;
import com.gitpitch.utils.*;
import play.Configuration;
import play.Logger;
import play.cache.*;
import play.libs.ws.*;

import javax.inject.*;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/*
 * GitHub API integration service.
 */
@Singleton
public class GitService {

    private final Logger.ALogger log = Logger.of(this.getClass());

    private final ConcurrentHashMap<String, CountDownLatch> repoLatchMap =
            new ConcurrentHashMap();
    private final ConcurrentHashMap<String, CountDownLatch> yamlLatchMap =
            new ConcurrentHashMap();
    private final ConcurrentHashMap<String, CountDownLatch> markdownLatchMap =
            new ConcurrentHashMap();

    private final GRSManager grsManager;
    private final DiskService diskService;
    private final PrintService printService;
    private final ShellService shellService;
    private final CacheTimeout cacheTimeout;
    private final BackEndThreads backEndThreads;
    private final MarkdownModelFactory markdownModelFactory;
    private final ComposableService composableService;
    private final WSClient wsClient;
    private final CacheApi pitchCache;
    private final Configuration configuration;

    @Inject
    public GitService(GRSManager grsManager,
                      DiskService diskService,
                      PrintService printService,
                      ShellService shellService,
                      CacheTimeout cacheTimeout,
                      BackEndThreads backEndThreads,
                      MarkdownModelFactory markdownModelFactory,
                      ComposableService composableService,
                      WSClient wsClient,
                      CacheApi pitchCache,
                      Configuration configuration) {

        this.grsManager = grsManager;
        this.diskService = diskService;
        this.printService = printService;
        this.shellService = shellService;
        this.cacheTimeout = cacheTimeout;
        this.backEndThreads = backEndThreads;
        this.markdownModelFactory = markdownModelFactory;
        this.composableService = composableService;
        this.wsClient = wsClient;
        this.pitchCache = pitchCache;
        this.configuration = configuration;
    }

    /*
     * Fetch GitHub repo meta-data on GitHub API.
     */
    public CountDownLatch fetchRepo(PitchParams pp) {

        final String grmKey = GitRepoModel.genKey(pp);
        log.debug("fetchRepo: pp={}", pp);
        CountDownLatch freshLatch = new CountDownLatch(1);
        CountDownLatch activeLatch =
                repoLatchMap.putIfAbsent(grmKey, freshLatch);

        if (activeLatch != null) {

            /*
             * Non-null activeLatch implies a fetchRepo() operation
             * is already in progress for this /{user}/{repo}. So
             * activeLatch so caller can block until operation completes.
             */
            log.debug("fetchRepo: pp={}, already in progress, " +
                    "returning existing activeLatch={}", pp, activeLatch);
            return activeLatch;

        } else {

            GRS grs = grsManager.get(pp);
            final GRSService grsService = grsManager.getService(grs);
            String apiCall = grsService.repo(pp);

            log.debug("fetchRepo: apiCall={}", apiCall);
            final long start = System.currentTimeMillis();

            WSRequest apiRequest = wsClient.url(apiCall);

            grs.getHeaders().forEach((k,v) -> {
                apiRequest.setHeader(k, v);
            });

            CompletableFuture<WSResponse> apiFuture =
                    apiRequest.get().toCompletableFuture();

            CompletableFuture<GitRepoModel> rmFuture =
                    apiFuture.thenApplyAsync(apiResp -> {

                        log.debug("fetchRepo: pp={}, fetch meta time-taken={}",
                                pp, (System.currentTimeMillis() - start));

                        log.info("{}: API Rate Limit Status [ {}, {} ]",
                                grs.getName(),
                                apiResp.getHeader(API_RATE_LIMIT),
                                apiResp.getHeader(API_RATE_LIMIT_REMAINING));

                        try {

                            if (apiResp.getStatus() == HTTP_OK) {

                                try {

                                    JsonNode json = apiResp.asJson();
                                    GitRepoModel grm = grsService.model(pp, json);

                                    /*
                                     * Update pitchCache with new GitRepoModel
                                     * generated using GitHub API response data.
                                     */
                                    pitchCache.set(grm.key(), grm, cacheTimeout.grm(pp));

                                } catch (Exception ex) {
                                    /*
                                     * Prevent any runtime errors, such as JSON parsing,
                                     * from propogating to the front end.
                                     */
                                    log.warn("fetchRepo: pp={}, unexpected ex={}", pp, ex);
                                }

                            } else {

                                log.debug("fetchRepo: pp={}, fail status={}",
                                        pp, apiResp.getStatus());

                                try {

                                    String remainingHdr =
                                            apiResp.getHeader(API_RATE_LIMIT_REMAINING);
                                    int rateLimitRemaining =
                                            Integer.parseInt(remainingHdr);

                                    if (rateLimitRemaining <= 0) {
                                        log.warn("WARNING! {} API rate limit exceeded.", grs.getName());
                                    }

                                } catch (Exception rlex) {
                                }
                            }

                        } catch (Exception rex) {
                            log.warn("fetchRepo: pp={}, unexpected runtime ex={}", pp, rex);
                        }

                        /*
                         * Current operation completed, so remove latch associated
                         * with operation from repoLatchMap to permit future operations
                         * on this /{user}/{repo}.
                         */
                        releaseCountDownLatch(repoLatchMap, grmKey);

                        /*
                         * Operation completed, valid result cached, no return required.
                         */
                        return null;

                    }, backEndThreads.POOL)
                            .handle((result, error) -> {

                                if (error != null) {

                                    log.warn("fetchRepo: pp={}, fetch error={}", pp, error);
                                    releaseCountDownLatch(repoLatchMap, grmKey);
                                }

                                return null;
                            });

            return freshLatch;
        }
    }

    /*
     * Fetch PITCHME.yaml configuration from GitHub repo.
     */
    public CountDownLatch fetchYAML(PitchParams pp) {

        final String ssmKey = SlideshowModel.genKey(pp);

        CountDownLatch freshLatch = new CountDownLatch(1);
        CountDownLatch activeLatch =
                yamlLatchMap.putIfAbsent(ssmKey, freshLatch);

        if (activeLatch != null) {
            /*
             * A non-null activeLatch implies a fetchYAML()
             * operation is already in progress for the current
             * /{user}/{repo}?b={branch}.
             */
            log.debug("fetchYAML: pp={}, already in progress, " +
                    "returning existing activeLatch={}", pp, activeLatch);
            return activeLatch;

        } else {

            CompletableFuture<Void> syncFuture =
                    CompletableFuture.supplyAsync(() -> {

                        GRS grs = grsManager.get(pp);
                        GRSService grsService = grsManager.getService(grs);
                        int downYAML = grsService.download(pp,
                                PitchParams.PITCHME_YAML, pp.YAML());
                        boolean downOk = downYAML == STATUS_OK;
                        log.debug("fetchYAML: pp={}, downloaded YAML={}", pp, downYAML);

                        /*
                         * Update pitchCache with new SlideshowModel.
                         */
                        SlideshowModel ssm =
                                SlideshowModel.build(pp, downOk, grsService, diskService);
                        pitchCache.set(ssm.key(), ssm, cacheTimeout.ssm(pp));

                        /*
                         * Current operation completed, so remove latch associated
                         * with operation from yamlLatchMap to permit future
                         * operations on this /{user}/{repo}?b={branch}.
                         */
                        releaseCountDownLatch(yamlLatchMap, ssmKey);

                        /*
                         * Operation completed, valid result cached, no return required.
                         */
                        return null;

                    }, backEndThreads.POOL)
                            .handle((result, error) -> {

                                if (error != null) {

                                    log.warn("fetchYAML: pp={}, fetch error={}", pp, error);
                                    releaseCountDownLatch(yamlLatchMap, ssmKey);
                                }

                                return null;
                            });

            return freshLatch;
        }

    }

    /*
     * Fetch PITCHME.md markdown from GitHub repo.
     */
    public CountDownLatch fetchMarkdown(PitchParams pp) {

        final String mdmKey = MarkdownModel.genKey(pp);

        CountDownLatch freshLatch = new CountDownLatch(1);
        CountDownLatch activeLatch =
                markdownLatchMap.putIfAbsent(mdmKey, freshLatch);

        if (activeLatch != null) {
            /*
             * A non-null activeLatch implies a fetchMarkdown()
             * operation is already in progress for the current
             * /{user}/{repo}?b={branch}.
             */
            log.debug("fetchMarkdown: pp={}, already in progress, " +
                    "returning existing activeLatch={}", pp, activeLatch);
            return activeLatch;

        } else {

            CompletableFuture<Void> syncFuture =
                    CompletableFuture.supplyAsync(() -> {

                        GRS grs = grsManager.get(pp);
                        GRSService grsService = grsManager.getService(grs);

                        int downStatus = grsService.download(pp,
                                PitchParams.PITCHME_MD, pp.MD());

                        if (downStatus == STATUS_OK) {

                            /*
                             * Process Composable Presentation includes
                             * found within PITCHME.md.
                             */
                            composableService.compose(pp, grs, grsService);

                            String ssmKey = SlideshowModel.genKey(pp);
                            Optional<SlideshowModel> ssmo =
                                    Optional.ofNullable(pitchCache.get(ssmKey));

                            /*
                             * The SSM in the pitchCache can be absent if
                             * we are currently processing a print or offline
                             * download. In these caes, quickly reconstitute
                             * the SSM using YAML on disk so markdown rendering
                             * can honor all custom YAML configurations.
                             */
                            if(!ssmo.isPresent()) {

                                SlideshowModel ssm =
                                    SlideshowModel.build(pp, true, grsService, diskService);
                                ssmo = Optional.of(ssm);
                            }

                            MarkdownRenderer mrndr = MarkdownRenderer.build(pp,
                                    ssmo, grsService, diskService);

                            // MarkdownModel mdm = MarkdownModel.consume(mrndr);
                            MarkdownModel mdm =
                                (MarkdownModel) markdownModelFactory.create(mrndr);
                            pitchCache.set(mdmKey, mdm, cacheTimeout.mdm(pp));

                        } else {
                            log.debug("fetchMarkdown: pp={}, failed status={}",
                                    pp, downStatus);
                        }

                        /*
                         * Current operation completed, so remove latch associated
                         * with operation from markdownLatchMap to permit future
                         * operations on this /{user}/{repo}?b={branch}.
                         */
                        releaseCountDownLatch(markdownLatchMap, mdmKey);

                        /*
                         * Operation completed, valid result cached, no return required.
                         */
                        return null;

                    }, backEndThreads.POOL)
                            .handle((result, error) -> {

                                if (error != null) {

                                    log.warn("fetchMarkdown: pp={}, fetch error={}",
                                            pp, error);
                                    releaseCountDownLatch(markdownLatchMap, mdmKey);
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
     * on /{user}/{repo}/{branch} associated with latchKey.
     */
    private void releaseCountDownLatch(ConcurrentHashMap<String,
            CountDownLatch> latchMap,
                                       String latchKey) {

        CountDownLatch completedLatch = latchMap.remove(latchKey);

        if (completedLatch != null) {
            completedLatch.countDown();
        }

    }

    private static final String GHUB_REPO_META =
            "https://api.github.com/repos/";
    private static final String GIT_MASTER = "master";
    private static final String SLASH = "/";
    private static final int HTTP_OK = 200;
    private static final int STATUS_OK = 0;
    private static final String API_HEADER_AUTH = "Authorization";
    private static final String API_HEADER_TOKEN = "token ";
    private static final String API_RATE_LIMIT = "X-RateLimit-Limit";
    private static final String API_RATE_LIMIT_REMAINING = "X-RateLimit-Remaining";
}
