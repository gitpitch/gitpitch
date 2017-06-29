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

import com.gitpitch.utils.PitchParams;
import javax.inject.*;

/*
 * GitPitch Cache Timeout Policies.
 *
 * Content in master is considered stable so typically
 * uses longer timeouts. Content in feature branches is 
 * considered under development so shorter timeouts
 * are used to ensure changes are reflected quickly.
 */
@Singleton
public class CacheTimeout {

    /*
     * Return cache timeout in seconds for GitRepoModel.
     */
    public int grm(PitchParams pp) {
        if (pp.isLongLived()) {
            return pp.isMaster() ? LL_MASTER_TIMEOUT : LL_BRANCH_TIMEOUT;
        } else {
            return pp.isMaster() ? GRM_MASTER_TIMEOUT : GRM_BRANCH_TIMEOUT;
        }
    }

    /*
     * Return cache timeout in seconds for SlideshowModel.
     */
    public int ssm(PitchParams pp) {
        if (pp.isLongLived()) {
            return pp.isMaster() ? LL_MASTER_TIMEOUT : LL_BRANCH_TIMEOUT;
        } else {
            return pp.isMaster() ? SSM_MASTER_TIMEOUT : SSM_BRANCH_TIMEOUT;
        }
    }

    /*
     * Return cache timeout in seconds for MarkdownModel.
     */
    public int mdm(PitchParams pp) {
        if (pp.isLongLived()) {
            return pp.isMaster() ? LL_MASTER_TIMEOUT : LL_BRANCH_TIMEOUT;
        } else {
            return pp.isMaster() ? MDM_MASTER_TIMEOUT : MDM_BRANCH_TIMEOUT;
        }
    }

    /*
     * Return cache timeout in milliseconds for print PDF file.
     */
    public long pdf(PitchParams pp) {
        return pp.isMaster() ? PDF_CACHE_MAX_AGE_MASTER : PDF_CACHE_MAX_AGE_BRANCH;
    }

    /*
     * Return cache timeout in milliseconds for offline ZIP file.
     */
    public long zip(PitchParams pp) {
        return pp.isMaster() ? ZIP_CACHE_MAX_AGE_MASTER : ZIP_CACHE_MAX_AGE_BRANCH;
    }

    /*
     * PDF generation cache timeouts (ms) for PITCHME.pdf.
     */
    public static final long PDF_CACHE_MAX_AGE_MASTER = 60 * 1000 * 20;
    public static final long PDF_CACHE_MAX_AGE_BRANCH = 20 * 1000;
    /*
     * Offline generation cache timeouts (ms) for PITCHME.zip.
     */
    public static final long ZIP_CACHE_MAX_AGE_MASTER = 60 * 1000 * 20;
    public static final long ZIP_CACHE_MAX_AGE_BRANCH = 20 * 1000;
    /*
     * Long Lived cache timeouts (sec) for GitPitch-owned repos.
     */
    private static final int LL_MASTER_TIMEOUT = 60 * 60;
    private static final int LL_BRANCH_TIMEOUT = 15;
    /*
     * GitRepoModel cache timeouts (sec) for GitHub repo meta-data.
     */
    private static final int GRM_MASTER_TIMEOUT = 60 * 30;
    private static final int GRM_BRANCH_TIMEOUT = 60 * 30;
    /*
     * SlideshowModel cache timeouts (sec) for PITCHME.[css|jpg].
     */
    private static final int SSM_MASTER_TIMEOUT = 15;
    private static final int SSM_BRANCH_TIMEOUT = 15;
    /*
     * MarkdownModel cache timeouts (sec) for PITCHME.md.
     */
    private static final int MDM_MASTER_TIMEOUT = SSM_MASTER_TIMEOUT;
    private static final int MDM_BRANCH_TIMEOUT = SSM_BRANCH_TIMEOUT;
}
