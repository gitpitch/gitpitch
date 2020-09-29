# Desktop Offline Publishing

Using the desktop app you can export any slide deck as a PDF document or a PPTX slide deck with just one click.

![Screenshot showing the Desktop Offline Publishing Manager](../_images/gitpitch-desktop-offline-publishing.png)

PDF is a highly portable document format that is great for sharing or printing any slide deck. While PPTX slides can be viewed in Microsoft PowerPoint, LibreOffice, Apple Keynote, and Google Slides.

### Settings

The set of `print` settings let you customize PDF and PPTX output when exporting GitPitch slide decks.
These settings should be enabled or disabled as required in the [PITCHME.yaml](/conventions/pitchme-yaml.md) for your slide deck.

### Defaults

```yaml
print-fragments : true
print-notes     : false
print-footer    : false
print-hires     : true
```

### Details

By default, the following document export controls are activated:

1. Each [markdown fragment](/grid-layouts/fragments.md) and [code fragment](/code/presenting.md) is printed on an individual page.
1. Speaker [notes](/speaker/notes.md) are not printed.
1. Slide [footnotes](/settings/footnote.md) are not printed.
1. PPTX screens are captured in hi-resolution.

When all *print* settings are enabled the following document export controls are activated:

1. Each [markdown fragment](/grid-layouts/fragments.md) and [code fragment](/code/presenting.md) is printed on an individual page.
1. Slide-specific speaker [notes](/speaker/notes.md) are printed on an individual page.
1. Slide [footnotes](/settings/footnote.md) are printed on each page.
1. PPTX screens are captured in hi-resolution.

### Customize

You can override any of the `print` setting values for custom behavior by enabling or disabling any of these settings in the [PITCHME.yaml](/conventions/pitchme-yaml.md) for your slide deck. For example:

```yaml
print-fragments : false
print-notes     : false
print-footer    : false
print-hires     : false
```

