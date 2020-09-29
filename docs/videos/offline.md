# Offline Videos

[GitPitch Desktop](/desktop/) can stream *MP4* and *WebM* video content directly from the local working directory. 

Offline video streaming can be very useful when you need to present or deliver training content that relies on video. But when you can not guarantee reliable network connectivity. For example, when speaking at a public conference or working at an offsite training location.

### Offline Paths

All paths to offline video content specified within [PITCHME.md](/conventions/pitchme-md.md) markdown must be relative paths to video content in your local working directory or Git repository.

### Offline Syntax

Special `--?video=` delimiter syntax can be used to display MP4 or WebM content as offline video on any slide. To display an MP4 or WebM video when working offline use the following delimiter syntax:

```markdown
---?video=relative/path/to/video.mp4

Additional slide content goes here.

```

?> MP4 offline video content will *auto-play* when you first visit the slide within your deck.

