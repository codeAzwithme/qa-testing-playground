# Restful-Booker API - Test Cases

**Source:** Restful-booker.pdf (apidoc export, 24/05/2026)
**Base URL:** https://restful-booker.herokuapp.com
**Test data:** admin / password123 (from docs). Booking IDs and names are examples, create your own where noted.

**Written for:** QA engineers importing these into Jira.

## Conventions

- Each test case has: Title, Summary, Precondition, Steps to reproduce, Expected Result, Actual Result, Status.
- **Summary** carries `Priority` (P1 = critical path, P2 = important, P3 = edge / negative), `Type` (Functional, Negative, Boundary, Integration, Documentation) and `Source`.
- **Source:** `Doc` = behaviour stated in the PDF. `Inferred` = PDF is silent, expected result is a standard REST assumption. Confirm with the team before marking pass.
- **Actual Result** is blank until the case is run. **Status** values: Not Run, Pass, Fail, Blocked.
- **Doc defects** are flagged in the Summary or Expected Result.

---

## Auth - CreateToken (POST /auth)

### TC-AUTH-01
- **Title:** Create token with valid credentials returns token
- **Summary:** P1 | Functional | Source: Doc. Valid admin credentials return an auth token.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. Send `POST https://restful-booker.herokuapp.com/auth` with header `Content-Type: application/json`.
  2. Body: `{"username": "admin", "password": "password123"}`.
  3. Read the response.
- **Expected Result:** HTTP 200. Body contains `token` as a non-empty string.
- **Actual Result:**
- **Status:** Not Run

### TC-AUTH-02
- **Title:** Create token with wrong password does not return token
- **Summary:** P1 | Negative | Source: Inferred. Wrong password must not yield a token.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `POST /auth` with `Content-Type: application/json`.
  2. Body: `{"username": "admin", "password": "wrongpass"}`.
- **Expected Result:** No `token` in the response body. Response contains an error reason (for example `"reason": "Bad credentials"`). Status code must be recorded; live API returns 200 with the reason field, so confirm the expected code.
- **Actual Result:**
- **Status:** Not Run

### TC-AUTH-03
- **Title:** Create token with unknown username is rejected
- **Summary:** P2 | Negative | Source: Inferred. Unknown user must not yield a token.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `POST /auth` with body `{"username": "nobody", "password": "password123"}`.
- **Expected Result:** No `token` returned. Error reason returned, as in TC-AUTH-02.
- **Actual Result:**
- **Status:** Not Run

### TC-AUTH-04
- **Title:** Username is case sensitive
- **Summary:** P3 | Boundary | Source: Inferred. `Admin` must not authenticate as `admin`.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `POST /auth` with body `{"username": "Admin", "password": "password123"}`.
- **Expected Result:** No `token` returned. Behaviour must be the same as TC-AUTH-02. If the team decides usernames are case-insensitive, update this case.
- **Actual Result:**
- **Status:** Not Run

### TC-AUTH-05
- **Title:** Missing password field
- **Summary:** P2 | Negative | Source: Inferred. Request without password must be rejected cleanly.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `POST /auth` with body `{"username": "admin"}`.
- **Expected Result:** No `token` returned. Error response (not a 5xx).
- **Actual Result:**
- **Status:** Not Run

### TC-AUTH-06
- **Title:** Empty body
- **Summary:** P3 | Negative | Source: Inferred. Empty JSON object must be rejected cleanly.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `POST /auth` with `Content-Type: application/json` and body `{}`.
- **Expected Result:** No `token` returned. Error response (not a 5xx).
- **Actual Result:**
- **Status:** Not Run

### TC-AUTH-07
- **Title:** Malformed JSON body
- **Summary:** P3 | Negative | Source: Inferred. Truncated JSON must not crash the API.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `POST /auth` with body `{"username": "admin", "password": ` (truncated).
- **Expected Result:** HTTP 400 (or other 4xx). No token. No 5xx and no stack trace in the body.
- **Actual Result:**
- **Status:** Not Run

### TC-AUTH-08
- **Title:** SQL injection string in username is rejected
- **Summary:** P2 | Negative (security) | Source: Inferred. Injection payload must not bypass login.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `POST /auth` with body `{"username": "admin' OR '1'='1", "password": "x"}`.
- **Expected Result:** No `token` returned. No 5xx.
- **Actual Result:**
- **Status:** Not Run

### TC-AUTH-09
- **Title:** Token from CreateToken authorises PUT and DELETE
- **Summary:** P1 | Integration | Source: Doc. Token works as auth on protected endpoints.
- **Precondition:** A booking exists (see TC-CB-01).
- **Steps to reproduce:**
  1. `POST /auth` and copy the `token` value.
  2. `PUT /booking/{id}` with header `Cookie: token={token}` and a valid body.
- **Expected Result:** HTTP 200. Booking is updated.
- **Actual Result:**
- **Status:** Not Run

---

## Booking - GetBookingIds (GET /booking)

