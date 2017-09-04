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
import com.gitpitch.git.GRS;
import com.gitpitch.git.GRSService;
import com.gitpitch.git.GRSManager;
import com.gitpitch.services.DiskService;
import com.gitpitch.utils.PitchParams;
import com.gitpitch.utils.DelimParams;
import com.gitpitch.utils.YAMLOptions;
import java.util.*;
import java.nio.file.*;
import javax.inject.*;
import play.Logger;
import play.Logger.ALogger;

/*
 * PITCHME.md code support service.
 */
@Singleton
public class CodeService {

    private final Logger.ALogger log = Logger.of(this.getClass());

    private final GRSManager grsManager;
    private final DiskService diskService;

    @Inject
    public CodeService(GRSManager grsManager,
                       DiskService diskService) {

        this.grsManager = grsManager;
        this.diskService = diskService;
    }

    public String build(String md,
                        DelimParams dp,
                        PitchParams pp,
                        YAMLOptions yOpts,
                        MarkdownModel mdm) {

        String codeBlock = mdm.extractCodeDelim(md);

        try {

            String codePath = dp.get(MarkdownModel.DELIM_QUERY_CODE);
            String langHint = dp.get(MarkdownModel.DELIM_QUERY_LANG);
            String slideTitle = dp.get(MarkdownModel.DELIM_QUERY_TITLE);

            GRS grs = grsManager.get(pp);
            GRSService grsService = grsManager.getService(grs);

            int downStatus =
                grsService.download(pp, SOURCE_CODE, codePath);

            if(downStatus == 0) {
                String code = diskService.asText(pp, SOURCE_CODE);
                return buildCodeBlock(mdm.extractCodeDelim(md),
                                      code, langHint, slideTitle);
            } else {
                return buildCodeBlockError(mdm.extractCodeDelim(md), codePath);
            }

        } catch (Exception gex) {}
        return codeBlock;
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

   private String buildCodeBlockError(String delim, String codePath) {
        return new StringBuffer(delim)
                                .append(MarkdownModel.MD_SPACER)
                                .append(SOURCE_CODE_DELIMITER)
                                .append(MarkdownModel.MD_SPACER)
                                .append(codePath)
                                .append(MarkdownModel.MD_SPACER)
                                .append(SOURCE_CODE_NOT_FOUND)
                                .append(MarkdownModel.MD_SPACER)
                                .toString();
   }

    private static final String SOURCE_CODE = "PITCHME.code";
    private static final String SOURCE_CODE_DELIMITER =
        "### Code Block Delimiter";
    private static final String SOURCE_CODE_NOT_FOUND =
        "### Source File Not Found";

}
