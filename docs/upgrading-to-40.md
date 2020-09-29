# Upgrading to GitPitch 4.0

GitPitch 4.0 was released in the autumn, 2020. The 4.0 release introduces many exciting new features including a brand new layout system for GitPitch markdown presentations called [Grid Layouts](/grid-layouts/README.md). This new layout system is now the default layout system for GitPitch slide decks.

!> All new GitPitch slide decks should use the grid layouts system and 4.0 syntax per these docs.

### 3.0 Deprecated Syntax

The old layout system called [Snap Layouts](/auxiliary/snap-layouts.md) is now *deprecated*. Old snap layouts syntax is not supported within 4.0 slide decks.

### 3.0 Deprecated Decks

In order to work with 3.0 slide decks using the new [desktop app](/desktop/README.md) you must activate the following setting in the [PITCHME.yaml](/conventions/pitchme-yaml.md) for those old decks:

```yaml
gitpitch : 3.0
```

When this setting is activated as shown above your 3.0 slide decks can be viewed and updated in the new desktop app. This setting also ensures that your 3.0 slide decks will continue to render as expected when published online until their [scheduled end-of-life](#end-of-life-30).

### 3.0 End-of-Life

Legacy 3.0 slide decks published on **gitpitch.com** that are currently using *snap layouts* will continue to be available until June 30, 2021. From July 1, 2021 these legacy slide decks using this deprecated syntax will no longer be available.

If you want your legacy slide decks to render beyond June 30, 2021 you will need to update the markdown for your slides using new [4.0 grid layouts syntax](/grid-layouts/).
