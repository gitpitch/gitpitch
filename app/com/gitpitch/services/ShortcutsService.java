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
import com.gitpitch.utils.YAMLOptions;
import org.apache.commons.io.FilenameUtils;
import java.util.*;
import javax.inject.*;
import play.Logger;
import play.Logger.ALogger;

/*
 * PITCHME.md feature-shortcuts support service.
 */
@Singleton
public class ShortcutsService {

    private final Logger.ALogger log = Logger.of(this.getClass());

    public boolean fragmentFound(String md) {
        boolean found = false;
        if(md != null) {
            String trimmed = md.trim();
            if(trimmed.startsWith(MarkdownModel.MD_FRAG_OPEN) &&
                trimmed.endsWith(MarkdownModel.MD_FRAG_CLOSE)) {
                found = true;
            }
        }
        return found;
    }

    /*
     * Expand shortcut syntax for fragment with fully expanded
     * HTML comment syntax.
     */
    public String expandFragment(String md) {
        int fragCloseIdx = md.lastIndexOf(MarkdownModel.MD_FRAG_CLOSE);
        return md.substring(0, fragCloseIdx) + FRAGMENT;
    }

    /*
     * Note, the space before opening bracket is intentional
     * so tag injection can happen directly alongside other
     * text in the markdown fragment.
     */
    private static final String FRAGMENT =
        " <!-- .element: class=\"fragment\" -->";
}
