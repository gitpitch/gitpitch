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

import com.gitpitch.utils.PitchParams;
import java.util.Map;
import java.util.HashMap;

/*
 * Git Respository Service model.
 */
public class GRS {

    private final String name;
    private final String type;
    private final String site;
    private final String apiBase;
    private final String apiToken;
    private final String apiTokenHeader;
    private final String rawBase;
    private final boolean isDefault;

    private GRS(String name,
                String type,
                String site,
                String apiBase,
                String apiToken,
                String apiTokenHeader,
                String rawBase,
                boolean isDefault) {

        this.name = name;
        this.type = type;
        this.site = site;
        this.apiBase = apiBase;
        this.apiToken = apiToken;
        this.apiTokenHeader = apiTokenHeader;
        this.rawBase = rawBase;
        this.isDefault = isDefault;
    }

    public static GRS build(Map<String,String> grsCfg) {

        String name = grsCfg.get("name");
        String type = grsCfg.get("type");
        String site = grsCfg.get("site");
        String apiBase = grsCfg.get("apibase");
        String apiToken = grsCfg.get("apitoken");
        String apiTokenHeader = grsCfg.get("apitokenheader");
        String rawBase = grsCfg.get("rawbase");
        boolean isDefault = Boolean.parseBoolean(grsCfg.get("default"));

        if(name != null && type != null && site != null &&
                apiBase != null && rawBase != null) {
            return new GRS(name, type, site, apiBase, apiToken,
                    apiTokenHeader, rawBase, isDefault);
        } else {
            return null;
        }
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public String getSite() { return site; }
    public String getApiBase() { return apiBase; }
    public String getApiToken() { return apiToken; }
    public String getApiTokenHeader() { return apiTokenHeader; }
    public String getRawBase() { return rawBase; }
    public boolean isDefault() { return isDefault; }

    public Map<String,String> getHeaders() {
        Map<String,String> hdrs = new HashMap<String,String>();
        if(getApiToken() != null && getApiTokenHeader() != null) {
            hdrs.put(getApiTokenHeader(), getApiToken());
        }
        return hdrs;
    }

    public String toString() {
        return "GRS[ " + name + " ][ " + type + " ]";
    }

}
