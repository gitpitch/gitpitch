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
package com.gitpitch.policies;

import play.Configuration;
import javax.inject.*;

/*
 * External dependency manager for the GitPitch server.
 */
@Singleton
public final class Dependencies {

    private final Configuration cfg;
    private final String cdn;
    private final String revealjsVersion;
    private final String bootstrapVersion;
    private final String jqueryVersion;
    private final String fontawesomeVersion;
    private final String octiconsVersion;
    private final String highlightjsVersion;
    private final Boolean highlightPluginEnabled;

    @Inject
    public Dependencies(Configuration cfg) {
        this.cfg = cfg;
        this.cdn = cfg.getString("gitpitch.dependency.cdn");
        this.revealjsVersion = cfg.getString("gitpitch.dependency.revealjs");
        this.bootstrapVersion = cfg.getString("gitpitch.dependency.bootstrap");
        this.jqueryVersion = cfg.getString("gitpitch.dependency.jquery");
        this.fontawesomeVersion = cfg.getString("gitpitch.dependency.fontawesome");
        this.octiconsVersion = cfg.getString("gitpitch.dependency.octicons");
        this.highlightjsVersion = cfg.getString("gitpitch.dependency.highlightjs");
        this.highlightPluginEnabled =
            cfg.getBoolean("gitpitch.dependency.highlight.plugin", false);
    }

    public String revealjs(boolean offline, String versionOverride) {
        return build(offline, REVEALJS, (versionOverride != null) ?
            versionOverride : revealjsVersion);
    }

    public String bootstrap(boolean offline) {
        return build(offline, BOOTSTRAP, bootstrapVersion);
    }

    public String jquery(boolean offline) {
        return build(offline, JQUERY, jqueryVersion);
    }

    public String fontawesome(boolean offline) {
        return build(offline, FONTAWE, fontawesomeVersion);
    }

    public String octicons(boolean offline) {
        return build(offline, OCTICONS, octiconsVersion);
    }

    public String highlightjs(boolean offline) {
        return build(offline, HIGHLIGHT, highlightjsVersion);
    }

    public String gitpitchjs(boolean offline) {
        return build(offline, GIPITCHJS);
    }

    public String gitpitchimg(boolean offline) {
        return build(offline, GIPITCHIMG);
    }

    public Boolean highlightPluginEnabled() {
        return highlightPluginEnabled;
    }

    private String build(boolean offline, String libName) {

        if(offline) {
            return "./assets" + libName;
        } else {
            return (cdn != null) ? (cdn + libName) : (DEFAULT_CDN + libName);
        }
    }

    private String build(boolean offline, String libName, String libVersion) {
        return build(offline, libName) + libVersion;
    }

    private static final String GIPITCHJS = "/js";
    private static final String GIPITCHIMG = "/img";
    private static final String REVEALJS  = "/reveal.js/";
    private static final String BOOTSTRAP = "/bootstrap/";
    private static final String JQUERY    = "/jquery/";
    private static final String FONTAWE   = "/font-awesome/";
    private static final String OCTICONS  = "/octicons/";
    private static final String HIGHLIGHT = "/highlight.js/";
    
    private static final String DEFAULT_CDN = "/assets/libs";
}
