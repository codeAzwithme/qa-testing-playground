# QA Testing Playground

A collection of QA resources, reusable prompt templates, sample test artifacts, a runnable Selenium + Java + TestNG example for the Salesforce login page, and a Rest Assured + Java + TestNG framework for the RESTful Booker API.

## Repository contents

### `All templates/`

Reusable prompt and reference material:

- `01_TestCaseGeneration_Prompt.md`, `02_TestCases_from_prd.txt`, `03_API_Test_Generation.md`, `04_Negative_TC_Only.md`, `05_Secuirty_Test.md`, and `06_Regression_Suite.md` — prompts for generating test cases for common QA tasks.
- `Anti_Hallucinations_Rules.md` — rules for evidence-based, traceable QA output.
- `blank-template-rice-pot.md` — fill-in RICE-POT prompt template.
- `Rice Pot SKILL for Tc gen.md` — RICE-POT prompt-builder guidance.
- `RICE-POT-TestCase-Prompt.md.pdf` — exported RICE-POT test-case prompt reference.
- `SeleniumFrameworkPrompt.md` — prompt for generating a Selenium login automation framework.
- `Rest_assured_framework_java_prompt_with_antihallucinatory_rule.md` — RICE-POT prompt for generating the RESTful Booker Rest Assured framework under anti-hallucination rules: only documented facts, "Insufficient information to determine." for gaps, every assertion traceable to the API documentation, and a Verified Facts / Missing Information / Generated Output / Self-Validation output format.
- `TestPlanGeneratorPrompt.md` — prompt for generating a Test Plan from a PRD, following the format of a reference Test Plan document.

### `Prompt Engineering/`

- `RICEPOT/` — RICE-POT test-case-generation skill, the exported prompt PDF, a VWO login PRD, and its sample Excel test-case workbook.
- `Vibe Coding TC Gen/` — Restful Booker API reference PDF and a Markdown suite of API test cases.

### `Selenium_framework/`

A Maven project that automates the Salesforce login page at `https://login.salesforce.com/?locale=in`.

- `src/main/java/com/salesforce/automation/pages/LoginPage.java` — Page Object Model for login form interactions and assertions.
- `src/main/java/com/salesforce/automation/utils/DriverFactory.java` — Chrome WebDriver setup and teardown.
- `src/test/java/com/salesforce/automation/tests/BaseTest.java` — shared browser lifecycle and login-page setup.
- `src/test/java/com/salesforce/automation/tests/ValidLoginTest.java` — login-page UI checks, remember-me behavior, username entry, and optional valid-login check.
- `src/test/java/com/salesforce/automation/tests/InvalidLoginTest.java` — invalid, empty, and rejected credential checks, including input-handling cases.
- `testng.xml` — TestNG suite that runs the valid- and invalid-login test classes.
- `pom.xml` — Maven dependencies, Java compiler settings, and Surefire/TestNG configuration.
- `SeleniumFrameworkPrompt.md` — prompt used as framework-generation guidance. `SKILL.md` is a copy of the RICE-POT test-case skill for reference.

### `Project Task/`

`RestAssured_API_Testing_Framework_UsingJava/` is a Maven project that automates the RESTful Booker API at `https://restful-booker.herokuapp.com` with Rest Assured, TestNG, and Jackson. Its `prompt.md` is the RICE-POT prompt the framework was built from, and `prompt_with_antihallucinatory_rule.md` adds the rule that every assertion must trace to the provided API documentation.

