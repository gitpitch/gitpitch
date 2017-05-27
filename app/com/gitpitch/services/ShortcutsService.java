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

    public boolean listFragmentFound(String md) {
        boolean found = false;
        if(md != null) {
            String trimmed = md.trim();
            if(trimmed.startsWith(MarkdownModel.MD_LIST_FRAG_OPEN) &&
                trimmed.endsWith(MarkdownModel.MD_LIST_FRAG_CLOSE)) {
                found = true;
            }
        }
        return found;
    }

    public boolean codeFragmentFound(String md) {
        boolean found = false;
        if(md != null) {
            String trimmed = md.trim();
            if(trimmed.startsWith(MarkdownModel.MD_CODE_FRAG_OPEN)) {
                found = true;
            }
        }
        return found;
    }

    /*
     * Expand shortcut syntax for list fragment with fully expanded
     * HTML comment syntax.
     */
    public String expandListFragment(String md) {
        int fragCloseIdx = md.lastIndexOf(MarkdownModel.MD_LIST_FRAG_CLOSE);
        return md.substring(0, fragCloseIdx) + FRAGMENT;
    }

    /*
     * Expand shortcut syntax for code fragment with fully expanded
     * HTML span syntax with data-code-focus class.
     */
    public String expandCodeFragment(String md) {

        try {
            String codeFragRange = null;
            String codeFragNote = null;

            int codeFragStart =
                md.indexOf(MarkdownModel.MD_CODE_FRAG_OPEN);
            int codeFragEnd =
                md.indexOf(MarkdownModel.MD_CODE_FRAG_CLOSE);

            if(codeFragEnd > codeFragStart) {
                codeFragRange =
                    md.substring(codeFragStart+2, codeFragEnd);
            }

            int codeFragNoteStart =
                md.indexOf(MarkdownModel.MD_CODE_FRAG_NOTE_OPEN);
            int codeFragNoteEnd =
                md.indexOf(MarkdownModel.MD_CODE_FRAG_NOTE_CLOSE);

            if(codeFragNoteEnd > codeFragNoteStart) {
                codeFragNote =
                    md.substring(codeFragNoteStart+1, codeFragNoteEnd);
            }

            if(codeFragRange != null) {
                md = buildCodeFragment(codeFragRange, codeFragNote);
            }

        } catch(Exception cfex) {
        } finally {
            return md;
        }
    }

    /*
     * Construct a HTML fragment for a code fragment based on the
     * reveal-code-focus plugin syntax:
     * <span class="fragment current-only" data-code-focus="1-9">Note</span>
     */
    private String buildCodeFragment(String range, String note) {
        if(note == null) note = "";
        return new StringBuffer(HTML_CODE_FRAG_OPEN)
                .append(range)
                .append(HTML_CODE_FRAG_CLOSE)
                .append(note)
                .append(HTML_CODE_FRAG_NOTE_CLOSE)
                .toString();
    }

    /*
     * Note, the space before opening bracket is intentional
     * so tag injection can happen directly alongside other
     * text in the markdown fragment.
     */
    private static final String FRAGMENT =
        " <!-- .element: class=\"fragment\" -->";
    private static final String HTML_CODE_FRAG_OPEN =
        "<span class=\"fragment current-only\" data-code-focus=\"";
    private static final String HTML_CODE_FRAG_CLOSE = "\">";
    private static final String HTML_CODE_FRAG_NOTE_CLOSE = "</span>";
}
