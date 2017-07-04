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

import com.gitpitch.git.GRSService;
import com.gitpitch.services.DiskService;
import org.yaml.snakeyaml.Yaml;
import play.Logger;
import play.Logger.ALogger;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.*;

/*
 * PITCHME.yaml options utility.
 */
public final class YAMLOptions {

    private final static Logger.ALogger log =
            Logger.of("com.gitpitch.utils.YAMLOptions");

    private final Map<String, String> _yProps;
    private final GRSService grsService;

    private YAMLOptions(Map<String, String> yProps,
                        GRSService grsService) {
        this._yProps = yProps;
        this.grsService = grsService;
    }

    public static YAMLOptions build(PitchParams pp,
                                    GRSService grsService,
                                    DiskService diskService) {

        YAMLOptions yOpts = null;

        try {

            /*
             * Instantiate YAML parser.
             */
            Yaml yaml = new Yaml();

            /*
             * Build path to PITCHME_YAML on disk.
             */
            Path yPath = diskService.asPath(pp, PITCHME_YAML);
            File yFile = yPath.toFile();

            if (yFile.exists()) {

                /*
                 * Use YAML parser to transform YAML properties to Map.
                 */
                Map<String, String> yProps =
                        (Map<String, String>) yaml.load(new FileReader(yPath.toFile()));

                /*
                 * Handle PITCHME.yaml empty file without properites.
                 */
                if (yProps == null) {
                    yProps = new HashMap<String, String>();
                }

                log.debug("build: pp={}, props={}", pp, yProps);

                yOpts = new YAMLOptions(yProps, grsService);
            } else {
                log.debug("build: pp={}, yaml not found={}", yFile);
            }

        } catch (Exception yex) {

            log.warn("build: pp={}, parsing YAML ex={}", pp, yex);

        } finally {

            return yOpts;
        }

    }

    public boolean fixedTheme(PitchParams pp) {
        return _yProps.get(THEME_OPTION) != null;
    }

    public String fetchTheme(PitchParams pp) {

        String theme = _yProps.get(THEME_OPTION);
        return pp.isValidTheme(theme) ? theme : pp.theme;
    }

    public String fetchThemeCSS(PitchParams pp) {

        return new StringBuffer(fetchTheme(pp)).append(DOT_CSS)
                .toString();
    }

    public boolean hasThemeOverride(PitchParams pp) {
        return (_yProps.get(THEME_OVERRIDE_OPTION) != null);
    }

    public String fetchThemeOverride(PitchParams pp) {

        String overridePath = _yProps.get(THEME_OVERRIDE_OPTION);

        if (isAbsolute(overridePath)) {
            return overridePath;
        } else {
            return grsService.raw(pp, overridePath);
        }
    }

    public boolean hasLogo() {
        return (_yProps.get(LOGO_OPTION) != null);
    }

    public String fetchLogo(PitchParams pp) {

        String logoPath = _yProps.get(LOGO_OPTION);

        if (isAbsolute(logoPath)) {
            return logoPath;
        } else {
            return grsService.raw(pp, logoPath);
        }
    }

    public boolean hasLogoPosition() {
        return (_yProps.get(LOGO_POSITION_OPTION) != null);
    }

    public String fetchLogoPosition(PitchParams pp) {

        String logoPosition = _yProps.get(LOGO_POSITION_OPTION);
        return pp.isValidPosition(logoPosition) ? logoPosition : pp.DEFAULT_LOGO_POSITION;
    }

    public boolean hasImageBg() {
        return (_yProps.get(IMAGE_BG_OPTION) != null);
    }

    public String fetchImageBg(PitchParams pp) {

        String imgBgPath = _yProps.get(IMAGE_BG_OPTION);

        if (isAbsolute(imgBgPath)) {
            return imgBgPath;
        } else {
            return grsService.raw(pp, imgBgPath);
        }
    }

    public String fetchImageBgSize(PitchParams pp) {
        String bgSize = _yProps.get(IMAGE_BG_SIZE_OPTION);
        if (bgSize == null) {
            return DEFAULT_BG_SIZE;
        } else {
            return bgSize;
        }
    }

    public String fetchTransition(PitchParams pp) {

        String transition = _yProps.get(TRANSITION_OPTION);

        if (TRANSITIONS.contains(transition))
            return transition;
        else
            return DEFAULT_TRANSITION;
    }

    public Integer fetchAutoSlide(PitchParams pp) {
        return fetchIntegerOption(pp, AUTOSLIDE_OPTION);
    }

    public Boolean fetchVerticalCenter(PitchParams pp) {
        return fetchBooleanOption(pp, VERTICAL_CENTER, true);
    }

    public Boolean fetchLoop(PitchParams pp) {
        return fetchBooleanOption(pp, LOOP_OPTION);
    }

