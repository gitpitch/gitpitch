# Speaker Demo Gods

If you have ever given a live presentation at a conference or meetup then you know only too well that the number of problems you experience with your slides, microphone, remote control device, etc. is directly proportional to the size and importance of your audience ;-)

GitPitch understands your pain. And offers the following *pain-killers* for common scenarios:

- [Presenting using your own Laptop ](#personal-laptop)
- [Presenting using the Conference Laptop](#conference-laptop)
- [Presenting using PowerPoint or PDF](#powerpoint-or-pdf)

### Personal Laptop

Depending on the conference Wifi to serve your slides reliably when standing on stage in front of a live audience can be a little like walking a tightrope without the benefit of a safety net. While possible. It is rarely recommended.

Breaking all dependencies on the conference Wifi and network connections in general is one great way to keep the demo gods at bay. And for this, GitPitch offers comprehensive offline support.

**GitPitch Desktop** is a dedicated desktop tool for working and presenting offline. It can be used to develop, preview, and present slides offline. With *zero dependencies* on the network. This makes it the perfect antidote to the demo gods. You can learn more about GitPitch Desktop [here](/desktop/). And about speaker specific features [here](/speaker/).


### Conference Laptop

There are times when a conference organizer will insist that you present your slides using a dedicated conference laptop that is pre-configured and pre-connected to the conference audio and display systems.

In these circumstances you rarely have the luxury of installing your own presentation software such as [GitPitch Desktop](/desktop/) on their device. Which often means that you have to download your slide deck over the network to the conference laptop prior to going on stage. More often than not, during a pre-conference A/V check.

If this is the situation you are likely to face then it is important that you understand a little about **gitpitch.com** lazy-loading policies. These policies apply to the loading of assets on your slides, such as images and videos.

By default, **gitpitch.com** uses a *lazy-loading* strategy for slide deck assets. This means that only a small number of the images used by your slide deck are loaded when you first open your slide deck in your browser. When you start moving through your slides on **gitpitch.com** the GitPitch server detects upcoming slides that need to display image assets and it will automatically fetch those images over the network. You can think of this as a *just-in-time* loading strategy for your slide deck assets.

This strategy makes a lot of sense when you are sharing your slide deck online.  But in the context of a live slide deck presentation when you first load your slides during an A/V check potentially hours before going live on stage, this *just-in-time* strategy may leave you exposed to the demo gods if the network goes down at any point after your initial check.

For this eventuality GitPitch supports an `eager-loading` setting that can be activated for any slide deck:

```yaml
eager-loading : true
```

When activated, this setting ensures that all slide deck assets are loaded over the network when you first open your presentation in the browser. This greatly reduces the risk inherent in unreliable conference Wifi when you are live on-stage.

> Note, this approach still runs the risk of the browser on the conference laptop evicting your slide deck asset dependencies from the browser cache before you go live on-stage. For this reason, this *eager-loading* approach is only recommended if you can not export and present your slide deck as PTTX or PDF.

### PowerPoint or PDF

Sometimes conference organizers will insist all presentations be submitted ahead of time as Microsoft PowerPoint (PPTX) or PDF files. No problem. GitPitch Desktop can export your slide decks as a Microsoft PowerPoint or PDF document with just one click.

![GitPitch Slide Deck Open In PowerPoint](../_images/gitpitch-desktop-offline-publishing.png)

For further details, see the [Desktop Guide](/desktop/).
