# Test Plan template format

The format comes from `assets/Test Plan - Template.docx` (the instructor's template) and nothing else. This file
lists every section in order, says which text is fixed and which is filled from Jira, and how to write each
filled section. Read it before writing the spec.

## Contents
1. Page 1
2. Section table
3. How to fill each section
4. Quirks of the template that are kept on purpose
5. What was left out of the template and why

## 1. Page 1

Header (first page only): `Created by <author> - Test Plan (<product_short>)` and `TheTestingAcademy.com`.
The "Created by" part appears only when the user supplies an author. Then a horizontal rule, the table of
contents, another rule. The "TheTestingAcademy" watermark is on every page. The body starts on page 2.

## 2. Section table

Order and heading levels are fixed. `build_test_plan.js` produces them, and `inspect_docx.js --check` verifies them.

| # | Heading (level) | Content | Source |
|---|---|---|---|
| 1 | Objective (H2) | Paragraphs, optional bullet list | Jira |
| 2 | Scope (H2) | Fixed intro line, numbered test-type groups with "-" lines, fixed closing note | Jira + fixed |
| 3 | Inclusions (H3) | Bold group label, then one line per point | Jira |
| 4 | Test Environments (H3) | 7 fixed lines, table `Name / Env url`, platform list | fixed + Jira |
| 5 | Defect Reporting Procedure (H3) | 7 fixed lines, table `Defect Process / POC`, `Tools - <tool>` | fixed + Jira |
| 6 | Test Strategy (H3) | Whole section is fixed | fixed |
| 7 | Test Schedule (H3) | 2 fixed lines, table `Task / Dates`, bold duration line | fixed + Jira |
| 8 | Test Deliverables. (H3, the heading ends with a period) | Fixed intro, table `Deliverables / Description / Target Completion Date` | fixed + Jira (dates) |
| 9 | Entry and Exit Criteria (H3) | Fixed intro, `Requirement Analysis` label, then Entry/Exit (H4); Test Execution (H3) with Entry/Exit (H4); Test Closure (H3) with Entry/Exit (H4) | fixed |
| 10 | Tools (H4) | Fixed lead-in, bullet list | Jira |
| 11 | Risks and Mitigations (H4) | Fixed lead-in, `Risk:` / `Mitigation:` pairs | Jira |
| 12 | Approvals (H4) | Whole section is fixed | fixed |

Fixed text lives in `assets/fixed-text.json` and is copied from the template word for word (hard-wrapped lines
joined). One typo was corrected: "The metrics and metrics" became "The metrics". Never rewrite fixed text to fit
a project. If the user wants different process text, they change the template, not the plan.

## 3. How to fill each section

General rule: every sentence must be backed by the Jira issue (description, comments, sub-tasks, child issues,
linked issues, fields). Reword for the template's voice, but do not add a fact. Keep requirement IDs (FR1, AC-2,
REQ-7) in the text so a reader can trace a line back to the ticket.

**Objective.**
- Paragraph 1 follows the template's sentence: "The goal of this test plan is to ensure the quality,
  functionality, and reliability of <product> ...". Add the product URL if the ticket gives one (it becomes a link)
  and name the Jira key.
- Next paragraphs: what the product is and the business objectives, only as the ticket states them.
- Bullets: the ticket's own list of features, modules or technologies, when it has one. Skip if it has none.

**Scope.** Numbered groups, each a test type, each with "-" lines that say what is verified.
- Functional Testing is always there when the ticket lists functional requirements. Cite the IDs and priorities.
- Add another group only when the ticket states a requirement that calls for it: a performance target gives
  Performance Testing, a security statement gives Security Testing, and so on. A test type the ticket never
  mentions does not go in, even if it is common practice. The generic types in Test Strategy already cover the
  process side.
- If a number is missing (the ticket says "high visitor volumes" but gives no figure), say so in the line:
  `Visitor volume to be tested: Insufficient information to determine.`

**Inclusions.** One bold label per feature, flow or requirement group. Under it, one line per point, phrased as
what will be verified ("Verify that ..."), each carrying its requirement ID and priority when the ticket gives
them. This is where traceability lives, because the template has no separate traceability table.

**Test Environments.** Table rows come from the ticket: an environment name and its URL. If the ticket gives a URL
but no name, leave the name empty and the generator writes `Insufficient information to determine.`. Platform
list: operating systems, browsers, devices the ticket says will be tested. A feature the product has (for example
"cross-browser QA") is not a statement about which browsers this team will test, so do not turn it into one.

**Defect Reporting Procedure.** Table rows: a defect area and its point of contact, only if the ticket names
them (assignee, component lead, owner fields count). `tool` is the defect tracker only if the ticket says which.
That the requirement itself sits in Jira is not a statement about where defects go.

**Test Schedule.** Dates from the ticket only. The four rows (`test_plan`, `test_case_creation`,
`test_case_execution`, `summary_reports`) get a date only when the ticket says what that date is for, for example a
sprint start and end, or a release date for a named milestone. `duration` is free text in the template's pattern,
for example "2 Sprints to Test the Application", written only when the ticket states it. A Jira due date that
does not say which activity it belongs to goes into `duration` as a plain sentence
(`Jira due date for PROJ-12: 2026-12-15. Duration to test the application: Insufficient information to determine.`),
not into a row. A created or updated date of the issue is not a schedule.

**Test Deliverables.** Only the target dates come from the ticket. Defect Reports defaults to `NA`, as in the
template.

**Tools.** Test tools the ticket names. Tools that are part of the product (its integrations) are not test tools.

**Risks and Mitigations.** Risks and mitigations the ticket states, one pair each. If the ticket lists a risk
without a mitigation, the mitigation becomes `Insufficient information to determine.`.

## 4. Quirks of the template that are kept on purpose

These look odd but they are the instructor's format, so the generator reproduces them:
- Heading levels are uneven: Tools, Risks and Mitigations, and Approvals are Heading 4, so they sit under
  Test Closure in the table of contents.
- The heading "Test Deliverables." ends with a period.
- Strategy line "Step3 – ..." has no space after "Step", and the first strategy line has no "Step 1" label.
- "Task Time Duration" appears under the Test Schedule lead-in line.
- The seven Test Environments lines and the seven Defect Reporting lines are written as "such as ..." guidance.
  They are kept as they are. They are not statements about this project.

## 5. What was left out of the template and why

Present in the template file, absent from generated plans, because they belong to the Restful Booker example and
not to the format:
- The header author "Pramod Dutta" and the title "(Restful Booker)". The title now shows the product.
- The two stray lines under Objective ("Track both leading and lagging indicators ...", "Keep an eye on guardrail
  metrics ...") and the bullets "Sprint boot", "My SQL", "Documentation via flagger".
- The 20 Restful Booker scope items and the CRUD inclusions.
- The QA / Pre Prod rows, the Devesh / Sonal / Prajeeth contacts, "Tools - JIRA", the tool list, the three
  example risks, "2 Sprints".
- The Restful Booker documentation screenshot under Test Environments.
- The yellow highlighting on the template's environment and defect text.
- The empty paragraphs used as padding on page 1, and the table-of-contents entry "Test Plan", whose bookmark is
  undefined in the template (it shows "Error! Bookmark not defined.").
- The Test Deliverables table is a picture in the template. It is rebuilt as a real table with the same columns,
  rows and red header, so the dates can be filled in.
