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

import com.gitpitch.utils.PitchParams;
import com.gitpitch.git.GRS;
import com.gitpitch.git.GRSService;
import com.gitpitch.services.WebService;
import com.gitpitch.models.MarkdownModel;
import play.Logger;

import javax.inject.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Arrays;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
 * Composable service supporting Markdown includes in
 * the form of:
 * 
 * ---?include=/path/to/some.md
 * +++?include=/path/to/other.md
 */
@Singleton
public class ComposableService {

    private final Logger.ALogger log = Logger.of(this.getClass());

    private final DiskService ds;
    private final WebService ws;

    @Inject
    public ComposableService(DiskService ds,
                                     WebService ws) {

        this.ds = ds;
        this.ws = ws;
    }

    /*
     * Make best effort to process composable presentation
     * includes within PITCHME.md.
     */
    public void compose(PitchParams pp,
                        GRS grs,
                        GRSService grsService) {

        try {

            Path mdPath = ds.asPath(pp, PitchParams.PITCHME_MD);

            String composed = null;
            try (Stream<String> stream = Files.lines(mdPath)) {

                composed = stream.map(md -> {

                    if(md.startsWith(HSLIDE_INCLUDE)) {

                        String included = handleInclude(pp,
                                                        md,
                                                        HSLIDE_INCLUDE,
                                                        grsService,
                                                        grs.getHeaders());
                        if(included != null) {
                            return MarkdownModel.HSLIDE_DELIM_DEFAULT +
                                    NEWLINES + included;
                        } else {
                            return notFound(MarkdownModel.HSLIDE_DELIM_DEFAULT,
                                    includePath(md, HSLIDE_INCLUDE));
                        }
                    } else
                    if(md.startsWith(VSLIDE_INCLUDE)) {

                        String included = handleInclude(pp,
                                                        md,
                                                        VSLIDE_INCLUDE,
                                                        grsService,
                                                        grs.getHeaders());
                        if(included != null) {
                            return MarkdownModel.VSLIDE_DELIM_DEFAULT +
                                    NEWLINES + included;
                        } else {
                            return notFound(MarkdownModel.VSLIDE_DELIM_DEFAULT,
                                    includePath(md, VSLIDE_INCLUDE));
                        }
                    } else {
                        return md;
                    }
                }).collect(Collectors.joining("\n"));

                boolean overwritten = overwrite(pp, mdPath, composed);
                log.debug("compose: overwritten md={}", overwritten);

            } catch (Exception mex) {
                log.warn("Markdown processing ex={}", mex);
                composed = "PITCHME.md could not be parsed.";
            }

        } catch(Exception stex) {

        }
    }

    private String handleInclude(PitchParams pp,
                                 String md,
                                 String includeDelim,
                                 GRSService grsService,
                                 Map<String,String> headers) {

        String included = null;
        String includePath = includePath(md, includeDelim);

        if(FORBIDDEN.contains(includePath)) {
            included = notPermitted(includeDelim, includePath);
        } else {
            included = fetchInclude(pp, includePath,
                    includeDelim, grsService, headers);
        }
        return included;
    }

    private String fetchInclude(PitchParams pp,
                                String includePath,
                                String includeDelim,
                                GRSService grsService,
                                Map<String,String> headers) {

        String fetched = null;

        try {

            if(!isAbsolute(includePath))
                includePath = grsService.raw(pp, includePath, true);

            fetched = ws.fetchText(pp, includePath, headers);

        } catch(Exception fiex) {
            log.warn("fetchInclude: pp={}, error={}", pp, fiex);
        }

        return fetched;
    }

    private String includePath(String md, String includeDelim) {
        try {
            return md.substring(includeDelim.length());
        } catch(Exception ipex) {
            return md;
        }
    }

    private boolean overwrite(PitchParams pp,
                              Path mdPath,
                              String stitched) {
        try {
            Files.write(mdPath, stitched.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING );
            return true;
        } catch(Exception fex) {
            log.warn("overwrite: pp={}, write error={}", pp, fex);
            return false;
        }
    }

    private String notFound(String delim, String path) {
        StringBuffer buf = new StringBuffer(delim);
        buf.append(NEWLINES)
           .append("### GitPitch ?Include")
           .append(NEWLINES).append(".").append(NEWLINES)
           .append(path)
           .append(NEWLINES).append(".").append(NEWLINES)
           .append("Markdown File Not Found");
        return buf.toString();
    }

    private String notPermitted(String delim, String path) {
        StringBuffer buf = new StringBuffer(NEWLINES);
        buf.append("### GitPitch ?Include")
           .append(NEWLINES).append(".").append(NEWLINES)
           .append(path)
           .append(NEWLINES).append(".").append(NEWLINES)
           .append("Path Not Permitted");
        return buf.toString();
    }

    private boolean isAbsolute(String path) {
        return path.startsWith(HTTP);
    }

    public final String HSLIDE_INCLUDE =
        MarkdownModel.HSLIDE_DELIM_DEFAULT + "?include=";
    public final String VSLIDE_INCLUDE =
        MarkdownModel.VSLIDE_DELIM_DEFAULT + "?include=";
    private final String HTTP = "http";
    private final String NEWLINES = "\n\n";
    private final List<String> FORBIDDEN =
        Arrays.asList(PitchParams.PITCHME_MD, "./PITCHME.md");
}
