# Create card, update birthday and status, and verify via API and wallet view

## Preconditions
- Tester has valid credentials and a working `AUTH_TOKEN` for the API (or access to Swagger UI).
- Tester can access the Swagger/UI and Postman (or any API client) to send requests.
- Tester has a device or browser capable of viewing the generated wallet links (Apple Wallet / Google Pay).
- Network connectivity to the test/demo environment: `https://bff-api.demo.litecard.io`.

---

## Test data
- Use a unique card payload (email or identifier) so the created record is identifiable in the test environment.
- Example birthday values (ISO 8601):
  - Original birthday on creation: `1990-01-01T00:00:00.000Z`
  - Updated birthday for verification: `2025-10-25T10:26:01.963Z`
- Example statuses: `INACTIVE`, `DELETED`.

---

## Objective
Create a card via API, update the card’s birthday, verify the change via `GET /card/{id}`, then update status to `INACTIVE` and `DELETED` and verify via API and wallet view.  
This is a **happy-path end-to-end manual test case**.

---

## Step-by-step procedure

1. **Create a new card**
   - **Action:** Call `POST /card` with a payload including a unique email and initial birthday `1990-01-01T00:00:00.000Z`.
   - **Validation:** Check response HTTP 201/200 and confirm a `cardId` is returned.
   - **Expected Result:** A new card is created; response body contains valid `cardId`, `appleLink`, or `googleLink`.

2. **Record the cardId and wallet links**
   - **Action:** Copy `cardId`, `appleLink`, and `googleLink` from the response.
   - **Validation:** Confirm values are not null and URLs open in a browser.
   - **Expected Result:** Valid `cardId` string and wallet links openable in device/browser.

3. **Confirm initial birthday**
   - **Action:** `GET /card/{cardId}`.
   - **Validation:** Locate `cardOwnerCopy.birthday`.
   - **Expected Result:** Birthday equals `1990-01-01T00:00:00.000Z`.

4. **Update birthday**
   - **Action:** `PATCH /card`
     ```json
     {
       "cardId": "<cardId>",
       "cardPayload": { "birthday": "2025-10-25T10:26:01.963Z" }
     }
     ```
   - **Validation:** Check response 200 and success message.
   - **Expected Result:** Birthday field updated successfully.

5. **Verify birthday update**
   - **Action:** `GET /card/{cardId}`.
   - **Validation:** Inspect `cardOwnerCopy.birthday`.
   - **Expected Result:** Birthday equals `2025-10-25T10:26:01.963Z` (ISO 8601).

6. **Open wallet preview (optional visual)**
   - **Action:** Open `appleLink` or `googleLink`.
   - **Validation:** Confirm displayed date matches updated birthday.
   - **Expected Result:** Wallet pass shows correct birthday; API value confirmed in step 5.

7. **Update status to INACTIVE**
   - **Action:** `POST /card/status`
     ```json
     { "cardId": "<cardId>", "status": "INACTIVE" }
     ```
   - **Validation:** Check response 200 and `success:true`.
   - **Expected Result:** Status successfully changed to INACTIVE.

8. **Verify INACTIVE status**
   - **Action:** `GET /card/{cardId}`.
   - **Validation:** Confirm `status` = `INACTIVE`.
   - **Expected Result:** Card status returned as INACTIVE.

9. **Update status to DELETED**
   - **Action:** `POST /card/status`
     ```json
     { "cardId": "<cardId>", "status": "DELETED" }
     ```
   - **Validation:** Check response 200 or 404.
   - **Expected Result:**  
     - 200 → Card deleted successfully.  
     - 404 + `"message":"Not found. Card has been deleted"` → log “This Card is already deleted”.

10. **Verify deletion**
    - **Action:** `GET /card/{cardId}`.
    - **Validation:** Expect 404 or status `DELETED`.
    - **Expected Result:** API confirms card deleted or not found; wallet link no longer accessible.

---

## Notes & Acceptance Criteria
- Use ISO 8601 dates for all birthday updates.  
- Visual wallet checks are optional; API `GET` response is the source of truth.  
- Only the positive (happy-path) flow is covered—no negative or exploratory steps.

---

**End of manual_test_case.md**
