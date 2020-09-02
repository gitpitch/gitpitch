# PITCHME.yaml

GitPitch slide decks are developed using standard [markdown](/text/basics.md) and powerful [widget syntax](/grid-layouts/native-widgets.md). Beyond markdown itself, the appearance and behavior of slide decks can be manipulated using custom settings. By convention, GitPitch custom settings are maintained within a **PITCHME.yaml** file.

To give you a sense of the kinds of settings found in a PITCHME.yaml file the following snippet demonstrates some sample custom settings:

```yaml
gitpitch: 4.0

published: true

theme-override: assets/css/PITCHME.css

custom-fonts: [
  "assets/fonts/Inter.woff2",
  "assets/fonts/YanoneKaffeesatz.woff2"
]

title: "GitPitch 4.0 - Powerpoint for Geeks"
```

Any *settings* that are directly related to a specific feature are documented directly in the corresponding feature guide. A few standalone settings are also supported. These standalone settings are documented in the [Settings Guide](/settings/).

### Setting Policies

[YAML Policy](../_snippets/yaml-public-policy.md ':include')

[YAML Policy](../_snippets/yaml-private-policy.md ':include')

For details about publishing slide decks using private repos see the [Cloud Publishing Guide](/cloud/).

