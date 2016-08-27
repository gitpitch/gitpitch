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

import com.gitpitch.utils.PitchParams;
import java.util.StringJoiner;

/*
 * GitHub RFE issue support utility.
 */
public final class RFE {

    /*
     * Build message that offers the user a
     * chance to submit a Feature Request (Issue)
     * against the repo identified by PitchParams.
     */
    public static String build(PitchParams pp) {

        String newIssue = "https://github.com/" +
                pp.user + "/" + pp.repo + "/issues/new?title=" +
                "GitPitch - Let's get the word out!" +
                "&body=I would really like to see a " +
                "[GitPitch slideshow presentation](https://gitpitch.com) for this repo." +
                " Help me help you get the word out, " +
                "just add PITCHME.md. @gitpitch";

        StringBuffer buf = new StringBuffer();
        buf.append("<span style=\"font-size:1.1em\">PITCHME.md 404</span><br><br>")
                .append("\n\n")
                .append("<span style=\"font-size:0.9em\">")
                .append("Would you like to submit a feature request to the ")
                .append("repository owner asking them to add a GitPitch for this repo?")
                .append(" If so, click <a target=\"_blank\" href=\"")
                .append(newIssue)
                .append("\">here.</a></span>");

        return buf.toString();
    }

    public static final String PITCHME_NOT_FOUND_ON_BRANCH =
            new StringJoiner("\n\n").add("<span style=\"font-size:1.1em\">PITCHME.md 404</span><br><br>")
                    .add("<span style=\"font-size:0.9em\">No GitPitch found for this branch.</span>")
                    .toString();

}
