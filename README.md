# Chuck Norris plugin for Jenkins

[![Jenkins Plugin](https://img.shields.io/jenkins/plugin/v/chucknorris.svg)](https://plugins.jenkins.io/chucknorris)
[![GitHub release](https://img.shields.io/github/release/jenkinsci/chucknorris-plugin.svg?label=changelog)](https://github.com/jenkinsci/chucknorris-plugin/releases/latest)
[![Jenkins Plugin Installs](https://img.shields.io/jenkins/plugin/i/chucknorris.svg?color=blue)](https://plugins.jenkins.io/chucknorris)

<a href="https://jenkins.io">
    <img width="400" src="https://www.jenkins.io/images/jenkins-logo-title-dark.svg" alt="Jenkins logo"> 
</a>

## What it is

This plugin adds an absolutely delightful feature to Jenkins:
depending if your build succeeds, fails, or is unstable,
it will show a picture of Chuck Norris *auto-adapting* (that's right, computer science it is!) to the build result!

Seeing that, already many thousands (yes, *thousands*!) of people have decided to take the plunge and install this plugin to finally see the light.
Why not you?

## Table of Contents

- [Download & Installation](#download--installation)
- [Usage](#usage)
  - [Freestyle / Maven Jobs](#freestyle--maven-jobs)
  - [Pipeline Jobs](#pipeline-jobs)
  - [Custom facts](#custom-facts)
- [Screenshots](#screenshots)
- [Release Notes](#release-notes)
- [Testimonials](#testimonials)
- [Localization](#localization)
- [Credits](#credits)
- [Maintainer](#maintainer)

## Download & Installation

Install this plugin from the Jenkins Plugin Manager (_Manage Jenkins > Plugins > Available plugins_),
or download the [latest .hpi](https://updates.jenkins.io/latest/chucknorris.hpi) and upload it via _Manage Jenkins > Plugins > Advanced settings_.

## Usage

### Freestyle / Maven jobs

1.  Open the job’s configuration page, click **Add post-build action**, and select **Activate Chuck Norris**.
2.  Save the configuration.
3.  Run a build. After it completes, Chuck Norris (along with a random quote) will appear on the job and build pages.

With [Job DSL](https://plugins.jenkins.io/job-dsl/), including jobs created through Configuration as Code, use the `chuckNorris` symbol:

```groovy
freeStyleJob('my-app') {
    publishers {
        chuckNorris()
    }
}
```

### Pipeline jobs

Add the `chuckNorris()` step to your Jenkinsfile:

```groovy
pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                echo 'Building...'
            }
        }
    }
    post {
        always {
            chuckNorris()
        }
    }
}
```

Put `chuckNorris()` in `post { always { ... } }` so Chuck reacts to the final build result.
Calling the step more than once in a run is fine: the latest call replaces the earlier fact.

The step prints the fact to the build log and returns it, so you can reuse it elsewhere:

```groovy
post {
    always {
        script {
            def fact = chuckNorris()
            echo "Today's wisdom: ${fact}"
        }
    }
}
```

### Moods

| Build result          | Chuck's mood                 |
|-----------------------|------------------------------|
| Success               | Thumbs up                    |
| Unstable / running    | Alert                        |
| Failure               | Bad ass                      |
| Aborted / not built   | Disappointed                 |

### Custom facts

Administrators can add their own facts, for example team in-jokes, under **Manage Jenkins > System > Chuck Norris**,
one per line. Builds pick from them as well as the built-in facts, each fact equally likely. Tick
**Use only the custom facts** to leave the built-in ones out (while the list is empty, builds still use the built-in
facts).

Custom facts are shown as written, in every language, and HTML-escaped like the built-in ones. A build keeps the text of
the fact it picked, so editing the list later does not change older builds.

With [Configuration as Code](https://plugins.jenkins.io/configuration-as-code/):

```yaml
unclassified:
  chuckNorris:
    customFacts: |-
      Chuck Norris reviews his own pull requests.
      Chuck Norris's builds are never flaky.
    customFactsOnly: true
```

### Screenshots

| Success | Unstable |
|---------|----------|
| ![Successful build: Chuck gives a thumbs up](docs/images/build-success.png) | ![Unstable build: Chuck is on alert](docs/images/build-unstable.png) |
| **Failure** | **Aborted / not built** |
| ![Failed build: Chuck is bad ass](docs/images/build-failure.png) | ![Aborted build: Chuck is disappointed](docs/images/build-aborted.png) |

The fact of the latest build also appears on the job page, and Chuck works with the dark theme too:

| Job page | Dark theme |
|----------|------------|
| ![Job page showing the latest fact](docs/images/project-page.png) | ![Failed build in the dark theme](docs/images/build-failure-dark.png) |

The step also prints the fact to the build log:

![Console output with the Chuck Norris fact](docs/images/console-log.png)

## Release Notes

See [GitHub Releases](https://github.com/jenkinsci/chucknorris-plugin/releases).

## Testimonials

-   Uncle Bob Martin said ChuckNorris Plugin [is *very* motivating](https://x.com/unclebobmartin/status/10741488856).

## Localization

Facts are displayed in the user's browser locale when available. Supported languages:

- English (default)
- Italian
- German
- Brazilian
- Russian
- Dutch
- Simplified Chinese
- Traditional Chinese
- Spanish
- French
- Japanese

The plugin's own text is translated into the same languages: the post-build action and step names, and the lines it writes to the build log. The fact line in the **Console Output** page is shown in the viewer's language, the same as on the build page. The plain text log (*View as plain text*, `consoleText`) and the value returned by `chuckNorris()` use the Jenkins controller's language, because they are produced before anyone views them.

Contributions of new translations are welcome — see the `FactGenerator.properties` (facts) and `Messages.properties` (plugin text) files under `src/main/resources`.

## Credits

* [Chuck Norris 'The Programmer' facts](https://web.archive.org/web/2020/http://www.codesqueeze.com/the-ultimate-top-25-chuck-norris-the-programmer-jokes)
* [Emotional Jenkins Plugin](https://plugins.jenkins.io/emotional-jenkins-plugin/).

## Maintainer

This plugin is actively maintained. Co-contributors and feedback are welcome — please open an issue or pull request.

---

[Jenkins community](https://www.jenkins.io/participate/) • [Report a bug](https://github.com/jenkinsci/chucknorris-plugin/issues)
