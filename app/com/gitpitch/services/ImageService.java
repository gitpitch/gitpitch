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
import com.gitpitch.utils.PitchParams;
import com.gitpitch.utils.DelimParams;
import com.gitpitch.utils.YAMLOptions;
import org.apache.commons.io.FilenameUtils;
import java.util.*;
import javax.inject.*;
import play.Logger;
import play.Logger.ALogger;

/*
 * PITCHME.md image support service.
 */
@Singleton
public class ImageService {

    private final Logger.ALogger log = Logger.of(this.getClass());

    public String buildBackground(PitchParams pp,
                                  YAMLOptions yOpts) {

        return buildBackground(yOpts.fetchImageBg(pp),
                               yOpts.fetchImageBgSize(pp),
                               yOpts.fetchImageBgColor(pp),
                               yOpts.fetchImageBgPosition(pp),
                               yOpts.fetchImageBgRepeat(pp),
                               yOpts.fetchTransition(pp));
    }

    public String buildBackground(String md,
                                  DelimParams dp,
                                  PitchParams pp,
                                  String defaultSize,
                                  String defaultColor,
                                  String defaultPos,
                                  String defaultRepeat,
                                  String defaultTransition,
                                  MarkdownModel mdm) {

        String bgUrl = dp.get(MarkdownModel.DELIM_QUERY_IMAGE);
        bgUrl = mdm.linkLive(pp, bgUrl);
        String bgSize = dp.get(MarkdownModel.DELIM_QUERY_SIZE, defaultSize);
        String bgColor = dp.get(MarkdownModel.DELIM_QUERY_COLOR, defaultColor);
        String bgPos = dp.get(MarkdownModel.DELIM_QUERY_POSITION, defaultPos);
        String bgRepeat =
            dp.get(MarkdownModel.DELIM_QUERY_REPEAT, defaultRepeat);
        String bgTransition =
            dp.get(MarkdownModel.DELIM_QUERY_TRANSITION, defaultTransition);
        return buildBackground(bgUrl, bgSize, bgColor, bgPos, bgRepeat, bgTransition);
    }

    private String buildBackground(String bgUrl,
                                   String bgSize,
                                   String bgColor,
                                   String bgPosition,
                                   String bgRepeat,
                                   String bgTransition) {

        return new StringBuffer(MarkdownModel.MD_SPACER)
                .append(MarkdownModel.MD_IMAGE_OPEN)
                .append(bgUrl)
                .append(MarkdownModel.MD_IMAGE_SIZE)
                .append(bgSize)
                .append(MarkdownModel.MD_IMAGE_COLOR)
                .append(bgColor)
                .append(MarkdownModel.MD_IMAGE_POSITION)
                .append(bgPosition)
                .append(MarkdownModel.MD_IMAGE_REPEAT)
                .append(bgRepeat)
                .append(MarkdownModel.MD_IMAGE_TRANSITION)
                .append(bgTransition)
                .append(MarkdownModel.MD_CLOSER)
                .append(MarkdownModel.MD_SPACER)
                .toString();
    }

    public String buildBackgroundOffline(String md) {

        try {

            String frag = md.substring(MarkdownModel.MD_IMAGE_OPEN.length());
            String fragUrl = frag.substring(0, frag.indexOf("\""));
            String imageName = FilenameUtils.getName(fragUrl);
            String imageUrl = IMG_OFFLINE_DIR + imageName;

            return md.replace(fragUrl, imageUrl);
        } catch (Exception bex) {
        }

        return md;
    }

    public String buildInlineOffline(String imageName) {

        return new StringBuffer(IMG_INLINE_OPEN).append(imageName)
                .append(IMG_INLINE_CLOSE)
                .toString();

    }

    public String buildTagOffline(String md) {

        try {

            String imageTagUrl = tagLink(md);
            String imageTagName = FilenameUtils.getName(imageTagUrl);
            String offlineTagUrl = IMG_OFFLINE_DIR + imageTagName;
            return md.replace(imageTagUrl, offlineTagUrl);
        } catch(Exception tex) {}

        return md;
    }

    public boolean inline(String md) {
        return md.startsWith(MarkdownModel.MD_LINK_OPEN);
    }

    public boolean background(String md) {
        return md.contains(MarkdownModel.DATA_IMAGE_ATTR);
    }

    /*
     * Return true is HTML image tag found.
     */
    public boolean tagFound(String md) {
        return md.startsWith(IMG_TAG_OPEN);
    }

    public String tagLink(String md) {

        int linkTagStart = md.indexOf(IMG_TAG_SRC_OPEN);
        int linkStart = linkTagStart + IMG_TAG_SRC_OPEN.length();
        int linkEnd =
            md.indexOf(IMG_TAG_SRC_CLOSE, linkStart);

        String tagLink = IMG_TAG_LINK_UNKNOWN;
        if(linkTagStart != -1 && linkEnd != -1) {
            tagLink = md.substring(linkStart, linkEnd);
        }

        return tagLink;
    }

    private static final String IMG_OFFLINE_DIR = "./assets/md/assets/";
    private static final String IMG_INLINE_OPEN = "![Image](" + IMG_OFFLINE_DIR;
    private static final String IMG_INLINE_CLOSE = ")";
    private static final String IMG_TAG_OPEN = "<img ";
    private static final String IMG_TAG_SRC_OPEN = "src=\"";
    private static final String IMG_TAG_SRC_CLOSE = "\"";
    private static final String IMG_TAG_LINK_UNKNOWN = "#";
    private static final String IMG_CUSTOM_SIZE_OPTION = "&size=";
}
