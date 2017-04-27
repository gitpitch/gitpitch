package com.gitpitch;

import org.junit.Test;
import play.Logger;
import play.libs.ws.*;
import play.test.TestServer;

import java.util.concurrent.CompletionStage;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.test.Helpers.*;

public class FunctionalTest {

    final static Logger.ALogger log = Logger.of("com.gitpitch.ServerTest");
    private static final String LANDING_PITCH_FOUND = "/gitpitch/hello-world";
    private static final String LANDING_PITCH_NOT_FOUND = "/gitpitch/hello-world/not-found";
    private static final String SLIDESHOW_PITCH_FOUND =
            "/pitchme/slideshow/github/gitpitch/hello-world/master";
    private static final String SLIDESHOW_PITCH_NOT_FOUND =
            "/pitchme/slideshow/github/gitpitch/hello-world/not-found";
    private static final String MARKDOWN_PITCH_FOUND =
            "/pitchme/markdown/github/gitpitch/hello-world/master/PITCHME.md";
    private static final String MARKDOWN_PITCH_NOT_FOUND =
            "/pitchme/markdown/github/gitpitch/hello-world/not-found/PITCHME.md";
    private static final String UNSUPPORTED_ENDPOINT = "/unsupported";
    private static final String INVALID_ENDPOINT = "/invalid/endpoint/";
    private static final int SERVER_TEST_PORT = 3333;
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE_HTML = "text/html; charset=utf-8";
    private static final String CONTENT_TYPE_MD = "text/markdown";
    private static final String CONTENT_TYPE_UNDEF = null;

    @Test
    public void testLandingPitchFound() throws Exception {

        TestServer server = testServer(3333);
        running(server, () -> {
            try {
                WSClient ws = play.libs.ws.WS.newClient(3333);
                CompletionStage<WSResponse> completionStage =
                        ws.url(LANDING_PITCH_FOUND).get();
                WSResponse response = completionStage.toCompletableFuture().get();
                ws.close();
                assertEquals(OK, response.getStatus());
                assertEquals(CONTENT_TYPE_HTML, response.getHeader(CONTENT_TYPE));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    @Test
    public void testLandingPitchNotFound() throws Exception {

        TestServer server = testServer(3333);
        running(server, () -> {
            try {
                WSClient ws = play.libs.ws.WS.newClient(3333);
                CompletionStage<WSResponse> completionStage =
                        ws.url(LANDING_PITCH_NOT_FOUND).get();
                WSResponse response = completionStage.toCompletableFuture().get();
                ws.close();
                assertEquals(OK, response.getStatus());
                assertEquals(CONTENT_TYPE_HTML, response.getHeader(CONTENT_TYPE));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    @Test
    public void testSlideshowPitchFound() throws Exception {

        TestServer server = testServer(3333);
        running(server, () -> {
            try {
                WSClient ws = play.libs.ws.WS.newClient(3333);
                CompletionStage<WSResponse> completionStage =
                        ws.url(SLIDESHOW_PITCH_FOUND).get();
                WSResponse response = completionStage.toCompletableFuture().get();
                ws.close();
                assertEquals(OK, response.getStatus());
                assertEquals(CONTENT_TYPE_HTML, response.getHeader(CONTENT_TYPE));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    @Test
    public void testSlideshowPitchNotFound() throws Exception {

        TestServer server = testServer(3333);
        running(server, () -> {
            try {
                WSClient ws = play.libs.ws.WS.newClient(3333);
                CompletionStage<WSResponse> completionStage =
                        ws.url(SLIDESHOW_PITCH_NOT_FOUND).get();
                WSResponse response = completionStage.toCompletableFuture().get();
                ws.close();
                assertEquals(OK, response.getStatus());
                assertEquals(CONTENT_TYPE_HTML, response.getHeader(CONTENT_TYPE));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    @Test
    public void testMarkdownPitchFound() throws Exception {

        TestServer server = testServer(3333);
        running(server, () -> {
            try {
                WSClient ws = play.libs.ws.WS.newClient(3333);
                CompletionStage<WSResponse> completionStage =
                        ws.url(MARKDOWN_PITCH_FOUND).get();
                WSResponse response = completionStage.toCompletableFuture().get();
                ws.close();
                assertEquals(OK, response.getStatus());
                assertEquals(CONTENT_TYPE_MD, response.getHeader(CONTENT_TYPE));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    @Test
    public void testMarkdownPitchNotFound() throws Exception {

        TestServer server = testServer(3333);
        running(server, () -> {
            try {
                WSClient ws = play.libs.ws.WS.newClient(3333);
                CompletionStage<WSResponse> completionStage =
                        ws.url(MARKDOWN_PITCH_NOT_FOUND).get();
                WSResponse response = completionStage.toCompletableFuture().get();
                ws.close();
                assertEquals(OK, response.getStatus());
                assertEquals(CONTENT_TYPE_MD, response.getHeader(CONTENT_TYPE));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    @Test
    public void testUnsupportedEndpoint() throws Exception {

        TestServer server = testServer(SERVER_TEST_PORT);
        running(server, () -> {
            try {
                WSClient ws = play.libs.ws.WS.newClient(SERVER_TEST_PORT);
                CompletionStage<WSResponse> completionStage =
                        ws.url(UNSUPPORTED_ENDPOINT).get();
                WSResponse response = completionStage.toCompletableFuture().get();
                ws.close();
                assertEquals(SEE_OTHER, response.getStatus());
                assertEquals(CONTENT_TYPE_UNDEF, response.getHeader(CONTENT_TYPE));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

    @Test
    public void testInvalidEndpoint() throws Exception {

        TestServer server = testServer(SERVER_TEST_PORT);
        running(server, () -> {
            try {
                WSClient ws = play.libs.ws.WS.newClient(SERVER_TEST_PORT);
                CompletionStage<WSResponse> completionStage =
                        ws.url(INVALID_ENDPOINT).get();
                WSResponse response = completionStage.toCompletableFuture().get();
                ws.close();
                assertEquals(SEE_OTHER, response.getStatus());
                assertEquals(CONTENT_TYPE_UNDEF, response.getHeader(CONTENT_TYPE));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }

}