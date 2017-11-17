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

    public String process(String md, PitchParams pp, YAMLOptions yOpts) {

        String processed = md;

        try {
            int faCycles = 5; // Prevent infinite loop.
            while(fontAwesomeFound(processed) && faCycles > 0) {
                String expanded = expandFontAwesome(processed);
                faCycles = processed.equals(expanded) ? 0 : (faCycles-1);
                processed = expanded;
            }
        } catch(Exception ex) {}

        return processed;
    }

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
            if(md.startsWith(MarkdownModel.MD_CODE_FRAG_OPEN)) {
                found = true;
            }
        }
        return found;
    }

    public boolean titleHintFound(String md) {
      boolean found = false;
      if(md != null) {
          if(md.startsWith(MarkdownModel.MD_TITLE_HINT_OPEN)) {
              found = true;
          }
      }
      return found;
    }

    public boolean fontAwesomeFound(String md) {
        boolean found = false;
        if(md != null) {
            if(md.contains(MarkdownModel.MD_FA_OPEN)) {
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
                md.lastIndexOf(MarkdownModel.MD_CODE_FRAG_NOTE_CLOSE);

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
     * Expand title-hint shortcut syntax into corresponding
     * HTML hidden span for detection by menu.js.
     */
    public String expandTitleHint(String md) {

        try {

            int hintStart =
                md.indexOf(MarkdownModel.MD_TITLE_HINT_OPEN) +
                    MarkdownModel.MD_TITLE_HINT_OPEN.length();
            int hintEnd =
                md.lastIndexOf(MarkdownModel.MD_TITLE_HINT_CLOSE);

            if(hintEnd > hintStart) {
                String hint = md.substring(hintStart, hintEnd);
                md =  new StringBuffer(TITLE_HINT_SPAN_OPEN)
                          .append(hint)
                          .append(TITLE_HINT_SPAN_CLOSE)
                          .toString();
            }

        } catch(Exception cfex) {
        } finally {
            return md;
        }
    }

    /*
     * Expand shortcut syntax for font-awesome with fully expanded
     * HTML span syntax with optional text.
     */
    public String expandFontAwesome(String md) {

        String mdLeft = "";
        String faName = "";
        String faNote = "";
        String mdRight = "";
        String expandedFA = "";

        try {
            int faOpen = md.indexOf(MarkdownModel.MD_FA_OPEN);
            int faClose = md.indexOf(MarkdownModel.MD_FA_CLOSE);

            if(faClose > faOpen) { /* faName exists */

                mdLeft = md.substring(0, faOpen);
                faName =
                    md.substring(faOpen + MarkdownModel.MD_FA_OPEN.length(),
                                 faClose);

                String rmd = md.substring(faClose + 1);

                int faNoteOpen = rmd.indexOf(MarkdownModel.MD_FA_NOTE_OPEN);
                int faNoteClose = rmd.indexOf(MarkdownModel.MD_FA_NOTE_CLOSE);

                if(faNoteOpen == 0) { /* faNote immediately follows faName */

                    faNote =
                        rmd.substring(MarkdownModel.MD_FA_NOTE_OPEN.length(),
                            faNoteClose);
                    mdRight = rmd.substring(faNoteClose + 1);
                } else {
                    mdRight = rmd;
                }

                expandedFA = buildFontAwesome(faName, faNote);
            }

        } catch(Exception ex) {
        } finally {
            return mdLeft + expandedFA + mdRight;
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
     * Construct a HTML fragment for an FA icon.
     */
    private String buildFontAwesome(String font, String text) {
        if(text == null) text = "";
        return new StringBuffer(HTML_FONT_FRAG_OPEN)
                .append(font)
                .append(HTML_FONT_FRAG_CLOSE)
                .append(text)
                .append(HTML_FONT_FRAG_TEXT_CLOSE)
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
        "<span class=\"code-presenting-annotation fragment current-only\" data-code-focus=\"";
    private static final String HTML_CODE_FRAG_CLOSE = "\">";
    private static final String HTML_CODE_FRAG_NOTE_CLOSE = "</span>";
    private static final String TITLE_HINT_SPAN_OPEN =
        "<span class=\"menu-title\" style=\"display: none\">";
    private static final String TITLE_HINT_SPAN_CLOSE = "</span>";
    private static final String HTML_FONT_FRAG_OPEN = "<i class=\"fa fa-";
    private static final String HTML_FONT_FRAG_CLOSE = "\" aria-hidden=\"true\"> ";
    private static final String HTML_FONT_FRAG_TEXT_CLOSE = "</i>";

}