### TC-GBI-01
- **Title:** Get all booking IDs with no query parameters
- **Summary:** P1 | Functional | Source: Doc. Unfiltered list returns all booking IDs.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `GET https://restful-booker.herokuapp.com/booking`.
- **Expected Result:** HTTP 200. Body is a JSON array. Each element is an object with a numeric `bookingid`. IDs are unique.
- **Actual Result:**
- **Status:** Not Run

### TC-GBI-02
- **Title:** Filter by firstname
- **Summary:** P1 | Functional | Source: Doc. Firstname filter returns only matching bookings.
- **Precondition:** A booking with firstname `Sally` exists.
- **Steps to reproduce:**
  1. `GET /booking?firstname=sally`.
- **Expected Result:** HTTP 200. Array contains only booking IDs whose firstname matches. Each ID resolves to a booking with firstname Sally (verify with TC-GB-01).
- **Actual Result:**
- **Status:** Not Run

### TC-GBI-03
- **Title:** Filter by lastname
- **Summary:** P1 | Functional | Source: Doc. Lastname filter returns only matching bookings.
- **Precondition:** A booking with lastname `Brown` exists.
- **Steps to reproduce:**
  1. `GET /booking?lastname=Brown`.
- **Expected Result:** HTTP 200. Array contains only IDs of bookings with lastname Brown.
- **Actual Result:**
- **Status:** Not Run

### TC-GBI-04
- **Title:** Filter by firstname and lastname together
- **Summary:** P2 | Functional | Source: Doc. Combined filters narrow the result set.
- **Precondition:** Bookings for Sally and Brown exist.
- **Steps to reproduce:**
  1. `GET /booking?firstname=Sally&lastname=Brown`.
- **Expected Result:** HTTP 200. Only bookings matching both fields are returned. Result is a subset of TC-GBI-02 and TC-GBI-03 results.
- **Actual Result:**
- **Status:** Not Run

### TC-GBI-05
- **Title:** Filter by checkin date
- **Summary:** P1 | Functional | Source: Doc. Checkin filter returns bookings on or after the date.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `GET /booking?checkin=2014-03-13`.
- **Expected Result:** HTTP 200. Every returned booking has checkin on or after 2014-03-13.
- **Actual Result:**
- **Status:** Not Run

### TC-GBI-06
- **Title:** Filter by checkout date
- **Summary:** P2 | Functional | Source: Doc. Checkout filter returns bookings on or after the date.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `GET /booking?checkout=2019-01-01`.
- **Expected Result:** HTTP 200. Every returned booking has checkout on or after 2019-01-01.
- **Actual Result:**
- **Status:** Not Run

### TC-GBI-07
- **Title:** Filter with no matches returns empty array
- **Summary:** P2 | Boundary | Source: Inferred. No match gives empty list, not an error.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `GET /booking?firstname=ZzzNoSuchName`.
- **Expected Result:** HTTP 200. Body is `[]`. Not a 404 and not a 5xx.
- **Actual Result:**
- **Status:** Not Run

### TC-GBI-08
- **Title:** Checkin date in far future returns empty array
- **Summary:** P3 | Boundary | Source: Inferred. Far-future date matches nothing.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `GET /booking?checkin=2999-12-31`.
- **Expected Result:** HTTP 200. Body is `[]`.
- **Actual Result:**
- **Status:** Not Run

### TC-GBI-09
- **Title:** Invalid date format is rejected
- **Summary:** P2 | Negative | Source: Doc (format rule) / Inferred (error). Doc says format must be CCYY-MM-DD. Error code is not stated.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `GET /booking?checkin=13-03-2014`.
- **Expected Result:** Error response (4xx preferred). Must not return unfiltered results silently. Record the actual behaviour and raise with the team if it ignores the filter.
- **Actual Result:**
- **Status:** Not Run

### TC-GBI-10
- **Title:** Non-date string in checkin is rejected
- **Summary:** P3 | Negative | Source: Inferred. Garbage date value must not crash the API.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `GET /booking?checkin=abc`.
- **Expected Result:** Error response (4xx preferred). No 5xx.
- **Actual Result:**
- **Status:** Not Run

### TC-GBI-11
- **Title:** Name filter is case sensitive or insensitive (clarify)
- **Summary:** P3 | Boundary | Source: Inferred. Doc is silent on filter case rules.
- **Precondition:** A booking with firstname `Sally` exists.
- **Steps to reproduce:**
  1. `GET /booking?firstname=SALLY`.
  2. `GET /booking?firstname=sally` and compare with step 1.
- **Expected Result:** Behaviour is documented and consistent. Doc is silent, so raise a question with the team before marking pass.
- **Actual Result:**
- **Status:** Not Run

### TC-GBI-12
- **Title:** Filter with special characters in name
- **Summary:** P3 | Boundary | Source: Inferred. URL-encoded apostrophe handled safely.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `GET /booking?lastname=O%27Brien` (URL encoded apostrophe).
- **Expected Result:** HTTP 200. Array returned (empty if no match). No 5xx.
- **Actual Result:**
- **Status:** Not Run

---

## Booking - GetBooking (GET /booking/:id)

