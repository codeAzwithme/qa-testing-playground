# QA Testing Playground

A collection of QA resources, reusable prompt templates, sample test artifacts, and a runnable Selenium + Java + TestNG example for the Salesforce login page.

## Repository contents

### `All templates/`

Reusable prompt and reference material:

- `01_TestCaseGeneration_Prompt.md`, `02_TestCases_from_prd.txt`, `03_API_Test_Generation.md`, `04_Negative_TC_Only.md`, `05_Secuirty_Test.md`, and `06_Regression_Suite.md` — prompts for generating test cases for common QA tasks.
- `Anti_Hallucinations_Rules.md` — rules for evidence-based, traceable QA output.
- `blank-template-rice-pot.md` — fill-in RICE-POT prompt template.
- `Rice Pot SKILL for Tc gen.md` — RICE-POT prompt-builder guidance.
- `RICE-POT-TestCase-Prompt.md.pdf` — exported RICE-POT test-case prompt reference.
- `SeleniumFrameworkPrompt.md` — prompt for generating a Selenium login automation framework.

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

### `Test Data/`

Convenience copies of the Restful Booker API reference PDF, VWO login PRD PDF, and VWO login test-case workbook. Related copies are also kept beside the prompt-engineering examples that use them.

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

## Test scope and notes

- The login page object uses XPath locators and explicit waits; the driver factory configures a five-second implicit wait and a 40-second page-load timeout.
- Valid-login verification checks that the browser leaves the Salesforce login URL and that no login error is shown. It does not assert a specific post-login destination.
- Invalid-login tests check the observed error-message text fragments. Because these tests exercise a live external page, Salesforce UI, anti-automation controls, or error wording changes may affect results.
- The Restful Booker Markdown suite distinguishes documented behavior from inferred expectations. Review inferred cases with the API owner before treating them as requirements.
- Example credentials or values appearing in sample API documentation are test examples, not credentials for this Selenium framework.

## Working with the RICE-POT resources

RICE-POT organizes a prompt into **Role, Instructions, Context, Example, Parameters, Output,** and **Tone**. The materials here include a prompt-builder template and a test-case-generation skill that emphasizes input traceability, documenting unknowns, and avoiding invented behavior. Provide the source PRD and any relevant supporting evidence when using these resources.

## Contributing

Keep sample data non-sensitive, avoid committing real credentials or generated build output, and update this README when adding top-level resources or changing framework setup and test commands.
