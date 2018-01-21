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
package com.gitpitch.git.vendors;

import com.gitpitch.git.*;
import com.gitpitch.models.*;
import com.gitpitch.utils.PitchParams;
import com.fasterxml.jackson.databind.JsonNode;

import javax.inject.*;
import play.Logger;

/*
 * GitHub API Service.
 */
@Singleton
public class GitHub extends GRSService {

    private final Logger.ALogger log = Logger.of(this.getClass());

    public GitRepoModel model(PitchParams pp, JsonNode json) {
        return GitHubRepoModel.build(pp, json);
    }

    public String raw(PitchParams pp) {

        GRS grs = grsManager.get(TYPE);

        return new StringBuffer(grs.getRawBase())
                .append(pp.user)
                .append(SLASH)
                .append(pp.repo)
                .append(SLASH)
                .append(grs.compoundBranch(pp.branch))
                .append(SLASH)
                .toString();
    }

    public String repo(PitchParams pp) {

        GRS grs = grsManager.get(TYPE);

        return new StringBuffer(grs.getApiBase())
                .append(GITHUB_REPO_API)
                .append(pp.user)
                .append(SLASH)
                .append(pp.repo)
                .toString();
    }

    public String gist(PitchParams pp, String gid, String fid) {

      GRS grs = grsManager.get(TYPE);

      StringBuffer gistLink = new StringBuffer(grs.getGistBase())
                                              .append(gid)
                                              .append(SLASH)
                                              .append(GITHUB_GIST_RAW);

      if(fid != null) {
        gistLink = gistLink.append(SLASH).append(fid);
      }

      return gistLink.toString();
    }

    public static final String TYPE = "github";
    private static final String GITHUB_REPO_API = "repos/";
    private static final String GITHUB_GIST_RAW = "raw";
}
