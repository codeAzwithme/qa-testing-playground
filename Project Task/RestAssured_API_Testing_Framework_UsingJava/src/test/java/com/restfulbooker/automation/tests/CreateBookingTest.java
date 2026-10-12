package com.restfulbooker.automation.tests;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.restfulbooker.automation.constants.StatusCodes;
import com.restfulbooker.automation.models.Booking;
import com.restfulbooker.automation.models.CreateBookingResponse;
import com.restfulbooker.automation.specs.BaseSpec;
import com.restfulbooker.automation.utils.BookingDataFactory;

public class CreateBookingTest extends BaseTest {

    @Test(description = "Booking - CreateBooking: answers 200 with the new booking id and the submitted booking")
    public void createBookingReturnsIdAndEchoesPayload() {
        Booking payload = BookingDataFactory.validBooking();

        ValidatableResponse response = anonymousBookingApi.createBooking(payload)
                .then()
                .spec(BaseSpec.getJsonResponseSpec(StatusCodes.OK));
        CreateBookingResponse created = response.extract().as(CreateBookingResponse.class);
        trackBooking(created.getBookingId());

        response.body(matchesJsonSchemaInClasspath(Schemas.CREATE_BOOKING_RESPONSE))
                .body("bookingid", notNullValue());
        assertBookingFields(response, CREATED_BOOKING_PATH, payload);
        Assert.assertEquals(created.getBooking(), payload, "Created booking differs from the request payload");
    }
}
