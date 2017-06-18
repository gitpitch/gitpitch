[![GitPitch](https://gitpitch.com/assets/badge.svg)](https://gitpitch.com/gitpitch/gitpitch/master) [![Build Status](https://semaphoreci.com/api/v1/onetapbeyond/gitpitch/branches/master/shields_badge.svg)](https://semaphoreci.com/onetapbeyond/gitpitch)

# Markdown Presentations For Everyone on GitHub, GitLab, Bitbucket, Gitea, Gogs, and GitBucket

#### WEBSITE: [www.gitpitch.com](https://gitpitch.com) | HOW-TO : [GitPitch Wiki](https://github.com/gitpitch/gitpitch/wiki) | TWITTER: [@gitpitch](https://twitter.com/gitpitch)

- [What is GitPitch?](#what-is-gitpitch)
- [Is GitPitch for you?](#is-gitpitch-for-you)
- [How does GitPitch work?](#how-does-gitpitch-work)
- [Compelling Presentations](#gitpitch-slideshow-presentations-are-compelling)
- [Customizable Presentations](#gitpitch-slideshow-presentations-are-customizable)
- [Modular Presentations](#gitpitch-slideshow-presentations-are-modular)
- [Code-Aware Presentations](#gitpitch-slideshow-presentations-are-code-aware)
- [Social Presentations](#gitpitch-slideshow-presentations-are-social)
- [Speaker-Ready Presentations](#gitpitch-slideshow-presentations-are-speaker-ready)
- [Presentations That Just Work](#gitpitch-slideshow-presentations-just-work)
- [An Open Source Project](#gitpitch---an-open-source-project)

## What is GitPitch?

GitPitch is a service that turns **[PITCHME.md](https://gitpitch.com/#gitpitch-pitchme-markdown)** markdown into online and offline, interactive slideshows. You can use it to promote, pitch or present anything from designs and best practices, to code snippets and complete frameworks.

Each slideshow presentation is made instantly available online just as soon as you git-commit and push **PITCHME.md** on any branch within a public [GitHub, GitLab, Bitbucket, Gitea, Gogs, or GitBucket](https://github.com/gitpitch/gitpitch/wiki/Git-Repo-Services) repo.

![URL](images/gp-url.jpg)

GitPitch is an open source project so you can fork, clone, and modify the source. You can find detailed build instructions [here](https://github.com/gitpitch/gitpitch/wiki/Server-Build-Instructions). You can also submit bug, feature and pull-requests on this repo. If you like this project please show your support with a [GitHub star](https://github.com/gitpitch/gitpitch/stargazers). Much appreciated.

## Is GitPitch for you?

If you ever find yourself needing to present a concept, design, library, integration, framework, or even course work:

- To colleagues, clients or customers
- To students
- Or at meetups or conferences

Then GitPitch is for you. Simply capture your ideas in Markdown and let GitPitch automatically turn those ideas into compelling, responsive, online and offline slideshow presentations.

## How does GitPitch work?

GitPitch presentations are powered by the amazing [reveal.js](https://github.com/hakimel/reveal.js) presentation framework. But with GitPitch there is nothing to download. All you need is your favorite text editor. And an account on GitHub, GitLab, or Bitbucket. Or your instance of GitHub Enterprise, Gitea, Gogs, or GitBucket.

![TERMINAL](images/gp-terminal.png)

No more Keynote. No more PowerPoint. Just Markdown. Then git-commit on any branch within a public GitHub, GitLab, Bitbucket, or a private Gitea, Gogs, GitBucket repo.

## GitPitch slideshow presentations are compelling

![SLIDESHOW](http://res.cloudinary.com/gitpitch/image/upload/github-integration/gp-slideshow-theme-style.png)

Each slideshow presentation is beautifully rendered, fully responsive, and highly interactive with a rich set of features including:

- [Markdown](https://github.com/gitpitch/gitpitch/wiki/Slide-Markdown) slides
- [Code](https://github.com/gitpitch/gitpitch/wiki/Code-Slides) and [GIST](https://github.com/gitpitch/gitpitch/wiki/GIST-Slides) slides
- [Image](https://github.com/gitpitch/gitpitch/wiki/Image-Slides) and [Video](https://github.com/gitpitch/gitpitch/wiki/Video-Slides) slides
- [Math Notation](https://github.com/gitpitch/gitpitch/wiki/Math-Notation-Slides) and [Chart](https://github.com/gitpitch/gitpitch/wiki/Chart-Slides) slides
- [Fragment](https://github.com/gitpitch/gitpitch/wiki/Fragment-Slides) slides

See the [Kitchen Sink Slideshow Presentation](https://gitpitch.com/gitpitch/kitchen-sink) for a live demonstration of GitPitch slideshow features.

## GitPitch slideshow presentations are customizable

Choose between six distinct [visual themes](https://github.com/gitpitch/gitpitch/wiki/Theme-Setting):

![SLIDESHOW](http://res.cloudinary.com/gitpitch/image/upload/github-integration/gp-slideshow-night-style.png)

Or further [customize the look and feel](https://github.com/gitpitch/gitpitch/wiki/Slideshow-Settings) of your slideshow presentations using background images, your own logo and even [custom css](https://github.com/gitpitch/gitpitch/wiki/Slideshow-Custom-CSS).

![SLIDESHOW](http://res.cloudinary.com/gitpitch/image/upload/github-integration/gp-slideshow-bg-style.png)

## GitPitch slideshow presentations are modular

Create and deliver content and course materials as a series of slideshow presentations using [modular markdown](https://github.com/gitpitch/gitpitch/wiki/Modular-Markdown) and [shared assets](https://github.com/gitpitch/gitpitch/wiki/Asset-Sharing) (css, images, etc.) in a single git repo.


## GitPitch slideshow presentations are code-aware

![CODE-PRESENTING](images/gp-code-presenting-preview.gif)

Step-through static-code blocks or the complete source-code from any file in the repo, line-by-line or section-by-section, from directly within any slideshow presentation using [code-presenting](https://github.com/gitpitch/gitpitch/wiki/Code-Presenting).

## GitPitch slideshow presentations are social

GitPitch slideshow presentations are designed for sharing. You can:

- View any GitPitch presentation at its public URL
- [Promote](https://github.com/gitpitch/gitpitch/wiki/Slideshow-GitHub-Badge) any GitPitch presentation using a GitHub badge
- [Embed](https://github.com/gitpitch/gitpitch/wiki/Slideshow-Embedding) any GitPitch presentation within a blog or website
- [Share](https://github.com/gitpitch/gitpitch/wiki/Slideshow-Sharing) any GitPitch presentation on Twitter, LinkedIn, etc
- [Print](https://github.com/gitpitch/gitpitch/wiki/Slideshow-Printing) any GitPitch presentation as PDF document
- [Download and present](https://github.com/gitpitch/gitpitch/wiki/Slideshow-Offline) any GitPitch presentation offline

Support for these social features can be found in the Web page footer of each slideshow presentation.

## GitPitch slideshow presentations are speaker-ready

GitPitch supports a [speaker notes window](https://github.com/gitpitch/gitpitch/wiki/Speaker-Notes-Window) that can be opened for any GitPitch [offline presentation](https://github.com/gitpitch/gitpitch/wiki/Slideshow-Offline). The speaker notes window shows the current slide, provides a preview of the upcoming slide, and also includes a timer that helps keep track of time once you launch the presentation.

![SPEAKER-NOTES](images/gp-speaker-notes.png)

## GitPitch slideshow presentations just work

GitPitch requires no sign-up. And no configuration. Just add **PITCHME.md** ;)

The [GitPitch Wiki](https://github.com/gitpitch/gitpitch/wiki) provides a detailed `How-To` that walks you through getting started, building, customizing, and sharing your GitPitch slideshow presentations.

## GitPitch - An Open Source Project

The GitPitch server is a [Play Framework 2.5.x](https://playframework.com/) Web application open-sourced under an MIT License. This means you can fork, clone, and modify the source and build and test the server on your local machine.

You can find detailed build instructions [here](https://github.com/gitpitch/gitpitch/wiki/Server-Build-Instructions). You can also submit bug, feature and pull-requests on this repo. If you like this project please show your support with a [GitHub star](https://github.com/gitpitch/gitpitch/stargazers). Much appreciated.

GitPitch has been designed to follow standard Play Framework conventions. In order to understand the layout of this project see the following article, [Anatomy of a Play Application](https://playframework.com/documentation/2.5.x/Anatomy).

## MIT License

Copyright (c) 2016 David Russell

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
