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

import com.gitpitch.git.GRS;
import com.gitpitch.git.vendors.*;
import com.gitpitch.models.GitRepoModel;
import com.gitpitch.policies.Runtime;
import com.gitpitch.utils.PitchParams;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import play.Logger;
import play.Logger.ALogger;

/*
 * Rendering model for views.Slideshow.scala.html.
 */
public class GitRepoRenderer {

    private final Logger.ALogger log = Logger.of(this.getClass());

    private final PitchParams _pp;
    private final GitRepoModel _grm;
    private final Runtime runtime;
    private List<GRS> _grsServices;
    /*
     * Relative URLs for view components.
     */
    private String _slideshowBase;
    private String _slideshowURL;
    private String _markdownURL;
    /*
     * Absolute URLs for Git page links.
     */
    private String _orgHub;
    private String _repoHub;
    private String _starHub;
    private String _forkHub;

    private GitRepoRenderer(PitchParams pp, List<GRS> grsServices) {
        this(pp, null, null, grsServices);
    }

    private GitRepoRenderer(PitchParams pp,
                            GitRepoModel grm,
                            Runtime runtime,
                            List<GRS> grsServices) {

        this._pp = pp;
        this._grm = grm;
        this.runtime = runtime;
        this._grsServices = grsServices;

        if (grm != null) {

            /*
             * Initialize properties based on valid repository
             * data returned on Git Git API as GitRepoModel, if exists.
             */

            this._slideshowURL =
                com.gitpitch.controllers.routes.PitchController
                    .slideshow(_grm.owner(),
                    _grm.name(),
                    _pp.branch,
                    _pp.grs,
                    _pp.theme,
                    _pp.pitchme,
                    _pp.notes, null, null, null).toString();

            this._markdownURL = com.gitpitch.controllers.routes.PitchController
                    .markdown(_pp.grs,
                    _grm.owner(),
                    _grm.name(),
                    _pp.branch,
                    _pp.pitchme).toString();

            Optional<GRS> grso =
                    this._grsServices.stream()
                                     .filter(grs -> grs.getType().equals(_pp.grs))
                                     .findFirst();

            Optional<GRS> grsoDefault =
                    this._grsServices.stream()
                                     .filter(grs -> grs.isDefault())
                                     .findFirst();

            String grsSite = null;

            if(grso.isPresent()) {
                grsSite = grso.get().getSite();
            } else {
                if(grsoDefault.isPresent())
                    grsSite = grsoDefault.get().getSite();
                else
                    grsSite = GRS_GITHUB_COM;
            }

            this._orgHub = new StringBuffer(grsSite).append(_grm.owner())
                    .toString();

            this._repoHub =
                    new StringBuffer(_orgHub).append(SLASH)
                            .append(this._grm.name())
                            .toString();

            switch(_pp.grs) {

                case GitHub.TYPE:
                    this._starHub =
                        new StringBuffer(this._repoHub).append(SLASH)
                                                       .append(GRS_GITHUB_STARS)
                                                       .toString();
                    this._forkHub =
                        new StringBuffer(this._repoHub).append(SLASH)
                                                       .append(GRS_GITHUB_FORKS)
                                                       .toString();
                    break;
                case GitLab.TYPE:
                    this._starHub =
                        new StringBuffer(this._repoHub).append(SLASH)
                                                       .append(GRS_GITLAB_STARS)
                                                       .toString();
                    this._forkHub =
                        new StringBuffer(this._repoHub).append(SLASH)
                                                       .append(GRS_GITLAB_FORKS)
                                                       .append(_pp.branch)
                                                       .toString();
                    break;
                case BitBucket.TYPE:
                    this._starHub =
                        new StringBuffer(this._repoHub).append(SLASH)
                                                       .append(GRS_BITBUCKET_STARS)
                                                       .toString();
                    this._forkHub =
                        new StringBuffer(this._repoHub).append(SLASH)
                                                       .append(GRS_BITBUCKET_FORKS)
                                                       .toString();
                    break;

                default:
                    this._starHub = "#";
                    this._forkHub = "#";
                    break;
            }
        } else {

            /*
             * Initialize properties to non-null defaults as valid Git
             * repository data is not available.
             */

            this._slideshowBase = HTTP_HASH;
            this._slideshowURL = HTTP_HASH;
            this._orgHub = HTTP_HASH;
            this._repoHub = HTTP_HASH;
            this._starHub = HTTP_HASH;
            this._forkHub = HTTP_HASH;
        }
    }

