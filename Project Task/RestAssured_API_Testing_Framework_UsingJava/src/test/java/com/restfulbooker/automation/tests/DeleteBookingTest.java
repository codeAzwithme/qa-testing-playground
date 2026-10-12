package com.restfulbooker.automation.tests;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.restfulbooker.automation.clients.BookingApi;
import com.restfulbooker.automation.models.Booking;
import com.restfulbooker.automation.utils.BookingDataFactory;

public class DeleteBookingTest extends BaseTest {

    private Booking booking;
    private int bookingId;

    @BeforeMethod
    public void createBookingToDelete() {
        booking = BookingDataFactory.validBooking();
        bookingId = createTrackedBooking(booking).getBookingId();
    }

    @Test(description = "Booking - DeleteBooking: DELETE with the token cookie answers 201 and the booking id leaves the list")
    public void deleteWithTokenCookieRemovesBooking() {
        assertDeleteApplied(tokenBookingApi);
    }

    @Test(description = "Booking - DeleteBooking: DELETE with the Basic authorization header answers 201 and the booking id leaves the list")
    public void deleteWithBasicAuthRemovesBooking() {
        assertDeleteApplied(basicAuthBookingApi);
    }

    private void assertDeleteApplied(BookingApi bookingApi) {
        assertBookingIdsContain(nameFilters(booking), bookingId);

        deleteBookingAndAssertCreated(bookingApi, bookingId);

        assertBookingIdsExclude(nameFilters(booking), bookingId);
    }
}
