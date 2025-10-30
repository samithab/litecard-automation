# Manual Test Case: Create card, update birthday and status, and verify via API and wallet view

**Test Case ID:** MTC-LC-001  
**Title:** Create card, update birthday and status, and verify via API and wallet view  
**Author:** Samitha (Sr. Quality Assurance Engineer)  
**Date:** 2025-10-28  
**Preconditions:**
- Tester has valid Admin Dashboard credentials: `{{USERNAME}}` / `{{PASSWORD}}`.
- Tester has access to the public signup form `https://demo.litecard.io/form/custom/{{FORM_ID}}`.
- Tester has access to the API Swagger / Postman collection at the appropriate base URL (e.g., `https://bff-api.demo.litecard.io/api/v1`).
- Tester has a smartphone or emulator with Apple Wallet (iOS) or Google Wallet (Android) available to add and view passes.
- The test environment allows creating passes and making API updates against the created cardIds.
- Network access from device and test workstation to the demo environment is available.

**Test Data / Notes:**
- Use a unique email for the card creation, e.g. `qa.<lastname>+<index>@example.com`.
- Birthday values must be in ISO 8601 format (example): `1990-10-25T00:00:00.000Z`.
- This test covers the happy path only (positive verification of expected behavior).

---

## Step-by-step procedure (happy path)

### Step 1 — Create a new pass via public signup form
1. Open a desktop browser and navigate to the public signup form URL:  
   `https://demo.litecard.io/form/custom/{{FORM_ID}}`.
2. Fill the required fields on the form. For **Email**, use a unique test email (e.g., `qa.last+001@example.com`). Complete any other mandatory fields per form requirements.
3. Submit the form.

**Validation (what to check & where):**
- On the submission confirmation page, verify that:
  - There is a success message or confirmation text indicating the pass was created.
  - The page displays **“Add to Apple Wallet”** and **“Add to Google Wallet”** buttons/links.

**Expected result:**
- The form submits successfully and both **Add to Apple Wallet** and **Add to Google Wallet** options are visible on the confirmation/thank-you page.

---

### Step 2 — Add the created pass to a Wallet (Apple or Google)
1. On the confirmation page, click **Add to Apple Wallet** (iOS device) or **Add to Google Wallet** (Android device), following the device-specific flow to add the pass to the wallet app.
2. Complete any device prompts to add/save the pass to the wallet.

**Validation (what to check & where):**
- Open the Wallet app on the device and locate the newly added pass.
- Verify that the pass displays basic identifying information (e.g., business name, partial email, pass title).

**Expected result:**
- The pass appears in the device's Wallet app and is visible with its primary fields (title, issuer). At this point the birthday field may be empty or set to the default if created without a birthday.

---

### Step 3 — Locate the cardId in Admin Dashboard and map to email
1. In a desktop browser, open Admin Dashboard: `https://demo.litecard.io/`
2. Log in using `{{USERNAME}}` and `{{PASSWORD}}`.
3. Navigate to the **Passes** (cards) table. Use filters or search to find the record created with the test email from Step 1 (search by email).
4. Open the pass record / row details and identify the `cardId` value.

**Validation (what to check & where):**
- Confirm the `email` column (or card detail) matches the test email.
- Confirm the `cardId` is visible in the pass details (UI or expanded row).

**Expected result:**
- The passes table contains an entry with the test email and a non-empty `cardId` value. Document and record the `cardId` (store in a CSV/JSON as `email,cardId`).

---

### Step 4 — Authenticate to API (store access token)
1. Open Swagger UI or Postman for the API base URL (e.g., `https://bff-api.demo.litecard.io/api/v1`).
2. Execute the authentication/login request (as specified by the API) using valid credentials or the provided authentication flow.
3. Store the returned `access_token` in the environment/session for subsequent requests.

**Validation (what to check & where):**
- Check that the authentication response status is `200 OK` (or documented success code).
- Verify the response body contains an `access_token` (non-empty string).

**Expected result:**
- Authentication request succeeds and an `access_token` is returned and stored for later API calls.

---

