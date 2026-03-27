---
name: add-translation
description: "Add a new language translation to a Java project using ResourceBundle-based i18n. Translates all entries, verifies quality via independent Google Translate back-translation, adds tests, updates docs, and handles git workflow. Use this skill whenever the user asks to add a translation, translate to a new language, add a locale, or do i18n work on a Java project with .properties files."
---

# Add Translation

Adds a complete new language translation to a Java project that uses `ResourceBundle`-based i18n. The key quality gate is independent back-translation verification via Google Translate — this catches issues that same-model translation/verification misses (ambiguous words, broken puns, wrong technical terms).

## Input Validation

Before doing any work, validate the user's request:

1. **No language specified** — ask the user which language they want to add. Do not guess.

2. **Validate the language/locale code:**
   - The user may provide a language name ("Slovak", "French") or a locale code ("sk", "fr", "pt_BR", "en_GB")
   - Map language names to ISO 639-1 codes. If a specific regional code is given (e.g., `en_US`, `pt_BR`), validate that both the language and country parts are real ISO codes
   - If the language is not a real language or the code is not a valid ISO 639-1 (or 639-1 + ISO 3166-1 for regional variants), tell the user the language/code is invalid and stop. Do not attempt to translate to fictional or made-up languages

3. **Check for existing translations** — search the project for a properties file with this locale suffix (e.g., `_sk`, `_pt_BR`). If one already exists, tell the user the translation already exists and point them to the file path. Do not overwrite it unless they explicitly ask

## Phase 1: Discovery

1. **Find ResourceBundle files** — search for `*.properties` files under `src/` that follow the pattern `BaseName.properties` / `BaseName_xx.properties`. The base file (no locale suffix) is the English source.

2. **If multiple ResourceBundle families exist**, list them and ask the user which one to translate.

3. **Parse the base file** to determine:
   - Total number of translatable entries (skip metadata keys whose values are just numbers, like `fact.count=82`)
   - Key naming pattern (e.g., `fact.0` through `fact.81`)
   - Any special escaping conventions (`\=`, `\:`)

4. **Locate supporting files:**
   - Test class: search `src/test/` for `*Test.java` files that reference the ResourceBundle class name
   - README or docs: check for `README.md` in project root
   - Build config: read `CLAUDE.md` if it exists for project-specific build commands

5. **Report findings** to the user before proceeding.

**If no .properties files are found**, inform the user this project doesn't use ResourceBundle i18n and stop.

## Phase 2: Translation

1. Create `BaseName_XX.properties` (or `BaseName_xx_YY.properties` for regional variants) in the same directory as the base file.

2. Add a comment header matching existing translations (e.g., `# Topic facts (Language Name)`).

3. Copy all key-value pairs. For each value:
   - Translate to the target language
   - Use `\uXXXX` Unicode escapes for ALL non-ASCII characters — this is required by Java .properties format
   - Preserve `\=` and `\:` escaping for literal equals/colons in values
   - Preserve format placeholders (`{0}`, `%s`, `%d`, etc.) exactly as-is
   - Preserve code identifiers, class names, method names (e.g., `.Dispose()`, `.DropKick()`, `ChuckNorrisException`)
   - Preserve proper nouns and technical terms (e.g., `NP-Hard`, `RSA`, `GOTO`)
   - Maintain humor, puns, and wordplay. When a pun is language-specific (e.g., "expressions" meaning both facial expressions and regular expressions), find the closest equivalent in the target language that preserves the double meaning

4. Include any metadata keys unchanged (e.g., `fact.count=82`).

## Phase 3: Back-Translation Verification

This is the critical quality gate. Claude's own back-translation has blind spots because the same model translating in both directions can make consistent errors that cancel out. Google Translate provides an independent signal.

### First pass: Full verification

Run the bundled script to back-translate all entries via Google Translate:

```bash
python3 ~/.claude/skills/add-translation/scripts/back_translate.py \
  <translated_file> <locale_code> <base_english_file>
```

The script outputs JSON with each entry's `original_english`, `translated` text, and `back_translated` text from Google Translate.

Review every entry in the output. For each one, compare the Google Translate back-translation to the original English and flag issues:
- **Semantic errors**: meaning changed, puns broken, wrong technical terms, ambiguous words that Google resolved differently than intended
- **Typos**: words in the translation that produced garbled back-translations
- **Tone/nuance**: intensifiers lost, metaphors changed, tense shifts, missing qualifiers

Fix all flagged issues in the properties file.

### Second pass: Re-verify fixes

Re-run the script with only the fixed keys to confirm the fixes landed:

```bash
python3 ~/.claude/skills/add-translation/scripts/back_translate.py \
  <translated_file> <locale_code> <base_english_file> key1 key2 key3
```

If issues persist after this second fix iteration, present the remaining problems to the user with:
- The original English text
- The current translation
- The Google Translate back-translation
- The nature of the discrepancy

Ask the user whether each remaining issue is acceptable or needs manual intervention. Some differences are inherent to translation (e.g., Google Translate rendering "beard" as "chin" in languages where one word covers both) and are fine to accept.

## Phase 4: Test & Documentation

1. **Add a test** in the existing test class, following the pattern of other locale tests:
   - Method name: `testGetFactReturns<Language>` (e.g., `testGetFactReturnsSlovak`)
   - Get entry 0 in English, get entry 0 in the new locale, assert they are not equal
   - Use `Locale.<CONSTANT>` if Java provides one (e.g., `Locale.FRENCH`), otherwise `new Locale("xx")` or `Locale.of("xx", "YY")` for regional variants
   - Use JUnit 5 (Jupiter) — JUnit 4 imports are banned in most Jenkins projects

2. **Update README.md** — add the new language to the localization/supported languages section, maintaining alphabetical order.

3. **Run the build** to verify everything passes. Use the command from CLAUDE.md if available, otherwise `mvn clean verify`. If formatting fails, run the project's formatter (e.g., `mvn spotless:apply`) and retry.

**If no test file is found**, skip the test but warn the user. Same for README.

## Phase 5: Git Workflow

1. **Create a branch** named `feature/i18n-<bundle-basename-lowercase>-XX` (e.g., `feature/i18n-facts-sk` for Slovak facts).

2. **Stage** the new properties file, modified test file, and modified README.

3. **Commit** with a title and descriptive body:
   - Title: `Add <Language> translation of <description>`
   - Body: describe what was translated, that quality was verified via Google Translate back-translation, and list the files changed

4. **Push** to remote with `git push -u origin <branch>`.

5. Do **not** create a PR — just report the branch name to the user.
