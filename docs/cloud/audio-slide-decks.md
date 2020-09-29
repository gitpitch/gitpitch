# Publish Audio Slide Decks

!> To unlock online publishing features for your slide decks [activate paid service](https://gitpitch.com/pricing).

Audio slide decks *enhance* traditional decks with the spoken word. By activating a *voice-over* for individual slides and slide fragments, you can unlock brand new ways to share knowledge, teach, and deliver training materials online.

### Publish 4.0

To publish a [4.0 slide deck](/whats-new-in-40.md) to the cloud you *must* activate the `gitpitch` setting in the [PITCHME.yaml](/conventions/pitchme-yaml.md) for your deck as shown here:

```yaml
gitpitch : 4.0
```

When activated as shown your slide deck can be published and shared in the cloud on **gitpitch.com**.

### Audio Activation

By default, audio features are disabled for slide decks. To activate audio features enable the `audio-deck` setting in the [PITCHME.yaml](/conventions/pitchme-yaml.md) for any deck:

```yaml
audio-deck : true
```


### Audio Controls

When audio is activated, each slide displays a 🎧 **headphones** icon in the bottom-left corner. Position your mouse over this icon to reveal the audio player controls ⏯ . These controls can be used to start and stop audio playback.

?> Once audio is playing, desktop browsers will auto-advance between audio clips. Mobile browsers require user interaction to advance between audio clips.

### Master v Fragment

The main audio file for any slide is referred to as the *master audio* file.  This audio file is guaranteed to play as soon as the slide is displayed. Master audio files are registered for a slide using the [master @audio widget](#master-audio). This widget must be declared following the delimiter for the slide.

Additional audio files can be registered for playback when individual fragments on your slide are displayed. Working with slide audio fragments is detailed in the following sections of this guide:

- [Grid Fragment Audio](#grid-audio)
- [Code Fragment Audio](#code-audio)
- [List Fragment Audio](#list-audio)


### Master Audio

To register the *master audio* file for a slide use the following **@audio** widget syntax. This widget *must* be declared directly following the *delimiter* for the slide. For example:

```markdown
---
@audio[Greeting](assets/audio/greeting.mp3)

### Hello, World!
```

?> The text specified within the *square-brackets* on the **@audio** widget is optional.

The path specified within the *round-brackets* on the **@audio** widget is required. Any relative path to a [valid audio file](#audio-formats) can be used. Whatever audio file is indicated on this path is the audio file that will play when the slide is displayed.

### Grid Audio

Grid audio syntax lets you register an audio file on any [grid layout block](grid-layouts/drag-and-drop.md). When registered, a layout block automatically inherits slide [fragment behavior](grid-layouts/fragments.md). For example:

```markdown
---
@audio[Deployment Intro](assets/audio/cloud.mp3)

[drag=30 100, drop=left]
## Cloud Deployment

[drag=70 100, drop=right, audio=assets/audio/deploy.mp3]
## A Bird's Eye View
![Deployment Model](assets/img/deployment.jpg)
```

This markdown snippet demonstrates a number of important features:

1. The snippet activates a [master audio](#master-audio) file **cloud.mp3** for this slide
1. This master audio file is played as soon as the slide is displayed
1. The snippet also activates grid audio **deploy.mp3** for playback on a layout block
1. This grid layout block automatically inherits [fragment behavior](grid-layouts/fragments.md)
1. The grid audio file is played when the block fragment is displayed on the slide

### Code Audio

Code audio syntax lets you register an audio file on any [code presenting fragment](code/presenting.md). For example:

```markdown
---
@audio[Code Intro](assets/audio/intro.mp3)

@code[drag=99, java zoom-20](src/lib/Utility.java)

@[1-4,zoom-16](assets/audio/java-imports.mp3)
@[9,10,14-18](assets/audio/java-method-args.mp3)
```

This markdown snippet demonstrates a number of important features:

1. The snippet declares a [master audio](#master-audio) file **intro.mp3** for this slide
1. This master audio file is played as soon as the slide is displayed
1. The snippet uses a [code widget](code/widgets.md) to render sample code on the slide
1. The snippet also declares two [code presenting fragments](/code/presenting.md) with **mp3** audio
1. These audio files are played when the corresponding *code fragments* are displayed

### List Audio

List audio syntax lets you register an audio file using [speaker notes for lists syntax](/speaker/notes.md) on any [list fragment](/lists/behaviors.md). For example:

```markdown
---
@audio[About](assets/audio/about.mp3)

## What is GitPitch?

@ul
- A modern PowerPoint @note[Markdown, Modular, Versioned](assets/audio/modern.mp3)
- Uniquely adapted for Devs @note[GitHub, GitLab, Bitbucket](assets/audio/developers.mp3)
- On MacOS, Linux, and Windows @note[Online, Offline, Export](assets/audio/everywhere.mp3)
@ulend
```

This markdown snippet demonstrates a number of important features:

1. The snippet declares a [master audio](#master-audio) file **about.mp3** for this slide
1. This master audio file is played as soon as the slide is displayed
1. The snippet uses a [list widget](/markdown/lists.md) to render list items as fragments
1. Each fragment uses [speaker notes for lists syntax](/speaker/notes.md) to register an audio file
1. The list fragment audio is played when the corresponding list item is displayed

### Silent Audio

It is strongly recommended that each slide and each slide-fragment within an audio slide deck has its own audio file. This approach delivers a seamless viewing and listening experience for your audience.

Any slide or slide-fragment without a corresponding audio file is simply displayed for up to *5 seconds* in silence. After that *silent pause* the deck automatically transitions to the next slide or fragment. You can override this default when activating audio deck features in your [PITCHME.yaml](/conventions/pitchme-yaml.md). For example:

```
audio-deck : 8
```

Here the *audio-deck* setting has the following effects:

1. The setting activates audio deck features for your slide deck
2. And the value on the setting activates a custom *silent pause* period measured in *seconds*.

This *silent pause* period is a global value. It applies to all slides and slide-fragments where an audio file has not been activated. If you want to display a slide or slide-fragment in silence for a period of time that differs from the global *silent pause* period, simply record *silence* for that custom duration, and then register the corresponding silent audio file using standard audio syntax.

### Customize Controls

The default styling and positioning of the 🎧 **headphones icon** for audio playback are set with the following *built-in* CSS style rules:

```css
div.audio-deck-icon {
    color: #E58537;
    position: fixed;
    left: 20px;
    bottom: 20px;
    z-index: 100;
    animation: shake-headphones 1.0s;
    animation-iteration-count: 5;
}

div.audio-deck-controls {
    position: fixed;
    left: 70px;
    bottom: 22px;
    z-index: 99;
}

div.audio-deck-controls > audio {
    opacity: 0.0;
    width: 200px;
    height: 40px;
}

div.audio-deck-controls:hover > audio {
    opacity:1;
    width: 200px;
}

div.audio-deck-icon:hover + div.audio-deck-controls > audio {
    opacity: 1;
}
```

You can override these defaults by activating [custom styles](/themes/custom-css) for your slide deck.  For example, you could override the default `#E58537` color with `blue` for the 🎧 **headphones icon** by activating the following rule:

```css

div.audio-deck-icon {
    color: blue;
}

```

These styling details are provided so you can easily make small tweaks to the audio player controls if needed. Making wholesale changes to these *built-in* CSS style rules for the 🎧 **headphones icon** is not recommended unless you perform extensive *cross-browser* testing before publishing your deck to the cloud.

### Audio Caching

Audio files used by audio slide decks are cached by the **gitpitch.com** server for up to **6 hours**. This means any changes to the audio files that you push to your upstream repositories on GitHub, GitLab, or Bitbucket will not be heard by your audience until the older versions of those same files expire in the cache.

For this reason, we strongly recommend that you develop your audio slide decks directly within [GitPitch Desktop](/desktop/). The *Desktop* does not cache audio files so you will hear changes to those files immediately.  Once work on your audio deck is complete, you can then push your presentation files, including the final versions of your audio files, to your upstream repository. At that point you will be ready to start sharing the link to your new audio slide deck online.

### Audio Formats

GitPitch audio slide decks are made possible by `HTML5` audio playback
support in your browser. The `HTML5` standard supports the following audio
formats: **MP3**, **WAV**, and **OGG**. But not all Web browsers are capable
of playing all of these audio formats.

The only audio format that will reliably play across all browsers is the **MP3** format. For this reason, at this time [Aug, 2020] it is strongly recommended that you use **MP3** audio files exclusively for your audio slide decks. Numerous [tools exist](#audio-tools) that can convert alternate audio formats to **MP3**.

### Audio Tools

You can use any tool to record the audio files for your audio slide decks. The only constraint is that the final format of your audio files must meet the conditions documented in [audio formats](#audio-formats).

However, if you are looking for a recommendation, from personal experience I can recommend [Audacity](https://www.audacityteam.org). It is a free, easy to use, open-source audio tool. Available on MacOS, and GNU/Linux, and Windows. You can learn more about **Audacity** and download the free software [here](https://www.audacityteam.org).

!> Note, GitPitch has no affiliation with the Audacity project. Simply a fan from experience.

There are also numerous online audio recording services, like the [Online Voice Recorder](https://online-voice-recorder.com). While I have no personal experience using these online tools they do present an alternative if you do not wish to install an audio tool on your desktop.

?> For consistent results always trim silence at the beginning and end for your audio recordings.
