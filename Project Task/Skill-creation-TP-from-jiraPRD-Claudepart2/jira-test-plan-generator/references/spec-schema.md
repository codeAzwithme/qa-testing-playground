# Test Plan spec (JSON) reference

`scripts/build_test_plan.js` reads one JSON file, the spec, and writes the .docx. The spec holds only what comes
from Jira. The template's fixed text is added by the script. Any field you leave out, set to `null`, or leave as
an empty string or empty list is written as `Insufficient information to determine.`, so the document never has
a blank cell and never gets a made-up value. The script prints every place where that happened (the "open
items"), and you report them to the user.

A complete worked example is `assets/example-spec.json`. It is fictional. Use it to see the shape and which
ticket statement goes into which field, and never copy its wording into another plan.

## Fields

| Field | Type | Required | Meaning |
|---|---|---|---|
| `jira_key` | string | no | The issue key, for your own record. Not printed in the document. |
| `product_short` | string | yes | Short product name. Goes in the page-1 title: `Test Plan (<product_short>)`. Use the name the ticket gives. If the ticket names no product, use the issue summary. |
| `product_full` | string | yes | Product name as the ticket gives it. Used in Scope: `Scope of Test Plan for <product_full>:` and the closing note. Same fallback: the issue summary. |
| `author` | string or null | no | Adds `Created by <author> - ` before the title. Leave null unless the user gave a name. |
| `objective.paragraphs` | string[] | no | Paragraphs under Objective. URLs inside become links. |
| `objective.bullets` | string[] | no | Real bullet list after the paragraphs. |
| `scope` | `{title, points[]}[]` | no | Numbered test-type groups. `title` without the colon. `points` become "-" lines. |
| `inclusions` | `{title, points[]}[]` | no | Bold label plus one line per point. |
| `environments.rows` | `{name, url}[]` | no | Table `Name / Env url`. Either cell may be null. |
| `environments.platforms` | string[] | no | Platform lines (OS, browsers, devices) under the table. |
| `defect.rows` | `{process, poc}[]` | no | Table `Defect Process / POC`. |
| `defect.tool` | string or null | no | Printed as `Tools - <tool>`. |
| `schedule.dates` | object | no | Keys `test_plan`, `test_case_creation`, `test_case_execution`, `summary_reports`. Each a string or null. |
| `schedule.duration` | string or null | no | Full bold line, for example `2 Sprints to Test the Application`. |
| `deliverables.dates` | object | no | Keys `test_plan`, `functional_test_cases`, `defect_reports`, `summary_reports`. `defect_reports` defaults to `NA`. |
| `tools` | string[] | no | Test tools, one bullet each. |
| `risks` | `{risk, mitigation}[]` | no | `Risk:` and `Mitigation:` lines. |

Keys that start with `_` (for example `_notes`) are ignored, so you can leave comments in the file.
Unknown keys give a warning. Wrong types give an error and no file.

## Writing style for the values

- Plain text. No markdown, no HTML. `&`, `<` and quotes are escaped by the script.
- Write a full sentence per point, in the present tense, starting with "Verify ..." for scope and inclusions.
- Keep requirement IDs and priorities exactly as the ticket writes them.
- Write `Insufficient information to determine.` yourself inside a line when only part of it is unknown
  (see Scope in `template-format.md`). Leave a whole field empty when all of it is unknown.
- Use the en dash and other characters from the ticket as they are. The file is UTF-8.

## Minimal valid spec

```json
{
  "product_short": "Acme Checkout",
  "product_full": "Acme Checkout Service"
}
```

This builds a plan with the fixed sections filled and every project-specific section marked
`Insufficient information to determine.`.
