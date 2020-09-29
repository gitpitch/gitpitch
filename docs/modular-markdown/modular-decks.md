# Modular Decks

This guide demonstrates the use of modular markdown to deliver modular slide decks. A modular slide deck is a deck that can be shared in it's complete form or as a series of *mini-decks*.

# Mini Decks

Lets revisit the example project described in the [Markdown Includes Guide](/modular-markdown/includes.md). Our introductory course on *functional* programming used the following project files and directories:

```
.
├── PITCHME.md
├── PITCHME.yaml
├── assets
│   ├── css
│   │   └── PITCHME.css
│   ├── fonts
│   │   └── Kaffee.woff2
│   └── img
│       ├── design.jpg
│       └── logo.png
└── topics
    ├── ast
    │   └── PITCHME.md
    ├── functions
    │   └── PITCHME.md
    ├── immutability
    │   └── PITCHME.md
    └── recursion
        └── PITCHME.md
```

> The slides for each topic covered by this course are developed within dedicated `PITCHME.md`.

Thanks to [GitPitch Conventions](/conventions/pitchme-md.md) any **PITCHME.md** markdown file found within a Git repository is automatically rendered by GitPitch Desktop and the GitPitch Cloud as a modern, responsive slideshow presentation.

Given our sample project used dedicated **PITCHME.md** for each course topic each of those topic specific files can be viewed and shared as a standalone slide deck aka. a mini-deck.

Assuming this project is published under the `acmecorp/courseware` repo then the following top-level slide decks and mini-decks can be viewed and [shared online](/cloud/):

<!-- tabs:start -->

#### ** GitHub Decks **

```
# The complete Functional Programming Course slide deck.
https://gitpitch.com/acmecorp/courseware

# The Functional Programming AST topic mini-deck.
https://gitpitch.com/acmecorp/courseware?p=topics/ast

# The Functional Programming Functions topic mini-deck.
https://gitpitch.com/acmecorp/courseware?p=topics/functions

# The Functional Programming Immutability topic mini-deck.
https://gitpitch.com/acmecorp/courseware?p=topics/immutability

# The Functional Programming Recursion topic mini-deck.
https://gitpitch.com/acmecorp/courseware?p=topics/recursion
```

#### ** GitLab Decks **

```
# The complete Functional Programming Course slide deck.
https://gitpitch.com/acmecorp/courseware?grs=gitlab

# The Functional Programming AST topic mini-deck.
https://gitpitch.com/acmecorp/courseware?grs=gitlab&p=topics/ast

# The Functional Programming Functions topic mini-deck.
https://gitpitch.com/acmecorp/courseware?grs=gitlab&p=topics/functions

# The Functional Programming Immutability topic mini-deck.
https://gitpitch.com/acmecorp/courseware?grs=gitlab&p=topics/immutability

# The Functional Programming Recursion topic mini-deck.
https://gitpitch.com/acmecorp/courseware?grs=gitlab&p=topics/recursion
```

#### ** Bitbucket Decks **

```
# The complete Functional Programming Course slide deck.
https://gitpitch.com/acmecorp/courseware?grs=bitbucket

# The Functional Programming AST topic mini-deck.
https://gitpitch.com/acmecorp/courseware?grs=bitbucket&p=topics/ast

# The Functional Programming Functions topic mini-deck.
https://gitpitch.com/acmecorp/courseware?grs=bitbucket&p=topics/functions

# The Functional Programming Immutability topic mini-deck.
https://gitpitch.com/acmecorp/courseware?grs=bitbucket&p=topics/immutability

# The Functional Programming Recursion topic mini-deck.
https://gitpitch.com/acmecorp/courseware?grs=bitbucket&p=topics/recursion
```

<!-- tabs:end -->

Used in conjunction with [Cloud Publishing](/cloud/) structuring slide decks in this way gives you great flexibility and control over how and when your published content is accessed by your audience.

