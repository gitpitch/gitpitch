# Publish Public Slide Decks

!> To unlock online publishing features for your slide decks [activate paid service](https://gitpitch.com/pricing).

Any slide deck created using [GitPitch Desktop](/desktop/) can be published and shared online at **gitpitch.com**. Public publishing is ideal for sharing and promotion of your work, projects, or business.

### Publish 4.0

To publish a [4.0 slide deck](/whats-new-in-40.md) to the cloud you *must* activate the `gitpitch` setting in the [PITCHME.yaml](/conventions/pitchme-yaml.md) for your deck as shown here:

```yaml
gitpitch : 4.0
```

When activated as shown your slide deck can be published and shared in the cloud on **gitpitch.com**.

### Using Public Repos

To publish a public slide deck using a *public* repository on GitHub, GitLab, or Bitbucket simply `git-push` your presentation files on any branch and immediately view or share your slide deck online using it's [unique URL](/cloud/slide-deck-urls).

### Using Private Repos

To publish a public slide deck using a *private* repository on GitHub, GitLab, or Bitbucket you must activate the `published` setting in the [PICHTME.yaml](/conventions/pitchme-yaml.md) for your deck.

```yaml
published: true
```

Once activated `git-push` your presentation files on any branch and immediately view or share your slide deck online using it's [unique URL](/cloud/slide-deck-urls).

?> Temporarily block public access by disabling this setting in your *PITCHME.yaml* file.

### Public Deck URLs

The following snippets demonstrate the basic structure of public slide deck URLs on **gitpitch.com**. Decks can be uniquely identified using PITCHME.md paths, branch names, tag names, and commit ids.

<!-- tabs:start -->

#### ** GitHub Deck**

```bash
# The default slide deck in the acmecorp/tech-talk repo, master branch.
https://gitpitch.com/acmecorp/tech-talk

# The default slide deck in the acmecorp/tech-talk repo, dev branch.
https://gitpitch.com/acmecorp/tech-talk/dev

# The coding/intro slide deck in the acmecorp/tech-talk repo, master branch.
https://gitpitch.com/acmecorp/tech-talk?p=coding/intro

# The coding/intro slide deck in the acmecorp/tech-talk repo, at tagX. 
https://gitpitch.com/acmecorp/tech-talk/tagX?p=coding/intro

# The coding/intro slide deck in the acmecorp/tech-talk repo, at commitX. 
https://gitpitch.com/acmecorp/tech-talk/commitX?p=coding/intro
```

#### ** GitLab Deck**

```bash
# The default slide deck in the acmecorp/tech-talk repo, master branch.
https://gitpitch.com/acmecorp/tech-talk?grs=gitlab

# The default slide deck in the acmecorp/tech-talk repo, dev branch.
https://gitpitch.com/acmecorp/tech-talk/dev?grs=gitlab

# The coding/intro slide deck in the acmecorp/tech-talk repo, master branch.
https://gitpitch.com/acmecorp/tech-talk?grs=gitlab&p=coding/intro

# The coding/intro slide deck in the acmecorp/tech-talk repo, at tagX. 
https://gitpitch.com/acmecorp/tech-talk/tagX?grs=gitlab&p=coding/intro

# The coding/intro slide deck in the acmecorp/tech-talk repo, at commitX. 
https://gitpitch.com/acmecorp/tech-talk/commitX?grs=gitlab&p=coding/intro
```

#### ** Bitbucket Deck**

```bash
# The default slide deck in the acmecorp/tech-talk repo, master branch.
https://gitpitch.com/acmecorp/tech-talk?grs=bitbucket&s=sneakpEEk

# The default slide deck in the acmecorp/tech-talk repo, dev branch.
https://gitpitch.com/acmecorp/tech-talk/dev?grs=bitbucket&s=sneakpEEk

# The coding/intro slide deck in the acmecorp/tech-talk repo, master branch.
https://gitpitch.com/acmecorp/tech-talk?grs=bitbucket&p=coding/intro

# The coding/intro slide deck in the acmecorp/tech-talk repo, at tagX. 
https://gitpitch.com/acmecorp/tech-talk/tagX?grs=bitbucket&p=coding/intro

# The coding/intro slide deck in the acmecorp/tech-talk repo, at commitX. 
https://gitpitch.com/acmecorp/tech-talk/commitX?grs=bitbucket&p=coding/intro
```

<!-- tabs:end -->

### Settings Policy

[YAML Policy](../_snippets/yaml-public-policy.md ':include')

[YAML Policy](../_snippets/yaml-private-policy.md ':include')


