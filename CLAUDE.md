# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

**Requires JDK 17+** (CI tests on JDK 17 and 21)

```bash
mvn clean verify          # Full build + tests + SpotBugs + Spotless check
mvn clean test            # Compile and run unit tests only
mvn test -Dtest=FactGeneratorTest                 # Run a single test class
mvn test -Dtest=FactGeneratorTest#testGetFactReturnsItalian  # Run a single test method
mvn spotless:apply        # Auto-fix code formatting
mvn spotless:check        # Check formatting without fixing
mvn clean hpi:hpi         # Build the .hpi plugin package
```

## Code Quality

- **Spotless** enforces formatting (`spotless.check.skip=false`) -- run `mvn spotless:apply` before committing
- **SpotBugs** runs at max effort/low threshold during `verify`
- **JUnit 4 imports are banned** -- use JUnit 5 (Jupiter) exclusively
- Tests use `@WithJenkins` + `JenkinsRule` for integration tests needing a Jenkins instance

## Architecture

This is a Jenkins plugin (HPI packaging) that displays a Chuck Norris fact and image on build/project pages.

**Two entry points into Jenkins:**
- **Freestyle/Maven jobs:** `CordellWalkerRecorder` (extends `Recorder`, implements `SimpleBuildStep`) -- configured as a post-build action via `BeardDescriptor`
- **Pipeline jobs:** `ChuckNorrisStep` -- invoked as `chuckNorris()` in Jenkinsfiles, delegates to `ChuckNorrisStepExecution` which creates a `CordellWalkerRecorder`

**Core flow:** Both paths create a `RoundhouseAction` attached to the build run. This action stores a random fact index and a `Style` (THUMB_UP/ALERT/BAD_ASS based on build result). Jelly templates (`summary.jelly`, `floatingBox.jelly`) render the image and fact on build/project pages.

**Gotcha:** `RoundhouseAction` has legacy fields (`style`, `fact`) for backward compatibility with v0.2 serialized data — do not remove them even though they appear unused.

**Localization:** `FactGenerator` uses Java `ResourceBundle` to load locale-specific facts from `FactGenerator[_locale].properties`. Facts are resolved at display time using the browser's locale via `StaplerRequest2.getLocale()`. Non-ASCII characters in properties files must use `\uXXXX` Unicode escapes.

## Adding a Translation

Use the `/add-translation <language>` skill to automate this entire workflow, including back-translation verification and git workflow.

**Manual steps:**
1. Create `src/main/resources/hudson/plugins/chucknorris/FactGenerator_XX.properties` (XX = locale code)
2. Include `fact.count=82` and keys `fact.0` through `fact.81` with Unicode-escaped diacritics
3. Verify translation quality via Google Translate back-translation (use `.claude/skills/add-translation/scripts/back_translate.py`) — Claude's own back-translation has blind spots since the same model can make consistent errors in both directions
4. Add a test in `FactGeneratorTest` verifying the new locale differs from English
5. Update the Localization section in `README.md`

## Git Conventions

- Commit messages must include a title AND a descriptive body (not just a title)
- Translation branches: `feature/i18n-facts-XX` (XX = locale code)
- Always run `mvn clean spotless:apply` and `mvn clean verify` before pushing

## CI

- **Jenkinsfile:** `buildPlugin()` on Linux (JDK 21) and Windows (JDK 17)
- **GitHub Actions:** `cd.yaml` for continuous delivery, `jenkins-security-scan.yml` for security scanning
- **Renovate** manages dependency updates via `jenkinsci/renovate-config`
