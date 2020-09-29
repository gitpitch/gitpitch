# The Theme Template

The **Theme Template** is the base theme for all GitPitch slide decks. The best way to manipulate the template is to use the [GitPitch Theme Builder](/theme/builder.md). The theme template has been crafted to deliver the following benefits:

1. Attractive default styling for GitPitch slide decks out-of-the-box.
1. Simple, fast customization of slide background, text fonts, colors, and more.
1. With consistent rendering everywhere - online, offline, PDF, and PowerPoint.

### Template Defaults

By default, the *theme template* is configured by the following set of [PITCHME.yaml](/conventions/pitchme-yaml.md) setting values:

```yaml
theme-background: [ "#FFF" ]
theme-headline  : [ "Montserrat", "#232B2B", "none", "#E67E22", "100", "130", "15" ]
theme-byline    : [ "Montserrat", "#232B2B", "none", "#E67E22", "100", "120", "15" ]
theme-text      : [ "Ubuntu", "#000", "none", "#E67E22", "100", "110", "15" ]
theme-links     : [ "#5289F7", "#5254F7" ]
theme-code      : [ "Source Code Pro", "100", "120" ]
highlight       : atom-one-light
theme-controls  : [ "#C0C0C0" ]
```

These default *theme template* settings are *implicitly* activated within your PITCHME.yaml. In order to override these defaults and create a custom look-n-feel for your slide deck, you can:

1. Use the [GitPitch Theme Builder](/theme/builder.md) OR
1. Copy and paste these default property values into your PITCHME.yaml.
1. Then update some or all of these settings with your own custom values.

The following sections introduce each of the **theme-\*** settings shown above. Each section includes a discussion of the set of valid values that can be activated on the corresponding property.

### Background Setting

The `theme-background` setting lets you set a custom slide background color for your deck.

```yaml
theme-background : [ "#FFF" ]
```

The `theme-background` property takes a single string value within square-brackets array syntax. The following formats are supported by this property value:

