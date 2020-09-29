# Video Backgrounds

!> Video background content must be served from video streaming CDNs.

When used judiciously video backgrounds can be used to enhance the visual impact of some slides.

### Video Paths

All paths to background video content specified within [PITCHME.md](/conventions/pitchme-md.md) markdown must be absolute paths to video content hosted on video streaming CDN.

### Block Syntax

Activate a `drag=100` value on the [Video Widget](/videos/widget.md) to render a background video on any slide:

```markdown
![drag=100, set=back-9](https://absolute/path/to/video.mp4)

Additional slide content goes here.
```

Note the use of `back-9` style to ensure the video remains in the background. This is important if you plan on adding additional content to the slide.

?> MP4 background video content will *auto-play* when you first visit the slide within your deck.

### Delim Syntax

Special `--?video=` delimiter syntax can be used to display MP4 or WebM content as a background video on any slide. To display an MP4 or WebM video as background for your slide use the following delimiter syntax:

```markdown
---?video=https://absolute/path/to/video.mp4

Additional slide content goes here.

```

?> MP4 background video content will *auto-play* when you first visit the slide within your deck.
