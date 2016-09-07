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
package com.gitpitch.models;

import com.gitpitch.models.MarkdownModel;
import com.gitpitch.git.GRSService;
import com.gitpitch.services.DiskService;
import com.gitpitch.utils.PitchParams;
import com.gitpitch.utils.YAMLOptions;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import play.Logger;
import play.Logger.ALogger;

/*
 * Rendering model for views.Slideshow.scala.html.
 */
public class SlideshowModel {

    private final Logger.ALogger log = Logger.of(this.getClass());

    /*
     * Parameter values passed on GitPitch API call.
     */
    private final PitchParams _pp;
    /*
     * PITCHME.yaml options.
     */
    private final YAMLOptions _yOpts;
    /*
     * GitPitch API /api/markdown/{user}/{repo}?b={branch}.
     */
    private final String _fetchMarkdown;
    /*
     * Derived immutable state.
     */
    private final String _pretty;
    private final String _cacheKey;

    private SlideshowModel(PitchParams pp,
                           YAMLOptions yOpts) {

        this._pp = pp;
        this._yOpts = yOpts;

        this._fetchMarkdown =
                com.gitpitch.controllers.routes.PitchController.markdown(pp.grs,
                        pp.user,
                        pp.repo,
                        pp.branch).url();

        this._pretty = new StringBuffer(SLASH)
                .append(this._pp.user)
                .append(SLASH)
                .append(this._pp.repo)
                .append(QMARK_BRANCH)
                .append(this._pp.branch)
                .toString();

        this._cacheKey = genKey(pp);
    }

    public static SlideshowModel build(PitchParams pp,
                                       boolean yamlFound,
                                       GRSService grsService,
                                       DiskService diskService) {

        YAMLOptions yOpts =
                yamlFound ? YAMLOptions.build(pp, grsService, diskService) : null;
        return new SlideshowModel(pp, yOpts);
    }

    /*
     * Generate a key for querying the cache for matching SlideshowModel.
     */
    public static String genKey(PitchParams pp) {

        return new StringBuffer(MODEL_ID).append(SLASH)
                .append(pp.grs)
                .append(SLASH)
                .append(pp.user)
                .append(SLASH)
                .append(pp.repo)
                .append(QMARK_BRANCH)
                .append(pp.branch)
                .toString();
    }

    public SlideshowModel clone(PitchParams pp) {
        return new SlideshowModel(pp, this._yOpts);
    }

    /*
     * Return PitchParams instance on SlideshowModel.
     */
    public PitchParams params() {
        return _pp;
    }

    /*
     * Return YAMLOptions instance on SlideshowModel.
     */
    public YAMLOptions options() {
        return _yOpts;
    }

    /*
     * Return true if custom theme in use.
     */
    public boolean fixedTheme() {
        return (_yOpts != null) ?
                _yOpts.fixedTheme(params()) : false;
    }

    /*
     * Return active theme name for slideshow.
     */
    public String fetchTheme() {

        return (_yOpts != null) ? _yOpts.fetchTheme(params()) :
                _pp.theme;
    }

    /*
     * Return active theme.css for slideshow.
     */
    public String fetchThemeCSS() {

        return (_yOpts != null) ? _yOpts.fetchThemeCSS(params()) :
                PitchParams.fetchThemeCSS(_pp.theme);
    }

    /*
     * Return theme override css for slideshow.
     */
    public boolean hasThemeOverride() {

        return (_yOpts != null) ?
            _yOpts.hasThemeOverride(params()) : false;
    }

    /*
     * Return theme override css for slideshow.
     */
    public String fetchThemeOverride() {

        String customCSS = null;

        if(hasThemeOverride()) {
            String oride = _yOpts.fetchThemeOverride(params());
            try {
                customCSS = IOUtils.toString(new URL(oride),
                        Charset.forName("UTF-8"));
            } catch(Exception ioex) {}
        }
        log.debug("fetchThemeOverride: customCSS={}", customCSS);
        return customCSS;
    }

    /*
     * Return active theme font for slideshow.
     */
    public String fetchThemeFont() {

        String font = THEME_FONTS.get(fetchTheme());
        return (font != null) ? font : THEME_FONT_DEFAULT;
    }

    /*
     * Return active theme font color inverse for slideshow.
     */
    public String fetchThemeFontColorInverse() {

        boolean darkTheme = PitchParams.isDarkTheme(fetchTheme());
        return darkTheme ? THEME_DARK_COLOR_INVERSE :
                THEME_LIGHT_COLOR_INVERSE;
    }

    /*
     * Return true if "logo" defined in PITCHME.yaml.
     */
    public boolean hasLogo() {

        return (_yOpts != null) ? _yOpts.hasLogo() : false;
    }

    /*
     * Return "logo" defined in PITCHME.yaml.
     */
    public String fetchLogo() {
        return (_yOpts != null) ? _yOpts.fetchLogo(params()) : "#";
    }

    /*
     * Return "logo" filename defined in PITCHME.yaml.
     */
    public String fetchLogoName() {
        return (_yOpts != null) ?
                FilenameUtils.getName(_yOpts.fetchLogo(params())) : "#";
    }