- `src/main/java/com/restfulbooker/automation/specs/BaseSpec.java` — request and response spec builders: base URI, JSON headers, token-cookie and Basic auth, status/response-time/content-type checks, and request/response logging when a validation fails.
- `src/main/java/com/restfulbooker/automation/clients/` — `AuthApi`, `BookingApi`, and `PingApi` endpoint clients built on a spec-bound `BaseApi`.
- `src/main/java/com/restfulbooker/automation/models/` — Jackson POJOs: `Auth`, `AuthResponse`, `Booking`, `BookingDates`, and `CreateBookingResponse`.
- `src/main/java/com/restfulbooker/automation/config/ConfigManager.java` and `src/main/resources/config.properties` — base URI, admin credentials, and response-time limit, each overridable with a `-D` property.
- `src/main/java/com/restfulbooker/automation/utils/BookingDataFactory.java` — fixed booking payloads: the create example from the API documentation, a replacement, and the documented partial-update example.
- `src/test/java/com/restfulbooker/automation/tests/` — `BaseTest` (clients, token, cleanup of created bookings) plus `PingTest`, `AuthTest`, `CreateBookingTest`, `GetBookingTest`, `UpdateBookingTest`, `PartialUpdateBookingTest`, `DeleteBookingTest`, and the chained `BookingE2ETest`.
- `src/test/resources/schemas/` — JSON schemas for the token, booking, create-booking response, and booking id list.
- `testng.xml` and `pom.xml` — suite definition, dependencies, and Surefire configuration.
- `api_test_cases_summary.md` — class-by-class list of the 22 API test cases and where they live.

`Test Plan Generator/` holds the inputs and the result of generating a Test Plan from a PRD:

- `TestPlanGeneratorPrompt.md` — a copy of the template in `All templates/`. Follow its "How to use this prompt" steps: upload a reference Test Plan document and the PRD PDF, then paste the prompt.
- `Product Requirements Document (PRD) VWO.com.pdf` — the PRD the prompt is run against.
- `Test Plan - VWO.docx` — the generated Test Plan for that PRD (17 pages). It follows the section order and tables of `Test Data/Test Plan - Template.docx`: Objective, Scope with Inclusions, Test Environments, Defect Reporting Procedure, Test Strategy, Test Schedule, Test Deliverables, Entry and Exit Criteria, Tools, Risks and Mitigations, and Approvals. As the prompt requires, it adds an Assumptions, Gaps and Open Questions section (20 gaps, rated testable or not as written where that applies) and a Requirements Traceability Matrix covering FR1–FR9, the five non-functional requirements, and the two user flows. Dates, environment URLs, and owners are marked TBD because the PRD does not provide them.

### `Test Data/`

Reference documents and convenience copies. Related copies are also kept beside the prompt-engineering examples and tasks that use them.

- `Restful-booker.pdf` — Restful Booker API reference (apidoc export).
- `VWO-Login-PRD.pdf` and `VWO-Login-TestCases.xlsx` — VWO login PRD and its sample test-case workbook.
- `Product Requirements Document (PRD) VWO.com.pdf` — PRD for the VWO platform: product overview, business objectives, target users, and core features. It is also the input in `Project Task/Test Plan Generator/`.
- `App VWO Login - API Documention - Requirment.docx` — requirements for the VWO login API: objective, endpoint, request payload, headers, and response samples.
- `VWO Project Requirement API Testing ( HLR).docx` — high-level requirements for testing the VWO login and registration APIs.
- `Test Plan - Restful Booker ATB12x.docx` — Test Plan for the Restful Booker API: objective, scope, test environments, strategy, schedule, entry and exit criteria, risks, and approvals.
- `Test Plan - Template.docx` — Test Plan reference format with the same section structure.
- `Test cases - Ultimate _ TheTestingAcademy.xlsx` — 14-sheet test-case workbook: a project test-case template, Restful Booker API sheets (health check, token, booking CRUD, integration scenarios), SOAP UI sheets, and an `App.vwo.co` sheet.

### `.claude/skills/`

Contains the `ricepot-testcases` skill definition for use in Claude Code-compatible workflows. A copy is also included under `Prompt Engineering/RICEPOT/`.

## Selenium framework setup

### Prerequisites

- Java Development Kit (JDK) 17 or later.
- Apache Maven 3.6 or later, available on `PATH`.
- Google Chrome installed. Selenium 4.21.0 starts Chrome through `ChromeDriver` (and Selenium Manager can resolve the driver when needed).
- Network access to the Salesforce login page for browser tests.

