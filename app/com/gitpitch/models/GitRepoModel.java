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

import com.gitpitch.utils.PitchParams;
import play.Logger;
import play.Logger.ALogger;

import java.util.*;

/*
 * Rendering model for views.Slideshow.scala.html.
 */
public abstract class GitRepoModel {

    private final Logger.ALogger log = Logger.of(this.getClass());

    /*
     * PitchParams.
     */
    protected PitchParams _pp;
    /*
     * Git repository owner type: User or Organization.
     */
    protected String _type;
    /*
     * Git repository description.
     */
    protected String _desc;
    /*
     * Git repository creation date.
     */
    protected String _created;
    /*
     * Git repository last updated date.
     */
    protected String _updated;
    /*
     * Git repository language.
     */
    protected String _lang;
    /*
     * Git repository stargazers_count.
     */
    protected int _stars;
    /*
     * Git repository forks_count.
     */
    protected int _forks;
    /*
     * Git repository open issues count.
     */
    protected int _issues;
    /*
     * Git repository has Wiki flag.
     */
    protected boolean _hasWiki;
    /*
     * Git repository has Pages (website) flag.
     */
    protected boolean _hasPages;
    /*
     * Derived immutable state.
     */
    protected String _pretty;
    protected String _cacheKey;
    /*
     * Git repo visibility.
     */
    protected boolean _private;

    /*
     * Generate a key for querying the cache for matching GitRepoModel.
     */
    public static String genKey(PitchParams pp) {

        return new StringBuffer(MODEL_ID).append(SLASH)
                .append(pp.grs)
                .append(SLASH)
                .append(pp.user)
                .append(SLASH)
                .append(pp.repo)
                .toString();
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

    /*
     * Repo API JSON response property names.
     */
    protected static final String OWNER = "owner";
    protected static final String TYPE  = "type";
    protected static final String DESCRIPTION = "description";
    protected static final String CREATED_AT = "created_at";
    protected static final String UPDATED_AT = "updated_at";
    protected static final String CREATED_ON = "created_on";
    protected static final String UPDATED_ON = "updated_on";
    protected static final String LANGUAGE   = "language";
    protected static final String STARGAZERS_COUNT = "stargazers_count";
    protected static final String STAR_COUNT = "star_count";
    protected static final String FORKS_COUNT = "forks_count";
    protected static final String OPEN_ISSUES = "open_issues";
    protected static final String OPEN_ISSUES_COUNT = "open_issues_count";
    protected static final String HAS_WIKI  = "has_wiki";
    protected static final String WIKI_ENABLED  = "wiki_enabled";
    protected static final String HAS_PAGES = "has_pages";
    protected static final String PUBLIC  = "public";
    protected static final String PRIVATE = "private";
    protected static final String IS_PRIVATE = "is_private";
    protected static final String NAMESPACE = "namespace";
    protected static final String VISIBILITY = "visiblity";

    protected static final String GIT_TYPE_ORG = "Organization";
    protected static final String SLASH = "/";
    /*
     * Model prefix identifier for cache key generator.
     */
    private static final String MODEL_ID = "GRM:";
}
