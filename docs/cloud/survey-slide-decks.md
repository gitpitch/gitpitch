# Publish Survey Slide Decks

!> To unlock online publishing features for your slide decks [activate paid service](https://gitpitch.com/pricing).

Cloud surveys let you gather feedback from your audience. Using professional surveys embedded directly inside your slide deck.

- [JotForm Surveys](#jotform-surveys)
- [SurveyMonkey Surveys](#surveymonkey-surveys)
- [Google Forms Surveys](#google-forms-surveys)

### Publish 4.0

To publish a [4.0 slide deck](/whats-new-in-40.md) to the cloud you *must* activate the `gitpitch` setting in the [PITCHME.yaml](/conventions/pitchme-yaml.md) for your deck as shown here:

```yaml
gitpitch : 4.0
```

When activated as shown your slide deck can be published and shared in the cloud on **gitpitch.com**.

### JotForm Surveys

Powered by [JotForm](https://jotform.com). To activate a JotForm survey on any slide use a `---?survey=` slide delimiter. For example:

```markdown
---?survey=https://form.jotform.me/81050187195456
```

?> JotForm survey links must begin with `https://form.jotform` as shown above. Urls created using url shortening services are not supported.

The *link* to your JotForm survey can be found within your JotForm dashboard under the *Publish* tab for your survey.

The `---?survey=` slide delimiter takes an optional `color` query parameter. It can be used to customize the color of the slide background visible around the survey form. For example:

```markdown
---?survey=https://form.jotform.me/81050187195456&color=yellow
```

Where the *color* query parameter takes any valid [CSS Color Value](https://developer.mozilla.org/en-US/docs/Web/CSS/color_value).

### SurveyMonkey Surveys

Powered by [SurveyMonkey](https://www.surveymonkey.com). To activate a SurveyMonkey survey on any slide use a `---?survey=` slide delimiter. For example:

```markdown
---?survey=https://www.surveymonkey.com/r/B73FNQK
```
?> SurveyMonkey survey links must begin with `https://www.surveymonkey.com` as shown above. Urls created using url shortening services are not supported.

The *link* to your SurveyMonkey survey can be found within your SurveyMonkey dashboard under the *Collect Responses* tab for your survey. The `---?survey=` slide delimiter takes an optional `color` query parameter. It can be used to customize the color of the slide background visible around the survey form. For example:

```markdown
---?survey=https://www.surveymonkey.com/r/B73FNQK&color=yellow
```

Where the *color* query parameter takes any valid [CSS Color Value](https://developer.mozilla.org/en-US/docs/Web/CSS/color_value).

### Google Forms Surveys

Powered by [Google Forms](https://forms.google.com). To activate a Google Forms survey on any slide use a `---?survey=` slide delimiter . For example:

```markdown
---?survey=https://docs.google.com/forms/d/e/1FAIpQLSe/viewform
```
?> Google Forms survey links must begin with `https://docs.google.com` as shown above. Urls created using url shortening services are not supported.

The link to your Google Forms survey can be found within your Google Forms dashboard. Open your survey, then click the *Send* button. Within the pop-up window, click the *Send via* link option. Then copy and use the link displayed in your browser.

The `---?survey=` slide delimiter takes an optional `color` query parameter. It can be used to customize the color of the slide background visible around the survey form. For example:

```markdown
---?survey=https://docs.google.com/forms/d/e/1FAIpQLSe/viewform&color=yellow
```

Where the *color* query parameter takes any valid [CSS Color Value](https://developer.mozilla.org/en-US/docs/Web/CSS/color_value).

