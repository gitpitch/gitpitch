# Image Widget

?> GitPitch widgets greatly enhance traditional markdown rendering capabilities for slide decks.

The image widget extends traditional markdown image syntax with support for positioning, sizing, transformations, and filters.

### Widget Paths

All paths to image files specified within [PITCHME.md](/conventions/pitchme-md.md) markdown must be relative to the *root directory* of your local working directory or Git repository.

### Widget Syntax

The following markdown snippet demonstrates image widget syntax:

```markdown
![properties...](path/to/image.file)
```

?> The `properties...` list expects a comma-separated list of property `key=value` pairs.

### Image Properties

The image widget supports the following image specific properties:

[Image Widget Properties](../_snippets/image-widget-properties.md ':include')

### Grid Native Props

The *Image Widget* is a [grid native widget](/grid-layouts/native-widgets.md) meaning it also directly supports [grid layouts](/grid-layouts/) properties:

[Grid Widget Properties](../_snippets/grid-widget-properties.md ':include')

### Sample Slide

The following slide demonstrates an image rendered using image widget syntax. The markdown snippet used to create this slide takes advantage of numerous *grid native properties* to position, size, and transform the image on the slide:

![Sample slide demonstrating the image widget](../_images/gitpitch-images-widget.png)

> This sample slide uses additional grid layouts blocks to set a custom [slide background color](/grid-layouts/backgrounds.md) and to render text alongside the sample image.

For details of the current image size limits policy see the [next guide](/images/size-limits-policy.md).

