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

import java.util.Arrays;
import java.util.List;

/*
 * GitPitch API Parameters Command Object.
 */
public class PitchParams {

    public String user;
    public String repo;
    public String branch;
    public String theme;
    public String notes;
    public String grs;
    public String pitchme;

    private PitchParams(String grs,
                        String user,
                        String repo) {

        this(grs, user, repo, null);
    }
    private PitchParams(String grs,
                        String user,
                        String repo,
                        String branch) {

        this(grs, user, repo, branch, null);
    }
    private PitchParams(String grs,
                        String user,
                        String repo,
                        String branch,
                        String theme) {
        this(grs, user, repo, branch, theme, null);
    }
    private PitchParams(String grs,
                        String user,
                        String repo,
                        String branch,
                        String theme,
                        String pitchme) {

        this(grs, user, repo, branch, theme, pitchme, null);
    }
    private PitchParams(String grs,
                        String user,
                        String repo,
                        String branch,
                        String theme,
                        String pitchme,
                        String notes) {

        this.grs = grs;

        this.user = user;
        this.repo = repo;
        this.branch = (branch != null) ? branch : DEFAULT_BRANCH;

        if (DEFAULT_THEMES.contains(theme))
            this.theme = theme;
        else
            this.theme = DEFAULT_THEME;

        this.pitchme = pitchme;
        this.notes = notes;
    }

    public static PitchParams build(String grs,
                                    String user,
                                    String repo) {

        return new PitchParams(grs, user, repo);
    }

    public static PitchParams build(String grs,
                                    String user,
                                    String repo,
                                    String branch) {

        return new PitchParams(grs, user, repo, branch);
    }

    public static PitchParams build(String grs,
                                    String user,
                                    String repo,
                                    String branch,
                                    String theme) {

        return new PitchParams(grs, user, repo, branch, theme);
    }

    public static PitchParams build(String grs,
                                    String user,
                                    String repo,
                                    String branch,
                                    String theme,
                                    String pitchme) {

        return new PitchParams(grs, user, repo, branch, theme, pitchme);
    }

    public static PitchParams build(String grs,
                                    String user,
                                    String repo,
                                    String branch,
                                    String theme,
                                    String pitchme,
                                    String notes) {

        return new PitchParams(grs, user, repo, branch, theme, pitchme, notes);
    }

    public static boolean isDarkTheme(String theme) {
        return DARK_THEMES.contains(theme);
    }

    public static boolean isLightTheme(String theme) {
        return LIGHT_THEMES.contains(theme);
    }

    public static String fetchThemeCSS(String theme) {
        return new StringBuffer(theme).append(DOT_CSS)
                .toString();
    }

    public String MD() {
        return (pitchme != null) ?
            pitchme + SLASH + PITCHME_MD : PITCHME_MD;
    }

    public String YAML() {
        return (pitchme != null) ?
            pitchme + SLASH + PITCHME_YAML : PITCHME_YAML;
    }

    public String PDF() {
        return (pitchme != null) ?
            pitchme + SLASH + PITCHME_PDF : PITCHME_PDF;
    }

    public boolean isMaster() {
        return GIT_MASTER.equals(this.branch);
    }

    public boolean isLongLived() {
        return USER_GITPITCH.equals(this.user);
    }

    public boolean darkTheme() {
        return DARK_THEMES.contains(this.theme);
    }

    public boolean lightTheme() {
        return LIGHT_THEMES.contains(this.theme);
    }

    public String pretty() {
        return new StringBuffer(SLASH).append(grs)
                .append(SLASH)
                .append(user)
                .append(SLASH)
                .append(repo)
                .append(SLASH)
                .append(branch)
                .toString();
    }

    public String toString() {
        return new StringBuffer(SLASH).append(grs)
                .append(SLASH)
                .append(user)
                .append(SLASH)
                .append(repo)
                .append(SLASH)
                .append(branch)
                .append(" [ ")
                .append(theme)
                .append(", ")
                .append(pitchme)
                .append(" ]")
                .toString();
    }

    public String asLogo() {
        return new StringBuffer(user).append(SPACED_SLASH)
                .append(repo)
                .toString();
    }

    public String asTitle() {
        return new StringBuffer("[ GitPitch ] ")
                                     .append(user)
                                     .append(SLASH)
                                     .append(repo)
                                     .append(SLASH)
                                     .append(branch)
                                     .toString();
    }

    public boolean isValidTheme(String themeName) {
        return DEFAULT_THEMES.contains(themeName);
    }

    public boolean isValidPosition(String logoPosition) {
        return LOGO_POSITIONS.contains(logoPosition);
    }

    public static final String PITCHME_MD = "PITCHME.md";
    public static final String PITCHME_YAML = "PITCHME.yaml";
    public static final String PITCHME_PDF = "PITCHME.pdf";
    public static final String DEFAULT_THEME = "white";
    public static final String DEFAULT_THEME_CSS = "white.css";
    public static final String DEFAULT_LOGO_POSITION = "top-left";
    private static final List<String> LOGO_POSITIONS =
            Arrays.asList("top-left", "top-right", "bottom-left", "bottom-right");
    private static final List<String> DARK_THEMES =
                    Arrays.asList("black", "moon", "night");
    private static final List<String> LIGHT_THEMES =
            Arrays.asList("beige", "sky", "white");
    private static final List<String> DEFAULT_THEMES =
            Arrays.asList("black", "moon", "night", "beige", "sky", "white");
    private static final String DOT_CSS = ".css";
    private final String USER_GITPITCH = "gitpitch";
    private final String GIT_MASTER = "master";
    private final String DEFAULT_BRANCH = "master";
    private final String PARAM_BRANCH = "?b=";
    private final String PARAM_THEME = "&t=";
    private final String PARAM_NOTES = "&n=";
    private final String SLASH = "/";
    private final String SPACED_SLASH = " / ";
    private final String AT = "@ ";

}
