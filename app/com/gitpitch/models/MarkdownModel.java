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

import com.gitpitch.models.SlideshowModel;
import com.gitpitch.utils.*;
import com.gitpitch.services.ImageService;
import com.gitpitch.services.VideoService;
import com.gitpitch.services.GISTService;
import org.apache.commons.io.FilenameUtils;
import com.google.inject.assistedinject.Assisted;
import javax.annotation.Nullable;
import play.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.*;

/*
 * Rendering model for PITCHME.md markdown.
 */
public class MarkdownModel implements Markdown {

    private final Logger.ALogger log = Logger.of(this.getClass());

    private final ImageService imageService;
    private final VideoService videoService;
    private final GISTService  gistService;
    private final MarkdownRenderer mrndr;
    private final String markdown;
    private String hSlideDelim = HSLIDE_DELIM_DEFAULT;
    private String vSlideDelim = VSLIDE_DELIM_DEFAULT;

    @Inject
    public MarkdownModel(ImageService imageService,
                         VideoService videoService,
                         GISTService  gistService,
                         @Nullable @Assisted MarkdownRenderer mrndr) {

        this.imageService = imageService;
        this.videoService = videoService;
        this.gistService  = gistService;
        this.mrndr = mrndr;

        String consumed = null;

        if(mrndr != null) {

            String gitRawBase = mrndr.gitRawBase();
            Path mdPath = mrndr.filePath(PITCHME_MD);

            File mdFile = mdPath.toFile();

            if (mdFile.exists()) {

                Optional<SlideshowModel> ssmo = mrndr.ssm();

                final PitchParams pp =
                        ssmo.isPresent() ? ssmo.get().params() : null;
                final YAMLOptions yOpts =
                        ssmo.isPresent() ? ssmo.get().options() : null;

                if(yOpts != null) {
                    hSlideDelim = yOpts.hasHorzDelim(pp) ?
                        yOpts.fetchHorzDelim(pp) : HSLIDE_DELIM_DEFAULT;
                    vSlideDelim = yOpts.hasVertDelim(pp) ?
                        yOpts.fetchVertDelim(pp) : VSLIDE_DELIM_DEFAULT;
                }

                try (Stream<String> stream = Files.lines(mdPath)) {

                    consumed = stream.map(md -> {
                        return process(md, pp, yOpts, gitRawBase);
                    }).collect(Collectors.joining("\n"));

                    consumed = postProcess(consumed, pp, yOpts, gitRawBase);

                } catch (Exception mex) {
                    log.warn("Markdown processing ex={}", mex);
                    consumed = "PITCHME.md could not be parsed.";
                }

            } else {
                log.warn("Markdown file does not exist, {}", mdFile);
            }

        }

        this.markdown = consumed;
    }

    /*
     * Process text, image, video and gist content
     * in PITCHME.md for online viewing.
     */
    private String process(String md,
                           PitchParams pp,
                           YAMLOptions yOpts,
                           String gitRawBase) {

        if (slideDelimFound(md)) {

            if (videoDelimFound(md)) {

                /*
                 * Inject slide specific video background:
                 *
                 * <!-- .slide: data-background-video="vidUrl" -->
                 */

                String videoBgUrl =
                    videoService.extractBgUrl(md, gitRawBase, this);
                return new StringBuffer(delimiter(md))
                        .append(videoService.buildBackground(videoBgUrl))
                        .toString();

            } else if (imageDelimFound(md)) {

                /*
                 * Inject slide specific image background:
                 *
                 * <!-- .slide: data-background-image="imgUrl" -->
                 */

                String imageBgUrl =
                    imageService.extractBgUrl(md, gitRawBase, this);

                return new StringBuffer(delimiter(md))
                        .append(imageService.buildBackground(pp, imageBgUrl))
                        .toString();

            } else if (gistDelimFound(md)) {

                String gist = new StringBuffer(gistService.build(md,
                                                pp, yOpts, this)).toString();
                return gist;

            }

            if (yOpts != null && yOpts.hasImageBg()) {

                /*
                 * Inject slideshow-wide background:
                 *
                 * <!-- .slide: data-background-image="imgUrl" -->
                 */
                return new StringBuffer(md)
                        .append(imageService.buildBackground(pp, yOpts))
                        .toString();

            } else {

                /*
                 * No customization on slide delimiter or background,
                 * return unmodified.
                 */
                return md;
            }

        } else if (markdownLinkFound(md)) {

            /*
             * Link found in markdown fragment, process.
             */

            int delim = md.indexOf(MD_LINK_DELIM);

            if (delim == NOT_FOUND) {

                /*
                 * Valid link syntax not found, return unmodified.
                 */
                return md;

            } else {

                /*
                 * Valid link syntax found, now process link to
                 * handle absolute, (GitHub) relative and video links.
                 */

                String splitLeft = md.substring(0, delim + 2);
                String splitRight = md.substring(delim + 2);

                /*
                 * Clone and clean splitRight string to begin
                 * extraction of the pitchLink.
                 */
                String pitchLink = cloneLink(splitRight);
                pitchLink = extractLink(pitchLink, gitRawBase);

                if (videoService.isVideo(pitchLink)) {
                    return videoService.buildVideo(pitchLink);
                } else if (linkAbsolute(splitRight)) {
                    return md;
                } else {
                    return splitLeft + gitRawBase + splitRight;
                }

            }

        } else {

            /*
             * Link-free markdown, return unmodified.
             */
            return md;
        }
    }

