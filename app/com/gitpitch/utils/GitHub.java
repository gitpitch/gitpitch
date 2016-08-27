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
package com.gitpitch.utils;

import com.gitpitch.utils.PitchParams;
import play.Logger;
import play.Logger.ALogger;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * GitHub API endpoint utility.
 */
public final class GitHub {

    private final static Logger.ALogger log =
            Logger.of("com.gitpitch.utils.GitHub");

    private static AtomicInteger atomicInt = new AtomicInteger();

    public static String rawAPI(PitchParams pp) {

        return new StringBuffer(GHUB_RAW_BASE)
                .append(pp.user)
                .append(SLASH)
                .append(pp.repo)
                .append(SLASH)
                .append(pp.branch)
                .append(SLASH)
                .toString();
    }

    public static String rawAPI(PitchParams pp,
                                String filename) {

        return rawAPI(pp, filename, false);
    }

    public static String rawAPI(PitchParams pp,
                                String filename,
                                boolean bypassCache) {

        if (bypassCache) {
            /*
             * Adding ?gp={rand} to the URL ensures that the
             * GitHub cache does not return cached data.
             */
            return rawAPI(pp) + filename + "?gp=" + atomicInt.getAndIncrement();
        } else {
            return rawAPI(pp) + filename;
        }
    }

    public static String repoAPI(PitchParams pp) {

        return new StringBuffer(GHUB_REPO_META)
                .append(pp.user)
                .append(SLASH)
                .append(pp.repo)
                .toString();
    }

    public static boolean call(String url) {

        if (url != null) {
            return url.startsWith(GHUB_API_BASE) ||
                    url.startsWith(GHUB_RAW_BASE);
        } else {
            return false;
        }
    }

    private static final String SLASH = "/";
    private static final String GHUB_RAW_BASE = "https://raw.githubusercontent.com/";
    private static final String GHUB_API_BASE = "https://api.github.com";
    private static final String GHUB_REPO_META = GHUB_API_BASE + "/repos/";
}
