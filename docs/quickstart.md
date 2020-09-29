# GitPitch 4.0 Quickstart

?> To follow along with this guide you'll need [GitPitch Desktop](/desktop/). It's a free download [available here](/free-trial.md).

I've prepared this guide to provide you with a hands-on introduction to GitPitch 4.0. Using the desktop app we will look at some sample slides together. And then tweak the sample markdown to explore some fun features on our slides.

If all goes well by the end of this guide you should be pretty excited about GitPitch. So with that lofty goal in mind...

### Let's Get Started

If you haven't downloaded the desktop app yet, go ahead and grab it [here](/free-trial.md).  As you'll see the trial is 100% free. With no sign-up. No credit card needed. And no time limits. Couldn't be easier.

OK. Now that we have the desktop app and are ready to go, let's get started:

1. First create a new empty directory on your local file system.
2. Then using this new directory as your local working directory, [launch the desktop app](/desktop/launch.md).

Once you can confirm that your desktop app is up and running we're ready to get our hands dirty.


### Starter Deck

Let's start by using the desktop app to auto-generate a sample slide deck. The following screenshots show the basic quickstart steps:

![Screenshot showing one-click quickstart](../_images/gitpitch-desktop-quickstart-collage.png)

1. On the desktop home screen, click on **Launch Slide Deck Manager**.
1. Once the deck manager is open, click the **Quickstart** link.
1. And that's it! Our sample slide deck is now open in [edit mode](/desktop/app-modes.md).

?> Note, the **Slide Deck Quickstart** is only displayed when your local working directory is empty.

### The User Interface

When our sample slide deck is opened we see the desktop in [edit mode](/desktop/app-modes.md). Edit mode gives us a split-screen environment in which to work that looks something like this:

![Screenshot showing quickstart sample slide](../_images/gitpitch-quickstart-edit-1.png)

- Up top we see a live preview of our slide deck.
- In the center there is a toolbar for deck related tools and actions.
- Down below there's an embedded markdown editor where we can make and save changes.

?> Saved changes in the markdown editor are automatically reflected in the live preview above.

You can use your touchpad or mouse to scroll within the embedded markdown editor. As you scroll through the markdown in the editor the display in the live preview window updates automatically.

### 1. Layout Basics

If you were scrolling up and down in the markdown editor to test the new UI make sure you scroll to the top now so you are looking at the first sample slide in the preview window. You should see the following:

![Screenshot showing quickstart sample slide](../_images/gitpitch-quickstart-edit-1.png)

A simple slide design, yes. But this slide demonstrates one of the most exciting new features in GitPitch 4.0. Pixel-perfect layouts for your slide content. Made possible by an innovative new [layout system for markdown](/grid-layouts/) unique to GitPitch slide decks.

?> GitPitch 4.0 delivers a brand new layout system that replicates the layout precision of traditional drag-and-drop solutions using a simple syntax directly within the markdown for your slide decks.

The [Grid Layouts Guide](/grid-layouts) provides all the details. But the basic syntax is highly intuitive and can be generalized as follows:

```markdown
[drag=width height, drop=x y]
```

This syntax declares a [layout block](/grid-layouts/drag-and-drop.md). Layout blocks are used to size and position markdown content with pixel perfect accuracy anywhere on your slides. With this knowledge in mind let's take a closer look at the markdown used to create our first sample slide:

```markdown
[drag=32 20, drop=5 0]

# **Quick**

[drag=90 50, drop=center, fit=3.4]

# Start
```

?> The sample values specified on the `drag=` and `drop=` properties above represent a `%` of slide width and height respectively. In most cases this level of precision is all you need. But when you truly need pixel-perfect accuracy, you can specify `px` values on these properties, for example, `drag=500px  350px`.

When viewed in [live mode](/desktop/app-modes.md) we can see that we have a simple yet attractive sample slide:

![Screenshot showing quickstart sample slide](../_images/gitpitch-quickstart-learn-drag-and-drop.jpg)