### TC-GB-01
- **Title:** Get existing booking as JSON (default)
- **Summary:** P1 | Functional | Source: Doc. Booking returned as JSON when no Accept header is sent.
- **Precondition:** Booking 1 exists with the example data (Sally Brown).
- **Steps to reproduce:**
  1. `GET https://restful-booker.herokuapp.com/booking/1` with no Accept header.
- **Expected Result:** HTTP 200. `Content-Type` is `application/json`. Body has:
  - `firstname` (string) = "Sally"
  - `lastname` (string) = "Brown"
  - `totalprice` (number)
  - `depositpaid` (boolean)
  - `bookingdates` (object) with `checkin` and `checkout` (dates CCYY-MM-DD)
  - `additionalneeds` (string)
- **Actual Result:**
- **Status:** Not Run

### TC-GB-02
- **Title:** Get existing booking as XML
- **Summary:** P2 | Functional | Source: Doc. Accept header switches output to XML.
- **Precondition:** Booking 1 exists.
- **Steps to reproduce:**
  1. `GET /booking/1` with header `Accept: application/xml`.
- **Expected Result:** HTTP 200. Body is XML with root `<booking>` containing the same fields as TC-GB-01.
- **Actual Result:**
- **Status:** Not Run

### TC-GB-03
- **Title:** Response field values match create request
- **Summary:** P1 | Integration | Source: Doc. Stored data equals data sent on create.
- **Precondition:** Booking created in TC-CB-01 (ID stored as `{id}`).
- **Steps to reproduce:**
  1. `GET /booking/{id}`.
- **Expected Result:** Every field matches the body sent in TC-CB-01.
- **Actual Result:**
- **Status:** Not Run

### TC-GB-04
- **Title:** Non-existent booking ID
- **Summary:** P2 | Negative | Source: Inferred. Unknown ID returns not found.
- **Precondition:** API reachable. ID 999999999 does not exist.
- **Steps to reproduce:**
  1. `GET /booking/999999999`.
- **Expected Result:** HTTP 404. No booking data in body.
- **Actual Result:**
- **Status:** Not Run

### TC-GB-05
- **Title:** Non-numeric booking ID
- **Summary:** P3 | Negative | Source: Inferred. Letters in ID handled safely.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `GET /booking/abc`.
- **Expected Result:** HTTP 404 (or other 4xx). No 5xx.
- **Actual Result:**
- **Status:** Not Run

### TC-GB-06
- **Title:** Negative booking ID
- **Summary:** P3 | Boundary | Source: Inferred. Negative ID handled safely.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `GET /booking/-1`.
- **Expected Result:** HTTP 404 (or other 4xx). No 5xx.
- **Actual Result:**
- **Status:** Not Run

### TC-GB-07
- **Title:** Every ID from GetBookingIds resolves
- **Summary:** P2 | Integration | Source: Doc. List and detail endpoints are consistent.
- **Precondition:** At least one booking exists.
- **Steps to reproduce:**
  1. `GET /booking` and collect all `bookingid` values.
  2. For each ID, `GET /booking/{id}`.
- **Expected Result:** Every call returns HTTP 200 with a booking object. No ID returns 404.
- **Actual Result:**
- **Status:** Not Run

---

## Booking - CreateBooking (POST /booking)

### TC-CB-01
- **Title:** Create booking with JSON payload
- **Summary:** P1 | Functional | Source: Doc. Valid JSON creates a booking and returns its ID.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `POST https://restful-booker.herokuapp.com/booking` with `Content-Type: application/json`.
  2. Body:
     ```json
     {"firstname": "Jim", "lastname": "Brown", "totalprice": 111, "depositpaid": true,
      "bookingdates": {"checkin": "2018-01-01", "checkout": "2019-01-01"},
      "additionalneeds": "Breakfast"}
     ```
- **Expected Result:** HTTP 200. Body has `bookingid` (number) and `booking` object. The `booking` object matches the request values exactly.
- **Actual Result:**
- **Status:** Not Run

### TC-CB-02
- **Title:** Create booking with XML payload
- **Summary:** P2 | Functional | Source: Doc. XML request body is accepted.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `POST /booking` with `Content-Type: text/xml`.
  2. Body: `<booking><firstname>Jim</firstname><lastname>Brown</lastname><totalprice>111</totalprice><depositpaid>true</depositpaid><bookingdates><checkin>2018-01-01</checkin><checkout>2019-01-01</checkout></bookingdates><additionalneeds>Breakfast</additionalneeds></booking>`
- **Expected Result:** HTTP 200. Body is XML with root `<created-booking>` and a `<bookingid>`.
- **Actual Result:**
- **Status:** Not Run

### TC-CB-03
- **Title:** Create booking with URL-encoded payload
- **Summary:** P2 | Functional | Source: Doc. Form-encoded body is accepted. Note: the PDF example is cut off in the export; confirm the exact bookingdates key format on the live API before final sign-off.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `POST /booking` with `Content-Type: application/x-www-form-urlencoded`.
  2. Body: `firstname=Jim&lastname=Brown&totalprice=111&depositpaid=true&bookingdates.checkin=2018-01-01&bookingdates.checkout=2019-01-01&additionalneeds=Breakfast`
