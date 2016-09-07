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

import com.fasterxml.jackson.databind.JsonNode;
import com.gitpitch.utils.PitchParams;
import play.Logger;
import play.Logger.ALogger;

import java.util.*;

/*
 * Rendering model for views.Landing.scala.html.
 */
public class GitHubRepoModel extends GitRepoModel {

    private final Logger.ALogger log = Logger.of(this.getClass());

    /*
     * Initialize instance of GitHubRepoModel.
     */
    private GitHubRepoModel(PitchParams pp) {
        this(pp, null);
    }

    /*
     * Initialize instance of GitHubRepoModel.
     */
    private GitHubRepoModel(PitchParams pp,
                            JsonNode json) {

        this._pp = pp;

        this._pretty = new StringBuffer(SLASH)
                .append(this._pp.user)
                .append(SLASH)
                .append(this._pp.repo)
                .toString();

        this._cacheKey = genKey(pp);

        /*
         * Generate derived data on instance only if the GitHub
         * API JSON is available for processing.
         */
        if (json != null) {

            JsonNode ownerNode = json.findPath("owner");
            this._type = ownerNode.findPath("type").textValue();

            this._desc = json.findPath("description").textValue();
            this._created = json.findPath("created_at").textValue();
            this._updated = json.findPath("updated_at").textValue();
            this._lang = json.findPath("language").textValue();

            this._stars = json.findPath("stargazers_count").asInt();
            this._forks = json.findPath("forks_count").asInt();
            this._issues = json.findPath("open_issues").asInt();

            this._hasWiki = json.findPath("has_wiki").asBoolean();
            this._hasPages = json.findPath("has_pages").asBoolean();

        } else {

            this._type = null;

            this._desc = null;
            this._created = null;
            this._updated = null;
            this._lang = null;

            this._stars = 0;
            this._forks = 0;
            this._issues = 0;

            this._hasWiki = false;
            this._hasPages = false;
        }

    }

    public static GitRepoModel build(PitchParams pp) {
        return build(pp, null);
    }

    public static GitRepoModel build(PitchParams pp, JsonNode json) {
        return new GitHubRepoModel(pp, json);
    }

    public String owner() {
        return _pp.user;
    }

    public String name() {
        return _pp.repo;
    }

    public String description() {
        return _desc;
    }

    public String lang() {
        return _lang;
    }

    public boolean byOrg() {
        return GIT_TYPE_ORG.equals(_type);
    }

    public int stargazers() {
        return _stars;
    }

    public int forks() {
        return _forks;
    }

    public String toString() {
        return _pretty;
    }

    public String key() {
        return _cacheKey;
    }
}