#### Drag-and-Drop Tweak and Test

Let's try making some changes to the markdown for this first sample slide and see what happens. Using the markdown editor, try tweaking the `drop=x y` coordinate on the layout block that positions the word `Quick` on the slide, like this:

```markdown
[drag=32 20, drop=35 0]

# **Quick**
```

The original `drop=5 0` property has been replaced with `drop=35 0`. When you make and save this change in the markdown editor the slide should automatically update in the live preview window.

Note, on MacOS you can use `Cmd+S` to save changes in the markdown editor. On Linux and Windows use `Ctrl+S` to save. You can also save by clicking on the <i class="fas fa-save fa-fw"></i> icon in the toolbar above the editor.

With this basic understanding, does the change and effect you observe makes sense? Try one more:

```markdown
[drag=32 20, drop=35 -0]

# **Quick**
```

This change introduced a *negative* value on the `y` coordinate on the sample layout block. Again make and save this change and observe what happens. By using a negative value you've just changed the anchor position for the y-axis from the top of the slide to the bottom.

As you become more familiar with GitPitch 4.0 you will come to realize that this drag-and-drop syntax while simple, is very, very powerful.

### 2. Fit is Fabulous

Let's move on to our second sample slide. Scroll within the markdown editor using your touchpad or mouse until you can see the following slide in the live preview window:

![Screenshot showing quickstart sample slide](../_images/gitpitch-quickstart-edit-2.png)

This sample slide demonstrates the power of `fit`. It's a property that gives us the ability to magnify or shrink content to \*fit\* perfectly within our layout blocks.

?> Fit takes a scaling value. Any value greater than `1` magnifies content. Any value less than `1` shrinks content. A value equal to `1` renders the content at it's original size.

Here is the markdown snippet from this sample slide:

```markdown
[drag=100, fit=5.5]

# BIG
```


This tiny snippet demonstrates the big effect *fit* can have on text based content. Including plain text content, heading content, list content, source code, math formulas, and even table data. There is even a special type of fit for images. For all the details see the [Grid Fit Guides](/grid-layouts/fit-text.md).

We can see how this sample slide looks when it's viewed in [live mode](/desktop/app-modes.md) here:

![Screenshot showing quickstart sample slide](../_images/gitpitch-quickstart-learn-fit.png)

#### Fit Tweak and Test

Using the markdown editor try different values on the `fit=` property for this sample slide and see how the text on the slide automatically shrinks or magnifies. Change the size and position of the sample layout block using `[drag=width height, drop=x y]` and then add your own text and see if you can \*fit\* it to your block.



### 3. Introducing Flow

We just learned that layout blocks are used to size and position content on our slides. This next sample slide introduces the concept of [layout flow](/grid-layouts/flow.md). Once again, scroll within the markdown editor using your touchpad or mouse until you can see the following sample slide in the live preview window:

![Screenshot showing quickstart sample slide](../_images/gitpitch-quickstart-edit-3.png)

Flow is used to control the layout direction for content within layout blocks. By default *column flow* is activated on each layout block. Column flow activates a *top-to-bottom* layout direction for block items.

Let's focus on this markdown snippet from our sample slide:

```markdown
[drag=30 100, drop=center]

## Night
![width=400](assets/img/stars.jpg)
## Skies
```

This snippet defines 3 block items: heading text, an image, and more heading text. In [live mode](/desktop/app-modes.md) we can see how these block items are rendered *top-to-bottom* within the layout block on our slide:

![Screenshot showing quickstart sample slide](../_images/gitpitch-quickstart-learn-flow-basics.png)

On our next sample slide we will have an opportunity to *tweak and test* flow for ourselves.

### 4. Flow Some More

The previous sample slide introduced us to the concept of [layout flow](/grid-layouts/flow.md). And the default *column flow* behavior. This sample slide demonstrates *row flow*. Row flow activates a *left-to-right* layout direction for block items.