- **Expected Result:** HTTP 200. Response contains `bookingid` and the booking values.
- **Actual Result:**
- **Status:** Not Run

### TC-CB-04
- **Title:** Create booking with XML response requested
- **Summary:** P2 | Functional | Source: Doc. Accept header switches response to XML.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `POST /booking` with `Content-Type: application/json` and `Accept: application/xml`. Use the JSON body from TC-CB-01.
- **Expected Result:** HTTP 200. Body is XML with root `<created-booking>`.
- **Actual Result:**
- **Status:** Not Run

### TC-CB-05
- **Title:** Create booking needs no authentication
- **Summary:** P1 | Functional | Source: Doc. Only PUT, PATCH and DELETE require auth.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `POST /booking` with a valid JSON body and no `Cookie` or `Authorization` header.
- **Expected Result:** HTTP 200 with `bookingid`.
- **Actual Result:**
- **Status:** Not Run

### TC-CB-06
- **Title:** Created booking appears in GetBookingIds
- **Summary:** P2 | Integration | Source: Doc. New booking is searchable.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. Create a booking (TC-CB-01) and note `bookingid`.
  2. `GET /booking?firstname=Jim&lastname=Brown`.
- **Expected Result:** The new `bookingid` is in the array.
- **Actual Result:**
- **Status:** Not Run

### TC-CB-07
- **Title:** Created booking is retrievable by ID
- **Summary:** P1 | Integration | Source: Doc. New booking can be read back.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. Create a booking and note `bookingid`.
  2. `GET /booking/{bookingid}`.
- **Expected Result:** HTTP 200. Data matches the create request.
- **Actual Result:**
- **Status:** Not Run

### TC-CB-08
- **Title:** Optional additionalneeds omitted
- **Summary:** P2 | Boundary | Source: Inferred. The PDF does not mark any create field as optional. Confirm the expected result with the team.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `POST /booking` with the JSON from TC-CB-01 minus `additionalneeds`.
- **Expected Result:** Clarify the rule. Either HTTP 200 with the field absent or null, or a 4xx. Record actual behaviour and raise as a question.
- **Actual Result:**
- **Status:** Not Run

### TC-CB-09
- **Title:** Missing firstname
- **Summary:** P2 | Negative | Source: Inferred. Required field missing must be rejected.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `POST /booking` with the JSON from TC-CB-01 minus `firstname`.
- **Expected Result:** Request rejected with a 4xx. No booking created. No 5xx.
- **Actual Result:**
- **Status:** Not Run

### TC-CB-10
- **Title:** Missing bookingdates
- **Summary:** P2 | Negative | Source: Inferred. Required object missing must be rejected.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `POST /booking` with the JSON from TC-CB-01 minus `bookingdates`.
- **Expected Result:** Request rejected with a 4xx. No booking created. No 5xx.
- **Actual Result:**
- **Status:** Not Run

### TC-CB-11
- **Title:** Empty firstname string
- **Summary:** P3 | Boundary | Source: Inferred. Empty string rule is not documented.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `POST /booking` with `"firstname": ""` and other fields valid.
- **Expected Result:** Request rejected with a 4xx, or accepted and echoed back as empty. Team decides the rule. Record actual behaviour.
- **Actual Result:**
- **Status:** Not Run

### TC-CB-12
- **Title:** totalprice as string instead of number
- **Summary:** P2 | Negative | Source: Inferred. Wrong data type for price.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `POST /booking` with `"totalprice": "111"` (quoted).
- **Expected Result:** Rejected with a 4xx, or accepted and stored as a number. Must not store a string silently. Record actual behaviour.
- **Actual Result:**
- **Status:** Not Run

### TC-CB-13
- **Title:** depositpaid as non-boolean
- **Summary:** P3 | Negative | Source: Inferred. Wrong data type for flag.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `POST /booking` with `"depositpaid": "yes"`.
- **Expected Result:** Rejected with a 4xx. No 5xx.
- **Actual Result:**
- **Status:** Not Run

### TC-CB-14
- **Title:** totalprice boundary zero
- **Summary:** P3 | Boundary | Source: Inferred. Zero price accepted.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `POST /booking` with `"totalprice": 0`.
- **Expected Result:** HTTP 200. `totalprice` echoed back as `0`.
- **Actual Result:**
- **Status:** Not Run

### TC-CB-15
- **Title:** totalprice negative
- **Summary:** P3 | Boundary | Source: Inferred. Negative price rule is not documented.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `POST /booking` with `"totalprice": -1`.
- **Expected Result:** Team decides whether negative prices are valid. Record actual behaviour and raise as a question. Must not 5xx.
- **Actual Result:**
- **Status:** Not Run

### TC-CB-16
- **Title:** Very large totalprice
- **Summary:** P3 | Boundary | Source: Inferred. Max 32-bit integer price.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `POST /booking` with `"totalprice": 2147483647`.
- **Expected Result:** HTTP 200 with the same value echoed, or a 4xx with a clear reason. No overflow or 5xx.
- **Actual Result:**
- **Status:** Not Run

