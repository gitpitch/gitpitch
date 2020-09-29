### List Styles

The list widget supports the following list-specific styles:

<details>
<summary>List Spacing Styles</summary>

- **list-spaced-xs-bullets**
- **list-spaced-sm-bullets**
- **list-spaced-bullets**
- **list-spaced-lg-bullets**
- **list-spaced-xl-bullets**

</details>

<details>
<summary>List Bullet Styles</summary>

- **list-disc-bullets**
- **list-circle-bullets**
- **list-square-bullets**
- **list-alpha-bullets**
- **list-roman-bullets**
- **list-boxed-bullets**

</details>

<details>
<summary>List Custom Styles</summary>

- **any-custom-css-style-name**

</details>

These list styles are documented in detail in the [List Styles Guide](/lists/styles.md).

### List Behaviors

The list widget supports the following list-specific behaviors:

<details>
<summary>List Presenting Behaviors</summary>

- **list-fade-bullets**
- **list-squash-bullets**
- **list-hide-bullets**

</details>

These list behaviors are documented in detail in the [List Behaviors Guide](/lists/behaviors.md).

### Grid Native Props

The list widget is a [grid native widget](/grid-layouts/native-widgets.md) meaning it also directly supports [grid layouts](/gr    id-layouts/) properties:

<details>
<summary>Grid Block Drag-and-Drop Properties</summary>

- **drag=**width height
- **drop=**x y | topleft | top | topright | left | center | right | bottomleft | bottom | bottomright

</details>

<details>
<summary>Grid Block Flow Properties</summary>

- **flow=**col | row | stack

</details>

<details>
<summary>Grid Block Styling Properties</summary>

- **bg=**color
- **bg=**path/to/image.file _opacity _color
- **pad=**top right bottom left
- **border=**width style color
- **set=**space-separated-css-style-names

</details>

<details>
<summary>Grid Block Text Control Properties</summary>

- **fit=**0.1..9.99
- **font=**font-family

</details>

<details>
<summary>Grid Block Behavior Properties</summary>

- **animate=**slideup | slidedown | slideleft | slideright | tada | flip | fadein | fadeout
- **animate=**bounceup | bouncedown | bounceleft | bounceright | speedleft | speedright
- **audio=**path/to/sound.file
- **stretch=**true | false
- **sync=**true | false | index 

</details>

<details>
<summary>Grid Block Power Properties</summary>

- **opacity=**0.1..1
- **rotate=**0..360
- **skewx=**0..360
- **skewy=**0..360
- **filter=**blur | bright | contrast | grayscale | hue | invert | saturate | sepia

</details>

These grid properties are documented in detail in the [Grid Layouts Guide](/grid-layouts/).

