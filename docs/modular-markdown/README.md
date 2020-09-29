# Modular Markdown

As developers we are accustomed to writing code in a modular fashion. Modularity is achieved using dedicated functions, files, and directories to partition code in meaningful ways. This approach often improves code clarity and simplifies modifications and maintenance over time.

This same approach can be adopted and these same benefits can be enjoyed when developing and maintaining GitPitch slide decks.

GitPitch encourages modular design at all levels of slide deck development. This applies equally to the content used on your slides and to the markdown used to structure your slide decks.

?> A modular approach to slide deck development brings big benefits to presentation authors and training instructors developing and maintaining slide decks over time.

### Problem

Imagine your slide deck depends on an image. That image was created using a powerful sketch tool like [Excalidraw](/desktop/tools.md). You import that image into your PowerPoint or Keynote presentation. Days later you realize there's a problem with the image. So you fire up Excalidraw and you fix the image. Now what?

The original static import of the image into your PowerPoint presentation does not reflect this change. So your presentation content is stale even though your image has been fixed.

Imagine a similar scenario where you want to display code on your slides. You copy and paste a block of code from your project into your slide deck. Over time the source code in your project evolves. But the static code originally pasted onto your slides is now out of date.

These are just two examples of a fundamental problem with traditional presentation tools. Statically imported slide content has the potential to become stale just as soon as it is imported into the deck.

### Solution

The solution is to avoid statically generated, monolithic slide decks. By embracing the benefits of modular content development and dynamic delivery. Consider the following snippets that demonstrate the basic markdown widget syntax used to inject content onto GitPitch slides:

<!-- tabs:start -->

#### ** Image **

```markdown
![drag=60, drop=right, border=8px solid black](assets/img/architecture.jpg)
```

#### ** Code **

```markdown
@code[scala, drag=99, fit=2.4](src/concurrent/Actor.scala?tags=init,onMessage)
```

#### ** GIST **

```markdown
@gist[ruby, drag=60](onetapbeyond/b3bb5ed063b7683ed51592edf4f80056?tags=timings,parse)
```

#### ** Diff **

```markdown
@diff[drag=80, pad=40px](vuejs/vue/e7d49cdcf2fd9a612e0dac7a7bea318824210881)
```

#### ** Mermaid **

```markdown
@mermaid[drag=50 80, drop=-5 10, theme=forest](src/mermaid/demo.mmd) 
```

#### ** PlantUML **

```markdown
@plantuml[drop=left, width=1000, pad=30px](src/uml/sequence.puml) 
```

#### ** Cloud **

```markdown
@cloud[drag=60 r90, drop=5, border=8px dashed gray](src/cloud/demo.py)
```

#### ** Tweet **

```markdown
@tweet[https://twitter.com/gitpitch/status/1259878926710644736]
```

#### ** Video **

```markdown
![drag=50, rotate=10](https://player.vimeo.com/video/125471012)
```

<!-- tabs:end -->

These snippets demonstrate a consistent approach across all content types: externally managed slide content is injected by reference. These types of content are never statically embedded on a slide.

With this approach, any updates to your Excalidraw image would be immediately reflected on your slides. Just as any updates to the source files in your project would be immediately reflected on your slides. All without you ever having to take on the maintenance burden of keeping these external dependencies in sync with your slide decks.

?> Synchronization between external slide content sources and your GitPitch decks is automatic.

### Delivery 

GitPitch slide deck delivery is dynamic. This means that anytime you view a GitPitch slide deck within the [desktop app](/desktop/) or in the [cloud](/cloud/) the slide deck is reconstituted at the time of viewing. There is no statically generated, monolithic file.

It is the dynamic nature of GitPitch slide decks that guarantees and delivers the automatic synchronization between external slide content sources and your slide deck on each and every viewing.

This guide has focused on slide deck development using modular content. The guides that follow turn our attention to the unique benefits made possible by [modular markdown](/modular-markdown/injection.md) within GitPitch slide decks.

### Versions

?> Does dynamic delivery allow the viewing and sharing of past versions of a GitPitch slide deck?

Thanks to the *git-native* nature of GitPitch slide decks, you can view past versions of any slide deck, anytime. By specifying a *tag name* or *commit id* corresponding to a past version of the deck on the URL for your slide deck. You can find more details about creating slide deck URLs [here](/cloud/public-slide-decks).


