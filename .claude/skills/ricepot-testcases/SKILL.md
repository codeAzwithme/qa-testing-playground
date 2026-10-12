---
name: ricepot-testcases
description: Generate enterprise-grade functional and non-functional test cases from a PRD, screenshots and supporting documents using the RICE-POT prompt method (Role, Instructions, Context, Example, Parameters, Output, Tone). Outputs an Excel sheet (.xlsx) with a fixed column set, requirement traceability, and an Open Questions sheet. Use when the user asks for test cases from a PRD or requirements doc, mentions RICE-POT or RICEPOT, or wants a test case sheet for a web page, feature or API.
---

# RICE-POT Test Case Generator

Turn a PRD plus screenshots into traceable test cases, with zero invented content.

## RICE-POT framework

| Letter | Component | Applied here |
|---|---|---|
| R | Role | Expert QA Functional Tester, 15+ years, functional and non-functional, enterprise-grade traceable cases |
| I | Instructions | Steps and rules in the Workflow section below |
| C | Context | Product under test and the provided PRD, screenshots and documents. Nothing else is a source |
| E | Example | One sample row, shown in the Example section |
| P | Parameters | Quality and accuracy constraints in the Parameters section |
| O | Output | Excel sheet with the exact columns listed in the Output section |
| T | Tone | Technical, precise, enterprise-grade. No commentary inside the artifact |

Adopt the Role for the whole task: write as an expert QA tester would.

## Inputs to collect first

1. **PRD** (md, pdf, docx or text). Required.
2. **Application screenshots**. Strongly recommended. They are the only direct evidence of the real UI.
3. **Supporting documents** (API docs, designs, earlier test cases). Optional.
4. **Product name and URL** under test.
5. **Output path** for the sheet. Default: next to the PRD.

If the PRD is missing, stop and ask for it. Do not proceed from memory of how the product "usually" works.

Read every input fully before writing any case. For PDFs, extract the text (for example `pdf-parse` in Node, or `pdftotext`). Look at each screenshot and list the elements actually visible.

## Workflow

1. **Inventory requirements.** List every requirement ID in the PRD (FR-, SR-, NFR, AC-, element IDs). Note which are tagged inferred or to-confirm.
2. **Inventory evidence.** For each screenshot, list the visible elements, labels, placeholders and states. Note anything cut off or not visible.
3. **Find gaps.** Mark anything missing, unclear or ambiguous: exact error texts, thresholds, timeouts, destination pages, account types. Collect these as open questions.
4. **Ask or proceed.**
   - If a gap blocks a whole feature (not just a detail), STOP and ask the user clarifying questions before writing.
   - If the user has asked for output regardless, or the gaps are details only, proceed. Apply the Parameters rules to every gap and list all gaps in the Open Questions sheet.
5. **Write cases.** Cover valid (positive) and invalid (negative) scenarios for every requirement. Include boundary, security and non-functional cases that the PRD actually states. Minimum 10 cases, more when PRD coverage needs it.
6. **Trace.** Every case maps to at least one requirement ID or screenshot, written in Misc (Comments).
7. **Generate the sheet** (see Output).
8. **Verify.** Re-read the file you produced. Check the row count, that TIDs are sequential, that cross-references between cases point at the right TID, and that every case has a trace.
9. **Report briefly.** State the file path, the number of cases, which cases rest on inference, and the open questions. No long recap.

## Mandatory rules (anti-hallucination)

Do NOT:
- Invent feature IDs or any feature not present in the PRD.
- Invent features, APIs, error codes, UI elements or behaviour.
- Assume default or "typical" system behaviour.
- Invent test data such as credentials. Describe the data type ("valid registered email") and list the real account as a precondition to be supplied.

Do:
- Derive every case strictly from the provided inputs.
- Describe UI elements exactly as they appear in the screenshot (labels, placeholders, button text).

## Parameters

