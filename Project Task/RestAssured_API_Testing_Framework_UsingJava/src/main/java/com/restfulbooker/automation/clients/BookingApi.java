package com.restfulbooker.automation.clients;

import java.util.Map;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import com.restfulbooker.automation.constants.Endpoints;
import com.restfulbooker.automation.models.Booking;

public class BookingApi extends BaseApi {

    private static final String ID_PARAM = "id";

    public BookingApi(RequestSpecification requestSpec) {
        super(requestSpec);
    }

    public Response getBookingIds() {
        return request()
                .when()
                .get(Endpoints.BOOKING);
    }

    public Response getBookingIds(Map<String, ?> filters) {
        return request()
                .queryParams(filters)
                .when()
                .get(Endpoints.BOOKING);
    }

    public Response getBooking(int bookingId) {
        return request()
                .pathParam(ID_PARAM, bookingId)
                .when()
                .get(Endpoints.BOOKING_BY_ID);
    }

    public Response createBooking(Booking booking) {
        return request()
                .body(booking)
                .when()
                .post(Endpoints.BOOKING);
    }

    public Response updateBooking(int bookingId, Booking booking) {
        return request()
                .pathParam(ID_PARAM, bookingId)
                .body(booking)
                .when()
                .put(Endpoints.BOOKING_BY_ID);
    }

    public Response partialUpdateBooking(int bookingId, Booking booking) {
        return request()
                .pathParam(ID_PARAM, bookingId)
                .body(booking)
                .when()
                .patch(Endpoints.BOOKING_BY_ID);
    }

    public Response deleteBooking(int bookingId) {
        return request()
                .pathParam(ID_PARAM, bookingId)
                .when()
                .delete(Endpoints.BOOKING_BY_ID);
    }
}
