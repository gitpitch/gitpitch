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
import play.Logger;

import javax.inject.*;
import java.io.IOException;
import java.nio.file.Path;

/*
 * Shell command execution service.
 */
@Singleton
public class ShellService {

    private final Logger.ALogger log = Logger.of(this.getClass());

    public int exec(String op, PitchParams pp, Path wd, String... cmd) {

        int resp = STATUS_UDEF;

        try {

            long startProc = System.currentTimeMillis();

            Process cmdProc = new ProcessBuilder(cmd).inheritIO()
                    .directory(wd.toFile())
                    .start();

            resp = cmdProc.waitFor();
            log.debug("exec: op={}, pp={}, time taken={}", op, pp,
                    (System.currentTimeMillis() - startProc));


        } catch (IOException ioex) {
            log.warn("exec: op={}, pp={}, ioex={}", op, pp, ioex);
            resp = STATUS_IO_EX;
        } catch (InterruptedException iex) {
            log.warn("exec: op={}, pp={}, iex={}", op, pp, iex);
            resp = STATUS_INT_EX;
        }

        return resp;
    }

    private static final int STATUS_OK = 0;
    private static final int STATUS_IO_EX = -777;
    private static final int STATUS_INT_EX = -888;
    private static final int STATUS_UDEF = -999;
}
