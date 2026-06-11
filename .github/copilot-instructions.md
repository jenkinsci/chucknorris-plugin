# Copilot Instructions - Jenkins Plugin (Generic)

This file guides GitHub Copilot and AI assistants for Jenkins plugin repositories.
The goal is consistent, safe, and reviewable contributions across plugins.

## Project baseline

- Project type: Jenkins plugin (Maven, HPI packaging)
- Main language: Java
- UI technology may include Jelly, JavaScript, and CSS
- Default branch is usually `master` unless the repo states otherwise

## Issue and PR linking

- Link GitHub issues as `#123`.
- Use closing keywords (`Fixes #123`) only when fully resolved.
- Minor maintenance tasks may not need an issue unless maintainers request one.

## Commit and PR title conventions

- PR title is used as changelog text: use imperative mood.
- Keep titles clear and scoped to one change.
- Prefer one logical change per PR.

## Coding rules

- Keep public API additions intentional and documented.
- Mark internal-only APIs with Jenkins access restriction annotations when needed.
- Avoid inline JavaScript and avoid `eval()`.
- Add comments only where they improve maintainability.
- Follow existing code style and plugin conventions.

## Localization

- Do not hardcode user-facing strings where localization is used.
- Add English keys to `*.properties` files for new UI text.

## Testing expectations

- Every non-trivial code change should include tests or a clear rationale when tests are not feasible.
- For behavior changes, explain test scenarios in the PR description.
- For UI changes, include screenshots when useful.

## Dependency updates

- Prefer small, isolated dependency PRs.
- Include links to release notes/changelogs when helpful.
- Flag breaking changes explicitly.

## Maintainer pre-merge checklist

1. PR description matches actual changes.
2. Linked issue(s) are correct when present.
3. Required CI checks are green.
4. At least one approval for non-trivial changes.
5. No unresolved blocking comments.
