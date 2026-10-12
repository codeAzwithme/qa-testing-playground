package com.restfulbooker.automation.utils;

import com.restfulbooker.automation.models.Booking;

public final class BookingDataFactory {

    private BookingDataFactory() {
    }

    public static Booking validBooking() {
        return Booking.builder()
                .firstName("Jim")
                .lastName("Brown")
                .totalPrice(111)
                .depositPaid(true)
                .bookingDates("2018-01-01", "2019-01-01")
                .additionalNeeds("Breakfast")
                .build();
    }

    public static Booking replacementBooking() {
        return Booking.builder()
                .firstName("Sally")
                .lastName("Green")
                .totalPrice(222)
                .depositPaid(false)
                .bookingDates("2020-03-01", "2020-03-15")
                .additionalNeeds("Dinner")
                .build();
    }

    public static Booking partialUpdate() {
        return Booking.builder()
                .firstName("James")
                .lastName("Brown")
                .build();
    }
}
