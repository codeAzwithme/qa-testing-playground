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

public class UpdateBookingTest extends BaseTest {

    private int bookingId;

    @BeforeMethod
    public void createBookingToUpdate() {
        bookingId = createTrackedBooking(BookingDataFactory.validBooking()).getBookingId();
    }

    @Test(description = "Booking - UpdateBooking: PUT with the token cookie answers 200 and stores the replacement")
    public void updateWithTokenCookieReplacesBooking() {
        assertUpdateApplied(tokenBookingApi);
    }

    @Test(description = "Booking - UpdateBooking: PUT with the Basic authorization header answers 200 and stores the replacement")
    public void updateWithBasicAuthReplacesBooking() {
        assertUpdateApplied(basicAuthBookingApi);
    }

    private void assertUpdateApplied(BookingApi bookingApi) {
        Booking replacement = BookingDataFactory.replacementBooking();

        ValidatableResponse response = bookingApi.updateBooking(bookingId, replacement)
                .then()
                .spec(BaseSpec.getJsonResponseSpec(StatusCodes.OK))
                .body(matchesJsonSchemaInClasspath(Schemas.BOOKING));

        assertBookingFields(response, ROOT_PATH, replacement);
        Assert.assertEquals(response.extract().as(Booking.class), replacement,
                "Update response differs from the replacement booking");
        Assert.assertEquals(fetchBooking(bookingId), replacement, "Update was not stored");
    }
}
