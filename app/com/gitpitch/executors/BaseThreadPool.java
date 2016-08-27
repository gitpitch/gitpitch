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
package com.gitpitch.executors;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import play.Logger;
import play.Logger.ALogger;
import play.inject.ApplicationLifecycle;

import javax.inject.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/*
 * Server thread pool for async operations.
 */
public abstract class BaseThreadPool {

    private final Logger.ALogger log = Logger.of(this.getClass());

    public final ExecutorService POOL;
    private final String poolName;
    private final int poolSize;
    private final ApplicationLifecycle lifecycle;

    protected BaseThreadPool(String poolName,
                             int poolSize,
                             ApplicationLifecycle lifecycle) {

        this.poolName = poolName;
        this.poolSize = poolSize;
        this.lifecycle = lifecycle;

        ThreadFactory namedTF =
                new ThreadFactoryBuilder().setNameFormat(poolName).build();

        POOL = Executors.newFixedThreadPool(poolSize, namedTF);

        lifecycle.addStopHook(() -> {

            try {
                POOL.shutdownNow();
            } catch (SecurityException ex) {
                log.warn("{}: thread pool shutdown ex={}", ex);
            } finally {
                log.info("{}: thread pool shutdown.", poolName);
            }
            return CompletableFuture.completedFuture(null);
        });
    }

}