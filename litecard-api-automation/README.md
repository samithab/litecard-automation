# Litecard API Automation (Postman)

## Overview
This project automates the **Litecard API testing workflow** using **Postman**.  
It demonstrates advanced data-driven API testing and dynamic execution logic, covering:

- Bulk **birthday updates** for multiple cards.
- Selective **status changes** to `INACTIVE` and `DELETED`.
- Validation via `GET Card by ID` responses and assertions.
- Environment-driven configuration with dynamic iteration handling.
- Robust skip logic to ensure only the target card IDs are updated.

This setup aligns with professional QA automation practices and meets assessment criteria for end-to-end API validation.

---

## Test Design Summary

| Test Area | Description |
|------------|--------------|
| **Authentication** | Retrieves and stores access tokens for API reuse. |
| **Birthday Update** | Updates birthdays on 5 cards using ISO 8601 format and validates via GET. |
| **Status Update (INACTIVE / DELETED)** | Updates 2 remaining cards from the same dataset. |
| **Dynamic Selection** | Uses environment variables (`INACTIVE_ID`, `DELETED_ID`) to select specific cards at runtime. |
| **Data-Driven Execution** | Iterates through a 10-record CSV file (cardId/email mapping). |
| **Conditional Skipping** | Non-target card IDs are skipped automatically within collection logic. |
| **Assertions** | Validate HTTP codes, response structure, and updated field values. |

---

## Folder Structure

```
litecard-api-automation/
│
├── collections/
│   └── Litecard_API_Automation.postman_collection.json
│
├── environments/
│   └── Litecard_Environment.postman_environment.json
│
├── data/
│   └── email_to_cardId.csv
│
├── manual_test_case.md
│
├── README.md
│
└── .gitignore
```

---

## Setup Instructions

### Prerequisites
- [Postman](https://www.postman.com/downloads/)
- [Node.js](https://nodejs.org/) (for Newman CLI runs)
- A valid Litecard API user account and access token
- GitHub (for version control)

---

### Environment Variables
Before running the collection, configure your environment in Postman:

| Variable | Example | Description |
|-----------|----------|-------------|
| `BASE_URL` | `https://bff-api.demo.litecard.io/api/v1` | Base API endpoint |
| `AUTH_TOKEN` | `<your-access-token>` | Bearer token for authentication |
| `INACTIVE_ID` | `5854335546` | Card ID to mark as INACTIVE |
| `DELETED_ID` | `3630524325` | Card ID to mark as DELETED |

> **Important:** Mask sensitive tokens before committing to GitHub.

---

### Test Data (CSV)
The project uses a CSV file `email_to_cardId.csv` containing 10 card IDs and their corresponding emails.

**Example:**
```csv
email,cardId
qa.sulakkana.01@example.com,1279495636
qa.sulakkana.02@example.com,5809893878
qa.sulakkana.03@example.com,8260043858
qa.sulakkana.04@example.com,3246150332
qa.sulakkana.05@example.com,7324061969
qa.sulakkana.06@example.com,5854335546
qa.sulakkana.07@example.com,3630524325
qa.sulakkana.08@example.com,7748344583
qa.sulakkana.09@example.com,7282918569
qa.sulakkana.10@example.com,2880553612
```

---

## Running the Tests

### Option 1 — Postman Collection Runner

1. Open **Postman** and import:
   - `Litecard_API_Automation.postman_collection.json`
   - `Litecard_Environment.postman_environment.json`
2. Go to **Runner**.
3. Select:
   - **Collection:** `Litecard API Automation`
   - **Environment:** `Litecard Environment`
   - **Data file:** `email_to_cardId.csv`
4. Click **Run** starting from `Process Birthday Logic`.

Postman will:
- Update birthdays on 5 cards.
- Skip non-target cards.
- Update 1 card to `INACTIVE`.
- Update 1 card to `DELETED`.
- Validate all updates via GET requests.

---

### Option 2 — Newman CLI (Recommended for CI)

Install Newman globally:
```bash
npm install -g newman
```

Run via CLI:
```bash
newman run collections/Litecard_API_Automation.postman_collection.json   -e environments/Litecard_Environment.postman_environment.json   -d data/email_to_cardId.csv
```

Generate a CLI HTML report:
```bash
newman run collections/Litecard_API_Automation.postman_collection.json   -e environments/Litecard_Environment.postman_environment.json   -d data/email_to_cardId.csv   -r cli,htmlextra
```

---

## Advanced Logic Implemented

### Controller / Action Design Pattern
The collection uses a **Controller–Action** structure to handle conditional execution:

| Controller | Action | Purpose |
|-------------|---------|----------|
| `Process Birthday Logic` | `Update Birthday` | Iterates through CSV, updates only 5 cards |
| `Process INACTIVE Logic` | `Update Card Status - INACTIVE` | Updates a single pre-defined card |
| `Process DELETED Logic` | `Update Card Status - DELETED` | Updates a single pre-defined card |
  
This pattern ensures no unintended API calls and maintains traceable logs per iteration.

### Conditional Flow
Each controller uses:
```js
postman.setNextRequest('Update Card Status - INACTIVE');
```
or
```js
postman.setNextRequest('Update Card Status - DELETED');
```
to control runtime flow within the same iteration — ensuring clean sequencing from birthday → inactive → deleted logic.

---

## 🧾 Assertions & Validation

| Check | Validation |
|--------|-------------|
| Birthday Update | `GET /card/{id}` → `cardOwnerCopy.birthday` matches ISO date |
| INACTIVE Update | `GET /card/{id}` → `status = INACTIVE` |
| DELETED Update | `GET /card/{id}` → `status = DELETED` or handled 404 `"Card has been deleted"` |
| Already Updated | Graceful pass: `"already inactive"` or `"Card has been deleted"` logged as acceptable |
| Unknown Response | Test marked failed with diagnostic log |

---

## Logs Example

```
This card matches INACTIVE_ID: 5854335546
Sending INACTIVE update for 5854335546
Card successfully marked as INACTIVE: 5854335546

This card matches DELETED_ID: 3630524325
Sending DELETED update for 3630524325
Card successfully marked as DELETED: 3630524325
```

---

## Deliverables

| Deliverable | Description |
|--------------|-------------|
| **Deliverable A** | UI Automation (Cypress / Selenium) → Generates email_to_cardId.csv |
| **Deliverable B** | API Automation (Postman) → Uses CSV for data-driven execution |
| **Deliverable C** | Manual Test Case (happy path) → Documented in `manual_test_case.md` |

---

## Tools & Tech Stack

| Tool | Purpose |
|------|----------|
| **Postman** | Collection-based API testing and pre/post scripts |
| **Newman** | CLI execution for CI/CD |
| **GitHub** | Version control and collaboration |
| **CSV Data File** | Data-driven test iteration |
| **Litecard Demo API** | Target API under test |

---

## Author

**Samitha Sulakkana**  
*Senior QA Engineer — Automation & API Testing Specialist*  
- Expertise: Postman, API Automation, TestNG, Selenium, and CI/CD pipelines  
- Focus: Designing scalable, maintainable test frameworks with reusable components  

---

## License
This project is licensed under the [MIT License](LICENSE).

---

## Summary

This project demonstrates:
- Practical **data-driven API automation** using Postman.
- Use of **environment variables** and **runtime control** (`setNextRequest()`). 
- Proper **validation and logging** of each API operation.
- QA best practices in **structure, documentation, and reusability**.

A Senior QA could integrate this into CI pipelines (e.g., GitHub Actions) for continuous regression of Litecard API endpoints.

---