One more time, scroll within the markdown editor using your touchpad or mouse until you can see the following sample slide in the live preview window:

![Screenshot showing quickstart sample slide](../_images/gitpitch-quickstart-edit-4.png)

The markdown snippet from this sample slide is shown here:

```markdown
[drag=100 30, drop=center, flow=row]

## Night
![width=400](assets/img/stars.jpg)
## Skies
```

Note the addition of the `flow=row` property to our sample layout block. Activating this property is all that was needed to change the layout direction for our sample markdown block.

?> The big take away from this sample slide is that layout block sizing, positioning, and flow can all be controlled *without requiring any changes* to the markdown for individual block items.

We can see how this sample slide looks when it's viewed in [live mode](/desktop/app-modes.md) here:

![Screenshot showing quickstart sample slide](../_images/gitpitch-quickstart-learn-flow-advanced.png)

#### Flow Tweak and Test

Working within the markdown editor try alternating between `flow=row` and `flow=col` on this layout block and experiment with adjusting the size and position of the block to accomodate these changes.

### 5. Lively Lists

Using the markdown editor scroll down until you can see the next sample slide in the live preview window. It should look like this:

![Screenshot showing quickstart sample slide](../_images/gitpitch-quickstart-edit-5.png)

?> To navigate between slides or slide fragments click on the <i class="fas fa-backward fa-fw"></i> and <i class="fas fa-forward fa-fw"></i> icons in the toolbar.

At first glance it's not particularly impressive. But this is in fact a very special slide. Let me explain why.

This sample slide introduces the concept of [Markdown Widgets](/grid-layouts/native-widgets.md). Widgets are a hugely important feature in GitPitch markdown. They greatly enhance traditional markdown rendering capabilities for slide decks by introducing *rich-content and behaviors*.

Probably the best way to understand any *behavior* is to experience it directly. So let's *experience* this sample slide when it's viewed and navigated in [live mode](/desktop/app-modes.md):

![Screenshot showing quickstart sample slide](../_images/gitpitch-quickstart-learn-lists.gif)

For now let's focus on the content that appears on the left side of the slide. The list content. The corresponding markdown snippet for that content is shown here:

```markdown
@ul[list-spaced-bullets list-fade-bullets, drag=30 100, drop=left, fit=1.4, set=text-white, sync=true]
- Distracted
- Attentive
- Rewarded
@ul
```

At first this may look complex but it's quite simple when we break it down as follows:

- The `@ul` tags indicate the list content is under the control of a [list widget](/lists/widget.md).
- A *list widget* is a special syntax that activates styles and behaviors on list content.
- The `list-spaced-bullets` is an example of a [list-specific style](/lists/styles.md).
- The `list-fade-bullets` is an example of a [list-specific behavior](/lists/behaviors.md).
- The now familiar `drag=30 100, drop=left,...` are [layout properties](/grid-layouts/native-widgets.md).

If this feels like a lot to take in right now, don't worry. It's enough to understand that a combination of built-in styles and built-in behaviors can be activated on all kinds of widgets. And each widget represents a rich content type that can be used to bring your slides alive.

#### Lists Tweak and Test

Use the markdown editor replace the `list-spaced-bullets` style with `list-square-bullets` and see what effect these changes have when you save and then view the sample slide. For fun activate both of these styles on your sample list and see what happens. We will see on the next slide how we can activate new and interesting *behaviors* on this list content too.

### 6. Stacks of Fun

Once again use the markdown editor to scroll down until you can see the next sample slide. It should look something like this:

![Screenshot showing quickstart sample slide](../_images/gitpitch-quickstart-edit-6.png)

?> To navigate between slides or slide fragments click on the <i class="fas fa-backward fa-fw"></i> and <i class="fas fa-forward fa-fw"></i> icons in the toolbar.