    public static GitRepoRenderer build(PitchParams pp,
                                        GitRepoModel grm,
                                        Runtime runtime,
                                        List<GRS> grsServices) {

        return new GitRepoRenderer(pp, grm, runtime, grsServices);
    }

    /*
     * Return PitchParams on this GitRepoRenderer.
     */
    public PitchParams params() {
        return _pp;
    }

    /*
     * Return GitRepoModel on this GitRepoRenderer.
     */
    public GitRepoModel model() {
        return _grm;
    }

    /*
     * Return Git {user} name.
     */
    public String user() {
        return _pp.user;
    }

    /*
     * Return Git {repo} name.
     */
    public String repo() {
        return _pp.repo;
    }

    /*
     * Return Git {branch} name.
     */
    public String branch() {
        return _pp.branch;
    }

    /*
     * Return Git {pitchme} path.
     */
    public String pitchme() {
        return (_pp.pitchme != null) ?
            _pp.pitchme.replaceAll("/", ".") : PITCHME;
    }

    /*
     * Return pitch slideshow theme.
     */
    public String theme() {
        return _pp.theme;
    }

    /*
     * Return pitch slideshow grs.
     */
    public String grs() {
        return _pp.grs;
    }

    /*
     * Return true is branch is GIT_MASTER.
     */
    public boolean isMaster() {
        return _pp.isMaster();
    }

    /*
     * Return pitch slideshow theme.css.
     */
    public String themeCSS() {
        return _pp.theme + CSS;
    }

    /*
     * Return relative URL to slideshow.
     */
    public String slideshowURL() {
        return _slideshowURL;
    }

    /*
     * Return relative URL to slideshow.
     */
    public String slideshowURL(String theme) {
        return com.gitpitch.controllers.routes.PitchController
                .slideshow(_grm.owner(),
                _grm.name(),
                _pp.branch,
                _pp.grs,
                theme,
                _pp.pitchme,
                _pp.notes, null, null, null).toString();
    }

    /*
     * Return absolute URL to slideshow.
     */
    public String slideshowAbs() {

        return com.gitpitch.controllers.routes.PitchController
                .slideshow(_pp.user,
                _pp.repo,
                _pp.branch,
                _pp.grs,
                _pp.theme,
                _pp.pitchme,
                _pp.notes, null, null, null).absoluteURL(isEncrypted(),
                            hostname());
    }

    /*
     * Return URL to oEmbed discovery link for slideshow.
     */
    public String oembed(String url, String format) {

        log.debug("oembed: [ {}, {} ]", url, format);
        return com.gitpitch.controllers.routes.PitchController.oembed(url,
                format, null, null, null, null, null)
                .absoluteURL(isEncrypted(), hostname());
    }

    /*
     * Return relative URL to PITCHME.md markdown.
     */
    public String markdownURL() {
        return _markdownURL;
    }

    /*
     * Return https://{grs}/{user}.
     */
    public String orgHub() {
        return _orgHub;
    }

    /*
     * Return https://{grs}/{user}/{repo}.
     */
    public String repoHub() {
        return _repoHub;
    }

    /*
     * Return https://{grs}/{user}/{repo}/tree/{branch}
     */
    public String branchHub(PitchParams pp) {

        String branchHub = "#";

        switch(pp.grs) {
            case GRS_GITHUB:
            case GRS_GITLAB:
            case GRS_GITBUCKET:
                branchHub = _repoHub + GIT_TREE + pp.branch;
                break;
            case GRS_BITBUCKET:
                branchHub = _repoHub + GIT_SRC + pp.branch;
                break;
            case GRS_GITEA:
                branchHub = _repoHub + GIT_SRC_BRANCH + pp.branch;
                break;
            case GRS_GOGS:
                branchHub = _repoHub + GIT_SRC + pp.branch;
                break;
        }
        return branchHub;
    }

    /*
     * Return https://{grs}/{user}/{repo}/blob/{branch}/{pitchme}
     */
    public String pitchHub(PitchParams pp) {

        String pitchPath =
            (pp.pitchme != null) ? (pp.branch + SLASH + pp.pitchme) : pp.branch;
        String pitchHub = "#";

        switch(pp.grs) {
            case GRS_GITHUB:
            case GRS_GITLAB:
            case GRS_GITBUCKET:
                pitchHub = _repoHub + GIT_BLOB + pitchPath + DEFAULT_PITCHME;
                break;
            case GRS_BITBUCKET:
                pitchHub = _repoHub + GIT_SRC + pitchPath + DEFAULT_PITCHME;
                break;
            case GRS_GITEA:
                pitchHub = _repoHub + GIT_SRC_BRANCH + pitchPath + DEFAULT_PITCHME;
                break;
            case GRS_GOGS:
                pitchHub = _repoHub + GIT_SRC + pitchPath + DEFAULT_PITCHME;
                break;
        }
        return pitchHub;
    }

