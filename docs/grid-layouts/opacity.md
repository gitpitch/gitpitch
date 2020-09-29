# Grid Opacity

The grid *opacity* property is used to control the opacity or transparency of a [layout block](/grid-layouts/drag-and-drop.md). Adjusting the opacity of a block also affects the opacity of all associated  block items.

?> When discussing grid layouts *content-within-blocks* are referred to as *block items*.

### Opacity Activation

The following basic syntax is used to control the *opacity* of a layout block:

```
[drag=width height, drop=x y, opacity=level]
```

Where the `level` option on the `opacity=` property can take any value from `0.0` to `1.0`. A layout block with an opacity value of `0.0` is invisible.

The following sample slide screenshot demonstrates *opacity* controls activated on sample blocks:

![Sample slide demonstrating grid layouts opacity](../_images/gitpitch-grid-layouts-opacity.png)

The first sample block on this slide activates `opacity=0.3` to create an subtle yet eye-catching code themed background for the slide. The second sample block activates `opacity=0.6` to create a semi-transparent blue background. The final sample block overlays the blue background to deliver the final and all important message...GitPitch Rocks!

