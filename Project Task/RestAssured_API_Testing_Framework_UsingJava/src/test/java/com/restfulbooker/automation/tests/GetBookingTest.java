package com.restfulbooker.automation.tests;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.hasItem;

import java.util.Map;

import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.restfulbooker.automation.constants.StatusCodes;
import com.restfulbooker.automation.models.Booking;
import com.restfulbooker.automation.specs.BaseSpec;
import com.restfulbooker.automation.utils.BookingDataFactory;

public class GetBookingTest extends BaseTest {

    private Booking booking;
    private int bookingId;

    @BeforeClass
    public void createBookingFixture() {
        booking = BookingDataFactory.validBooking();
        bookingId = createTrackedBooking(booking).getBookingId();
    }

    @Test(description = "Booking - GetBookingIds: unfiltered list answers 200 and contains the created booking id")
    public void bookingIdsIncludeCreatedBooking() {
        anonymousBookingApi.getBookingIds()
                .then()
                .spec(BaseSpec.getResponseSpec(StatusCodes.OK))
                .body(matchesJsonSchemaInClasspath(Schemas.BOOKING_IDS))
                .body("bookingid", hasItem(bookingId));
    }

    @Test(description = "Booking - GetBookingIds: firstname filter contains the created booking id")
    public void filterByFirstNameIncludesCreatedBooking() {
        assertBookingIdsContain(Map.of("firstname", booking.getFirstName()), bookingId);
    }

    @Test(description = "Booking - GetBookingIds: lastname filter contains the created booking id")
    public void filterByLastNameIncludesCreatedBooking() {
        assertBookingIdsContain(Map.of("lastname", booking.getLastName()), bookingId);
    }

    @Test(description = "Booking - GetBookingIds: firstname and lastname filters contain the created booking id")
    public void filterByFirstAndLastNameIncludesCreatedBooking() {
        assertBookingIdsContain(nameFilters(booking), bookingId);
    }

    @Test(description = "Booking - GetBooking: answers 200 with the created booking")
    public void getBookingByIdReturnsCreatedBooking() {
        ValidatableResponse response = anonymousBookingApi.getBooking(bookingId)
                .then()
                .spec(BaseSpec.getJsonResponseSpec(StatusCodes.OK))
                .body(matchesJsonSchemaInClasspath(Schemas.BOOKING));

        assertBookingFields(response, ROOT_PATH, booking);
        Assert.assertEquals(response.extract().as(Booking.class), booking,
                "Fetched booking differs from the created booking");
    }
}
