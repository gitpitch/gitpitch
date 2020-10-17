# Markdown Includes

A [markdown injection widget](/modular-markdown/injection.md) provides a simple way to inject a **reusable markdown snippet** on any slide. Markdown includes provide a simple way to inject **reusable slides** into any slide deck.

?> Markdown includes are one more way GitPitch supports modular design for slide decks.

### Include Paths

All paths on the `---?incude=` slide delimiter specified within [PITCHME.md](/conventions/pitchme-md.md) markdown must be relative to the root directory of your local working directory or Git repository.

### Include Syntax

Special `--?include=` delimiter syntax can be used to import modular slide content directly into your slide deck:

```markdown
---?include=path/to/PITCHME.md
```

The slide contents within the `path/to/PITCHME.md` file is automaticaly inlined into your markdown file at the point where you declared the delimiter. There are no limits on the number of `---?include=` slide delimiters you can use to create a slide deck.

### Sample Deck

Consider the following sample project files and directories. Imagine these files have been developed for an introductory course on *functional* programming:

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

The top-level `PITCHME.md` markdown file can then use the `---?include=` slide delimiter to aggregate the slide content from each of the topic specific files to deliver the complete slide deck:

```markdown
[drag=100, drop=center]
# Functional Programming

---?include=topics/ast/PITCHME.md

---?include=topics/fuctions/PITCHME.md

---?include=topics/immutability/PITCHME.md

---?include=topics/recursion/PITCHME.md

---

[drag=100, drop=center, fit=2.1]
# That's all folks!

```

This sample PITCHME.md file would result in a slide deck that contains the following slides:

- Every slide defined in this sample [PITCHME.md](/conventions/pitchme-md.md) file plus
- Every slide defined in the `topics/ast/PITCHME.md` file plus
- Every slide defined in the `topics/functions/PITCHME.md` file plus
- Every slide defined in the `topics/immutability/PITCHME.md` file plus
- Every slide defined in the `topics/recursion/PITCHME.md` file

Taking a modular approach to topic specific content by maintaining dedicated markdown files per topic is considered a best practice. Simplifying development, team collaboration, and maintenance over time.

### Alt Structure

This section presents an alternative file and directory structure for our example *functional* programming course content. Notice how the content under the `topics` directory has been restructured as a series of markdown files per topic all maintained within a single directory:

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
    ├── ast.md
    ├── functions.md
    ├── immutability.md
    └── recursion.md
```

Again the top-level `PITCHME.md` markdown file can then use the `---?include=` slide delimiter to aggregate the slide content from each of the topic specific files:

```markdown
[drag=100, drop=center]
# Functional Programming

---?include=topics/ast.md

---?include=topics/fuctions.md

---?include=topics/immutability.md

---?include=topics/recursion.md

---

[drag=100, drop=center, fit=2.1]
# That's all folks!

```

Before deciding which approach is best for you we recommend reading the [Modular Slide Decks Guide](/modular-markdown/modular-decks.md).

