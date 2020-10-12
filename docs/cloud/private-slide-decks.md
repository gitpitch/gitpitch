# Publish Private Slide Decks

!> To unlock online publishing features for your slide decks [activate paid service](https://gitpitch.com/pricing).

Private publishing is ideal for providing early-access or previews of your slide deck. It can also be used to manage temporary audiences for your slide deck. Private publishing is also known as *stealth mode*.

### Publish 4.0

To publish a [4.0 slide deck](/whats-new-in-40.md) to the cloud you *must* activate the `gitpitch` setting in the [PITCHME.yaml](/conventions/pitchme-yaml.md) for your deck as shown here:

```yaml
gitpitch : 4.0
```

When activated as shown your slide deck can be published and shared in the cloud on **gitpitch.com**.

### Using Public Repos

Private publishing is not supported within *public* repositories.

### Using Private Repos

To publish a private slide deck using a *private* repository on GitHub, GitLab, or Bitbucket you must activate the `stealth` setting in the [PICHTME.yaml](/conventions/pitchme-yaml.md) for your deck. The stealth setting accepts a comma-separated list of one or more stealth-tokens. A token can be any arbitrary string value you define.

The following *PITCHME.yaml* snippet demonstrates a single value on the `stealth` setting:

```yaml
stealth: 091827
```

The following *PITCHME.yaml* snippet demonstrates multiple values on the `stealth` setting:

```yaml
stealth: 091827, AcmeCorp, sneakpEEk
```

Note, all stealth tokens are case-sensitive.

Once activated, `git-push` your presentation files on any branch. A valid token is then required to open the [unique URL](#private-deck-urls) for your private slide deck in the cloud. The token must be appended to the URL using `?s={token}` or `&s={token}` query parameter syntax.

?> Any attempt to access a private slide deck without a valid stealth-token will be denied.

### Private Deck URLs

The following snippets demonstrate the basic structure of private slide deck URLs on **gitpitch.com**. Decks can be uniquely identified using PITCHME.md paths, branch names, tag names, and commit ids.

<!-- tabs:start -->

#### ** GitHub Deck**

```bash
# The default slide deck in the acmecorp/tech-talk repo, master branch.
https://gitpitch.com/acmecorp/tech-talk?s=sneakpEEk

# The default slide deck in the acmecorp/tech-talk repo, main branch.
https://gitpitch.com/acmecorp/tech-talk/main?s=sneakpEEk

# The default slide deck in the acmecorp/tech-talk repo, dev branch.
https://gitpitch.com/acmecorp/tech-talk/dev?s=sneakpEEk

# The coding/intro slide deck in the acmecorp/tech-talk repo, master branch.
https://gitpitch.com/acmecorp/tech-talk?p=coding/intro&s=091827

# The coding/intro slide deck in the acmecorp/tech-talk repo, at tagX. 
https://gitpitch.com/acmecorp/tech-talk/tagX?p=coding/intro&s=091827

# The coding/intro slide deck in the acmecorp/tech-talk repo, at commitX. 
https://gitpitch.com/acmecorp/tech-talk/commitX?p=coding/intro&s=091827
```

#### ** GitLab Deck**

```bash
# The default slide deck in the acmecorp/tech-talk repo, master branch.
https://gitpitch.com/acmecorp/tech-talk?grs=gitlab&s=sneakpEEk

# The default slide deck in the acmecorp/tech-talk repo, main branch.
https://gitpitch.com/acmecorp/tech-talk/main?grs=gitlab&s=sneakpEEk

# The default slide deck in the acmecorp/tech-talk repo, dev branch.
https://gitpitch.com/acmecorp/tech-talk/dev?grs=gitlab&s=sneakpEEk

# The coding/intro slide deck in the acmecorp/tech-talk repo, master branch.
https://gitpitch.com/acmecorp/tech-talk?grs=gitlab&p=coding/intro&s=091827

# The coding/intro slide deck in the acmecorp/tech-talk repo, at tagX. 
https://gitpitch.com/acmecorp/tech-talk/tagX?grs=gitlab&p=coding/intro&s=091827

# The coding/intro slide deck in the acmecorp/tech-talk repo, at commitX. 
https://gitpitch.com/acmecorp/tech-talk/commitX?grs=gitlab&p=coding/intro&s=091827
```

#### ** Bitbucket Deck**

```bash
# The default slide deck in the acmecorp/tech-talk repo, master branch.
https://gitpitch.com/acmecorp/tech-talk?grs=bitbucket&s=sneakpEEk

# The default slide deck in the acmecorp/tech-talk repo, main branch.
https://gitpitch.com/acmecorp/tech-talk/main?grs=bitbucket&s=sneakpEEk

# The default slide deck in the acmecorp/tech-talk repo, dev branch.
https://gitpitch.com/acmecorp/tech-talk/dev?grs=bitbucket&s=sneakpEEk

# The coding/intro slide deck in the acmecorp/tech-talk repo, master branch.
https://gitpitch.com/acmecorp/tech-talk?grs=bitbucket&p=coding/intro&s=091827

# The coding/intro slide deck in the acmecorp/tech-talk repo, at tagX. 
https://gitpitch.com/acmecorp/tech-talk/tagX?grs=bitbucket&p=coding/intro&s=091827

# The coding/intro slide deck in the acmecorp/tech-talk repo, at commitX. 
https://gitpitch.com/acmecorp/tech-talk/commitX?grs=bitbucket&p=coding/intro&s=091827
```

<!-- tabs:end -->

?> To revoke access to a private deck simply delete stealth tokens in your *PITCHME.yaml* file.

### Settings Policy

[YAML Policy](../_snippets/yaml-private-policy.md ':include')

