# Background Settings

The set of `background` settings let you control the placement, size, and characteristics of a default background image. A default background image is automatically activated for every slide in your deck.

### Background

#### Purpose

The `background` setting lets you specify a default background image for *all slides* within your slide deck.

#### Syntax

```yaml
background : assets/img/bg.png
```

#### Details

?> Settings are activated using the [PITCHME.yaml](/conventions/pitchme-yaml.md) for your slide deck.

The `background` setting supports both relative and absolute paths to an image file. The background image file type can be any JPEG, PNG, SVG or GIF image file.

?> Override default background images on individual slides using [Grid Backgrounds](/grid-layouts/backgrounds.md).

### Background-Size

#### Purpose

The `background-size` setting lets you specify a scaling policy for a default background image.

#### Syntax

```yaml
background-size : auto 90%
```

#### Details

?> Settings are activated using the [PITCHME.yaml](/conventions/pitchme-yaml.md) for your slide deck.

The `background-size` setting can take any value that is valid on the [background-size css property](https://developer.mozilla.org/en-US/docs/Web/CSS/background-size).

### Background-Position

#### Purpose

The `background-position` setting lets you specify a custom position for a default background image.

#### Syntax

```yaml
background-position : left
```

#### Details

?> Settings are activated using the [PITCHME.yaml](/conventions/pitchme-yaml.md) for your slide deck.

The `background-position` setting can take any value that is valid on the [background-position css property](https://developer.mozilla.org/en-US/docs/Web/CSS/background-position). The `background-position` setting is often used in conjuction with the [background-size](#background-size) setting to deliver a final custom background image configuration for a presentation.

### Background-Color

#### Purpose

The `background-color` setting lets you specify a custom color to fill any exposed slide area not covered by a default background image. 

#### Syntax

```yaml
background-color : black
```

#### Details

?> Settings are activated using the [PITCHME.yaml](/conventions/pitchme-yaml.md) for your slide deck.

The `background-color` setting can take any value that is valid on the [background-color css property](https://developer.mozilla.org/en-US/docs/Web/CSS/background-color). The `background-color` setting is often used in conjuction with the [background-size](#background-size) setting to deliver a final custom background image configuration for a presentation.

### Background-Repeat

#### Purpose

The `background-repeat` setting lets you specify a custom image repeat policy to be applied to a default background image.

#### Syntax

```yaml
background-repeat : repeat-x
```

#### Details

?> Settings are activated using the [PITCHME.yaml](/conventions/pitchme-yaml.md) for your slide deck.

The `background-repeat` setting can take any value that is valid on the [background-repeat css property](https://developer.mozilla.org/en-US/docs/Web/CSS/background-repeat).

### Background-Opacity

#### Purpose

The `background-opacity` setting lets you specify a custom opacity filter to be applied to a default background image. 

#### Syntax

```yaml
background-opacity : 75
```

#### Details

?> Settings are activated using the [PITCHME.yaml](/conventions/pitchme-yaml.md) for your slide deck.

The `background-opacity` setting must take a value defined within the following series of permissible values: 10, 15, 20, 25, 30,…,90, 95, 100. Each value represents a percentage of full image transparency at 100%.

