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
package com.gitpitch.utils;

import com.gitpitch.models.SlideshowModel;
import com.gitpitch.services.DiskService;
import com.gitpitch.utils.GitHub;
import play.Logger;

import java.nio.file.Path;
import java.util.Optional;

/*
 * Rendering model for PITCHME.md markdown.
 */
public class MarkdownRenderer {

    private final Logger.ALogger log = Logger.of(this.getClass());

    private final PitchParams _pp;
    private final Optional<SlideshowModel> _ssm;
    private DiskService diskService;

    private MarkdownRenderer(PitchParams pp,
                             Optional<SlideshowModel> ssm,
                             DiskService diskService) {

        this._pp = pp;
        this._ssm = ssm;
        this.diskService = diskService;
    }

    public static MarkdownRenderer build(PitchParams pp,
                                         Optional<SlideshowModel> ssm,
                                         DiskService diskService) {

        return new MarkdownRenderer(pp, ssm, diskService);
    }

    public PitchParams pp() {
        return _pp;
    }

    public Optional<SlideshowModel> ssm() {
        return _ssm;
    }

    public String gitRawBase() {
        return GitHub.rawAPI(_pp);
    }

    public Path filePath(String filename) {
        return diskService.asPath(_pp, filename);
    }

    public boolean hasLogo() {

        if (_ssm.isPresent()) {

            SlideshowModel model = _ssm.get();
            log.debug("hasLogo: ssm found, hasLogo={}", model.hasLogo());
            return model.hasLogo();

        } else {
            log.debug("hasLogo: ssm not found, so no logo.");
            return false;
        }
    }

    public String fetchLogo() {

        if (_ssm.isPresent()) {

            SlideshowModel model = _ssm.get();
            return model.fetchLogo();

        } else {
            return "#";
        }

    }

}
