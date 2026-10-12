package com.restfulbooker.automation.tests;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.restfulbooker.automation.clients.BookingApi;
import com.restfulbooker.automation.constants.StatusCodes;
import com.restfulbooker.automation.models.Booking;
import com.restfulbooker.automation.specs.BaseSpec;
import com.restfulbooker.automation.utils.BookingDataFactory;

public class PartialUpdateBookingTest extends BaseTest {

    private Booking original;
    private int bookingId;

    @BeforeMethod
    public void createBookingToPatch() {
        original = BookingDataFactory.validBooking();
        bookingId = createTrackedBooking(original).getBookingId();
    }

    @Test(description = "Booking - PartialUpdateBooking: PATCH with the token cookie answers 200 and changes the supplied fields")
    public void patchWithTokenCookieChangesSuppliedFields() {
        assertPatchApplied(tokenBookingApi);
    }

    @Test(description = "Booking - PartialUpdateBooking: PATCH with the Basic authorization header answers 200 and changes the supplied fields")
    public void patchWithBasicAuthChangesSuppliedFields() {
        assertPatchApplied(basicAuthBookingApi);
    }

    private void assertPatchApplied(BookingApi bookingApi) {
        Booking patch = BookingDataFactory.partialUpdate();
        Booking expected = original.toBuilder()
                .firstName(patch.getFirstName())
                .lastName(patch.getLastName())
                .build();

        ValidatableResponse response = bookingApi.partialUpdateBooking(bookingId, patch)
                .then()
                .spec(BaseSpec.getJsonResponseSpec(StatusCodes.OK))
                .body(matchesJsonSchemaInClasspath(Schemas.BOOKING));

        assertBookingFields(response, ROOT_PATH, expected);
        Assert.assertEquals(response.extract().as(Booking.class), expected,
                "Patch response differs from the expected booking");
        Assert.assertEquals(fetchBooking(bookingId), expected, "Patch was not stored");
    }
}
