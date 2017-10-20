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
 * Rendering model for views.Slideshow.scala.html.
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

        if(pp != null) {
            this._pretty = new StringBuffer(SLASH)
                    .append(this._pp.user)
                    .append(SLASH)
                    .append(this._pp.repo)
                    .toString();
            this._cacheKey = genKey(pp);
        }

        /*
         * Generate derived data on instance only if the GitHub
         * API JSON is available for processing.
         */
        if (json != null) {
            JsonNode ownerNode = json.findPath(OWNER);
            this._type = ownerNode.findPath(TYPE).textValue();
            this._desc = json.findPath(DESCRIPTION).textValue();
            this._created = json.findPath(CREATED_AT).textValue();
            this._updated = json.findPath(UPDATED_AT).textValue();
            this._lang = json.findPath(LANGUAGE).textValue();
            this._stars = json.findPath(STARGAZERS_COUNT).asInt();
            this._forks = json.findPath(FORKS_COUNT).asInt();
            this._issues = json.findPath(OPEN_ISSUES).asInt();
            this._hasWiki = json.findPath(HAS_WIKI).asBoolean();
            this._hasPages = json.findPath(HAS_PAGES).asBoolean();
            this._private = json.findPath(PRIVATE).asBoolean();
        }

    }

    public static GitRepoModel build(PitchParams pp) {
        return build(pp, null);
    }

    public static GitRepoModel build(PitchParams pp, JsonNode json) {
        return new GitHubRepoModel(pp, json);
    }

}
