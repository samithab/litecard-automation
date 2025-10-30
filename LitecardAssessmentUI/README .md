# LiteCard UI Automation Framework

## Overview

This automation framework is designed to **automate LiteCard’s end-to-end UI flows** — from creating multiple passes through the **public signup form** to extracting and exporting corresponding **card IDs** from the **Admin Dashboard**.

Built with **Selenium, Java, and TestNG**, the framework follows the **Page Object Model (POM)** for scalability, maintainability, and reusability.

---

## Automation Flow Summary

### **1. Signup Form Automation**
- Navigate to the public signup form:  
  `https://demo.litecard.io/form/custom/{{FORM_ID}}`
- Generate **10 unique emails** using the format:  
  `qa.{{CANDIDATE_LASTNAME}}+{{index}}@example.com`
- Submit the form 10 times to create 10 passes.
- After each successful submission, verify the appearance of:  
  **Add to Apple Wallet** button  
  **Add to Google Wallet** button
- Store all generated emails into `emails_created.csv`.

### **2. Admin Dashboard Automation**
- Login to the Admin Dashboard:  
  `https://demo.litecard.io/` using credentials from `test.properties`
- Open the **Passes (Cards)** table.
- Locate the **10 newly created records** based on the emails.
- Extract corresponding **card IDs** for each email.
- Export the mapping to:
  - `email_to_cardId.csv`
  - `email_to_cardId.json`

### **3. Export Schema**

**CSV Format**
```csv
email,cardId
qa.doe+1@example.com,1234567890
qa.doe+2@example.com,0987654321
```

**JSON Format**
```json
[
  { "email": "qa.doe+1@example.com", "cardId": "1234567890" },
  { "email": "qa.doe+2@example.com", "cardId": "0987654321" }
]
```

---

## Framework Architecture

```
src
└── test
    └── java
        └── com.litecard
            ├── listeners
            │   └── TestListener.java
            ├── ui
            │   ├── base
            │   │   └── BasePage.java
            │   ├── pages
            │   │   ├── SignupPage.java
            │   │   ├── AdminLoginPage.java
            │   │   └── AdminPassesPage.java
            │   └── tests
            │       ├── BaseTest.java
            │       ├── SignupTest.java
            │       └── AdminExtractTest.java
            ├── utils
            │   ├── CsvUtils.java
            │   ├── DriverFactory.java
            │   ├── EmailGenerator.java
            │   ├── IoUtils.java
            │   ├── ScreenshotUtils.java
            │   └── WaitUtils.java
            └── config
                └── TestConfig.java
resources
├── test.properties
└── log4j2.xml (optional)
target
└── screenshots/
```

---

## Configuration

All configurable parameters are stored in `src/test/resources/test.properties`:

```properties
# Base configuration
base.url=https://demo.litecard.io
signup.form.url=https://demo.litecard.io/form/custom/{{FORM_ID}}

# Admin credentials
admin.username={{USERNAME}}
admin.password={{PASSWORD}}

# Output paths
output.file=target/output/email_to_cardId.csv
output.json=target/output/email_to_cardId.json
```

---

## Utilities

| Utility | Description |
|----------|--------------|
| **EmailGenerator** | Generates 10 unique emails based on candidate name. |
| **CsvUtils** | Creates and updates CSV files (`emails_created.csv`, `email_to_cardId.csv`). |
| **IoUtils** | Handles file operations for JSON export. |
| **WaitUtils** | Manages explicit and fluent waits for web elements. |
| **ScreenshotUtils** | Captures screenshots for test failures (stored in `target/screenshots/`). |
| **DriverFactory** | Manages WebDriver initialization (Chrome/Edge/Firefox). |

---

## Key Test Classes

| Test Class | Purpose |
|-------------|----------|
| **SignupTest** | Automates signup form submissions and email creation. |
| **AdminExtractTest** | Logs into the admin dashboard, fetches card IDs, and exports mappings. |
| **BaseTest** | Handles common test setup, teardown, and driver management. |

---

## Execution Guide

### **1. Prerequisites**
- Java 17 or higher  
- Maven 3.9+  
- Chrome browser and matching ChromeDriver  

### **2. Run Tests**

#### Using Maven:
```bash
mvn clean test
```

#### Run Specific Test:
```bash
mvn test -Dtest=SignupTest
mvn test -Dtest=AdminExtractTest
```

#### Generate All Artifacts:
After running all tests, the following files will be generated:

```
target/output/
├── emails_created.csv
├── email_to_cardId.csv
└── email_to_cardId.json
```

---

## Test Flow Summary

| Step | Action | Output |
|------|---------|---------|
| 1 | Run `SignupTest` | Generates `emails_created.csv` |
| 2 | Run `AdminExtractTest` | Reads `emails_created.csv`, fetches cardIds, exports mapping |
| 3 | Final Output | `email_to_cardId.csv` and `email_to_cardId.json` |

---

## Design Highlights

- **Page Object Model (POM)** — ensures test code separation from page logic.  
- **Data-Driven Testing** — powered by CSV files.  
- **Reusable Utilities** — centralized helper methods for easy maintenance.  
- **Reporting & Logging** — integrated TestNG listeners for test result tracking.  
- **Error Handling & Recovery** — captures screenshots on failure and logs stack traces.  

---

## Reports & Screenshots

- TestNG HTML reports are available under:
  ```
  target/surefire-reports/
  ```
- Screenshots for failed tests:
  ```
  target/screenshots/
  ```

---

## Future Enhancements

- Integrate **Allure** or **Extent Reports** for enhanced reporting.  
- Add **CI/CD pipeline** (GitHub Actions or Jenkins).  
- Parameterize signup count via `test.properties`.  
- Support parallel browser execution.  
- Add Slack notification for nightly runs.  

---

## Author

**Samitha Sulakkana**  
_Senior QA Automation Engineer_  
  


---
