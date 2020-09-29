# Slide Delimiters

To distinguish markdown content on one slide from the next use `---` delimiter syntax. Each delimiter denotes the start of a new slide. For example, this [PITCHME.md](/conventions/pitchme-md) snippet creates 3 distinct slides:

```markdown
# Hello, World!

---

![System Architecture](assets/img/architecture.png)

---

# The End
```

The `---` delimiter syntax creates a series of slides that use (default) horizontal navigation. GitPitch also supports `+++` delimiter syntax that supports vertical navigation between slides. Try out the following markdown snippet and experiment with a mix of horizontal and veritical navigation within your slide deck:

```markdown
# Hello, World!

---

# Greetings

+++

# Hello

+++ 

# Hola

+++

# Namaste

---

# The End
```

Typically horizontal navigation is used to present top-level information. Vertical navigation is optional. But is often used to drill-down into details.

?> As a conference speaker *vertical* slides may be your *time-permitting* slides. As an educator *veritcal* slides may be used to provide reference materials supporting the core slide content.

