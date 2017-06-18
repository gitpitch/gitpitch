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

import com.gitpitch.factory.MarkdownModelFactory;
import com.gitpitch.git.*;
import com.gitpitch.git.vendors.*;
import com.gitpitch.models.Markdown;
import com.gitpitch.models.MarkdownModel;
import com.gitpitch.services.*;
import com.gitpitch.policies.CacheTimeout;
import com.gitpitch.policies.Dependencies;
import com.gitpitch.executors.FrontEndThreads;
import com.gitpitch.executors.BackEndThreads;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/*
 * Guice DI injection dependencies for the GitPitch server.
 */
public class Module extends AbstractModule {

    @Override
    public void configure() {
        bind(DiskService.class).asEagerSingleton();
        bind(GitService.class).asEagerSingleton();
        bind(PitchService.class).asEagerSingleton();
        bind(PrintService.class).asEagerSingleton();
        bind(OfflineService.class).asEagerSingleton();
        bind(ShellService.class).asEagerSingleton();
        bind(ImageService.class).asEagerSingleton();
        bind(VideoService.class).asEagerSingleton();
        bind(GISTService.class).asEagerSingleton();
        bind(CodeService.class).asEagerSingleton();
        bind(ShortcutsService.class).asEagerSingleton();
        bind(WebService.class).asEagerSingleton();
        bind(ComposableService.class).asEagerSingleton();
        bind(GRSManager.class).asEagerSingleton();
        bind(GitHub.class).asEagerSingleton();
        bind(GitLab.class).asEagerSingleton();
        bind(BitBucket.class).asEagerSingleton();
        bind(Gitea.class).asEagerSingleton();
        bind(Gogs.class).asEagerSingleton();
        bind(GitBucket.class).asEagerSingleton();
        bind(FrontEndThreads.class).asEagerSingleton();
        bind(BackEndThreads.class).asEagerSingleton();
        bind(Dependencies.class).asEagerSingleton();
        bind(CacheTimeout.class).asEagerSingleton();
        install(new FactoryModuleBuilder().implement(Markdown.class, MarkdownModel.class)
                                          .build(MarkdownModelFactory.class));
    }

}