- [CSS Background Named Color](https://developer.mozilla.org/en-US/docs/Web/CSS/background-color)
- [CSS Background Hex Code Color](https://developer.mozilla.org/en-US/docs/Web/CSS/background-color)
- [CSS Background RGB Code Color](https://developer.mozilla.org/en-US/docs/Web/CSS/background-color)
- [CSS Background HSLA Code Color](https://developer.mozilla.org/en-US/docs/Web/CSS/background-color)
- [CSS Background Linear Gradient](https://developer.mozilla.org/en-US/docs/Web/CSS/linear-gradient)

The following snippet demonstrates a series of sample values for the `theme-background` property:

```yaml
theme-background : [ "green" ]

theme-background : [ "#5289F7" ]

theme-background : [ "rgb(255, 255, 128)" ]

theme-background : [ "hsla(50, 33%, 25%, .75)" ]

theme-background : [ "linear-gradient(#e66465, #9198e5)" ]

theme-background : [ "linear-gradient(180deg, white 50%, black 50%)" ]

```

> Note, your own **PITCHME.yaml** should define this property once and only once.

### Headline Setting

The `theme-headline` setting lets you set custom styling for markdown **H1** and **H2** heading content.

```yaml
theme-headline : [ "Montserrat", "#232B2B", "none", "#E67E22", "100", "130", "15" ]
```

The `theme-headline` property takes a series of comma-separated string values within square-brackets array syntax. These values can be generalized as follows:

```yaml
theme-headline : [ "family", "color", "transform", "emphasis", "scale", "line-height", "padding" ]
```

These values support the following custom stylings:

- **family** - set a custom [font family](#template-font-families) for headline content
- **color** - set a custom [text color](#template-text-colors) for headline content
- **transform** - set a custom [text transform](#template-text-transforms) for headline content
- **emphasis** - set a custom [text color](#template-text-colors) for headline bold and italic content
- **scale** - set a custom font scale (% value) for headline content
- **line-height** - set a custom font line-height (% value) for headline content
- **padding** - set a custom vertical padding (px value) for headline content

### Byline Setting

The `theme-byline` setting lets you set custom styling for markdown **H3** and **H4** heading content.

```yaml
theme-byline : [ "Montserrat", "#232B2B", "none", "#E67E22", "100", "120", "15" ]
```

Tha `theme-byline` property takes a series of comma-separated string values within square-brackets array syntax. These values can be generalized as follows:

```yaml
theme-byline : [ "family", "color", "transform", "emphasis", "scale", "line-height", "padding" ]
```

These values support the following custom stylings:

- **family** - set a custom [font family](#template-font-families) for byline content
- **color** - set a custom [text color](#template-text-colors) for byline content
- **transform** - set a custom [text transform](#template-text-transforms) for byline content
- **emphasis** - set a custom [text color](#template-text-colors) for byline bold and italic content
- **scale** - set a custom font scale (% value) for byline content
- **line-height** - set a custom font line-height (% value) for byline content
- **padding** - set a custom vertical padding (px value) for byline content

### Text Setting

The `theme-text` setting lets you set custom styling for markdown regular text content.

```yaml
theme-text : [ "Ubuntu", "#000", "none", "#E67E22", "100", "110", "15" ]
```

The `theme-text` property takes a series of comma-separated string values within square-brackets array syntax. These values can be generalized as follows:

```yaml
theme-text : [ "family", "color", "transform", "emphasis", "scale", "line-height", "padding" ]
```

These values support the following custom stylings:

- **family** - set a custom [font family](#template-font-families) for text content
- **color** - set a custom [text color](#template-text-colors) for text content
- **transform** - set a custom [text transform](#template-text-transforms) for text content
- **emphasis** - set a custom [text color](#template-text-colors) for text bold and italic content
- **scale** - set a custom font scale (% value) for text content
- **line-height** - set a custom font line-height (% value) for text content
- **padding** - set a custom vertical padding (px value) for text content

### Links Setting

The `theme-links` setting lets you set custom styling for markdown hyperlinks.

```yaml
theme-links : [ "#5289F7", "#5254F7" ]
```

The `theme-links` property takes two comma-separated string values within square-brackets array syntax. These values can be generalized as follows:

```yaml
theme-links : [ "color", "hover" ]
```

These values support the following custom stylings:

- **color** - set a custom [text color](#template-text-colors) for hyperlink content
- **hover** - set a custom [text color](#template-text-colors) for hyperlink content in hover state

### Code Setting

The `theme-code` setting lets you set a custom styling for code blocks.

```yaml
theme-code : [ "Source Code Pro", "100", "120" ]
```

The `theme-code` property takes three comma-separated string values within square-brackets array syntax. These values can be generalized as follows:

```yaml
theme-links : [ "family", "scale", "line-height" ]
```

These values support the following custom stylings:

- **family** - set a custom monospaced font family for code blocks
- **scale** - set a custom monospaced font scale (% value) for code blocks
- **line-height** - set a custom font line-height (% value) for code blocks

The following monospaced font families for code are supported:

- Source Code Pro
- Inconsolata
- Iosevka
- [Fira Code](https://github.com/tonsky/FiraCode)
- [Victor Mono](https://rubjo.github.io/victor-mono)
- Victor Mono Italic

Note, both the Fira Code and Victor Mono fonts support *symbol ligatures* that are custom designed for rendering code with a little flair. The following snippet demonstrates sample usage of this property:

```yaml
theme-code : [ "Source Code Pro" ]

theme-code : [ "Inconsolata" ]

theme-code : [ "Fira Code" ]
```

> Note, your own **PITCHME.yaml** should define this property once and only once.

### Controls Setting

The `theme-controls` setting lets you set a custom color for the navigation controls, menu icon, and progress bar for your slide deck.

```yaml
theme-controls : [ "#C0C0C0" ]
```

### Template Font Families

The theme template has built-in support for a wide range of [Google Fonts](https://fonts.google.com) These fonts can be used to customize and enhance the appearance of any slide deck.

The following font families are available for [headline](#headline-setting), [byline](#byline-setting), and [text](#text-setting) content:

- Lato
- Montserrat
- Nunito Sans
- Open Sans
- Oswald
- Poppins
- Prompt
- Quicksand
- Raleway
- Roboto
- Source Sans Pro
- Ubuntu
- Work Sans

Beyond these general purpose fonts there are also a number of banner fonts. These banner fonts are a little more dramatic and stylized so are best suited to [headline](#headline-setting) and [byline](#byline-setting) content. While not prohibited, banner fonts are typically not recommended for regular [text](#text-setting) content.

- Bubblegum
- Concert One
- Righteous

> Note, all font family names are case-sensitive and must be used exactly as shown above.

If you are not a professional designer or you are simply looking for help choosing a professional looking font pairing for your slide deck then check out the **Popular Pairings** section for each font on the *Google Fonts* website. For example, see the popular pairing section for the [Montserrat Font](https://fonts.google.com/specimen/Montserrat).

### Template Text Colors

Theme template colors for [headline](#headline-setting), [byline](#byline-setting), and [text](#text-setting) content are powered by standard [CSS Colors](https://developer.mozilla.org/en-US/docs/Web/CSS/color_value). Including support for:

- Color Keywords
- Color RGB Hex Values

If you are not a professional designer or you are simply looking for help choosing a professional looking color palette for your slide deck you might want to check out some of these free online tools:

- [Color Space](https://mycolor.space)
- [Color Drop]https://colordrop.io)
- [Material Design](https://material.io/tools/color)

> Note, GitPitch has no affiliation with these services, links are simply provided as helpful tips.

### Template Text Transforms

Theme template text transforms for [headline](#headline-setting), [byline](#byline-setting), and [text](#text-setting) content are powered by standard [CSS Text Transforms](https://developer.mozilla.org/en-US/docs/Web/CSS/text-transform). The set of supported text transforms are shown here:

- capitalize
- uppercase
- lowercase
- none

> Note, all text transform names are case-sensitive and must be used exactly as shown above.

These transforms are best suited to [headline](#headline-setting) and [byline](#byline-setting) content.

### Template Debug Tips

Being aware of the following tips should help quickly resolve most problems related to activating custom values on theme template settings.

#### Tip #1

If you set custom values on `theme-` settings but still see default styles when viewing your slide deck this typically indicates a syntax error in your PITCHME.yaml file.

#### Tip #2

The single most common syntax error when setting custom values on `theme-` settings is forgetting to specify property values as quoted strings.

