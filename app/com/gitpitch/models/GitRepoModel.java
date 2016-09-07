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
 * Rendering model for views.Landing.scala.html.
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

    /*
     * Return Git repository owner.
     */
    public abstract String owner();

    /*
     * Return Git repository name.
     */
    public abstract String name();

    /*
     * Return Git repository description.
     */
    public abstract String description();

    /*
     * Return Git repository language.
     */
    public abstract String lang();

    /*
     * Return true if Git repository owner is an organization.
     */
    public abstract boolean byOrg();

    /*
     * Return number of Git repository stargazers.
     */
    public abstract int stargazers();

    /*
     * Return number of Git repository forks.
     */
    public abstract int forks();

    public String toString() {
        return _pretty;
    }

    public String key() {
        return _cacheKey;
    }

    protected static final String GIT_TYPE_ORG = "Organization";
    protected static final String SLASH = "/";
    /*
     * Model prefix identifier for cache key generator.
     */
    private static final String MODEL_ID = "GRM:";
}
