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
package com.gitpitch.oembed;

import com.gitpitch.utils.PitchLink;
import com.gitpitch.utils.PitchParams;
import com.gitpitch.git.GRSManager;
import com.gitpitch.git.GRSService;

/*
 * GitPitch oEmbed Provider.
 *
 * https://oembed.com
 */
public class PitchEmbed {

    public String title;
    public String author_name;
    public String html;

    public String type = TYPE;
    public String version = VERSION;
    public String provider_name = GITPITCH;
    public String provider_url = GITPITCH_URL;
    public String cache_age = CACHE;
    public String width = DEFAULT_WIDTH;
    public String height = DEFAULT_HEIGHT;
    public String author_url;
    public String thumbnail_url = DEFAULT_THUMB;
    public String thumbnail_width;
    public String thumbnail_height;

    public static PitchEmbed build(GRSManager grsManager,
                                      String url) {
        return build(grsManager,
                     url,
                     DEFAULT_WIDTH,
                     DEFAULT_HEIGHT,
                     null, null);
    }

    public static PitchEmbed build(GRSManager grsManager,
                                   String url,
                                   String width,
                                   String height,
                                   String maxWidth,
                                   String maxHeight) {

        PitchLink pl = PitchLink.build(url);
        PitchParams pp = PitchParams.build(pl.grs, pl.user, pl.repo, pl.branch);
        String authorUrl = grsManager.get(pp).getSite();
        authorUrl = new StringBuffer(authorUrl).append(pp.user)
                                               .append(SLASH)
                                               .append(pp.repo)
                                               .toString();
        return new PitchEmbed(pp,
                              pl,
                              authorUrl,
                              width,
                              height,
                              maxWidth,
                              maxHeight);
    }

    private PitchEmbed(PitchParams pp,
                       PitchLink pl,
                       String authorUrl,
                       String width,
                       String height,
                       String maxWidth,
                       String maxHeight) {

        this.title = buildTitle(pp);
        this.author_name = pl.user;
        this.author_url = authorUrl;
        this.width = buildWidth(width, maxWidth, maxHeight);
        this.height = buildHeight(height, maxWidth, maxHeight);
        this.thumbnail_width = this.width;
        this.thumbnail_height = this.height;
        this.html = buildHTML(pl.url, this.width, this.height);
    }

    private String buildTitle(PitchParams pp) {
        return GITPITCH_PRESENTS + pp.pretty().substring(1);
    }

    private String buildWidth(String width,
                              String maxWidth,
                              String maxHeight) {
        if(maxWidth != null)
            return maxWidth;
        if(maxHeight != null) {
             try {
                /*
                 * Maintain aspect ratio [ 1.38 : 1 ] [ 960px : 700px ].
                 */
                int maxH = Integer.parseInt(maxHeight);
                return String.valueOf((int) Math.round(maxH * 1.38));
             } catch(Exception ex) {}
        }
        return (width != null) ? width : DEFAULT_WIDTH;
    }

    private String buildHeight(String height,
                               String maxWidth,
                               String maxHeight) {
        if(maxHeight != null)
            return maxHeight;
        if(maxWidth != null) {
             try {
                /*
                 * Maintain aspect ratio [ 1 : 0.73 ] [ 960px : 700px ].
                 */
                int maxW = Integer.parseInt(maxWidth);
                return String.valueOf((int) Math.round(maxW * 0.73));
             } catch(Exception ex) {}
        }
        return (height != null) ? height : DEFAULT_HEIGHT;
    }

    private String buildHTML(String url, String width, String height) {

        return new StringBuffer(HTML_OPEN).append(HTML_WIDTH)
                                          .append(width)
                                          .append(HTML_HEIGHT)
                                          .append(height)
                                          .append(HTML_SRC)
                                          .append(url)
                                          .append(HTML_BORDER)
                                          .append(HTML_MARGINS)
                                          .append(HTML_SCROLL)
                                          .append(HTML_CLOSE)
                                          .toString();
    }

    private final static String GITPITCH = "GitPitch";
    private final static String GITPITCH_PRESENTS = "GitPitch Presents: ";
    private final static String GITPITCH_URL = "https://gitpitch.com";
    private final static String TYPE = "rich";
    private final static String VERSION = "1.0";
    private final static String CACHE = "180";
    private final static String DEFAULT_WIDTH = "960";
    private final static String DEFAULT_HEIGHT = "700";
    private final static String DEFAULT_THUMB =
        "https://gitpitch.com/gitpitch-icon-black.jpg";

    private final static String HTML_OPEN = "<iframe ";
    private final static String HTML_WIDTH = "width=\"";
    private final static String HTML_HEIGHT = "\" height=\"";
    private final static String HTML_SRC = "\" src=\"";
    private final static String HTML_BORDER = "\" frameborder=\"0\" ";
    private final static String HTML_MARGINS =
        "marginwidth=\"0\" marginheight=\"0\" ";
    private final static String HTML_SCROLL = "scrolling=\"no\"";
    private final static String HTML_CLOSE = " allowfullscreen></iframe>";
    private final static String SLASH = "/";

}
