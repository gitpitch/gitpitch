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
import com.gitpitch.services.DiskService;
import com.gitpitch.services.ImageService;
import com.gitpitch.git.GRS;
import com.gitpitch.git.GRSService;
import com.gitpitch.git.GRSManager;
import com.gitpitch.utils.PitchParams;
import com.gitpitch.utils.DelimParams;
import com.gitpitch.utils.YAMLOptions;
import java.util.*;
import java.nio.file.*;
import javax.inject.*;
import play.Logger;
import play.Logger.ALogger;

/*
 * PITCHME.md GIST support service.
 */
@Singleton
public class GISTService {

    private final Logger.ALogger log = Logger.of(this.getClass());

    private final GRSManager grsManager;
    private final DiskService diskService;
    private final ImageService imageService;

    @Inject
    public GISTService(GRSManager grsManager,
                       DiskService diskService,
                       ImageService imageService) {

        this.grsManager = grsManager;
        this.diskService = diskService;
        this.imageService = imageService;
    }

    public String build(String md,
                        DelimParams dp,
                        PitchParams pp,
                        YAMLOptions yOpts,
                        MarkdownModel mdm) {

        try {

            String gid = dp.get(MarkdownModel.DELIM_QUERY_GIST);

            if(backCompat(gid)) {

              return buildBackCompat(md, dp, pp, yOpts, mdm);

            } else {

              String fileHint = dp.get(MarkdownModel.DELIM_QUERY_FILE);
              String langHint = dp.get(MarkdownModel.DELIM_QUERY_LANG);
              String slideTitle = dp.get(MarkdownModel.DELIM_QUERY_TITLE);
              String extractedDelim = mdm.extractGISTDelim(md);

              GRS grs = grsManager.get(pp);
              GRSService grsService = grsManager.getService(grs);
              Path branchPath = diskService.ensure(pp);
              String gistLink = grsService.gist(pp, gid, fileHint);
              log.debug("build: generated gistLink={}", gistLink);

              int downStatus =
                  diskService.download(pp, branchPath, gistLink,
                                       GIST_CODE, grs.getHeaders());

              if(downStatus == 0) {
                  String code = diskService.asText(pp, GIST_CODE);
                  return buildCodeBlock(extractedDelim,
                                        code, langHint, slideTitle);
              } else {
                  return buildCodeBlockError(extractedDelim, gid, fileHint);
              }

            }

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

    private String buildCodeBlock(String delim,
                                  String code,
                                  String langHint,
                                  String slideTitle) {

        StringBuffer slide =  new StringBuffer(delim)
                                  .append(MarkdownModel.MD_SPACER);

        if(slideTitle != null) {
            slide = slide.append(MarkdownModel.MD_SPACER)
                         .append(slideTitle)
                         .append(MarkdownModel.MD_SPACER);
        }

        slide = slide.append(MarkdownModel.MD_CODE_BLOCK_OPEN);

        if(langHint != null) {
            slide = slide.append(langHint);
        }

        return slide.append(MarkdownModel.MD_SPACER)
                    .append(code)
                    .append(MarkdownModel.MD_SPACER)
                    .append(MarkdownModel.MD_CODE_BLOCK_CLOSE)
                    .append(MarkdownModel.MD_SPACER)
                    .toString();
   }

   private String buildCodeBlockError(String delim,
                                      String codePath,
                                      String fileHint) {

        String filePath = fileHint != null ? fileHint : GIST_DEFAULT_FILE;
        filePath = "[ " + filePath + " ]";
        return new StringBuffer(delim)
                                .append(MarkdownModel.MD_SPACER)
                                .append(GIST_DELIMITER)
                                .append(MarkdownModel.MD_SPACER)
                                .append(codePath)
                                .append(MarkdownModel.MD_SPACER)
                                .append(filePath)
                                .append(MarkdownModel.MD_SPACER)
                                .append(GIST_NOT_FOUND)
                                .append(MarkdownModel.MD_SPACER)
                                .toString();
   }

   private boolean backCompat(String gid) {
     return !gid.contains(GIST_ID_DELIM);
   }

    /*
     * Provide backward compatible support for embedding
     * GIST within an iframe. This approach has been superceded
     * by injecting GIST source into code-blocks.
     */
    private String buildBackCompat(String md,
                                   DelimParams dp,
                                   PitchParams pp,
                                   YAMLOptions yOpts,
                                   MarkdownModel mdm) {

        try {

            String gid = dp.get(MarkdownModel.DELIM_QUERY_GIST);

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

    private static final String GIST_CODE = "PITCHME.gist";
    private static final String GIST_DELIMITER = "### GIST Delimiter";
    private static final String GIST_NOT_FOUND = "### GIST Not Found";
    private static final String GIST_ID_DELIM = "/";
    private static final String GIST_DEFAULT_FILE = "Default File";

    /*
     * BackCompat GIST IFrame Frags (scrolling permitted).
     */
    private static final String GIST_DIV_OPEN = "<div data-gist=\"true\" ";
    private static final String GIST_DIV_CLASS = "class=\"stretch\">";
    private static final String GIST_IFR_OPEN =
            "<iframe width=\"100%\" height=\"100%\" src=\"";
    private static final String GIST_IFR_CLSE =
            "\" frameborder=\"0\" scrolling=\"yes\" allowfullscreen></iframe>";
    private static final String GIST_DIV_CLSE = "</div>";
    /*
     * BackCompat Offline GIST Frags.
     */
    private static final String OFFLINE_GIST = "#### GIST Slide Disabled";
    private static final String OFFLINE_NOTICE = "#### [ GitPitch Offline ]";

}
