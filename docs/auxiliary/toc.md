# Table of Contents

GitPitch can generate a table of contents for your slide deck. When activated, the table of contents is accessed by:

1. Pressing the `M` key on your keyboard.
1. Or by clicking on the *burger-menu* icon displayed in the bottom-left corner of your slide deck.

?> Your table of contents can be used to browse content or navigate between slides.

### Activation

To activate a table of contents for your slide deck enable the `toc` setting in your [PITCHME.yaml](/conventions/pitchme-yaml.md):

```yaml
toc: true
```

### Customize

You can use the `@toc[label]` widget to set a custom label for any slide within the table of contents for your slide deck. This widget should be used directly following the delimiter for the slide. For example:

```markdown
@toc[Introduction]

# Hello, World!

---
@toc[About Me]

![BIO](assets/img/profile.png)

---
@toc[Conclusion]

# The End
```

If you activate this feature for a slide deck it is recommended that you specify a custom `@toc[label]` for each slide in that deck. If you do not specify an explicit `@toc[label]` widget on a slide, GitPitch will attempt to auto-generate a label based on the content of the slide but results may vary.
