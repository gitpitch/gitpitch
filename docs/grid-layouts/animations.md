# Grid Animations

The grid *animate* property is used to activate a slide animation for a [layout block](grid-layouts/drag-and-drop.md). The animation applies to the block itself and all associated block items.

?> When discussing grid layouts *content-within-blocks* are referred to as *block items*.

### Animate Activation

The following basic syntax is used to activate a *animation* behavior on a layout block:

```
[drag=width height, drop=x y, animate=type _speed]
```

Where the `type` option on the `animate=` property can take one of the following supported animation types:

- slideup
- slidedown
- slideleft
- slideright
- bounceup
- bouncedown
- bounceleft
- bounceright
- speedleft
- speedright
- tada
- flip
- fadein
- fadeout

?> The `_speed` option is optional. When specified it accepts a value of *slower* or *faster*.

Here is a short video clip that demonstrates block animation in action:

![Sample slide demonstrating grid layouts animations](../_images/gitpitch-grid-layouts-animations.gif)

