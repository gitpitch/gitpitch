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
package com.gitpitch.services;

import com.gitpitch.models.MarkdownModel;
import com.gitpitch.services.ImageService;
import com.gitpitch.utils.PitchParams;
import com.gitpitch.utils.YAMLOptions;
import java.util.*;
import javax.inject.*;
import play.Logger;
import play.Logger.ALogger;

/*
 * PITCHME.md GIST support service.
 */
@Singleton
public class GISTService {

    private final Logger.ALogger log = Logger.of(this.getClass());

    /*
     * Offline GIST Frags.
     */
    private static final String OFFLINE_GIST = "#### GIST Slide Disabled";
    private static final String OFFLINE_NOTICE = "#### [ GitPitch Offline ]";
    /*
     * GIST IFrame Frags (scrolling permitted).
     */
    private static final String GIST_DIV_OPEN = "<div data-gist=\"true\" ";
    private static final String GIST_DIV_CLASS = "class=\"stretch\">";
    private static final String GIST_IFR_OPEN =
            "<iframe width=\"100%\" height=\"100%\" src=\"";
    private static final String GIST_IFR_CLSE =
            "\" frameborder=\"0\" scrolling=\"yes\" allowfullscreen></iframe>";
    private static final String GIST_DIV_CLSE = "</div>";

    private final ImageService imageService;

    @Inject
    public GISTService(ImageService imageService) {

        this.imageService = imageService;
    }

    public String build(String md,
                        PitchParams pp,
                        YAMLOptions yOpts,
                        MarkdownModel mdm) {

        try {

            String gid =
                    md.substring(mdm.horizGISTDelim().length());

            String gistCallback =
                    com.gitpitch.controllers.routes.PitchController.gist(gid)
                                                                   .url();

            String slideType = mdm.isHorizontal(md) ?
                    mdm.horizDelim() : mdm.vertDelim();

            String yamlBg = null;
            if (yOpts != null && yOpts.hasImageBg()) {
                /*
                 * GIST slides need PITCHME.yaml bg injected.
                 */
                yamlBg = imageService.buildBackground(pp, yOpts);
            }

            StringBuffer gistBuf =
                    new StringBuffer(slideType).append(MarkdownModel.MD_SPACER);

            if (yamlBg != null) {
                gistBuf = gistBuf.append(yamlBg)
                        .append(MarkdownModel.MD_SPACER);
            }

            return gistBuf.append(GIST_DIV_OPEN)
                    .append(GIST_DIV_CLASS)
                    .append(GIST_IFR_OPEN)
                    .append(gistCallback)
                    .append(GIST_IFR_CLSE)
                    .append(GIST_DIV_CLSE)
                    .append(MarkdownModel.MD_SPACER)
                    .toString();

        } catch (Exception gex) {
            /*
             * Invalid GIST syntax, return clean slide delimiter.
             */
            return mdm.extractGISTDelim(md);
        }

    }

    public String offline() {
        return new StringBuffer(OFFLINE_GIST).append(MarkdownModel.MD_SPACER)
                .append(OFFLINE_NOTICE)
                .toString();
    }

}