The project uses Selenium Java 4.21.0, TestNG 7.10.2, and Maven Surefire 3.2.5. The framework is configured for Java 17 source and target compatibility.

### Run the test suite

From the repository root in PowerShell:

```powershell
cd Selenium_framework
mvn clean test
```

To run Chrome headlessly:

```powershell
mvn clean test -Dheadless=true
```

The suite runs both TestNG classes from `testng.xml`. The UI checks, remember-me checks, username-entry check, and invalid-login cases do not require a real Salesforce account. The valid-credentials test is skipped when credentials are not provided.

To enable that test, pass credentials as Maven user properties:

```powershell
mvn clean test "-Dsf.user=YOUR_SALESFORCE_USERNAME" "-Dsf.pass=YOUR_SALESFORCE_PASSWORD"
```

Use an account you are authorized to test. Do not commit credentials or place real credentials in source files. Command-line arguments may be retained in shell history or visible to other local processes; use this approach only in an appropriately secured environment.

## Rest Assured API framework setup

### Prerequisites

- Java Development Kit (JDK) 17 or later.
- Apache Maven 3.6 or later, available on `PATH`.
- Network access to `https://restful-booker.herokuapp.com`.

The project uses Rest Assured 5.5.7, TestNG 7.10.2, Jackson 2.19.4, and Maven Surefire 3.2.5, compiled for Java 17.

### Run the test suite

From the repository root in PowerShell:

```powershell
cd "Project Task/RestAssured_API_Testing_Framework_UsingJava"
mvn clean test
```

The suite runs 22 tests in about one minute. To run a single class:

```powershell
mvn test "-Dtest=BookingE2ETest"
```

Defaults live in `src/main/resources/config.properties`. Override any of them with Maven user properties:

```powershell
mvn clean test "-Dbase.uri=https://restful-booker.herokuapp.com" "-Dmax.response.time.ms=3000"
```

Supported properties: `base.uri`, `auth.username`, `auth.password`, and `max.response.time.ms`. The admin credentials in `config.properties` are the public sandbox credentials from the RESTful Booker documentation; do not replace them with real credentials in source files.

## Test scope and notes

- The login page object uses XPath locators and explicit waits; the driver factory configures a five-second implicit wait and a 40-second page-load timeout.
- Valid-login verification checks that the browser leaves the Salesforce login URL and that no login error is shown. It does not assert a specific post-login destination.
- Invalid-login tests check the observed error-message text fragments. Because these tests exercise a live external page, Salesforce UI, anti-automation controls, or error wording changes may affect results.
- The Restful Booker Markdown suite distinguishes documented behavior from inferred expectations. Review inferred cases with the API owner before treating them as requirements.
- Example credentials or values appearing in sample API documentation are test examples, not credentials for this Selenium framework.
- The Rest Assured tests create their own booking from fixed payloads and delete it in `BaseTest` teardown, so they do not depend on seed data in the shared RESTful Booker sandbox.
- Every Rest Assured assertion traces to the Restful Booker API documentation (`Test Data/Restful-booker.pdf`) or the framework prompt, and each test `description` names the documentation section it checks. Behavior the documentation does not state is not asserted: failure responses for missing or invalid credentials, unknown booking ids, and invalid payloads; the response bodies of `/ping` and DELETE; and any response-time limit.
- The `checkin`/`checkout` list filters are documented but not automated: when checked against the live API they did not follow the documented greater-than-or-equal comparison.
- `max.response.time.ms` (default 8000) is a configurable ceiling chosen for this framework, not a documented API limit.

## Working with the RICE-POT resources

RICE-POT organizes a prompt into **Role, Instructions, Context, Example, Parameters, Output,** and **Tone**. The materials here include a prompt-builder template and a test-case-generation skill that emphasizes input traceability, documenting unknowns, and avoiding invented behavior. Provide the source PRD and any relevant supporting evidence when using these resources.

## Contributing

Keep sample data non-sensitive, avoid committing real credentials or generated build output, and update this README when adding top-level resources or changing framework setup and test commands.