    /*
     * Return https://github.com/{user}/{repo}/stargazers.
     */
    public String starHub() {
        return _starHub;
    }

    /*
     * Return https://github.com/{user}/{repo}/network.
     */
    public String forkHub() {
        return _forkHub;
    }

    /*
     * Return number of Git repository stargazers.
     */
    public int stargazers() {
        return (_grm != null) ? _grm.stargazers() : 0;
    }

    /*
     * Return number of Git repository forks.
     */
    public int forks() {
        return (_grm != null) ? _grm.forks() : 0;
    }

    /*
     * Return Git repository language.
     */
    public String repoLang() {

        String repoLang = null;

        if(_grm != null) {
            if(_grm.lang() != null && _grm.lang().length() > 0) {
                repoLang = _grm.lang();
            }
        }
        return repoLang;
    }

    /*
     * Return true if ViewModel represents a valid
     * repository on Git.
     */
    public boolean isValid() {
        return _grm != null;
    }

    /*
     * Return the "best fit" repo language or branch
     * name when rendering the presentation.
     */
    public String displayLangOrBranch() {

        if (!isValid()) {
            return _pp.branch;
        } else if (isMaster() && repoLang() != null) {
            return repoLang();
        } else {
            return _pp.branch;
        }
    }

    public String pageLink() {
        return pageLink(false);
    }

    public String pageLink(boolean absolute) {
        return pageLink(absolute, null);
    }

    public String pageLink(boolean absolute, String grs) {

        grs = (grs != null) ? grs : _pp.grs;

        if (absolute)
            return com.gitpitch.controllers.routes.PitchController
                    .slideshow(_pp.user,
                    _pp.repo,
                    _pp.branch,
                    grs,
                    _pp.theme,
                    _pp.pitchme,
                    _pp.notes,
                    null, null, null)
                    .absoluteURL(isEncrypted(),
                            hostname());
        else
            return com.gitpitch.controllers.routes.PitchController
                    .slideshow(_pp.user,
                    _pp.repo,
                    _pp.branch,
                    grs,
                    _pp.theme,
                    _pp.pitchme,
                    _pp.notes, null, null, null).toString();
    }

    public String pageLinkWithTheme(String theme) {

        return com.gitpitch.controllers.routes.PitchController
                .slideshow(_pp.user,
                _pp.repo,
                _pp.branch,
                _pp.grs,
                theme,
                _pp.pitchme,
                _pp.notes,
                null, null, null).toString();
    }

    public String printLink() {

        return com.gitpitch.controllers.routes.PitchController.print(_pp.grs,
                _pp.user,
                _pp.repo,
                _pp.branch,
                _pp.theme,
                _pp.pitchme,
                _pp.notes).toString();
    }

    public String printBrowserLink() {

        return com.gitpitch.controllers.routes.PitchController
                .slideshow(_pp.user,
                _pp.repo,
                _pp.branch,
                _pp.grs,
                _pp.theme,
                _pp.pitchme,
                _pp.notes,
                null,
                "false",
                "true").toString();
    }

    public String offlineLink() {

        return com.gitpitch.controllers.routes.PitchController.offline(_pp.grs,
                _pp.user,
                _pp.repo,
                _pp.branch,
                _pp.theme,
                _pp.pitchme,
                _pp.notes).toString();
    }

    public String twitterLink() {

      return new StringBuffer("http://twitter.com/share?text=")
                  .append("GitPitch presentation...")
                  .append("&url=")
                  .append(pageLink(true, null))
                  .append("&hashtags=gitpitch")
                  .toString();

    }

    public String linkedInLink() {

      return new StringBuffer("https://www.linkedin.com/shareArticle")
                  .append("?source=gitpitch")
                  .append("&mini=true")
                  .append("&summary=GitPitch%20presentation...")
                  .append("&title=GitPitch")
                  .append("&url=")
                  .append(pageLink(true, null))
                  .toString();

    }

