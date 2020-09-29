# Google Analytics Setting

### Purpose

The `gatoken` setting lets you activate *Google Analytics* tracking for your slide deck.

### Syntax

```yaml
gatoken : UA-1111111-1
```

### Details

?> Settings are activated using the [PITCHME.yaml](/conventions/pitchme-yaml.md) for your slide deck.

When activated, the `gatoken` setting will enable *Google Analytics* tracking for your slide deck.

### GA Property

In order to enable the `gatoken` setting you must first create your own GA Property for **gitpitch.com**. To create a GA Property do as follows:

1. Log into [Google Analytics](https://analytics.google.com)
1. Create a new GA Property
1. For your new GA Property, specify https://gitpitch.com as the property URL

Once your new GA Property has been created, look under *Tracking Info* to find your new GA *tracking code*. Use this *tracking code* as the value on the `gatoken` setting in your PITCHME.yaml.

> It may take up to 24 hours for GA to start displaying initial viewing statistics.

