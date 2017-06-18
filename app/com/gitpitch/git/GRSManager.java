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

import com.gitpitch.git.vendors.*;
import com.gitpitch.services.DiskService;
import com.gitpitch.utils.PitchParams;
import java.util.*;
import java.util.stream.Collectors;
import javax.inject.*;
import play.Configuration;
import play.Logger;
import play.Logger.ALogger;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Git Repository Service manager.
 */
@Singleton
public class GRSManager {

    private final Logger.ALogger log = Logger.of(this.getClass());

    private final DiskService diskService;
    private final GitHub gitHubService;
    private final GitLab gitLabService;
    private final BitBucket bitBucketService;
    private final Gitea giteaService;
    private final Gogs gogsService;
    private final GitBucket gitBucketService;
    private final Configuration cfg;
    private final Map<String,GRS> grsStore = new HashMap<String,GRS>();
    private GRS grsDefault;

    @Inject
    public GRSManager(DiskService diskService,
                      GitHub gitHubService,
                      GitLab gitLabService,
                      BitBucket bitBucketService,
                      Gitea giteaService,
                      Gogs gogsService,
                      GitBucket gitBucketService,
                      Configuration cfg) {

        this.diskService = diskService;

        this.gitHubService = gitHubService;
        this.gitHubService.init(this, diskService);

        this.gitLabService = gitLabService;
        this.gitLabService.init(this, diskService);

        this.bitBucketService = bitBucketService;
        this.bitBucketService.init(this, diskService);

        this.giteaService = giteaService;
        this.giteaService.init(this, diskService);

        this.gogsService = gogsService;
        this.gogsService.init(this, diskService);

        this.gitBucketService = gitBucketService;
        this.gitBucketService.init(this, diskService);

        this.cfg = cfg;

        List grsCfg = cfg.getList("gitpitch.git.repo.services");
        List<HashMap<String,String>> grsCfgList = (List<HashMap<String,String>>) grsCfg;

        GRS fallback = GRS.build(GRS_FALLBACK);

        for(HashMap<String,String> grsMap : grsCfgList) {
            GRS grs = GRS.build(grsMap);

            if(grs != null) {

                grsStore.put(grs.getType(), grs);
                if(grs.isDefault())
                    grsDefault = grs;

            } else {
                log.warn("rejecting: {}", grsMap);
            }
        }

        if(grsStore.isEmpty()) {
            grsStore.put(fallback.getType(), fallback);
            grsDefault = fallback;
            log.info("fallback: {}, type={}, true",
                    grsDefault.getName(), grsDefault.getType());
        }

        if(grsDefault == null) {
            grsDefault = fallback;
        }

        grsStore.forEach((k,v) -> {
            log.info("activating: {}, type={}, default={}",
                    v.getName(), k, v.isDefault());
        });
    }

    public GRS get(PitchParams pp) {
        return get(pp.grs);
    }

    public GRS get(String grsType) {
        GRS grs = grsStore.get(grsType);
        return (grs != null) ? grs : grsDefault;
    }

    public String getType(String grsType) {
        GRS grs = grsStore.get(grsType);
        return (grs != null) ? grs.getType() : grsDefault.getType();
    }

    public GRSService getService(GRS grs) {

        GRSService service;

        switch(grs.getType()) {

            case GitHub.TYPE:
                log.debug("getService: matching GitHub");
                service = gitHubService;
                break;
            case GitLab.TYPE:
                log.debug("getService: matching GitLab");
                service = gitLabService;
                break;
            case BitBucket.TYPE:
                log.debug("getService: matching BitBucket");
                service = bitBucketService;
                break;
            case Gitea.TYPE:
                log.debug("getService: matching Gitea");
                service = giteaService;
                break;
            case Gogs.TYPE:
                log.debug("getService: matching Gogs");
                service = gogsService;
                break;
            case GitBucket.TYPE:
                log.debug("getService: matching GitBucket");
                service = gitBucketService;
                break;
            default:
                log.debug("getService: defaulting GitHub");
                service = gitHubService;
        }

        log.debug("getService: returning={}", service);
        return service;
    }

    public List<GRS> listGRS() {
        List<GRS> services = grsStore.entrySet()
                       .stream()
                       .map(Map.Entry::getValue)
                       .collect(Collectors.toList());
        log.debug("listGRS: {}", services);
        return services;
    }

    static Map<String,String> GRS_FALLBACK = new HashMap<String, String>();
    static {
        GRS_FALLBACK.put("name", "GitHub");
        GRS_FALLBACK.put("type", "github");
        GRS_FALLBACK.put("apibase", "https://api.github.com/");
        GRS_FALLBACK.put("apitoken", null);
        GRS_FALLBACK.put("rawbase", "https://raw.githubusercontent.com/");
        GRS_FALLBACK.put("default", "true");
    }
}