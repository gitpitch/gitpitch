# CSS Utility Styles

This guide documents the set of CSS *utility-styles* that are available *out-of-the-box* to GitPitch presentation authors.  While you can create your own [custom css styles](/theme/custom-css.md) for any slide deck, these *utility-styles* are built-in to support common use-cases.

?> Built-in styles are activated on markdown widgets using the [Grid Set Property](/grid-layouts/set.md).

### Text Colors

The following `text-{color}` styles are available out-of-the-box for activation on any markdown widget:

```css
.text-black  { color: #000000 !important; }
.text-blue   { color: #4487F2 !important; }
.text-gray   { color: #777777 !important; }
.text-green  { color: #8EA33B !important; }
.text-orange { color: #F26225 !important; }
.text-gold   { color: #E58537 !important; }
.text-pink   { color: #E71E60 !important; }
.text-purple { color: #643B85 !important; }
.text-yellow { color: #F5DB2E !important; }
.text-white  { color: #FFFFFF !important; }
```

```markdown
[drag=100, drop=center, set=text-pink]

Hello, World!
```

?> You can use these built-in text colors or create your own [custom text color styles](/theme/custom-css.md).

### Text Weights

The following `text-{weight}` styles are available out-of-the-box for activation on any markdown widget:

```css
.text-bold       { font-weight: bold !important; }
.text-italic     { font-style: italic !important; }
.text-italics    { font-style: italic !important; }
.text-uppercase  { text-transform: uppercase !important; }
.text-lowercase  { text-transform: lowercase !important; }
.text-capitalize { text-transform: capitalize !important; }
.text-smallcaps  { font-variant: small-caps !important; }
```

```markdown
[drag=50 15, drop=bottom, set=text-bold text-uppercase]

Hello, World!
```

### Text Fonts

The following `font-{family}-{weight}` styles are available out-of-the-box for activation on any markdown widget:

```css
.font-lato         	{ font-family: "Lato" !important; }
.font-montserrat   	{ font-family: "Montserrat" !important; }
.font-nunito-sans  	{ font-family: "Nunito Sans" !important; }
.font-open-sans    	{ font-family: "Open Sans" !important; }
.font-oswald       	{ font-family: "Oswald" !important; }
.font-poppins      	{ font-family: "Poppins" !important; }
.font-prompt       	{ font-family: "Prompt" !important; }
.font-quicksand     { font-family: "Quicksand" !important; }
.font-raleway      	{ font-family: "Raleway" !important; }
.font-roboto       	{ font-family: "Roboto" !important; }
.font-ubuntu       	{ font-family: "Ubuntu" !important; }
.font-work-sans    	{ font-family: "Work Sans" !important; }
.font-bubblegum    	{ font-family: "Bubblegum" !important; }
.font-concert-one  	{ font-family: "Concert One" !important; }
.font-righteous    	{ font-family: "Righteous" !important; }
.font-source-sans-pro 	{ font-family: "Source Sans Pro" !important; }

.font-lato-medium         { font-family: "Lato Medium" !important; }
.font-montserrat-medium   { font-family: "Montserrat Medium" !important; }
.font-nunito-sans-medium  { font-family: "Nunito Sans Medium" !important; }
.font-open-sans-medium    { font-family: "Open Sans Medium" !important; }
.font-oswald-medium       { font-family: "Oswald Medium" !important; }
.font-poppins-medium      { font-family: "Poppins Medium" !important; }
.font-prompt-medium       { font-family: "Prompt Medium" !important; }
.font-quicksand-medium    { font-family: "Quicksand Medium" !important; }
.font-raleway-medium      { font-family: "Raleway Medium" !important; }
.font-roboto-medium       { font-family: "Roboto Medium" !important; }
.font-ubuntu-medium       { font-family: "Ubuntu Medium" !important; }
.font-work-sans-medium    { font-family: "Work Sans Medium" !important; }
.font-source-sans-pro-medium { font-family: "Source Sans Pro Medium" !important; }

.font-lato-heavy         { font-family: "Lato Heavy" !important; }
.font-montserrat-heavy   { font-family: "Montserrat Heavy" !important; }
.font-nunito-sans-heavy  { font-family: "Nunito Sans Heavy" !important; }
.font-open-sans-heavy    { font-family: "Open Sans Heavy" !important; }
.font-oswald-heavy       { font-family: "Oswald Heavy" !important; }
.font-poppins-heavy      { font-family: "Poppins Heavy" !important; }
.font-prompt-heavy       { font-family: "Prompt Heavy" !important; }
.font-quicksand-heavy    { font-family: "Quicksand Heavy" !important; }
.font-raleway-heavy      { font-family: "Raleway Heavy" !important; }
.font-roboto-heavy       { font-family: "Roboto Heavy" !important; }
.font-ubuntu-heavy       { font-family: "Ubuntu Heavy" !important; }
.font-work-sans-heavy    { font-family: "Work Sans Heavy" !important; }
.font-source-sans-pro-heavy { font-family: "Source Sans Pro Heavy" !important; }
```

