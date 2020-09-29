# Custom CSS

The [Theme Builder](/theme/builder.md) is recommended for making basic theme customizations for your slide decks. For more targeted customization you can activate custom CSS for any slide deck.

As a presentation author you can define and activate new CSS style rules that override the default style rules inherited from the base theme for your slide deck.

?> The base theme for your slide deck is defined by the [Theme Template](/theme/template.md).

### Activation

To enable custom CSS style rules you must activate the `theme-override` setting in the [PITCHME.yaml](/conventions/pitchme-yaml.md) for your slide deck. For example:

```yaml
theme-override : assets/css/PITCHME.css
```

Once activated, all custom CSS style rules will be applied to the content on your slides.


### Sample Slide

The following CSS snippet declares a custom style rule that activates a striped-effect using alternating colors on list items:

```css
ul.striped-list-items {
  color: #001F3F;
}

ul.striped-list-items > li:nth-child(even){
  color: white;
}
```

These custom style rules must be activated using the `theme-override` setting in the [PITCHME.yaml](/conventions/pitchme-yaml.md) for our sample deck. Once activated we can enable our custom `striped-list-items` style directly on any instance of the [list widget](/list/widgets.md) as shown here:

![Sample slide demonstrating list widget custom style](../_images/gitpitch-lists-styles-custom.png)

?> Note the activation of the custom **striped-list-items** style on the sample list widget above.

### Web Inspector

If you intend activating custom CSS style rules that override the base theme we recommend using a *Web Inspector* to directly inspect the content on your slides. By inspecting existing content you can quickly learn what style rules you may need to override to enable custom styles for your deck.

Web Inspectors are typically a *Developer Tools* feature in your browser. Inspectors give you an opportunity to experiment with *tweaks* and *overrides* and see the results instantly updated in your slideshow directly within the browser.

Once you have achieved the effects you are after using the Web Inspector you can then capture the new rules in your own custom CSS file. To then activate those custom CSS styles using the `theme-override` setting as described above.

