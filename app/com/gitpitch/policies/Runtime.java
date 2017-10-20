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
package com.gitpitch.policies;

import play.Configuration;
import play.Environment;
import java.util.List;
import javax.inject.*;

/*
 * External dependency manager for the GitPitch server.
 */
@Singleton
public final class Runtime {

    private final Configuration cfg;
    private final Environment env;

    @Inject
    public Runtime(Configuration cfg,
                   Environment env) {
        this.cfg = cfg;
        this.env = env;
    }

    public String config(String cid) {
        return cfg.getString(cid);
    }

    public List configList(String cid) {
        return cfg.getList(cid);
    }

    public List configStringList(String cid) {
        return cfg.getStringList(cid);
    }

    public boolean configBool(String cid) {
        return cfg.getBoolean(cid);
    }

    public boolean configBool(String cid, boolean defaultCfg) {
        return cfg.getBoolean(cid, defaultCfg);
    }

    public boolean isProd() {
        return env.isProd();
    }

}
