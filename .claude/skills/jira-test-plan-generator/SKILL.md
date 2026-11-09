---
name: jira-test-plan-generator
description: Generate a production-ready Test Plan Word document (.docx), named "Test Plan for <Jira ID>", from any Jira issue ID or link (PROJ-123, an epic, story, task or PRD ticket), laid out exactly like the user's Test Plan template (Objective, Scope, Inclusions, Test Environments, Defect Reporting Procedure, Test Strategy, Test Schedule, Test Deliverables, Entry and Exit Criteria, Tools, Risks and Mitigations, Approvals). Reads the requirement from Jira, fills only what the ticket states, and writes "Insufficient information to determine." for anything missing instead of inventing it. Use whenever the user hands over a Jira key or link as a requirement and wants a test plan, QA plan or test strategy document, even if they do not say "template" or "docx", and also when they give only a Jira ID with no other instruction. Not for test cases, automation code or bug triage.
compatibility: Needs Node.js 18+ and an Atlassian (Jira) MCP server with read access. Optional - Microsoft Word on Windows, to bake table-of-contents page numbers into the file.
metadata:
  author: qa-testing-playground
  version: "1.0.0"
allowed-tools: Bash(node:*) Read Write
---

# Jira Test Plan Generator

Turn one Jira issue into a Test Plan `.docx` that follows the instructor's template section by section.

The template is the whole format. This skill adds no section, no table, no column and no wording the template
does not have, and it takes no fact from anywhere except the Jira issue. A reviewer should be able to put the
output next to the template and see the same document, with the project's own details in it.

## Why the rules below exist

A test plan that sounds confident but contains invented dates, environments or owners is worse than one with
visible gaps, because people plan work around it. So every project-specific line must trace to the ticket,
and every gap must be visible in the document as `Insufficient information to determine.` The reader then knows
exactly what to ask the project team for.

## Inputs

- **Jira issue key or URL.** Required. Any project, any issue type. Example: `PAY-42`, or
  `https://acme.atlassian.net/browse/PAY-42`. The skill has nothing tied to one ticket or project, so every
  new key starts from a clean read of that ticket.
- **Output folder.** Optional. Default: a `test-plans` folder in the current directory.
- **Author name.** Optional. Adds "Created by <name> -" to the page-1 header. Leave it out if not given.

A bare Jira ID with no other words is a request for the test plan. If no key is given, ask for it. If the user
pasted ticket text instead of a key, use that text (see `references/jira-extraction.md`, section 5) and take the
key from the text. If the text has no key, ask for an ID to name the file.

## Workflow

1. **Read the ticket.** Follow `references/jira-extraction.md`: issue, comments, child issues, linked issues,
   key fields. If the issue cannot be read, stop and say why. Do not build a plan from a key alone.
2. **List the facts.** Before writing anything, note what the ticket actually states for each template section:
   product name and URL, objectives, requirement IDs and priorities, non-functional targets, environments,
   owners, dates, tools, risks. Note what it does not state. This list is your only source.
3. **Write the spec.** Create `spec.json` as described in `references/spec-schema.md`, section by section, using
   the rules in `references/template-format.md`. Put it in a scratch folder, not next to the deliverable.
   Leave a field empty when the ticket is silent. The generator fills the gap with the standard sentence.
4. **Build.** Run:
   ```
   node scripts/build_test_plan.js --spec <spec.json> --out "<folder>/Test Plan for <KEY>.docx"
   ```
   `<KEY>` is the Jira ID exactly as Jira writes it, for example `Test Plan for PAY-42.docx`. Script paths are
   relative to this skill's folder (the folder that holds this `SKILL.md`). It prints the number of sections and
   the list of open items.
5. **Verify.** Run `node scripts/inspect_docx.js "<file>.docx" --check`. It must say the headings match the
   template. Then run it with `--text` and read the plan once as the reviewer would: every requirement ID in the
   ticket appears, no line states something the ticket does not, nothing from the template's own example project
   (Restful Booker) is left. Fix the spec and rebuild if anything is off.
6. **Table of contents.** The file asks Word to update fields when opened. On Windows with Word installed, run
   `powershell -NoProfile -ExecutionPolicy Bypass -File scripts/refresh_toc.ps1 -Path "<file>.docx"` to put the
   page numbers in now. Otherwise tell the user to click **Yes** when Word asks to update fields.
7. **Report.** Keep it short: the file path, the Jira key and site, what was read (issue, comments, children,
   links, attachments not read), the number of open items and which sections they are in, and any ticket
   statement that was vague or marked "to be confirmed". Do not paste the whole plan into the chat.

## Rules

- **Only the ticket.** No product knowledge from memory, no "typical" environments, tools, schedules or owners.
  If it is not in the ticket it is not in the plan. Why: the plan will be reviewed as if the project team wrote it.
- **Gaps stay visible.** Use `Insufficient information to determine.` for unknowns, never a guess and never a blank.
  When only part of a line is unknown, write the known part and then the sentence for the rest.
- **Inference is labelled.** If a line rests on reading between the lines, end it with `Inference (low confidence).`
  Prefer leaving it out. Scope groups come from requirements the ticket states, so they are not inference.
- **Template text stays.** Test Strategy, Entry and Exit Criteria, Approvals and the descriptive lines under Test
  Environments and Defect Reporting Procedure are fixed. Do not edit, shorten or "improve" them. They are in
  `assets/fixed-text.json` and the script adds them.
- **Keep the trace.** Requirement IDs and priorities go into Scope and Inclusions as the ticket writes them.
  The template has no traceability table, and adding one would break the format.
- **No extra sections.** No "Assumptions", "Open Questions" or "Traceability Matrix" section. Open items live in
  the cells and in your chat report.
- **Read-only on Jira.** Never edit, comment on, transition or create issues.

## Output

- A `.docx` named `Test Plan for <KEY>.docx` (the Jira ID), built on the template package so fonts, heading
  styles, the first-page header, the watermark and the A4 page setup are the template's own.
- Two things differ from a plain copy of the template on purpose, both explained in
  `references/template-format.md`, section 5: the Test Deliverables picture is rebuilt as an editable table,
  and the example project's content is removed.

## Files in this skill

- `scripts/build_test_plan.js` - spec to `.docx`. No dependencies beyond Node.
- `scripts/inspect_docx.js` - outline, full text, and `--check` of heading order against the template.
- `scripts/verify_fixed_text.js` - confirms the fixed text still matches the template, for when the template or
  `assets/fixed-text.json` is edited.
- `scripts/refresh_toc.ps1` - optional, Windows with Word: bakes in TOC page numbers.
- `references/template-format.md` - sections, what is fixed, how to fill each one, what was left out.
- `references/spec-schema.md` - every spec field.
- `references/jira-extraction.md` - how to read the ticket and what to do when it cannot be read.
- `assets/Test Plan - Template.docx` - the template package the generator builds on.
- `assets/fixed-text.json` - the template's process text.
- `assets/example-spec.json` - a fictional worked spec. For shape only. Never reuse its wording.

## If the template changes

Replace `assets/Test Plan - Template.docx`, then update `assets/fixed-text.json` and the section code in
`scripts/build_test_plan.js` to match, and run `verify_fixed_text.js` and `inspect_docx.js --check`. The expected
heading list is at the top of `inspect_docx.js`.
