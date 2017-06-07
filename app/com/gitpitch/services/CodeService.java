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
                        PitchParams pp,
                        YAMLOptions yOpts,
                        String gitRawBase,
                        MarkdownModel mdm) {

        String codeBlock = mdm.extractCodeDelim(md);

        try {

            String codePath = extractCodePath(md, gitRawBase, mdm);

            String langHint = getLangHintOptionFromPath(codePath);
            codePath  = cleanOptionsFromPath(codePath);

            GRS grs = grsManager.get(pp);
            GRSService grsService = grsManager.getService(grs);

            int downStatus =
                grsService.download(pp, SOURCE_CODE, codePath);

            if(downStatus == 0) {
                String code = diskService.asText(pp, SOURCE_CODE);
                return buildCodeBlock(mdm.extractCodeDelim(md), code, langHint);
            } else {
                return buildCodeBlockError(mdm.extractCodeDelim(md),
                                            extractPath(md, mdm));
            }

        } catch (Exception gex) {}
        return codeBlock;
    }

    public String extractCodePath(String md,
                                  String gitRawBase,
                                  MarkdownModel mdm) {

        String codePath = null;

        try {

            String delim = mdm.extractCodeDelim(md);
            codePath = md.substring(delim.length());

        } catch (Exception pex) {
            log.warn("extractCodePath: ex={}", pex);
        }
        return codePath;
    }

    private String extractPath(String md, MarkdownModel mdm) {
        String path = null;

        try {
            String delim = mdm.extractCodeDelim(md);
            path = md.substring(delim.length());
        } catch (Exception pex) {
            log.warn("extractPath: ex={}", pex);
        }
        return path;
    }

    private String buildCodeBlock(String delim, String code, String langHint) {
        return new StringBuffer(delim)
                                .append(MarkdownModel.MD_SPACER)
                                .append(MarkdownModel.MD_CODE_BLOCK_OPEN)
                                .append(langHint)
                                .append(MarkdownModel.MD_SPACER)
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

    private String getLangHintOptionFromPath(String codePath) {
        String extractedHint = NO_LANG_HINT;
        try {
            int hintOptIdx = codePath.indexOf(LANG_HINT_OPTION);
            if(hintOptIdx != -1) {
                int hintOffset = hintOptIdx + LANG_HINT_OPTION.length();
                extractedHint = codePath.substring(hintOffset);
            }

        } catch(Exception ex) {}
        return extractedHint;
    }

    private String cleanOptionsFromPath(String codePath) {
        String cleanedPath = codePath;
        try {
            int hintOptIdx = codePath.indexOf(LANG_HINT_OPTION);
            if(hintOptIdx != -1) {
                cleanedPath = codePath.substring(0, hintOptIdx);
            }


        } catch(Exception cex) {}
        return cleanedPath;
    }

    private static final String SOURCE_CODE = "PITCHME.code";
    private static final String SOURCE_CODE_DELIMITER =
        "### Code Block Delimiter";
    private static final String SOURCE_CODE_NOT_FOUND =
        "### Source File Not Found";
    private static final String LANG_HINT_OPTION = "&lang=";
    private static final String NO_LANG_HINT = "";

}
