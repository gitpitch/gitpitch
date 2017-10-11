/*
 * MIT License
 *
 * Copyright (c) 2017 David Russell
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

import java.net.URL;
import java.util.Arrays;
import java.util.List;

/*
 * GitPitch URL utility parser.
 */
public class PitchLink {

    public String url;
    public String user = UNDEFINED;
    public String repo = UNDEFINED;
    public String branch = DEFAULT_BRANCH;
    public String grs = DEFAULT_GRS;

    private PitchLink(String url) {
        this.url = url;
    }
    private PitchLink(String url,
                      String user,
                      String repo,
                      String branch,
                      String grs) {

        this.url  = url;
        this.user = user;
        this.repo = repo;
        this.branch = branch;
        this.grs  = grs;
    }

    public static PitchLink build(String url) {
        try {
            URL urlo = new URL(url);
            String path = urlo.getPath();
            String[] paths = path.split(SLASH);
            String uPath = paths[1];
            String rPath = paths[2];
            String bPath = (paths.length == 4) ? paths[3] : DEFAULT_BRANCH;
            String query = urlo.getQuery();
            String gPath = DelimParams.build(query).get(GRS, DEFAULT_GRS);
            return new PitchLink(url, uPath, rPath, bPath, gPath);
        } catch(Exception ex) {
            return new PitchLink(url);
        }
    }

    private final static String DEFAULT_BRANCH = "master";
    private final static String DEFAULT_GRS = "github";
    private final static String UNDEFINED = "undefined";
    private final static String GRS = "grs";
    private final static String SLASH = "/";

}