    public String pageEmbed() {

        return new StringBuffer(EMBED_OPEN)
                .append(pageLink(true))
                .append(EMBED_CLOSE)
                .toString();
    }

    public String pageBadge() {

        return new StringBuffer(BADGE_OPEN)
                .append(pageLink(true))
                .append(BADGE_CLOSE)
                .toString();
    }

    public String getGRS(PitchParams pp) {
        String grsName = null;
        for(GRS grs : _grsServices) {
            if(grs.getType().equals(pp.grs)) {
                grsName = grs.getName();
            }
        }
        return grsName;
    }

    public List<GRS> listGRS() {
        return _grsServices;
    }

    public String getGRSIcon(PitchParams pp) {

        String icon = GIT_DEFAULT_ICON;

        switch(pp.grs) {

            case "github":
                icon = GITHUB_ICON;
                break;
            case "gitlab":
                icon = GITLAB_ICON;
                break;
            case "bitbucket":
                icon = BITBUCKET_ICON;
                break;
        }

        return icon;
    }

    public List<String> listThemes() {
      List<String> themesList =
        runtime.configStringList("gitpitch.revealjs.themes");
      if(themesList == null || themesList.isEmpty()) {
          themesList = DEPLOY_THEMES;
      }
      return themesList;
    }

    public String pageDescription() {
        if(model() != null && model().description() != null) {
            return model().description();
        } else {
            return AS_DESCR;
        }
    }

    /*
     * Return string representation of ViewModel.
     */
    public String toString() {
        return _slideshowURL;
    }

    private boolean isEncrypted() {
        return runtime.configBool("gitpitch.https");
    }

    private String hostname() {
        return runtime.config("gitpitch.hostname");
    }

    private static final String GIT_MASTER = "master";
    private static final String GIT_TREE   = "/tree/";
    private static final String GIT_BLOB   = "/blob/";
    private static final String GIT_SRC    = "/src/";
    private static final String GIT_SRC_BRANCH = "/src/branch/";
    private static final String CSS = ".css";
    private static final String MARKDOWN = "/pitchme/markdown/";
    private static final String SLASH = "/";
    private static final String QMARK_BRANCH = "?b=";
    private static final String QMARK_THEME = "&t=";
    private static final String HTTP_HASH = "#";
    private static final String EMBED_OPEN =
            "<iframe width='770' height='515' src='";
    private static final String EMBED_CLOSE =
            "' frameborder='0' allowfullscreen></iframe>";
    private static final String BADGE_OPEN =
            "[![GitPitch](https://gitpitch.com/assets/badge.svg)](";
    private static final String BADGE_CLOSE = ")";
    private static final String PITCHME = "PITCHME.md";
    private static final String DEFAULT_PITCHME = "/PITCHME.md";
    private static final String DEFAULT_THEME = "white";
    private static final List<String> THEMES = Arrays.asList(DEFAULT_THEME,
            "beige", "black", "moon", "night", "sky", "white");

    private static final String GRS_GITHUB_COM = "https://github.com/";
    private static final String GRS_GITHUB_STARS = "stargazers";
    private static final String GRS_GITHUB_FORKS = "network";
    private static final String GRS_GITLAB_STARS = "activity";
    private static final String GRS_GITLAB_FORKS = "graphs/";
    private static final String GRS_BITBUCKET_STARS = "commits/all";
    private static final String GRS_BITBUCKET_FORKS = "branches";

    private static final String GITHUB_ICON =
        "<span class='octicon octicon-mark-github'></span>";
    private static final String GITLAB_ICON =
        "<i class='fa fa-gitlab' aria-hidden='true'></i>";
    private static final String BITBUCKET_ICON =
        "<i class='fa fa-bitbucket' aria-hidden='true'></i>";
    private static final String GIT_DEFAULT_ICON =
        "<i class='fa fa-git-square' aria-hidden='true'></i>";

    private static final String GRS_GITHUB = "github";
    private static final String GRS_GITLAB = "gitlab";
    private static final String GRS_BITBUCKET = "bitbucket";
    private static final String GRS_GITBUCKET = "gitbucket";
    private static final String GRS_GITEA = "gitea";
    private static final String GRS_GOGS = "gogs";

    private static final String AS_DESCR =
        "Markdown Presentation powered by GitPitch.";

    private static final List<String> DEPLOY_THEMES =
            Arrays.asList("black", "moon", "night", "beige", "sky", "white");
}