    public String produce() {
        return markdown;
    }

    /*
     * Process text, image, video and gist content
     * in PITCHME.md for offline viewing.
     */
    public String offline(String md) {

        if (videoService.found(md)) {
            return videoService.offline();
        } else if (gistMarkdown(md)) {
            return gistService.offline();
        } else if (imageService.background(md)) {
            return imageService.buildBackgroundOffline(md);
        } else if (imageService.inline(md)) {

            /*
             * Link found in markdown fragment, process.
             */
            int delim = md.indexOf(MD_LINK_DELIM);

            if (delim == NOT_FOUND) {

                /*
                 * Valid link syntax not found, return unmodified.
                 */
                return md;

            } else {

                /*
                 * Valid link syntax found, now process link to
                 * handle absolute, (GitHub) relative and video links.
                 */

                String splitLeft = md.substring(0, delim + 2);
                String splitRight = md.substring(delim + 2);

                /*
                 * Clone and clean splitRight string to begin
                 * extraction of the pitchLink.
                 */
                String pitchLink = cloneLink(splitRight);
                pitchLink = extractLink(pitchLink, null);

                String imageName = FilenameUtils.getName(pitchLink);
                return imageService.buildInlineOffline(imageName);
            }

        } else {

            /*
             * Link-free markdown, return unmodified.
             */
            return md;
        }
    }

    /*
     * Return image asset URL from Markdown fragment.
     */
    public String offlineAssets(String md) {

        if (imageService.background(md)) {

            String frag = md.substring(MD_IMAGE_OPEN.length());
            String url = frag.substring(0, frag.indexOf("\""));
            return url;

        } else if (imageService.inline(md)) {

            /*
             * Link found in markdown fragment, process.
             */

            int delim = md.indexOf(MD_LINK_DELIM);

            if (delim == NOT_FOUND) {

                /*
                 * Valid link syntax not found, return unmodified.
                 */
                return null;

            } else {

                /*
                 * Valid link syntax found, now process link to
                 * handle absolute, (GitHub) relative and video links.
                 */

                String splitLeft = md.substring(0, delim + 2);
                String splitRight = md.substring(delim + 2);

                /*
                 * Clone and clean splitRight string to begin
                 * extraction of the pitchLink.
                 */
                String pitchLink = cloneLink(splitRight);

                int spaceIdx = pitchLink.indexOf(MD_LINK_SPACE);
                int brcktIdx = pitchLink.indexOf(MD_LINK_BRCKT);

                String origLink = null;

                if (spaceIdx == NOT_FOUND || spaceIdx > brcktIdx) {
                    origLink = pitchLink.substring(0, brcktIdx);
                } else {
                    origLink = pitchLink.substring(0, spaceIdx);
                }
                return origLink;
            }

        } else {
            return null;
        }
    }

    private boolean imageDelimFound(String md) {
        return md.startsWith(horizImageDelim()) || md.startsWith(vertImageDelim());
    }

    private boolean videoDelimFound(String md) {
        return md.startsWith(horizVideoDelim()) || md.startsWith(vertVideoDelim());
    }

    private boolean gistDelimFound(String md) {
        return md.startsWith(horizGISTDelim()) || md.startsWith(vertGISTDelim());
    }

    private String delimiter(String md) {
        return (md.startsWith(hSlideDelim)) ? horizDelim() : vertDelim();
    }

    private boolean slideDelimFound(String md) {
        return md.startsWith(horizDelim()) || md.startsWith(vertDelim());
    }

    private boolean markdownLinkFound(String md) {
        return md.startsWith(MD_LINK_OPEN);
    }

    private boolean gistMarkdown(String md) {
        return md.contains(DATA_GIST_ATTR);
    }

    private String cloneLink(String link) {

        String cloned = new String(link);

        /*
         * Strip slash prefix from relative links.
         */
        if (cloned.startsWith(MD_LINK_SLASH)) {
            return cloned.substring(1);
        } else {
            return cloned;
        }
    }

    private String extractLink(String link, String gitRawBase) {

        try {

            int spaceIdx = link.indexOf(MD_LINK_SPACE);
            int brcktIdx = link.indexOf(MD_LINK_BRCKT);

            String origLink = null;

            if (spaceIdx == NOT_FOUND || spaceIdx > brcktIdx) {
                origLink = link.substring(0, brcktIdx);
            } else {
                origLink = link.substring(0, spaceIdx);
            }

            if (linkAbsolute(link)) {
                link = origLink;
            } else {
                link = gitRawBase + origLink;
            }

            log.debug("extractLink: returning={}", link);
            return link;

        } catch (Exception lex) {
            log.warn("extractLink: link={}, ex={}", link, lex);
        }

        return null;
    }

