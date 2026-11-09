---
name: test-plan-create-skill
description: >-
  Generate a complete, formal software Test Plan from a Jira ticket. Use this
  whenever the user provides a Jira ticket — as a .md/.txt file, pasted text, OR
  a Jira issue key/URL (e.g. ABC-123) — and asks to "create a test plan",
  "generate a test plan", "write a QA test plan", "test plan for this
  feature/story/ticket", or runs /test-plan. The skill reads the ticket (from a
  file, pasted text, or fetched live via the connected Atlassian MCP when given
  an issue key), then fills a bundled formal test-plan template (Objective,
  Scope, Inclusions, Test Environments, Defect Reporting, Test Strategy, Schedule,
  Deliverables, Entry/Exit Criteria, Tools, Risks & Mitigations, Approvals) and
  writes the result as a ready-to-share markdown file. Trigger even when the user
  doesn't say the words "test plan" but clearly hands over a ticket and wants QA
  planning/coverage for it.
license: MIT
metadata:
  author: thetestingacademy
  version: 1.0.0
---

# Test Plan Create Skill

Turn a Jira ticket into a complete, standardized Test Plan markdown document. The
output always follows the same formal structure so every feature's plan looks
consistent and review-ready.

## When to use

The user gives you a feature/story/bug ticket and wants a QA test plan for it. The
ticket can arrive three ways — handle all of them:

1. **A file path** — `.md` or `.txt` containing the ticket text. Read it.
2. **Pasted text** — the ticket content is already in the conversation. Use it directly.
3. **A Jira issue key or URL** — e.g. `RB-42` or `https://x.atlassian.net/browse/RB-42`.
   Fetch it live via the Atlassian MCP (see below).

## Workflow

Follow these steps in order. Keep the user informed with a one-line status, not a wall of text.

### 1. Obtain the ticket content

- **File path given** → use the `Read` tool on it.
- **Pasted/inline text** → use it as-is.
- **Jira key/URL given** → fetch via the Atlassian MCP:
  - If you don't yet know the cloud id, call `getAccessibleAtlassianResources`.
  - Call `getJiraIssue` with the issue key (and cloudId) to pull summary, description,
    acceptance criteria, components, fix version, labels, and links.
  - If the MCP isn't connected or the fetch fails, tell the user and ask them to paste
    the ticket text or give a file instead — don't invent ticket content.

### 2. Extract the facts

Read `references/jira-mapping.md` (bundled next to this file) for the field-to-section
mapping. Pull out: feature name, system/API under test, URL(s), endpoints + HTTP verbs,
acceptance criteria, auth requirements, non-functional needs (perf/load/security/rate
limit), components/integrations, schedule signals (sprint, due date, fix version), and
any named tools (Postman, REST Assured, etc.).

### 3. Load the template

Read `assets/test-plan-template.md`. That is the canonical structure — keep its section
order and headings exactly; it's a standard client-facing deliverable.

### 4. Fill the template

- Replace every `{{PLACEHOLDER}}` with ticket-derived content.
- **Scope:** start from the canonical checklist, keep only the test types the ticket
  justifies, and write one feature-specific line for each (don't pad with all 20).
- **Inclusions:** group by HTTP verb (APIs) or by screen/flow (UI). Each bullet is a
  concrete behavior to verify.
- **Strategy:** name the test-design techniques you'd actually apply for this feature.
- Where the ticket gives no data for a field, write a sensible default and mark it
  `_(assumed — confirm)_` so the reader knows it wasn't in the source.
- Preserve any URL, environment, POC, schedule, or tool explicitly named in the ticket.

Remove the HTML `<!-- guidance -->` comments from the final output — they're authoring
hints, not part of the deliverable.

### 5. Write the file

Default output is **markdown**. Save to the current working directory as
`<issue-key-or-slug>-test-plan.md` (e.g. `RB-42-test-plan.md`, or
`restful-booker-api-test-plan.md` when there's no key). If a `test-plans/` or
`docs/` directory already exists in the project, put it there instead. Tell the
user the exact path.

**If the user asks for a `.docx` (Word) file** (or names an output dir like
`/output/testplan/docx`): write the markdown first, then convert it with the
bundled converter:

```bash
python3 scripts/md_to_docx.py <plan>.md <plan>.docx
```

The script uses `python-docx` and renders headings, bullet lists, blockquotes,
and the template's pipe tables (Environments, POCs, Schedule, Risks) as real Word
tables. Keep the `.md` alongside the `.docx`. If the requested output path is an
absolute root path that isn't writable (e.g. `/output/...`), create it relative
to the project root instead and tell the user where it landed. Verify the `.docx`
opens by loading it back with `python-docx` before reporting success.

### 6. Summarize

Give a 3-5 line recap: which ticket, which scope areas were included vs dropped and why,
any `_(assumed)_` fields the user should confirm, and the output path.

## Output rules

- One file, valid markdown, headings matching the template.
- No leftover `{{placeholders}}` and no authoring comments in the final file.
- Concrete and feature-specific over generic boilerplate.
- Don't fabricate ticket details — fetched/provided content only; assumptions clearly marked.