We are about to learn about a little bit of GitPitch magic called *layout stacks*. Let's focus on the content that will appear on the left side of the slide. The corresponding markdown snippet for that content is shown here:

```markdown
[drag=70 100, drop=left, flow=stack, sync=true]

## Hello, World!
@code[elixir, fit=0.9](src/demo.ex)
![width=1000, border=10px solid black](assets/img/dog-4.jpg)
```

The single most important feature here is the activation of `flow=stack` on our sample layout block. Stack is a unique type of [layout flow](#_3-introducing-flow) that activates **layout-and-behavior** for block items.

Once again we are going to focus on the *behavior* here so let's experience this sample slide when it’s viewed and navigated in [live mode](/desktop/app-modes.md):

![Screenshot showing quickstart sample slide](../_images/gitpitch-quickstart-learn-stacks.gif)

In this example our *layout stack* renders heading text, some source code, and an image. Importantly, a [layout stack](/grid-layouts/stacks.md) only ever displays one block item at a time. And just as you were able to step through [list items](#_5-lively-lists) on our last sample slide, we can step through *rich-content* stack items here too.

?> Layout stacks activate unique behaviors on their contained block items *without requiring any changes* to the markdown for the individual block items themselves.

#### Stacks Tweak and Test

If you are paying very close attention you may have noticed something unexpected about the way the list content is being displayed on the right side of this sample slide. Check out the description of the [list-squash-bullets behavior](/lists/behaviors.md). Pretty cool, right?

### 7. Coffee Break :)

Before moving on to our final sample slide let me ask you a quick question. Did you notice on the last two sample slides that each time we stepped into the slide new content was displayed simultaneously on the left and on the right of the slide. *What's up with that?*

The answer is **slide content synchronization**. And when your time permits I encourage you to learn all about it [here](/grid-layouts/synchronization.md).

Ok, now it's time to wrap up with one last sample slide...

### 8. Code Presenting

One last time, scroll down. You should be looking at a sample slide that renders code. Beautifully.

![Screenshot showing quickstart sample slide](../_images/gitpitch-quickstart-edit-7.png)

The previous two sample slides demonstrated dynamic behaviors. We were able to step through list content and stack content directly on our slides. Now we are going to learn how to step through source code on a slide using another great feature called *live code presenting*.

The corresponding markdown for this slide is shown in this short snippet:

```markdown
[drag=99, drop=center, fit=1.5]

@code[elixir](src/demo.ex)

[drag=30 10, drop=topright, font=bubblegum]

@[1-2,4,6](Live Code Presenting)
@[7-8]
@[7-9]
@[7-10]
@[7-11]
```

Once again let's break this down so we can understand what's happening here:

- The `@code` tag is an example use of a [code widget](/code/widgets.md).
- A *code widget* is a special syntax used to position, size, and render code content.
- The `fit=1.5` property let's us scale the source code within our layout block.
- And the `@[..]` code fragment widget activates *sharp-focus* on specific lines of code.

We can see how this sample slide looks and behaves when it's viewed in [live mode](/desktop/app-modes.md) here:

![Screenshot showing quickstart sample slide](../_images/gitpitch-quickstart-learn-code.gif)

Honestly, there are far too many great GitPitch code features to detail here. But when you're ready to dig deeper the  best place to start exploring is the [Live Code Presenting Guide](/code/presenting.md).

### Where to Next?

Whirlwind introduction over! Congrats on making it to the end of this quickstart guide.

![Screenshot showing quickstart sample slide](../_images/gitpitch-quickstart-learn-more.png)

While this quickstart covered only a small number of the features available in GitPitch 4.0 hopefully you are now excited and eager to learn more. And if that's the case then you might be wondering, where to go from here?

I recommend jumping over to the [Grid Layouts Guide](/grid-layouts/). It provides the perfect foundation for all other features. And before I go it's also worth mentioning that these docs are searchable. See the search box up in the top-left corner. Searching the docs is a great way to get answers to your questions fast.