- Output is deterministic: the same inputs give the same cases in the same order.
- Every assertion is traceable to a provided input (PRD, screenshot or document).
- If information is missing or unclear, write exactly: `Insufficient information to determine.`
- If a detail is inferred rather than stated, label it exactly: `Inference (low confidence)`. If the PRD itself tags the item as inferred or to-confirm, treat the expected result as an inference too.
- Enterprise-grade quality. Zero invented content.
- Put the missing detail inside the cell it belongs to, for example: `User lands on the dashboard. Dashboard content: Insufficient information to determine.`

## Output

**Format:** an Excel workbook (`.xlsx`), saved as `<Product>-<Feature>-TestCases.xlsx`. No preamble or commentary inside the sheet.

**Sheet 1, "Test Cases".** Columns, in this exact order:

| # | Column | Fill rule |
|---|---|---|
| 1 | Scenario | Feature group, for example Credential Login, Field Validation, Session, Security, Non-Functional |
| 2 | TID | `TC-001`, sequential, no gaps |
| 3 | Test Data | Data type or exact values. Never invented credentials |
| 4 | Test Case Description | Starts with "Verify ..." |
| 5 | Pre-Condition | State required before step 1 |
| 6 | Test Steps | Numbered, one action per step, using exact UI labels |
| 7 | Expected Result | Observable outcome, with `Insufficient information to determine.` for unknown details |
| 8 | Actual Result | Blank |
| 9 | Status | `Not Run` |
| 10 | Executed By (QA Name) | Blank |
| 11 | Misc (Comments) | `Trace: <PRD IDs / Screenshot>.` plus the inference label when it applies |
| 12 | Priority | High, Medium or Low |
| 13 | Is Automated | `No` unless the user says otherwise |

If the user asks for a different column set, use theirs instead. Keep the order they give. If they list a column twice, ask or treat it as a paste error and say so.

**Sheet 2, "Open Questions".** Columns: `#`, `Question`, `Affects` (TIDs). One row per gap found in the workflow.

**Formatting:** bold white header on a dark fill, wrapped text, top-aligned cells, thin borders, frozen header row and first two columns, auto-filter, sensible column widths. Dropdown validation: Status = Not Run, Pass, Fail, Blocked. Priority = High, Medium, Low. Is Automated = Yes, No.

### Building the file

Use Node with `exceljs` (no Python needed):

1. In a scratch directory, run `npm init -y` and `npm install exceljs`.
2. Write a generator script that holds the cases as an array and writes the workbook. Keep case data and formatting in one script so it can be re-run after edits.
3. Run it with the output path as an argument.
4. Read the file back with `exceljs` and print TID and description for every row to verify.

If Node is unavailable, write CSV with the same columns and tell the user it is CSV.

## Example row

Values are illustrative only.

| Field | Value |
|---|---|
| Scenario | Login |
| TID | TC-001 |
| Test Data | valid email + valid password |
| Test Case Description | Verify successful login with valid credentials |
| Pre-Condition | User account exists and is active |
| Test Steps | 1. Open the login page  2. Enter valid email  3. Enter valid password  4. Click "Sign in" |
| Expected Result | User is redirected to the dashboard |
| Actual Result | (blank) |
| Status | Not Run |
| Executed By (QA Name) | (blank) |
| Misc (Comments) | Trace: PRD FR-1, AC-1. |
| Priority | High |
| Is Automated | No |

## Coverage checklist

Use it to check coverage against what the PRD actually states. Skip any item the PRD does not mention.

- UI presence: every element in the screenshot.
- Positive flows for every functional requirement.
- Negative flows: wrong, empty, malformed and boundary inputs.
- Alternate paths: social login, SSO, recovery, 2FA, verification.
- Session and navigation: persistence, expiry, logout, redirects.
- Security: injection, XSS, transport, cookies, brute force, enumeration.
- Error handling: network failure, server error.
- Non-functional: performance, browsers, responsive, accessibility, localisation.

## Final report format

Keep it short:
- File path.
- Case count and the scenarios covered.
- How many cases carry the inference label.
- Top open questions that block marking Pass or Fail.
- Any departure from these rules and why.