### TC-CB-17
- **Title:** Checkout earlier than checkin
- **Summary:** P2 | Boundary | Source: Inferred. Doc has no date-order rule. Business rule must be confirmed.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `POST /booking` with `checkin: "2019-01-01"` and `checkout: "2018-01-01"`.
- **Expected Result:** Team decides. Either rejected with a 4xx, or accepted. Record and raise as a question.
- **Actual Result:**
- **Status:** Not Run

### TC-CB-18
- **Title:** Checkin equals checkout (same day)
- **Summary:** P3 | Boundary | Source: Inferred. Same-day stay rule is not documented.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `POST /booking` with `checkin` and `checkout` both `2018-01-01`.
- **Expected Result:** Team decides the rule. Record actual behaviour.
- **Actual Result:**
- **Status:** Not Run

### TC-CB-19
- **Title:** Invalid date format in bookingdates
- **Summary:** P2 | Negative | Source: Inferred. Non CCYY-MM-DD date must be rejected.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `POST /booking` with `"checkin": "01/01/2018"`.
- **Expected Result:** Rejected with a 4xx. Not created. No 5xx.
- **Actual Result:**
- **Status:** Not Run

### TC-CB-20
- **Title:** Impossible calendar date
- **Summary:** P3 | Boundary | Source: Inferred. 30 February must be rejected.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `POST /booking` with `"checkin": "2018-02-30"`.
- **Expected Result:** Rejected with a 4xx. Not created.
- **Actual Result:**
- **Status:** Not Run

### TC-CB-21
- **Title:** Unicode and special characters in names
- **Summary:** P3 | Boundary | Source: Inferred. Non-ASCII text survives round trip.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `POST /booking` with `"firstname": "José"`, `"lastname": "O'Brien-Ñúñez"`.
  2. `GET /booking/{bookingid}`.
- **Expected Result:** Create returns 200. GET returns the same strings with no mojibake or truncation.
- **Actual Result:**
- **Status:** Not Run

### TC-CB-22
- **Title:** Very long firstname
- **Summary:** P3 | Boundary | Source: Inferred. 1000-character name handled safely.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `POST /booking` with `firstname` of 1000 characters.
- **Expected Result:** Accepted and echoed in full, or rejected with a 4xx. No 5xx and no silent truncation.
- **Actual Result:**
- **Status:** Not Run

### TC-CB-23
- **Title:** Body Content-Type says JSON but body is XML
- **Summary:** P3 | Negative | Source: Inferred. Mismatched content type must be rejected.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `POST /booking` with `Content-Type: application/json` and XML body from TC-CB-02.
- **Expected Result:** Rejected with a 4xx. No booking created. No 5xx.
- **Actual Result:**
- **Status:** Not Run

---

## Booking - UpdateBooking (PUT /booking/:id)

### TC-UB-01
- **Title:** Update with Cookie token
- **Summary:** P1 | Functional | Source: Doc. Full update authorised by token cookie.
- **Precondition:** Token obtained from TC-AUTH-01. Booking 1 exists.
- **Steps to reproduce:**
  1. `PUT https://restful-booker.herokuapp.com/booking/1` with `Content-Type: application/json`, `Accept: application/json`, `Cookie: token={token}`.
  2. Body: `{"firstname": "James", "lastname": "Brown", "totalprice": 111, "depositpaid": true, "bookingdates": {"checkin": "2018-01-01", "checkout": "2019-01-01"}, "additionalneeds": "Breakfast"}`.
- **Expected Result:** HTTP 200. Body shows `firstname` = "James" and all other submitted values.
- **Actual Result:**
- **Status:** Not Run

### TC-UB-02
- **Title:** Update with Basic auth
- **Summary:** P1 | Functional | Source: Doc. Full update authorised by Basic header.
- **Precondition:** Booking 1 exists.
- **Steps to reproduce:**
  1. `PUT /booking/1` with `Authorization: Basic YWRtaW46cGFzc3dvcmQxMjM=` (base64 of admin:password123) and a valid JSON body.
- **Expected Result:** HTTP 200. Booking updated.
- **Actual Result:**
- **Status:** Not Run

### TC-UB-03
- **Title:** Update with XML body and XML response
- **Summary:** P2 | Functional | Source: Doc. XML in, XML out.
- **Precondition:** Booking 1 exists.
- **Steps to reproduce:**
  1. `PUT /booking/1` with `Content-Type: text/xml`, `Accept: application/xml`, Basic auth header, and the XML body from the PDF example.
- **Expected Result:** HTTP 200. Body is XML with root `<booking>` showing updated values.
- **Actual Result:**
- **Status:** Not Run

### TC-UB-04
- **Title:** Update with URL-encoded body
- **Summary:** P3 | Functional | Source: Doc. Form-encoded update. Note: the PDF example is cut off; confirm the full body on the live API.
- **Precondition:** Booking 1 exists.
- **Steps to reproduce:**
  1. `PUT /booking/1` with `Content-Type` and `Accept` set to `application/x-www-form-urlencoded`, Basic auth, and body `firstname=Jim&lastname=Brown&totalprice=111&depositpaid=true`.
