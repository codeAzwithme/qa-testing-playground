# API Test Cases Summary

22 API test cases, all in [src/test/java/com/restfulbooker/automation/tests/](src/test/java/com/restfulbooker/automation/tests/) inside the `Project Task/RestAssured_API_Testing_Framework_UsingJava` project. [testng.xml](testng.xml) runs them all.

- [PingTest](src/test/java/com/restfulbooker/automation/tests/PingTest.java) (1)
  - `healthCheckReturnsCreated` checks `GET /ping` answers 201 within the time limit.
- [AuthTest](src/test/java/com/restfulbooker/automation/tests/AuthTest.java) (1)
  - `validCredentialsReturnToken` checks `POST /auth` answers 200 with a token.
- [CreateBookingTest](src/test/java/com/restfulbooker/automation/tests/CreateBookingTest.java) (1)
  - `createBookingReturnsIdAndEchoesPayload` checks `POST /booking` answers 200 with the new `bookingid` and the submitted booking.
- [GetBookingTest](src/test/java/com/restfulbooker/automation/tests/GetBookingTest.java) (5)
  - `bookingIdsIncludeCreatedBooking` checks the unfiltered list contains the created id.
  - `filterByFirstNameIncludesCreatedBooking`
  - `filterByLastNameIncludesCreatedBooking`
  - `filterByFirstAndLastNameIncludesCreatedBooking`
  - `getBookingByIdReturnsCreatedBooking`
- [UpdateBookingTest](src/test/java/com/restfulbooker/automation/tests/UpdateBookingTest.java) (2), for `PUT`
  - `updateWithTokenCookieReplacesBooking`
  - `updateWithBasicAuthReplacesBooking`
- [PartialUpdateBookingTest](src/test/java/com/restfulbooker/automation/tests/PartialUpdateBookingTest.java) (2), for `PATCH`
  - `patchWithTokenCookieChangesSuppliedFields`
  - `patchWithBasicAuthChangesSuppliedFields`
- [DeleteBookingTest](src/test/java/com/restfulbooker/automation/tests/DeleteBookingTest.java) (2)
  - `deleteWithTokenCookieRemovesBooking` checks 201 and that the id leaves the list.
  - `deleteWithBasicAuthRemovesBooking`
- [BookingE2ETest](src/test/java/com/restfulbooker/automation/tests/BookingE2ETest.java) (8, chained in this order)
  1. `generateAuthToken`
  2. `createNewBooking`
  3. `fetchCreatedBooking`
  4. `replaceBooking` (PUT)
  5. `patchBooking`
  6. `fetchFinalBooking`
  7. `deleteBooking`
  8. `deletedBookingIsNotListed`

[BaseTest.java](src/test/java/com/restfulbooker/automation/tests/BaseTest.java) holds setup and cleanup, not test cases. The JSON schemas the tests validate against are in [src/test/resources/schemas/](src/test/resources/schemas/).

Run them from this project folder with `mvn clean test`.