```markdown
[drag=100 15, drop=top, set=font-righteous]

Hello, World!
```

These built-in font styles are defined to override the default font families in your base theme for plain text only. You will need to define your own [custom styles](/theme/custom-css.md) if you want to activate custom fonts to override your base theme for headline or byline content. For example:

```css
.headline-ubuntu h1,
.headline-ubuntu h2 { font-family: "Ubuntu Heavy" !important; }

.byline-quicksand h3,
.byline-quicksand h4 { font-family: "Quicksand Medium" !important; }
```

### Heading Colors

The following `{heading}-{color}` styles are available out-of-the-box for activation on any markdown widget:

```css
.h1-black  h1 { color: #000000 !important; }
.h1-blue   h1 { color: #4487F2 !important; }
.h1-gray   h1 { color: #777777 !important; }
.h1-green  h1 { color: #8EA33B !important; }
.h1-orange h1 { color: #F26225 !important; }
.h1-gold   h1 { color: #E58537 !important; }
.h1-pink   h1 { color: #E71E60 !important; }
.h1-purple h1 { color: #643B85 !important; }
.h1-yellow h1 { color: #F5DB2E !important; }
.h1-white  h1 { color: #FFFFFF !important; }

.h2-black  h2 { color: #000000 !important; }
.h2-blue   h2 { color: #4487F2 !important; }
.h2-gray   h2 { color: #777777 !important; }
.h2-green  h2 { color: #8EA33B !important; }
.h2-orange h2 { color: #F26225 !important; }
.h2-gold   h2 { color: #E58537 !important; }
.h2-pink   h2 { color: #E71E60 !important; }
.h2-purple h2 { color: #643B85 !important; }
.h2-yellow h2 { color: #F5DB2E !important; }
.h2-white  h2 { color: #FFFFFF !important; }

.h3-black  h3 { color: #000000 !important; }
.h3-blue   h3 { color: #4487F2 !important; }
.h3-gray   h3 { color: #777777 !important; }
.h3-green  h3 { color: #8EA33B !important; }
.h3-orange h3 { color: #F26225 !important; }
.h3-gold   h3 { color: #E58537 !important; }
.h3-pink   h3 { color: #E71E60 !important; }
.h3-purple h3 { color: #643B85 !important; }
.h3-yellow h3 { color: #F5DB2E !important; }
.h3-white  h3 { color: #FFFFFF !important; }

.h4-black  h4 { color: #000000 !important; }
.h4-blue   h4 { color: #4487F2 !important; }
.h4-gray   h4 { color: #777777 !important; }
.h4-green  h4 { color: #8EA33B !important; }
.h4-orange h4 { color: #F26225 !important; }
.h4-gold   h4 { color: #E58537 !important; }
.h4-pink   h4 { color: #E71E60 !important; }
.h4-purple h4 { color: #643B85 !important; }
.h4-yellow h4 { color: #F5DB2E !important; }
.h4-white  h4 { color: #FFFFFF !important; }
```

```markdown
[drag=80 40, drop=center, set=h1-blue h3-green]

# Introducing

### GitPitch Slide Decks
```

?> You can use these built-in heading colors or create your own [custom heading color styles](/theme/custom-css.md).

