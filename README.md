# LiteCard Automation Project

## Overview

This repository contains complete automation solutions for the **LiteCard Demo Application**, covering both **UI** and **API** workflows.  
The project is designed to demonstrate end-to-end automation — from creating digital passes via the public signup form to validating backend card data using API tests.

---

## Project Modules

### UI Automation (Selenium + Java + TestNG)
Automates the **LiteCard public signup flow** and the **Admin Dashboard validation**.

#### Key Features
- Navigates to the **LiteCard Public Signup Form** at  
  `https://demo.litecard.io/form/custom/{{FORM_ID}}`
- **Generates 10 unique emails** dynamically in the format  
  `qa.<lastname>+<index>@example.com`
- Submits the form **10 times** to create **10 passes**
- Validates the presence of **“Add to Apple Wallet”** and **“Add to Google Wallet”** buttons on successful submissions
- Logs in to the **Admin Dashboard** at  
  `https://demo.litecard.io/` using `{{USERNAME}} / {{PASSWORD}}`
- Extracts the **cardId** for each newly created record from the **Passes table**
- Exports mapping data to:
  - `email_to_cardId.csv`
  - (optional) `email_to_cardId.json`

#### Example exported CSV format
```csv
email,cardId
qa.samitha+1@example.com,2880553612
qa.samitha+2@example.com,5471311675
...
```

---

### API Automation (Postman Collection)

Automates LiteCard **API tests** using the data exported from UI automation (`email_to_cardId.csv` or `email_to_cardId.json`).

#### Key Features
- **Authenticates** and stores `access_token` for reuse across requests
- **Updates birthday** for 5 selected cards using ISO 8601 format:
  ```
  "1990-10-25T00:00:00.000Z"
  ```
- **Verifies birthday** update using `GET /card/{id}` and assertions
- **Updates status** of 2 different cards to `INACTIVE` and then **deletes** them
- **Verifies final status** or ensures `CARD_NOT_FOUND` response after deletion
- Uses the same exported file from UI tests as input for consistency
- Assertions validate both update and delete operations

#### Example assertion (Postman Test)
```javascript
pm.test("Birthday updated successfully", function () {
    pm.expect(pm.response.json().birthday).to.eql("1990-10-25T00:00:00.000Z");
});
```

---

## Project Structure

```
litecard-automation/
│
├── ui-automation/
│   ├── src/
│   │   ├── main/java/com/litecard/pages/
│   │   └── test/java/com/litecard/tests/
│   ├── pom.xml
│   ├── testng.xml
│   └── README_UI.md
│
├── api-automation/
│   ├── postman/
│   │   ├── LiteCard_API_Automation.postman_collection.json
│   │   └── LiteCard_Environment.json
│   └── README_API.md
│
├── exported-data/
│   ├── email_to_cardId.csv
│   └── email_to_cardId.json
│
├── .github/workflows/ci.yml
├── .gitignore
└── README.md
```

---

## Setup & Execution

### Prerequisites
Ensure the following tools are installed:
- **Java 17+** and **Maven 3.9+** (for Selenium UI automation)
- **Node.js 16+** and **npm** (for Newman if running API tests)
- **Postman** (optional for visual API execution)
- **ChromeDriver** (managed via WebDriverManager)

---

### Run UI Automation Tests

```bash
# Navigate to project directory
cd ui-automation

# Install dependencies
mvn clean install

# Execute the test suite
mvn test -DsuiteXmlFile=testng.xml
```

> The test will automatically:
> - Launch the public signup form
> - Generate and submit 10 unique emails
> - Validate wallet buttons
> - Log in to Admin Dashboard
> - Extract `cardId`s and create `email_to_cardId.csv`

---

### Run API Automation Tests (Postman / Newman)

#### Option 1: Using Postman UI
1. Import the Postman collection (`LiteCard_API_Automation.postman_collection.json`)
2. Import the environment file (`LiteCard_Environment.json`)
3. Set environment variables:
   - `baseUrl` → `https://bff-api.demo.litecard.io/api/v1`
   - `access_token` → generated dynamically in login request
4. Click **Run Collection**

#### Option 2: Using Newman (CLI)
```bash
cd api-automation/postman

# Install newman if not installed
npm install -g newman

# Run collection using exported data
newman run LiteCard_API_Automation.postman_collection.json   -e LiteCard_Environment.json   --iteration-data ../../exported-data/email_to_cardId.csv
```

---

## Test Coverage Summary

| Category | Description | Status |
|-----------|--------------|--------|
| UI Signup | Automates 10 form submissions with validation 
| Wallet Button Verification | Apple & Google Wallet visibility
| Admin Dashboard | Extracts 10 new pass records 
| Data Export | Generates `email_to_cardId.csv`
| API Auth | Authenticates & stores token 
| API Birthday Update | 5 passes with ISO 8601 format 
| API Status Update | 2 passes set to INACTIVE 
| API Delete | 2 passes deleted & verified

---

## Technologies Used
- **Language:** Java, JavaScript
- **UI Automation:** Selenium WebDriver, TestNG, WebDriverManager
- **API Automation:** Postman, Newman
- **Build Tool:** Maven
- **CI/CD:** GitHub Actions (Java CI workflow)
- **Data Format:** CSV & JSON

---

## CI Integration (Optional)
GitHub Actions workflow `.github/workflows/ci.yml` automatically runs:
```yaml
name: Java CI
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'
      - name: Run UI Tests
        run: mvn -f ui-automation/pom.xml test
```

---

## Collaborators
This project grants reviewer access to:
- **Nimesh** — Reviewer / QA Lead  
- **Danny** — Reviewer / Automation Lead  

---

## Deliverables Summary

| Deliverable | Description | Location |
|--------------|-------------|-----------|
| **Deliverable A** | Runnable UI Automation Project | `/ui-automation` |
| **Deliverable B** | Runnable API Automation Tests | `/api-automation` |
| **Exported Data** | CSV/JSON mapping file | `/exported-data/` |
| **README** | Documentation (this file) | `/README.md` |

---

## 🏁 How to Review
1. Clone or open this repo in GitHub.  
2. Review:
   - `ui-automation/src/test/...` for Selenium logic.
   - `api-automation/postman/...` for Postman requests and test assertions.
3. Refer to `exported-data/email_to_cardId.csv` for input mapping.  
4. Run the UI suite first → then API suite to reuse generated data.

---

## Author
**Samitha [Sr. Quality Assurance Engineer]**  
