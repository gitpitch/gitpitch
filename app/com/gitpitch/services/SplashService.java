/*
 * MIT License
 *
 * Copyright (c) 2017 David Russell
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
package com.gitpitch.services;

import com.gitpitch.models.MarkdownModel;
import com.gitpitch.utils.PitchParams;
import com.gitpitch.utils.YAMLOptions;
import org.apache.commons.io.FilenameUtils;
import java.util.*;
import javax.inject.*;
import play.Logger;
import play.Logger.ALogger;

/*
 * PITCHME.md splash-slide support service.
 */
@Singleton
public class SplashService {

    private final Logger.ALogger log = Logger.of(this.getClass());

    /*
     * Generate presentation splash-slide markdown.
     */
    public String build(String md, YAMLOptions yOpts, String delim) {

        return new StringBuffer(SPLASH_OPEN).append(SPLASH_LOGO_URL)
                                            .append(SPLASH_PAD)
                                            .append(SPLASH_LOGO_SIZE)
                                            .append(MarkdownModel.MD_SPACER)
                                            .append(SPLASH_TOC)
                                            .append(MarkdownModel.MD_SPACER)
                                            .append(SPLASH_CLOSE)
                                            .append(MarkdownModel.MD_SPACER)
                                            .append(delim)
                                            .append(MarkdownModel.MD_SPACER)
                                            .append(md)
                                            .toString();
    }

    private static final String SPLASH_OPEN =
      "<section data-background-color='black' data-background-image=\"";
    private static final String SPLASH_LOGO_URL =
      "https://gitpitch.com/gp-splash.svg";
    private static final String SPLASH_PAD = "\" ";
    private static final String SPLASH_LOGO_SIZE =
      "data-background-size='40%'>";
    private static final String SPLASH_TOC =
      "<span class='menu-title' style='display: none'>GitPitch</span>";
    private static final String SPLASH_CLOSE = "</section>";
}