- **Expected Result:** HTTP 200. Response contains the updated values in URL-encoded form.
- **Actual Result:**
- **Status:** Not Run

### TC-UB-05
- **Title:** Update without any auth
- **Summary:** P1 | Negative (security) | Source: Inferred. Unauthenticated update must be blocked.
- **Precondition:** Booking 1 exists.
- **Steps to reproduce:**
  1. `PUT /booking/1` with a valid JSON body and no `Cookie` or `Authorization` header.
  2. `GET /booking/1` to confirm no change.
- **Expected Result:** HTTP 403. Booking not changed.
- **Actual Result:**
- **Status:** Not Run

### TC-UB-06
- **Title:** Update with invalid token
- **Summary:** P1 | Negative (security) | Source: Inferred. Fake token must be rejected.
- **Precondition:** Booking 1 exists.
- **Steps to reproduce:**
  1. `PUT /booking/1` with `Cookie: token=invalidtoken123` and a valid body.
- **Expected Result:** HTTP 403. Booking not changed.
- **Actual Result:**
- **Status:** Not Run

### TC-UB-07
- **Title:** Update with wrong Basic auth password
- **Summary:** P2 | Negative (security) | Source: Inferred. Bad Basic credentials must be rejected.
- **Precondition:** Booking 1 exists.
- **Steps to reproduce:**
  1. Base64-encode `admin:wrongpass` and send as `Authorization: Basic ...`.
  2. `PUT /booking/1` with a valid body.
- **Expected Result:** HTTP 403. Booking not changed.
- **Actual Result:**
- **Status:** Not Run

### TC-UB-08
- **Title:** Update with a token from a previous session (reuse)
- **Summary:** P3 | Negative | Source: Inferred. Doc does not state token lifetime. Confirm before running.
- **Precondition:** A saved token that is expired (or the agreed wait period has passed). Booking 1 exists.
- **Steps to reproduce:**
  1. Get a token. Save it. Wait the agreed period (or use a token known to be expired).
  2. `PUT /booking/1` with that token.
- **Expected Result:** Confirm rule. If expired, HTTP 403.
- **Actual Result:**
- **Status:** Not Run

### TC-UB-09
- **Title:** Update non-existent booking
- **Summary:** P2 | Negative | Source: Inferred. Update on unknown ID must not create data.
- **Precondition:** Valid token. ID 999999999 does not exist.
- **Steps to reproduce:**
  1. `PUT /booking/999999999` with valid token and valid body.
- **Expected Result:** HTTP 404 (or 405/400). No booking created. No 5xx.
- **Actual Result:**
- **Status:** Not Run

### TC-UB-10
- **Title:** Update with missing fields (full replacement)
- **Summary:** P2 | Boundary | Source: Inferred. PUT is documented as updating the booking. Doc does not say whether omitted fields are cleared. Confirm.
- **Precondition:** Valid token. Booking 1 exists with `additionalneeds` set.
- **Steps to reproduce:**
  1. `PUT /booking/1` with a body that omits `additionalneeds`.
  2. `GET /booking/1`.
- **Expected Result:** Record the behaviour. Team confirms whether `additionalneeds` is cleared or kept.
- **Actual Result:**
- **Status:** Not Run

### TC-UB-11
- **Title:** Update with malformed JSON
- **Summary:** P3 | Negative | Source: Inferred. Broken JSON must not change data.
- **Precondition:** Valid token. Booking 1 exists.
- **Steps to reproduce:**
  1. `PUT /booking/1` with valid token and body `{"firstname": "James",`.
- **Expected Result:** HTTP 400 (or other 4xx). Booking not changed. No 5xx.
- **Actual Result:**
- **Status:** Not Run

### TC-UB-12
- **Title:** Update persists (read back)
- **Summary:** P1 | Integration | Source: Doc. Updated value is stored.
- **Precondition:** Valid token. A booking `{id}` exists.
- **Steps to reproduce:**
  1. `PUT /booking/{id}` with `firstname` = "Updated".
  2. `GET /booking/{id}`.
- **Expected Result:** `firstname` returned as "Updated".
- **Actual Result:**
- **Status:** Not Run

---

## Booking - PartialUpdateBooking (PATCH /booking/:id)

### TC-PU-01
- **Title:** Patch firstname only
- **Summary:** P1 | Functional | Source: Doc. Partial update changes only sent fields.
- **Precondition:** Booking exists with known values. Record them first. Valid token.
- **Steps to reproduce:**
  1. `PATCH https://restful-booker.herokuapp.com/booking/{id}` with `Content-Type: application/json`, `Cookie: token={token}`.
  2. Body: `{"firstname": "James"}`.
  3. `GET /booking/{id}`.
- **Expected Result:** HTTP 200. `firstname` = "James". All other fields unchanged from the recorded values.
- **Actual Result:**
- **Status:** Not Run

