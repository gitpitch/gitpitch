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

import com.fasterxml.jackson.databind.JsonNode;
import com.gitpitch.executors.FrontEndThreads;
import play.Configuration;
import play.Logger;
import play.Logger.ALogger;
import play.libs.ws.*;
import play.mvc.*;
import views.html.*;

import javax.inject.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * GitHub OAuth controller for GitPitch service.
 */
public class AuthController extends Controller {

    private final Logger.ALogger log = Logger.of(this.getClass());

    private final FrontEndThreads frontEndThreads;
    private final WSClient ws;
    private final Configuration cfg;

    @Inject
    public AuthController(FrontEndThreads frontEndThreads,
                          WSClient ws,
                          Configuration cfg) {

        this.frontEndThreads = frontEndThreads;
        this.ws = ws;
        this.cfg = cfg;
    }

    /*
     * Generate GitHub OAuth Request.
     */
    public Result authreq() {

        String state = Long.toHexString(System.currentTimeMillis());
        session("state", state);

        String redirectPath = GITHUB_OAUTH +
                "?client_id=" +
                "xxx" +
                "&scope=" +
                "public_repo" +
                "&state=" +
                state;

        return redirect(redirectPath);
    }

    public CompletionStage<Result> authorized(String code, String state) {

        log.debug("authorized: params code={}, state={}", code, state);
        String sessionUser = session("user");
        String sessionRepo = session("repo");
        String sessionState = session("state");
        log.debug("authorized: session.user={}", sessionUser);
        log.debug("authorized: session.repo={}", sessionRepo);
        log.debug("authorized: session.state={}", sessionState);

        if (state != null && state.equals(sessionState)) {

            WSRequest wsReq = ws.url(GITHUB_ACCESS)
                    .setHeader("Accept", "application/json")
                    .setQueryParameter("client_id", "xxx")
                    .setQueryParameter("client_secret", "xxx")
                    .setQueryParameter("code", code)
                    .setQueryParameter("state", state);

            return wsReq.post(" ").thenCompose(authResp -> {

                log.debug("authorized: status={}", authResp.getStatus());

                String accessToken = null;

                if (authResp.getStatus() == 200) {

                    try {

                        JsonNode json = authResp.asJson();

                        accessToken = json.findPath("access_token").textValue();
                        String accessScope = json.findPath("scope").textValue();
                        String accessType = json.findPath("token_type").textValue();
                        log.debug("authorized: json access_token={}", accessToken);
                        log.debug("authorized: json scope={}", accessScope);
                        log.debug("authorized: json token_type={}", accessType);


                    } catch (Exception jex) {
                        log.warn("authorized: parsing json ex={}", jex);
                    }
                }

                log.debug("authorized: returning accessToken={}", accessToken);
                return CompletableFuture.completedFuture(accessToken);

            }).handle((result, error) -> {

                log.debug("authorized: handle result={}, error={}", result, error);
                return ok("End-of-Auth");
            });

        } else {

            log.debug("authorized: state={} != sessionState={}",
                    state, sessionState);
            return CompletableFuture.completedFuture(ok("Failed to Authorize!"));

        }
    }

    private static final String GITHUB_OAUTH =
            "https://github.com/login/oauth/authorize";
    private static final String GITHUB_ACCESS =
            "https://github.com/login/oauth/access_token";
    private static final String GITHUB_OAUTH_CID = "client_id";
    private static final String GITHUB_OAUTH_STATE = "state";
}