    public Boolean fetchRemoteControl(PitchParams pp) {
        return fetchBooleanOption(pp, REMOTE_CONTROL_OPTION);
    }

    public Boolean fetchRTL(PitchParams pp) {
        return fetchBooleanOption(pp, RTL_OPTION);
    }

    public Boolean fetchShuffle(PitchParams pp) {
        return fetchBooleanOption(pp, SHUFFLE_OPTION);
    }

    public Boolean fetchMouseWheel(PitchParams pp) {
        return fetchBooleanOption(pp, MOUSE_WHEEL_OPTION);
    }

    public Boolean fetchCharts(PitchParams pp) {
        return fetchBooleanOption(pp, CHARTS_OPTION);
    }

    public Boolean mathEnabled(PitchParams pp) {
        String mathjax = _yProps.get(MATHJAX_OPTION);
        return (mathjax != null);
    }

    public Boolean fetchHistory(PitchParams pp) {
        return fetchBooleanOption(pp, HISTORY_OPTION, true);
    }

    public Boolean fetchSlideNumber(PitchParams pp) {
        return fetchBooleanOption(pp, SLIDE_NUMBER_OPTION);
    }

    public String mathConfig(PitchParams pp) {
        return mathEnabled(pp) ?
                _yProps.get(MATHJAX_OPTION) : MATHJAX_DEFAULT;
    }

    public Boolean highlightEnabled(PitchParams pp) {
        String highlight = _yProps.get(HIGHLIGHT_OPTION);
        if (highlight != null)
            highlight = highlight.toLowerCase();
        return HIGHLIGHT_CONFIGS.contains(highlight);
    }

    public String fetchHighlight(PitchParams pp) {
        return highlightEnabled(pp) ?
                (_yProps.get(HIGHLIGHT_OPTION) + ".css") :
                defaultHighlight(pp);
    }

    public boolean hasFootnote(PitchParams pp) {
        return _yProps.get(FOOTNOTE_OPTION) != null;
    }

    public String fetchFootnote(PitchParams pp) {
        return _yProps.get(FOOTNOTE_OPTION);
    }

    public boolean hasGAToken(PitchParams pp) {
        return (_yProps.get(GATOKEN_OPTION) != null);
    }

    public String fetchGAToken(PitchParams pp) {
        return _yProps.get(GATOKEN_OPTION);
    }

    public boolean hasHorzDelim(PitchParams pp) {
        return _yProps.get(HSLIDE_DELIM) != null;
    }

    public String fetchHorzDelim(PitchParams pp) {
        return _yProps.get(HSLIDE_DELIM);
    }

    public String fetchHorzDelimRegExp(PitchParams pp) {
        return _yProps.get(HSLIDE_DELIM_REGEXP);
    }

    public boolean hasVertDelim(PitchParams pp) {
        return _yProps.get(VSLIDE_DELIM) != null;
    }

    public String fetchVertDelim(PitchParams pp) {
        return _yProps.get(VSLIDE_DELIM);
    }

    public String fetchVertDelimRegExp(PitchParams pp) {
        return _yProps.get(VSLIDE_DELIM_REGEXP);
    }

    public String fetchRevealVersion(PitchParams pp) {
        return _yProps.get(REVEALJS_VERSION);
    }

    private Boolean fetchBooleanOption(PitchParams pp, String option) {
        return fetchBooleanOption(pp, option, false);
    }

    private Boolean fetchBooleanOption(PitchParams pp,
                                        String option, boolean dflt) {

        try {
            Object optionValue = _yProps.get(option);

            if (optionValue instanceof Boolean)
                return (Boolean) optionValue;
            else if (optionValue instanceof String)
                return Boolean.parseBoolean((String) optionValue);
            else
                return dflt;

        } catch (Exception bex) {
            return dflt;
        }
    }

    private Integer fetchIntegerOption(PitchParams pp, String option) {

        try {
            Object optionValue = _yProps.get(option);

            if (optionValue instanceof Integer)
                return (Integer) optionValue;
            else if (optionValue instanceof String)
                return Integer.parseInt((String) optionValue);
            else
                return 0;

        } catch (NumberFormatException nfex) {
            return 0;
        }
    }

    private String defaultHighlight(PitchParams pp) {
        return PitchParams.isDarkTheme(fetchTheme(pp)) ?
                HIGHLIGHT_DARK_DEFAULT : HIGHLIGHT_LIGHT_DEFAULT;
    }

    private boolean isAbsolute(String link) {
        return (link != null && link.startsWith(ABS_HTTP));
    }