### TC-PU-02
- **Title:** Patch lastname and firstname together
- **Summary:** P2 | Functional | Source: Doc. Multiple fields in one patch.
- **Precondition:** Booking `{id}` exists. Valid auth.
- **Steps to reproduce:**
  1. `PATCH /booking/{id}` with body `{"firstname": "James", "lastname": "Brown"}` and valid auth.
- **Expected Result:** HTTP 200. Both fields updated. Others unchanged.
- **Actual Result:**
- **Status:** Not Run

### TC-PU-03
- **Title:** Patch nested bookingdates with one date
- **Summary:** P2 | Boundary | Source: Inferred. Partial nested object behaviour is not documented.
- **Precondition:** Booking `{id}` exists. Valid auth.
- **Steps to reproduce:**
  1. Record current `bookingdates`.
  2. `PATCH /booking/{id}` with `{"bookingdates": {"checkin": "2020-05-01"}}`.
  3. `GET /booking/{id}`.
- **Expected Result:** Team confirms rule. Expected: `checkin` = 2020-05-01 and `checkout` unchanged. Record actual; if `checkout` is removed, log as defect.
- **Actual Result:**
- **Status:** Not Run

### TC-PU-04
- **Title:** Patch with empty body
- **Summary:** P3 | Boundary | Source: Inferred. Empty patch should be harmless.
- **Precondition:** Booking `{id}` exists. Valid auth.
- **Steps to reproduce:**
  1. `PATCH /booking/{id}` with body `{}` and valid auth.
- **Expected Result:** HTTP 200 with no fields changed, or a 4xx. No 5xx.
- **Actual Result:**
- **Status:** Not Run

### TC-PU-05
- **Title:** Patch with XML body
- **Summary:** P3 | Functional | Source: Doc. XML partial update.
- **Precondition:** Booking `{id}` exists.
- **Steps to reproduce:**
  1. `PATCH /booking/{id}` with `Content-Type: text/xml`, `Accept: application/xml`, Basic auth, body `<booking><firstname>James</firstname></booking>`.
- **Expected Result:** HTTP 200. XML response with `firstname` = James.
- **Actual Result:**
- **Status:** Not Run

### TC-PU-06
- **Title:** Patch without auth
- **Summary:** P1 | Negative (security) | Source: Inferred. Unauthenticated patch must be blocked.
- **Precondition:** Booking `{id}` exists.
- **Steps to reproduce:**
  1. `PATCH /booking/{id}` with body `{"firstname": "Hacker"}` and no auth.
- **Expected Result:** HTTP 403. Booking unchanged.
- **Actual Result:**
- **Status:** Not Run

### TC-PU-07
- **Title:** Patch with invalid token
- **Summary:** P2 | Negative (security) | Source: Inferred. Fake token must be rejected.
- **Precondition:** Booking `{id}` exists.
- **Steps to reproduce:**
  1. `PATCH /booking/{id}` with `Cookie: token=bad`.
- **Expected Result:** HTTP 403. Booking unchanged.
- **Actual Result:**
- **Status:** Not Run

### TC-PU-08
- **Title:** Patch non-existent booking
- **Summary:** P3 | Negative | Source: Inferred. Patch on unknown ID fails cleanly.
- **Precondition:** Valid auth. ID 999999999 does not exist.
- **Steps to reproduce:**
  1. `PATCH /booking/999999999` with valid auth and body.
- **Expected Result:** HTTP 404 (or 405/400). No 5xx.
- **Actual Result:**
- **Status:** Not Run

### TC-PU-09
- **Title:** Doc defect: curl examples use PUT for PartialUpdate
- **Summary:** P2 | Documentation | Source: Doc defect. PDF page 14 lists method PATCH, but the curl examples under PartialUpdateBooking use `-X PUT`. Confirm which method the live API accepts.
- **Precondition:** Booking `{id}` exists. Valid auth.
- **Steps to reproduce:**
  1. `PUT /booking/{id}` with body `{"firstname": "James"}` and valid auth.
  2. `PATCH /booking/{id}` with the same body and auth.
- **Expected Result:** PATCH returns 200 with a partial update. Record whether PUT also does a partial update or does a full replace. Log the doc inconsistency.
- **Actual Result:**
- **Status:** Not Run

---

## Booking - DeleteBooking (DELETE /booking/:id)

### TC-DB-01
- **Title:** Delete with Cookie token
- **Summary:** P1 | Functional | Source: Doc. Delete authorised by token cookie. Doc defect: success table says "Default HTTP 201 response", but the example response shows `HTTP/1.1 200 OK`. Log the actual status and raise the inconsistency with the team.
- **Precondition:** A booking exists (create via TC-CB-01). Token from TC-AUTH-01.
- **Steps to reproduce:**
  1. `DELETE https://restful-booker.herokuapp.com/booking/{id}` with `Content-Type: application/json`, `Cookie: token={token}`.
- **Expected Result:** Success response. Status code is recorded.
- **Actual Result:**
- **Status:** Not Run

