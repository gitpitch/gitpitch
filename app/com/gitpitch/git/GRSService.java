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
package com.gitpitch.git;

import com.gitpitch.git.*;
import com.gitpitch.models.GitRepoModel;
import com.gitpitch.utils.PitchParams;
import com.gitpitch.services.DiskService;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import com.fasterxml.jackson.databind.JsonNode;
import play.Logger;
import play.Logger.ALogger;

/*
 * Git Respository Service common interface.
 */
public abstract class GRSService {

    private final Logger.ALogger log = Logger.of(this.getClass());

    protected final AtomicInteger cacheBypass = new AtomicInteger();
    protected GRSManager grsManager;
    protected DiskService diskService;

    public void init(GRSManager grsManager,
                     DiskService diskService) {
        this.grsManager = grsManager;
        this.diskService = diskService;
    }

    /*
     * Return true if apiPath is a call on this GRSService instance.
     */
    public boolean call(PitchParams pp, String apiPath) {

        if(apiPath != null) {
            GRS grs = grsManager.get(pp);
            return apiPath.startsWith(grs.getApiBase()) ||
                    apiPath.startsWith(grs.getRawBase());
        } else {
            return false;
        }
    }

    /*
     * Return zero if file download completes successfully.
     */
    public int download(PitchParams pp, String filename, String filePath) {

        int status = 999;

        GRS grs = grsManager.get(pp);
        GRSService grsService = grsManager.getService(grs);
        Path branchPath = diskService.ensure(pp);
        String grsLink = raw(pp, filePath, true);
        log.debug("download: grsLink={}", grsLink);

        if (grsService.call(pp, grsLink)) {
            status = diskService.download(pp,
                                          branchPath,
                                          grsLink,
                                          filename,
                                          grs.getHeaders());
        }

        log.debug("download: returning status={}", status);
        return status;
    }

    /*
     * Return model representing Git repository meta-data.
     */
    public abstract GitRepoModel model(PitchParams pp, JsonNode json);

    /*
     * Return Raw API path for /user/repo/branch.
     */
    public abstract String raw(PitchParams pp);

    /*
     * Return Raw API path for /user/repo/branch/filename.
     */
    public String raw(PitchParams pp, String filename) {
        return raw(pp, filename, false);
    }

    /*
     * Return Raw API path for /user/repo/branch/filename with 
     * optional query param used to bypass GRS API caching.
     */
    public String raw(PitchParams pp,
                      String filename,
                      boolean bypassCache) {

        if (bypassCache) {
            return raw(pp) + filename +
                    "?gp=" + cacheBypass.getAndIncrement();
        } else {
            return raw(pp) + filename;
        }
    }

    /*
     * Return API path for repository meta call for /user/repo.
     */
    public abstract String repo(PitchParams pp);

    protected static final String SLASH = "/";

}