    /*
     * Return markdown for slideshow.
     */
    public String fetchMarkdown() {
        return _fetchMarkdown;
    }

    /*
     * Return true if slideshow speaker notes should be displayed.
     */
    public boolean showNotes() {
        return (_pp != null) ? Boolean.parseBoolean(_pp.notes) : false;
    }

    /*
     * Return "transition" defined in PITCHME.yaml.
     */
    public String fetchTransition() {
        return (_yOpts != null) ? _yOpts.fetchTransition(params()) :
                YAMLOptions.DEFAULT_TRANSITION;
    }

    /*
     * Return "autoslide" defined in PITCHME.yaml.
     */
    public int fetchAutoSlide() {
        return (_yOpts != null) ? _yOpts.fetchAutoSlide(params()) : 0;
    }

    /*
     * Return "vertical-center" defined in PITCHME.yaml.
     */
    public boolean fetchVerticalCenter() {
        return (_yOpts != null) ?
                _yOpts.fetchVerticalCenter(params()) : true;
    }

    /*
     * Return "loop" defined in PITCHME.yaml.
     */
    public boolean fetchLoop() {
        return (_yOpts != null) ? _yOpts.fetchLoop(params()) : false;
    }

    /*
     * Return "rtl" defined in PITCHME.yaml.
     */
    public boolean fetchRTL() {
        return (_yOpts != null) ? _yOpts.fetchRTL(params()) : false;
    }

    /*
     * Return "shuffle" defined in PITCHME.yaml.
     */
    public boolean fetchShuffle() {
        return (_yOpts != null) ? _yOpts.fetchShuffle(params()) : false;
    }

    /*
     * Return true if "mathjax" defined in PITCHME.yaml.
     */
    public boolean mathEnabled() {
        return (_yOpts != null) ? _yOpts.mathEnabled(params()) : false;
    }

    /*
     * Return "mathjax" defined in PITCHME.yaml.
     */
    public String fetchMathConfig() {
        return (_yOpts != null) ? _yOpts.mathConfig(params()) :
                YAMLOptions.MATHJAX_DEFAULT;
    }

    /*
     * Return active theme.css for slideshow.
     */
    public String fetchHighlightCSS() {

        if (_yOpts != null) {
            return _yOpts.fetchHighlight(params());
        } else {
            return _pp.darkTheme() ?
                    YAMLOptions.HIGHLIGHT_DARK_DEFAULT :
                    YAMLOptions.HIGHLIGHT_LIGHT_DEFAULT;
        }
    }

    public String fetchHorzDelim() {

        String delim = null;

        if(_yOpts != null && _yOpts.hasHorzDelim(params())) {

            String regexp = _yOpts.fetchHorzDelimRegExp(params());
            delim = (regexp != null) ? buildSlideDelim(regexp) :
                    buildSlideDelim(_yOpts.fetchHorzDelim(params()));
            log.debug("fetchHorzDelim: custom delim={}", delim);
        }

        if(delim == null) {
            delim = buildSlideDelim(MarkdownModel.HSLIDE_DELIM_DEFAULT);
        }
        return delim;
    }

    public String fetchVertDelim() {

        String delim = null;

        if(_yOpts != null && _yOpts.hasVertDelim(params())) {

            String regexp = _yOpts.fetchVertDelimRegExp(params());
            log.debug("fetchVertDelim: regexp={}", regexp);
            delim = (regexp != null) ? buildSlideDelim(regexp) :
                    buildSlideDelim(_yOpts.fetchVertDelim(params()));
            log.debug("fetchVertDelim: custom delim={}", delim);
        }

        if(delim == null) {
            delim = buildSlideDelim(MarkdownModel.VSLIDE_DELIM_DEFAULT);
        }
        return delim;
    }

    public String toString() {
        return _pretty;
    }

    public String key() {
        return _cacheKey;
    }

    private String buildSlideDelim(String pattern) {

        log.debug("buildSlideDelim: pattern={}", pattern);

        return new StringBuffer("(^").append(pattern)
                                     .append("$|^")
                                     .append(pattern)
                                     .append("\\?.*)")
                                     .toString();
    }

    private static final String SLASH = "/";
    private static final String QMARK_BRANCH = "?b=";
    private static final String PITCHME_YAML = "PITCHME.yaml";
    /*
     * Model prefix identifier for cache key generator.
     */
    private static final String MODEL_ID = "SSM:";
    private static final String THEME_FONT_DEFAULT =
            "Source Sans Pro, Helvetica, sans-serif";
    private static final String THEME_DARK_COLOR_INVERSE = "white";
    private static final String THEME_LIGHT_COLOR_INVERSE = "black";
    private static Map<String, String> THEME_FONTS =
            Collections.unmodifiableMap(new HashMap<String, String>() {
                {
                    put("black", "Source Sans Pro, Helvetica, sans-serif");
                    put("moon", "Lato, sans-serif");
                    put("night", "Open Sans, sans-serif");
                    put("beige", "Lato, sans-serif");
                    put("sky", "Open Sans, sans-serif");
                    put("white", "Source Sans Pro, Helvetica, sans-serif");
                }
            });
}
