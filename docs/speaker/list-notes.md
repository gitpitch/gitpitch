# List Notes

As speakers we often introduce concepts to our audiences step by step. Most commonly using list content on our slides. To match this common behavior GitPitch adds special speaker notes support on individual list items.

The following markdown snippet demonstrates how custom speaker notes can be associated with individual list items using `@note[Text of note here...]` widget syntax:

```markdown
@ul
- Markdown @note[GFM. Plus GitPitch Markdown Widgets.]
- Modular  @note[Supports modular design of slide decks using modular markdown.]
- Git Native @note[Manage slide decks as code. On GitHub, GitLab, and Bitbucket.]
- Speaker Ready @note[Dedicated speaker window, previews, timer, speaker notes.]
- Cross Platform @note[Available on Linux, MacOS, and Windows 10.]
@ulend

Note:

What makes GitPitch the perfect slide deck solution for developers?
```

When the `@note` widget is used on a list item the syntax must appear following the text content of the list item. When a `@note` is not specified for an individual list item the [speaker window](/speaker/window.md) will display any slide specific note if available.

The following screencast demonstrates how list notes are displayed within the [speaker window](/speaker/window.md) during a live presentation:

![Sample slide demonstrating list item notes](../_images/gitpitch-speaker-list-notes.gif)

