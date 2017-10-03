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
import com.gitpitch.services.ImageService;
import com.gitpitch.utils.PitchParams;
import com.gitpitch.utils.DelimParams;
import com.gitpitch.utils.YAMLOptions;
import java.util.*;
import javax.inject.*;
import play.Logger;
import play.Logger.ALogger;

/*
 * Slide utility service.
 */
@Singleton
public class SlideService {

    private final Logger.ALogger log = Logger.of(this.getClass());

    private final ImageService imageService;

    @Inject
    public SlideService(ImageService imageService) {
        this.imageService = imageService;
    }

   /*
    * Build basic slide structure including:
    *
    * 1. Clean delimiter and
    * 2. Optionally slide bg-image based on YAMLOptions.hasImageBg.
    */
   public String build(String md,
                       DelimParams dp,
                       PitchParams pp,
                       YAMLOptions yOpts,
                       MarkdownModel mdm) {

    StringBuffer structure = new StringBuffer(mdm.extractDelim(md));
    try {

        String slideType = mdm.isHorizontal(md) ?
               mdm.horizDelim() : mdm.vertDelim();

        structure = new StringBuffer(mdm.extractDelim(md))
                       .append(MarkdownModel.MD_SPACER);

        String yamlBg = null;
        if (yOpts != null && yOpts.hasImageBg()) {
           yamlBg = imageService.buildBackground(pp, yOpts);
        }

        if (yamlBg != null) {
           structure.append(yamlBg).append(MarkdownModel.MD_SPACER);
        }
    } catch(Exception ex) {
     log.warn("build: ex={}", ex);
    }
    return structure.toString();
   }

}