### Step 5 — Update birthday for the card via API (using ISO 8601)
1. From the `email_to_cardId.csv` mapping (or recorded `cardId`), select the `cardId` for the pass created in Step 1.
2. Using Swagger/Postman, call the Update Card (or Patch) endpoint to set the `birthday` field for the selected `cardId`. Example payload:

```json
{
  "cardId": "<cardId-from-step-3>",
  "cardPayload": {
    "birthday": "1990-10-25T00:00:00.000Z"
  }
}
```

3. Send the request using the stored `access_token` in the Authorization header.

**Validation (what to check & where):**
- Verify the update request returns a success status code (e.g., `200 OK` or `204 No Content` depending on API).
- Immediately call `GET /card/{cardId}` (or the documented Get Card endpoint) and check the `birthday` field in the response body.

**Expected result:**
- The update request returns success.
- The subsequent GET returns the card data where `birthday` equals the ISO 8601 value provided: `"1990-10-25T00:00:00.000Z"`.

---

### Step 6 — Verify birthday update reflected in Wallet view
1. On the device where the pass was added (Apple or Google Wallet), open the Wallet app and locate the pass.
2. View the pass details/full view and locate the birthday field or relevant personal info section.

**Validation (what to check & where):**
- Confirm the birthday shown on the pass matches `"1990-10-25"` (or full ISO representation if shown).
- If the Wallet client caches the pass, perform a refresh action if available (e.g., re-open the pass, pull-to-refresh, or re-download the pass via the original Add to Wallet link).

**Expected result:**
- The birthday displayed within the Wallet pass reflects the updated value (`1990-10-25`), confirming the API change is visible to the wallet user.

---

### Step 7 — Update status to INACTIVE via API
1. Select a different `cardId` from the exported mapping if testing multiple cards, or use the same `cardId` for status update as required by your test scope.
2. Call the Update Status endpoint (or relevant API) to set the status to `INACTIVE`:

Example payload:
```json
{
  "cardId": "<cardId-to-update>",
  "status": "INACTIVE"
}
```

3. Send the request with the `access_token` header.

**Validation (what to check & where):**
- Confirm the API returns a success status code (e.g., `200 OK`).
- Immediately call `GET /card/{cardId}` and inspect the `status` field.

**Expected result:**
- The GET response shows `status: "INACTIVE"` for the updated `cardId`.

---

### Step 8 — Verify status change visible to Wallet user
1. On the device's Wallet app, open the pass that was updated to `INACTIVE`.
2. Observe pass usability indicators:
   - If the app shows a status (e.g., “Inactive”, “Expired”, or similar) on the pass, confirm the label is present.
   - If the pass is removed by the service after status change, confirm it is no longer usable or no longer present in the Wallet (device behavior depends on integration).

**Validation (what to check & where):**
- Check the pass details on the device for any new status indicator or change in behavior (e.g., disabled button, grayed-out appearance).
- If the Wallet client supports remote updates, perform refresh or re-download steps to ensure changes are received.

**Expected result:**
- The Wallet pass shows the status change (inactive label, or is disabled/unusable). At minimum the backend `GET /card/{cardId}` confirms `INACTIVE` status.

---

## Test Completion / Artifacts to submit
- `email_to_cardId.csv` — mapping of created emails to cardIds (from Step 3).
- API request/response screenshots for:
  - Authentication response with token (Step 4)
  - Birthday update request and GET response showing updated birthday (Step 5)
  - Status update request and GET response showing `INACTIVE` (Step 7)
- Wallet screenshots showing:
  - Initial pass added to Wallet (Step 2)
  - Birthday displayed on the pass after update (Step 6)
  - Pass showing inactive/disabled state (Step 8) — if supported by the wallet client

---

## Notes & Assumptions
- Wallet display/update behavior may depend on Wallet provider (Apple vs Google) and the pass format; testers should account for small differences in how fields are presented.
- If the Wallet client does not immediately reflect remote updates, re-download or refresh the pass using the original Add-to-Wallet link to force an update.
- This test strictly follows the **happy path** and does not include exploratory or negative scenarios.

---

**End of Test Case: MTC-LC-001**
