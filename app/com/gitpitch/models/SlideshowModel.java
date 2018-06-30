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
                        pp.branch,
                        pp.pitchme).url();

        this._pretty = new StringBuffer(SLASH)
                .append(this._pp.user)
                .append(SLASH)
                .append(this._pp.repo)
                .append(PARAM_BRANCH)
                .append(this._pp.branch)
                .append(PARAM_PITCHME)
                .append(this._pp.pitchme)
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
                .append(PARAM_BRANCH)
                .append(pp.branch)
                .append(PARAM_PITCHME)
                .append(pp.pitchme)
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

    public boolean isDarkTheme() {
      return PitchParams.isDarkTheme(fetchTheme());
    }

    /*
     * Return true if "layout" in *-left.
     */
    public boolean isLeftLayout() {
        return (_yOpts != null) ? _yOpts.isLeftLayout(params()) : false;
    }

    /*
     * Return true if "layout" in *-right.
     */
    public boolean isRightLayout() {
        return (_yOpts != null) ? _yOpts.isRightLayout(params()) : false;
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
     * Return "logo-position" defined in PITCHME.yaml.
     */
    public boolean hasLogoPosition() {
        return (_yOpts != null) ? _yOpts.hasLogoPosition() : false;
    }

    /*
     * Return "logo-position" defined in PITCHME.yaml.
     */
    public String fetchLogoPosition() {
        String position = _yOpts.fetchLogoPosition(params());
        return LOGO_POSITIONS.get(position);
    }

    /*
     * Return true if "footnote" defined in PITCHME.yaml.
     */
    public boolean hasFootnote() {

        return (_yOpts != null) ? _yOpts.hasFootnote(params()) : false;
    }

    /*
     * Return "footnote" defined in PITCHME.yaml.
     */
    public String fetchFootnote() {
        return (_yOpts != null) ?
            _yOpts.fetchFootnote(params()) : "Footnote undefined.";
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
     * Return "controls-layout" defined in PITCHME.yaml.
     */
    public String fetchControlsLayout() {

        String layout = (_yOpts != null) ?
                _yOpts.fetchControlsLayout(params()) :
                YAMLOptions.DEFAULT_CONTROLS_LAYOUT;

        // Auto-relocate controls to 'edges' if logo-position is bottom-right.
        if(_yOpts != null && hasLogoPosition()) {
            String position = _yOpts.fetchLogoPosition(params());
            if(YAMLOptions.DEFAULT_CONTROLS_LAYOUT.equals(position)) {
                layout = YAMLOptions.EDGES_CONTROLS_LAYOUT;
            }
        }
        return layout;
    }

    /*
     * Return "autoslide" defined in PITCHME.yaml.
     */
    public int fetchAutoSlide(boolean printing) {

        if(printing)
            return 0; // Disable auto-slide when printing.
        else
            return (_yOpts != null) ? _yOpts.fetchAutoSlide(params()) : 0;
    }

    /*
     * Return "vertical-center" defined in PITCHME.yaml.
     */
    public boolean fetchVerticalCenter() {

        boolean vc = true;

        if(_yOpts == null) {
            vc = true;
        } else
        if(_yOpts.isTopLayout(params())) {
            vc = false;
        } else {
            vc = _yOpts.fetchVerticalCenter(params());
        }
        return vc;
    }

    /*
     * Return "loop" defined in PITCHME.yaml.
     */
    public boolean fetchLoop() {
        return (_yOpts != null) ? _yOpts.fetchLoop(params()) : false;
    }

    /*
     * Return "remote-control" defined in PITCHME.yaml.
     */
    public boolean remoteControlEnabled() {
        return (_yOpts != null) ? _yOpts.fetchRemoteControl(params()) : false;
    }

    /*
     * Return "remote-control-prevkey" defined in PITCHME.yaml.
     */
    public int remoteControlPrevKey() {
        int prevKeyCode = DEFAULT_PREV_KEYCODE;
        if(_yOpts != null) {
            prevKeyCode = _yOpts.fetchRemoteControlPrevKey(params());
        }
        return prevKeyCode != 0 ? prevKeyCode : DEFAULT_PREV_KEYCODE;
    }

    /*
     * Return "remote-control-nextkey" defined in PITCHME.yaml.
     */
    public int remoteControlNextKey() {
        int nextKeyCode = DEFAULT_NEXT_KEYCODE;
        if(_yOpts != null) {
            nextKeyCode = _yOpts.fetchRemoteControlNextKey(params());
        }
        return nextKeyCode != 0 ? nextKeyCode : DEFAULT_NEXT_KEYCODE;
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
     * Return "mousewheel" defined in PITCHME.yaml.
     */
    public boolean fetchMouseWheel() {
        return (_yOpts != null) ? _yOpts.fetchMouseWheel(params()) : false;
    }

    /*
     * Return true if "charts" defined in PITCHME.yaml.
     */
    public boolean chartsEnabled() {
        return (_yOpts != null) ? _yOpts.fetchCharts(params()) : false;
    }

    /*
     * Return true if "mathjax" defined in PITCHME.yaml.
     */
    public boolean mathEnabled() {
        return (_yOpts != null) ? _yOpts.mathEnabled(params()) : false;
    }

    /*
     * Return "history" defined in PITCHME.yaml.
     */
    public boolean fetchHistory() {
        return (_yOpts != null) ? _yOpts.fetchHistory(params()) : false;
    }

    /*
     * Return "slide-number" defined in PITCHME.yaml.
     */
    public boolean fetchSlideNumber() {
        return (_yOpts != null) ? _yOpts.fetchSlideNumber(params()) : false;
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

    /*
     * Return true if "published" set to true in PITCHME.yaml.
     */
    public boolean published() {

        return (_yOpts != null) ? _yOpts.published(_pp) : false;
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
            if(MarkdownModel.HSLIDE_REGEX_DEFAULT != null)
                delim = buildSlideDelim(MarkdownModel.HSLIDE_REGEX_DEFAULT);
            else
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
            if(MarkdownModel.VSLIDE_REGEX_DEFAULT != null)
                delim = buildSlideDelim(MarkdownModel.VSLIDE_REGEX_DEFAULT);
            else
                delim = buildSlideDelim(MarkdownModel.VSLIDE_DELIM_DEFAULT);
        }
        return delim;
    }

    /*
     * Return true if "gatoken" defined in PITCHME.yaml.
     */
    public boolean hasGAToken() {

        return (_yOpts != null) ? _yOpts.hasGAToken(params()) : false;
    }

    /*
     * Return "gatoken" defined in PITCHME.yaml.
     */
    public String fetchGAToken() {
        return (_yOpts != null) ?
            _yOpts.fetchGAToken(params()) : null;
    }

    /*
     * Return "revealjs-version" override for slideshow.
     */
    public String fetchRevealVersionOverride() {
        return (_yOpts != null) ?
            _yOpts.fetchRevealVersion(params()) : null;
    }

    /*
     * Return presentation Help message.
     */
    public String fetchHelp(boolean smallScreen) {
        return smallScreen ? SMALL_HELP : HELP;
    }

    public String toString() {
        return _pretty;
    }

    public String key() {
        return _cacheKey;
    }

    public String homePanel() {
        return com.gitpitch.controllers.routes.PitchController.home(_pp.grs,
                _pp.user,
                _pp.repo,
                _pp.branch,
                _pp.theme,
                _pp.pitchme,
                "false").toString();
    }

    public String gitPanel() {
        return com.gitpitch.controllers.routes.PitchController.git(_pp.grs,
                _pp.user,
                _pp.repo,
                _pp.branch,
                _pp.theme,
                _pp.pitchme,
                null).toString();
    }

    public String themesPanel() {
        return com.gitpitch.controllers.routes.PitchController.themes(_pp.grs,
                _pp.user,
                _pp.repo,
                _pp.branch,
                _pp.theme,
                _pp.pitchme,
                null).toString();
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
    private static final String PARAM_BRANCH = "?b=";
    private static final String PARAM_PITCHME = "&p=";
    private static final String PITCHME_YAML = "PITCHME.yaml";
    /*
     * Model prefix identifier for cache key generator.
     */
    private static final String MODEL_ID = "SSM:";
    private static final Integer DEFAULT_PREV_KEYCODE = 37;
    private static final Integer DEFAULT_NEXT_KEYCODE = 39;
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
    private static Map<String, String> LOGO_POSITIONS =
            Collections.unmodifiableMap(new HashMap<String, String>() {
                {
                    put("top-left", "position: fixed; top: 20px; left: 20px; z-index: 1");
                    put("top-right", "position: fixed; top: 20px; right: 20px; z-index: 1");
                    put("bottom-left", "position: fixed; bottom: 20px; left: 20px; z-index: 1");
                    put("bottom-right", "position: fixed; bottom: 20px; right: 20px; z-index: 1");
                }
            });

    private static final String HELP =
        "Navigate : Space / Arrow Keys | " +
        "<a href='#' onclick='RevealMenu.toggle()'>M</a> - Menu | " +
        "<a href='#' onclick='enterFullscreen()'>F</a> - Fullscreen | " +
        "<a href='#' onclick='enterOverview()'>O</a> - Overview | " +
        "<a href='#' onclick='enterBlackout()'>B</a> - Blackout | " +
        "<a href='#' onclick='RevealNotes.open()'>S</a> - Speaker | " +
        "<a href='#' onclick='enterHelp()'>?</a> - Help";

    private static final String SMALL_HELP =
        "<a href='#' onclick='RevealMenu.toggle()'>Menu</a> | " +
        "<a href='#' onclick='enterFullscreen()'>Fullscreen</a> | " +
        "<a href='#' onclick='enterOverview()'>Overview</a> | " +
        "<a href='#' onclick='enterBlackout()'>Blackout</a> | " +
        "<a href='#' onclick='RevealNotes.open()'>Speaker</a> | " +
        "<a href='#' onclick='enterHelp()'>Help</a>";
}
