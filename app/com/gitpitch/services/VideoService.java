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
import java.util.*;
import javax.inject.*;
import play.Logger;
import play.Logger.ALogger;

/*
 * PITCHME.md video support service.
 */
@Singleton
public class VideoService {

    private final Logger.ALogger log = Logger.of(this.getClass());

    /*
     * Determine if video link is valid based on MP4 extension
     * or if link to popular video hosting services.
     */
    public boolean isVideo(String videoLink) {

        boolean isVideo = false;

        if (videoLink != null) {

            if (videoLink.toLowerCase().endsWith(VIDEO_MP4)) {
                isVideo = true;
            } else if (videoLink.contains(YOUTUBE_EMBED)) {
                isVideo = true;
            } else if (videoLink.contains(VIMEO_EMBED)) {
                isVideo = true;
            }

        }

        return isVideo;
    }

    public String buildVideo(String videoLink) {

        if (videoLink.contains(YOUTUBE_EMBED)) {

            /*
             * Build YouTube <iframe> tag.
             *
             * Example link:
             * https://www.youtube.com/embed/mkiDkkdGGAQ
             *
             * Example link with offset start time:
             * https://www.youtube.com/embed/mkiDkkdGGAQ?start=121
             */

            String ifrOps = (videoLink.contains("?")) ?
                YTUBE_IFR_OPS_APPEND : YTUBE_IFR_OPS;

            return new StringBuffer(YTUBE_DIV_OPEN).append(YTUBE_IFR_OPEN)
                    .append(videoLink)
                    .append(ifrOps)
                    .append(YTUBE_IFR_CLSE)
                    .append(YTUBE_DIV_CLSE)
                    .toString();

        } else if (videoLink.contains(VIMEO_EMBED)) {

            /*
             * Build Vimeo <iframe> tag.
             *
             * Example link:
             * https://player.vimeo.com/video/111525512
             */
            return new StringBuffer(VIMEO_DIV_OPEN).append(VIMEO_IFR_OPEN)
                    .append(videoLink)
                    .append(VIMEO_IFR_FULL)
                    .append(VIMEO_IFR_CLSE)
                    .append(VIMEO_DIV_CLSE)
                    .toString();


        } else {

            /*
             * Build Reveal.js HTML5 <video> tag.
             *
             * Example link:
             * http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4
             */
            return new StringBuffer(RJS_VID_OPEN).append(RJS_VID_STRH)
                    .append(RJS_VID_AUTO)
                    .append(RJS_VID_SRC)
                    .append(videoLink)
                    .append(RJS_VID_CLSE)
                    .toString();
        }

    }

    public String extractBgUrl(String md,
                               String gitRawBase,
                               MarkdownModel mdm) {

        try {

            String delim = mdm.extractVideoDelim(md);
            String videoBgUrl = md.substring(delim.length());

            if (mdm.linkAbsolute(videoBgUrl)) {
                return videoBgUrl;
            } else {
                return new StringBuffer(gitRawBase).append(videoBgUrl)
                        .toString();
            }

        } catch (Exception pex) {
            log.warn("processVideoBg: ex={}", pex);
            /*
             * Invalid bg syntax, return clean slide delimiter.
             */
            return mdm.isHorizontal(md) ? mdm.horizDelim() : mdm.vertDelim();
        }
    }

    public String buildBackground(String bgUrl) {

        return new StringBuffer(MarkdownModel.MD_SPACER)
                .append(MarkdownModel.MD_SPACER)
                .append(MarkdownModel.MD_VIDEO_OPEN)
                .append(bgUrl)
                .append(MarkdownModel.MD_CLOSER)
                .append(MarkdownModel.MD_SPACER)
                .toString();
    }

    public boolean found(String md) {
        return (md != null &&
                (md.contains(DATA_VIDEO_ATTR) || md.contains(DATA_VIDEO_BG_ATTR)));
    }

    public String offline() {

        return new StringBuffer(OFFLINE_VIDEO).append(SPACER)
                .append(OFFLINE_NOTICE)
                .toString();
    }

    private static final String VIDEO_MP4 = "mp4";
    /*
     * Reveal.js Video Tag Frags.
     */
    private static final String RJS_VID_OPEN = "<video data-video=\"true\"";
    private static final String RJS_VID_STRH = "class=\"stretch\" ";
    private static final String RJS_VID_AUTO = "data-autoplay ";
    private static final String RJS_VID_SRC = "src=\"";
    private static final String RJS_VID_CLSE = "\"></video>";
    /*
     * Supported Video Hosting Services.
     */
    private static final String YOUTUBE_EMBED = "youtube.com/embed";
    private static final String VIMEO_EMBED = "player.vimeo.com/video";
    /*
     * YouTube Video Embed Tag Frags.
     */
    private static final String YTUBE_DIV_OPEN = "<div data-video=\"true\" class=\"stretch\">";
    private static final String YTUBE_IFR_OPEN = "<iframe width=\"100%\" height=\"100%\" src=\"";
    private static final String YTUBE_IFR_OPS = "?wmode=opaque&amp;rel=0&amp;vq=large\" ";
    private static final String YTUBE_IFR_OPS_APPEND = "&amp;wmode=opaque&amp;rel=0&amp;vq=large\" ";
    private static final String YTUBE_IFR_CLSE = "frameborder=\"0\" allowfullscreen></iframe>";
    private static final String YTUBE_DIV_CLSE = "</div>";
    /*
     * Vimeo Video Embed Tag Frags.
     */
    private static final String VIMEO_DIV_OPEN = "<div data-video=\"true\" class=\"stretch\">";
    private static final String VIMEO_IFR_OPEN = "<iframe width=\"100%\" height=\"100%\" src=\"";
    private static final String VIMEO_IFR_FULL = "\" webkitallowfullscreen mozallowfullscreen ";
    private static final String VIMEO_IFR_CLSE = "frameborder=\"0\" allowfullscreen></iframe>";
    private static final String VIMEO_DIV_CLSE = "</div>";
    /*
     * Video Slide Offline Support.
     */
    private static final String OFFLINE_VIDEO = "#### Video Slide Disabled";
    private static final String OFFLINE_NOTICE = "#### [ GitPitch Offline ]";
    private static final String DATA_VIDEO_ATTR = "data-video=";
    private static final String DATA_VIDEO_BG_ATTR = "data-background-video=";
    private static final String SPACER = "\n";

}
