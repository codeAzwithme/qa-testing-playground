package com.restfulbooker.automation.tests;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.restfulbooker.automation.clients.BookingApi;
import com.restfulbooker.automation.config.ConfigManager;
import com.restfulbooker.automation.constants.StatusCodes;
import com.restfulbooker.automation.models.Auth;
import com.restfulbooker.automation.models.AuthResponse;
import com.restfulbooker.automation.models.Booking;
import com.restfulbooker.automation.models.CreateBookingResponse;
import com.restfulbooker.automation.specs.BaseSpec;
import com.restfulbooker.automation.utils.BookingDataFactory;

public class BookingE2ETest extends BaseTest {

    private BookingApi bookingApi;
    private Booking created;
    private Booking replaced;
    private Booking patched;
    private int bookingId;

    @Test(description = "E2E 1 - Auth - CreateToken: admin credentials answer 200 with a token")
    public void generateAuthToken() {
        Response response = authApi.createToken(new Auth(ConfigManager.username(), ConfigManager.password()));

        response.then()
                .spec(BaseSpec.getResponseSpec(StatusCodes.OK))
                .body(matchesJsonSchemaInClasspath(Schemas.AUTH_TOKEN))
                .body("token", notNullValue());

        String token = response.as(AuthResponse.class).getToken();
        bookingApi = new BookingApi(BaseSpec.getTokenAuthRequestSpec(token));
    }

    @Test(dependsOnMethods = "generateAuthToken",
            description = "E2E 2 - Booking - CreateBooking: answers 200 with the new booking id and the submitted booking")
    public void createNewBooking() {
        created = BookingDataFactory.validBooking();

        ValidatableResponse response = bookingApi.createBooking(created)
                .then()
                .spec(BaseSpec.getJsonResponseSpec(StatusCodes.OK));
        CreateBookingResponse body = response.extract().as(CreateBookingResponse.class);
        bookingId = body.getBookingId();
        trackBooking(bookingId);

        response.body(matchesJsonSchemaInClasspath(Schemas.CREATE_BOOKING_RESPONSE))
                .body("bookingid", notNullValue());
        assertBookingFields(response, CREATED_BOOKING_PATH, created);
    }

    @Test(dependsOnMethods = "createNewBooking",
            description = "E2E 3 - Booking - GetBooking: answers 200 with the created booking")
    public void fetchCreatedBooking() {
        ValidatableResponse response = bookingApi.getBooking(bookingId)
                .then()
                .spec(BaseSpec.getJsonResponseSpec(StatusCodes.OK))
                .body(matchesJsonSchemaInClasspath(Schemas.BOOKING));

        assertBookingFields(response, ROOT_PATH, created);
        Assert.assertEquals(response.extract().as(Booking.class), created,
                "Fetched booking differs from the created booking");
    }

    @Test(dependsOnMethods = "fetchCreatedBooking",
            description = "E2E 4 - Booking - UpdateBooking: PUT answers 200 with the replacement booking")
    public void replaceBooking() {
        replaced = BookingDataFactory.replacementBooking();

        ValidatableResponse response = bookingApi.updateBooking(bookingId, replaced)
                .then()
                .spec(BaseSpec.getJsonResponseSpec(StatusCodes.OK))
                .body(matchesJsonSchemaInClasspath(Schemas.BOOKING));

        assertBookingFields(response, ROOT_PATH, replaced);
    }

    @Test(dependsOnMethods = "replaceBooking",
            description = "E2E 5 - Booking - PartialUpdateBooking: PATCH answers 200 with the supplied fields changed")
    public void patchBooking() {
        Booking patch = BookingDataFactory.partialUpdate();
        patched = replaced.toBuilder()
                .firstName(patch.getFirstName())
                .lastName(patch.getLastName())
                .build();

        ValidatableResponse response = bookingApi.partialUpdateBooking(bookingId, patch)
                .then()
                .spec(BaseSpec.getJsonResponseSpec(StatusCodes.OK))
                .body(matchesJsonSchemaInClasspath(Schemas.BOOKING));

        assertBookingFields(response, ROOT_PATH, patched);
    }

    @Test(dependsOnMethods = "patchBooking",
            description = "E2E 6 - Booking - GetBooking: returns the booking in its final state")
    public void fetchFinalBooking() {
        Assert.assertEquals(fetchBooking(bookingId), patched, "Stored booking differs from the expected final state");
    }

    @Test(dependsOnMethods = "fetchFinalBooking",
            description = "E2E 7 - Booking - DeleteBooking: DELETE answers 201")
    public void deleteBooking() {
        assertBookingIdsContain(nameFilters(patched), bookingId);

        deleteBookingAndAssertCreated(bookingApi, bookingId);
    }

    @Test(dependsOnMethods = "deleteBooking",
            description = "E2E 8 - Booking - GetBookingIds: the deleted booking id is no longer listed")
    public void deletedBookingIsNotListed() {
        assertBookingIdsExclude(nameFilters(patched), bookingId);
    }
}
