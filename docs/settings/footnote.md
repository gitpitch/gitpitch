# Footnote Setting

### Purpose

The `footnote` setting lets you display a custom footnote on every slide within your presentation.

### Syntax

```yaml
footnote : "© 2021 One Tap Ltd."
```

### Details

?> Settings are activated using the [PITCHME.yaml](/conventions/pitchme-yaml.md) for your slide deck.

Footnotes are visible on-screen when your slide deck is being viewed in regular and fullscreen mode.

### HTML

The `footnote` setting also supports the use of arbitrary HTML fragments alongside plain text. This feature supports:

- [Interactive Footnotes](#interactive-footnotes)
- [Custom Footnote Styling](#custom-footnote-styling)

### Interactive

Links can be embedded inside foonotes to help direct your audience to specific content within your presentation or to some external location for further reading, or product, service, or conference promotion.

For example, here is a footnote acting as quick link to a special offer on slide *10* within the presentation itself:

```yaml
footnote : "<a href='#/10'>ACME Dynamite Free Trial!</a>"
```

Here is another example, this time linking to an external webpage:

```
footnote : "GitPitch Features - <a href='https://gitpitch.com/features'>Learn More</a>"
```

### Styling

If you inject custom HTML into your footnote you are then responsible for styling it.

```yaml
footnote : "<a style='text-decoration: none; color: white;' href='#/12'>Learn More</a>"
```

> In this example *text-decoration* style on the link has been disabled. Recommended, but optional.

An alternative to adding styling rules directly within the footnote is to define a CSS class for your footnote using [custom CSS](/theme/custom-css.md). Then simply apply the custom style when defining your footnote.