    public static final String PITCHME_YAML = "PITCHME.yaml";
    public static final String DEFAULT_BG_SIZE = "100% 100%";
    public static final String DEFAULT_TRANSITION = "slide";
    public static final String MATHJAX_DEFAULT = "TeX-MML-AM_CHTML";
    public static final String HIGHLIGHT_DARK_DEFAULT = "github-gist.css";
    public static final String HIGHLIGHT_LIGHT_DEFAULT = "hybrid.css";
    /*
     * PITCHME.yaml Supported Options.
     */
    private static final String THEME_OPTION = "theme";
    private static final String THEME_OVERRIDE_OPTION = "theme-override";
    private static final String VERTICAL_CENTER = "vertical-center";
    private static final String LOGO_OPTION = "logo";
    private static final String LOGO_POSITION_OPTION = "logo-position";
    private static final String IMAGE_BG_OPTION = "background";
    private static final String IMAGE_BG_SIZE_OPTION = "background-size";
    private static final String TRANSITION_OPTION = "transition";
    private static final String AUTOSLIDE_OPTION = "autoslide";
    private static final String LOOP_OPTION = "loop";
    private static final String REMOTE_CONTROL_OPTION = "remote-control";
    private static final String RTL_OPTION = "rtl";
    private static final String SHUFFLE_OPTION = "shuffle";
    private static final String MOUSE_WHEEL_OPTION = "mousewheel";
    private static final String MATHJAX_OPTION = "mathjax";
    private static final String HIGHLIGHT_OPTION = "highlight";
    private static final String FOOTNOTE_OPTION = "footnote";
    private static final String GATOKEN_OPTION = "gatoken";
    private static final String CHARTS_OPTION = "charts";
    private static final String HISTORY_OPTION = "history";
    private static final String SLIDE_NUMBER_OPTION = "slide-number";
    private static final String REVEALJS_VERSION = "revealjs-version";

    private static final String HSLIDE_DELIM = "horz-delim";
    private static final String VSLIDE_DELIM = "vert-delim";
    private static final String HSLIDE_DELIM_REGEXP = "horz-delim-regexp";
    private static final String VSLIDE_DELIM_REGEXP = "vert-delim-regexp";

    private static final List<String> TRANSITIONS =
            Arrays.asList("default", "none", "fade", "slide",
                    "convex", "conconcave", "zoom");
    private static final List<String> MATHJAX_CONFIGS =
            Arrays.asList("default",
                    "TeX-MML-AM_CHTML", "TeX-MML-AM_HTMLorMML",
                    "TeX-MML-AM_SVG", "TeX-AMS-MML_HTMLorMML",
                    "TeX-AMS_CHTML", "TeX-AMS_SVG",
                    "TeX-AMS_HTML", "MML_CHTML", "MML_SVG",
                    "MML_HTMLorMML", "AM_CHTML", "AM_SVG",
                    "AM_HTMLorMML", "TeX-AMS-MML_SVG",
                    "Accessible");
    private static final List<String> HIGHLIGHT_CONFIGS =
            Arrays.asList("github-gist", "github", "default",
                    "agate", "androidstudio", "arduino-light",
                    "arta", "ascetic",
                    "atelier-cave-dark", "atelier-cave-light",
                    "atelier-dune-dark", "atelier-dune-light",
                    "atelier-estuary-dark", "atelier-estuary-dark",
                    "atelier-forest-dark", "atelier-forest-dark",
                    "atelier-health-dark", "atelier-health-dark",
                    "atelier-lakeside-dark", "atelier-lakeside-dark",
                    "atelier-plateau-light", "atelier-plateau-light",
                    "atelier-savanna-light", "atelier-savanna-light",
                    "atelier-seaside-light", "atelier-seaside-light",
                    "atelier-sulphurpool-light", "atelier-sulphurpool-light",
                    "brown-paper", "brown-papersq",
                    "codepen-embed", "color-brewer",
                    "dark", "darkula", "docco",
                    "dracula", "far", "foundation",
                    "googlecode", "grayscale",
                    "gruvbox-dark", "gruvbox-light",
                    "hopscotch", "hybrid", "idea", "ir-black",
                    "kimble.dark", "kimble.light",
                    "magula", "mono-blue", "monokai-sublime",
                    "monokai", "obsidian", "ocean",
                    "paraiso-dark", "paraiso-light",
                    "pojoaque", "purebasic",
                    "qtcreateor_dark", "qtcreateor_light",
                    "railscasts", "rainbow", "school-book",
                    "solarized-dark", "solarized-light",
                    "sunburst", "tomorrow-night-blue",
                    "tomorrow-night-bright",
                    "tomorrow-night-eighties", "tomorrow-night",
                    "tomorrow", "vs", "xcode", "xt256", "zenburn");
    private static final String ABS_HTTP = "http";
    private static final String DOT_CSS = ".css";
}