### TC-DB-02
- **Title:** Delete with Basic auth
- **Summary:** P1 | Functional | Source: Doc. Delete authorised by Basic header.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. Create a booking. Note `{id}`.
  2. `DELETE /booking/{id}` with `Authorization: Basic YWRtaW46cGFzc3dvcmQxMjM=`.
- **Expected Result:** Success response, same as TC-DB-01.
- **Actual Result:**
- **Status:** Not Run

### TC-DB-03
- **Title:** Deleted booking is gone
- **Summary:** P1 | Integration | Source: Inferred. Deleted booking is not retrievable or listed.
- **Precondition:** Valid token. A booking exists.
- **Steps to reproduce:**
  1. Create a booking and delete it (TC-DB-01).
  2. `GET /booking/{id}`.
  3. `GET /booking?firstname=...` using the deleted booking's firstname.
- **Expected Result:** GET by ID returns HTTP 404. ID is not in the GetBookingIds list.
- **Actual Result:**
- **Status:** Not Run

### TC-DB-04
- **Title:** Delete without auth
- **Summary:** P1 | Negative (security) | Source: Inferred. Unauthenticated delete must be blocked.
- **Precondition:** A booking `{id}` exists.
- **Steps to reproduce:**
  1. `DELETE /booking/{id}` with no `Cookie` or `Authorization` header.
  2. `GET /booking/{id}`.
- **Expected Result:** HTTP 403. Booking still exists.
- **Actual Result:**
- **Status:** Not Run

### TC-DB-05
- **Title:** Delete with invalid token
- **Summary:** P2 | Negative (security) | Source: Inferred. Fake token must be rejected.
- **Precondition:** A booking `{id}` exists.
- **Steps to reproduce:**
  1. `DELETE /booking/{id}` with `Cookie: token=invalid`.
- **Expected Result:** HTTP 403. Booking still exists.
- **Actual Result:**
- **Status:** Not Run

### TC-DB-06
- **Title:** Delete with wrong Basic password
- **Summary:** P2 | Negative (security) | Source: Inferred. Bad Basic credentials must be rejected.
- **Precondition:** A booking `{id}` exists.
- **Steps to reproduce:**
  1. `DELETE /booking/{id}` with Basic auth for `admin:wrongpass`.
- **Expected Result:** HTTP 403. Booking still exists.
- **Actual Result:**
- **Status:** Not Run

### TC-DB-07
- **Title:** Delete the same booking twice
- **Summary:** P2 | Negative | Source: Inferred. Repeat delete fails cleanly.
- **Precondition:** Valid auth. A booking `{id}` exists.
- **Steps to reproduce:**
  1. Delete booking `{id}` (TC-DB-01).
  2. Delete `{id}` again with the same auth.
- **Expected Result:** Second call returns 404 (or other 4xx). No 5xx.
- **Actual Result:**
- **Status:** Not Run

### TC-DB-08
- **Title:** Delete non-existent booking
- **Summary:** P3 | Negative | Source: Inferred. Delete on unknown ID fails cleanly.
- **Precondition:** Valid auth. ID 999999999 does not exist.
- **Steps to reproduce:**
  1. `DELETE /booking/999999999` with valid auth.
- **Expected Result:** HTTP 404 (or other 4xx). No 5xx.
- **Actual Result:**
- **Status:** Not Run

---

## Ping (GET /ping)

### TC-PING-01
- **Title:** Health check returns 201
- **Summary:** P1 | Functional | Source: Doc. The doc describes a "Default HTTP 201 response", the same wording used for DELETE. Confirm the body.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `GET https://restful-booker.herokuapp.com/ping`.
- **Expected Result:** HTTP 201 Created. Response body is `OK` or empty, per the doc's success table.
- **Actual Result:**
- **Status:** Not Run

### TC-PING-02
- **Title:** Ping does not need authentication
- **Summary:** P3 | Functional | Source: Inferred. Health check is public.
- **Precondition:** API reachable.
- **Steps to reproduce:**
  1. `GET /ping` with no headers.
- **Expected Result:** HTTP 201. No 401 or 403.
- **Actual Result:**
- **Status:** Not Run

---

## End-to-end

### TC-E2E-01
- **Title:** Full booking lifecycle
- **Summary:** P1 | Integration | Source: Doc. Auth, create, read, patch, put, delete, verify gone.
- **Precondition:** API reachable. Admin credentials admin / password123.
- **Steps to reproduce:**
  1. `POST /auth` with admin/password123. Save token.
  2. `POST /booking` with Jim Brown data. Save `bookingid`.
  3. `GET /booking/{bookingid}`. Confirm data matches step 2.
  4. `PATCH /booking/{bookingid}` with `{"firstname": "James"}` and token cookie.
  5. `GET /booking/{bookingid}`. Confirm `firstname` = James, other fields unchanged.
  6. `PUT /booking/{bookingid}` with a full body and Basic auth.
  7. `GET /booking/{bookingid}`. Confirm full update.
  8. `DELETE /booking/{bookingid}` with token cookie.
  9. `GET /booking/{bookingid}`. Confirm 404.
- **Expected Result:** Each step returns the expected status and data above.
- **Actual Result:**
- **Status:** Not Run
