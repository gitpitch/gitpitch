# List Behaviors

List behaviors help speakers present list content effectively. By minimizing on-slide distractions you can improve focus and engagement with your audience.

### Built-In

The [list widget](/lists/widget.md) supports the following list-specific behaviors:

[List Widget Behaviors](../_snippets/list-widget-behaviors.md ':include')

### Fade Bullets

The **list-fade-bullets** behavior helps to keep your audience focused on your current speaking point. The behavior is summarized as follows:

1. All previous list items are visibile yet faded.
1. The current list item is clearly visible.
1. All future list items are entirely hidden.

The following sample markdown snippet demonstrates **list-fade-bullets** activation on a list widget:

```markdown
@ol[list-fade-bullets]
1. GitPitch
1. Markdown
1. Modular
1. Git Native
1. Speaker Ready
1. Cross Platform
@ol
```

!> The **list-fade-bullets** behavior works seamlessly with [Grid Synchronization](/grid-layouts/synchronization.md) behavior.

### Squash Bullets

The **list-squash-bullets** behavior converts individual list items in your markdown into discrete talking points on your slide. This behavior gives you the benefits of preparing and maintaining your talking points as lists. But delivers those talking points without the visual ceremony of list content on your slides.

The following sample markdown snippet demonstrates **list-squash-bullets** activation on a list widget:

```markdown
@ul[list-squash-bullets]
- GitPitch
- Markdown
- Modular
- Git Native
- Speaker Ready
- Cross Platform
@ul
```

!> The **list-squash-bullets** behavior works seamlessly with [Grid Synchronization](/grid-layouts/synchronization.md) behavior.

### Hide Bullets

The **list-hide-bullets** behavior provides another way to keep your audience focused on your current speaking point. The behavior is summarized as follows:

1. All previous list items are entirely hidden.
1. The current list item is clearly visible.
1. All future list items are entirely hidden.

The following sample markdown snippet demonstrates **list-hide-bullets** activation on a list widget:

```markdown
@ul[list-hide-bullets]
- GitPitch
- Markdown
- Modular
- Git Native
- Speaker Ready
- Cross Platform
@ul
```