    private String postProcess(String md,
                                      PitchParams pp,
                                      YAMLOptions yOpts,
                                      String gitRawBase) {

        if (yOpts != null && yOpts.hasImageBg()) {

            if (!delimOnFirstSlide(md)) {

                /*
                 * Inject slideshow-wide background for
                 * implicit delim on first slide in the slideshow.
                 */
                md = imageService.buildBackground(pp, yOpts) + md;
            }
        }

        if (delimOnFirstSlide(md)) {

            int nIndex = md.indexOf(MD_SPACER);

            if (nIndex > 0) {
                String slimMd = md.substring(nIndex);
                md = slimMd;
            }
        }
        return md;
    }

    private boolean delimOnFirstSlide(String md) {
        return (md.startsWith(horizDelim()) || md.startsWith(vertDelim()));
    }

    public boolean isHorizontal(String md) {
        return (md.startsWith(horizDelim())) ? true : false;
    }
    
    public boolean linkAbsolute(String link) {
        return link.startsWith(MD_LINK_ABS);
    }

    public String extractImageDelim(String md) {
        return isHorizontal(md) ? horizImageDelim() : vertImageDelim();
    }

    public String extractVideoDelim(String md) {
        return isHorizontal(md) ? horizVideoDelim() : vertVideoDelim();
    }

    public String extractGISTDelim(String md) {
        return isHorizontal(md) ? horizGISTDelim() : vertGISTDelim();
    }

    /*
     * Generate a key for querying the cache for matching MarkdownModel.
     */
    public static String genKey(PitchParams pp) {

        return new StringBuffer(MODEL_ID).append(SLASH)
                .append(pp.grs)
                .append(SLASH)
                .append(pp.user)
                .append(SLASH)
                .append(pp.repo)
                .append(QMARK_BRANCH)
                .append(pp.branch)
                .toString();
    }

    public String horizDelim() {
        return hSlideDelim;
    }

    public String vertDelim() {
        return vSlideDelim;
    }

    public String horizImageDelim() {
        return horizDelim() + MD_HSLIDE_IMAGE;
    }

    public String vertImageDelim() {
        return vertDelim() + MD_VSLIDE_IMAGE;
    }

    public String horizVideoDelim() {
        return horizDelim() + MD_HSLIDE_VIDEO;
    }

    public String vertVideoDelim() {
        return vertDelim() + MD_VSLIDE_VIDEO;
    }

    public String horizGISTDelim() {
        return horizDelim() + MD_HSLIDE_GIST;
    }

    public String vertGISTDelim() {
        return vertDelim() + MD_VSLIDE_GIST;
    }

    /*
     * Markdown Parsing Identifiers | Delimiters.
     */
    public static final String HSLIDE_DELIM_DEFAULT = "#HSLIDE";
    public static final String VSLIDE_DELIM_DEFAULT = "#VSLIDE";

    public static final String MD_LINK_OPEN = "![";
    public static final String MD_IMAGE_OPEN =
            "<!-- .slide: data-background-image=\"";
    public static final String MD_VIDEO_OPEN =
            "<!-- .slide: data-background-video=\"";
    public static final String MD_IFRAME_OPEN =
            "<!-- .slide: data-background-iframe=\"";
    public static final String MD_IMAGE_SIZE =
            "\" data-background-size=\"100% 100%";
    public static final String MD_CLOSER = "\" -->";
    public static final String MD_SPACER = "\n";
    public static final String DATA_IMAGE_ATTR = "data-background-image=";

    private static final String MD_HSLIDE_IMAGE = "?image=";
    private static final String MD_VSLIDE_IMAGE = "?image=";
    private static final String MD_HSLIDE_VIDEO = "?video=";
    private static final String MD_VSLIDE_VIDEO = "?video=";
    private static final String MD_HSLIDE_GIST = "?gist=";
    private static final String MD_VSLIDE_GIST = "?gist=";

    private static final String SLASH = "/";
    private static final String QMARK_BRANCH = "?b=";
    private static final String MD_LINK_DELIM = "](";
    private static final String MD_LINK_ABS = "http";
    private static final String MD_LINK_SLASH = "/";
    private static final String MD_LINK_SPACE = " ";
    private static final String MD_LINK_BRCKT = ")";
    private static final String MD_LINK_MP4 = "mp4";
    private static final String DATA_GIST_ATTR = "data-gist=";
    private static final int NOT_FOUND = -1;
    /*
     * Model prefix identifier for cache key generator.
     */
    private static final String MODEL_ID = "MDM:";
    private static final String PITCHME_MD = "PITCHME.md";
}
