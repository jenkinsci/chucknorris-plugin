# Copilot PR Review Rules (Generic)

Use this checklist when reviewing, triaging, or cleaning up pull requests.

## Review workflow

### 1. Validate PR description

- Confirm title/body accurately describe the diff.
- Flag stale template text and mismatches.

### 2. Validate linked issues

- Confirm linked GitHub issues match the change intent.
- Minor chores may not require an issue unless repository policy says otherwise.

### 3. Validate implementation quality

- Check that code solves the stated problem.
- Flag unrelated changes, dead code, and debug leftovers.
- Ensure consistency with repository coding conventions.

### 4. Review comments context

- Open comments must be resolved or clearly acknowledged.
- Read resolved comments for context and prior decisions.

### 5. Require a clear continuation plan

- If work is incomplete, propose explicit next steps.
- Recommend split PRs when changes are mixed.

### 6. Define and verify testing

- Request both manual validation steps and automated test coverage for non-trivial changes.
- If tests are missing, require justification.
- Prefer running full verification before merge when feasible:

```bash
mvn -ntp clean verify
```

### 7. Branch hygiene

- Source branch should be up to date with target branch before merge.
- Confirm target branch is correct for the repository workflow.

## Quick checklist

- [ ] Description matches code changes
- [ ] Linked issues are correct (if present)
- [ ] Implementation is complete and scoped
- [ ] Open comments are resolved or acknowledged
- [ ] Testing is documented and adequate
- [ ] Branch is up to date with target
- [ ] Target branch is correct
