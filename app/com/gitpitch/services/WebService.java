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

import com.gitpitch.utils.PitchParams;
import play.Logger;
import play.libs.ws.*;

import javax.inject.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/*
 * HTTP(S) web service.
 */
@Singleton
public class WebService {

    private final Logger.ALogger log = Logger.of(this.getClass());

    private final WSClient ws;

    @Inject
    public WebService(WSClient ws) {

        this.ws = ws;
    }

    public byte[] fetchBytes(PitchParams pp,
                             String source,
                             Map<String,String> headers) {

        byte[] fetched = null;

        try {
            long start = System.currentTimeMillis();

            WSResponse downloadResp = download(pp, source, headers);

            if (downloadResp.getStatus() == HttpURLConnection.HTTP_OK) {

                fetched = downloadResp.asByteArray();
                log.debug("fetchBytes: pp={}, time-taken={} (ms) to " +
                                "read {} bytes from source={}", pp,
                        (System.currentTimeMillis() - start),
                        fetched.length, source);

            } else {

                log.debug("fetchBytes: pp={}, failed status={}, " +
                        "from source={}", pp, downloadResp.getStatus(), source);
            }
        } catch(Exception bex) {
            log.warn("fetchBytes: failed pp={}, from source={}, ex={}",
                    pp, source, bex);
        }

        return fetched;
    }

    public String fetchText(PitchParams pp,
                            String source,
                            Map<String,String> headers) {

        byte[] fetched = fetchBytes(pp, source, headers);
        if(fetched != null)
            return new String(fetched, StandardCharsets.UTF_8);
        else
            return null;
    }

    /*
     * Download file from HTTP(s) source url.
     */
    private WSResponse download(PitchParams pp,
                        String source,
                        Map<String,String> headers) {

        WSResponse downloadResp = null;

        try {

            log.debug("download: pp={}, source={}", pp, source);

            final long start = System.currentTimeMillis();

            final WSRequest downloadReq = ws.url(source);

            downloadReq.setHeader(API_CACHE_CONTROL, API_NO_CACHE);

            headers.forEach((k,v) -> {
                downloadReq.setHeader(k, v);
            });

            downloadResp =
                    downloadReq.get().toCompletableFuture().get();

        } catch (Exception dex) {
            log.warn("download: failed pp={}, from source={}, ex={}",
                    pp, source, dex);
        }

        return downloadResp;
    }

    private static final String API_HEADER_AUTH = "Authorization";
    private static final String API_HEADER_TOKEN = "token ";
    private static final String API_CACHE_CONTROL = "Cache-Control";
    private static final String API_NO_CACHE = "no-cache";
}
