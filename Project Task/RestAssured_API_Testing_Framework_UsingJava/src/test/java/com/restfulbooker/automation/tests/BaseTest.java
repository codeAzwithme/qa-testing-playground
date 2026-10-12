package com.restfulbooker.automation.tests;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.restassured.response.ValidatableResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.restfulbooker.automation.clients.AuthApi;
import com.restfulbooker.automation.clients.BookingApi;
import com.restfulbooker.automation.constants.StatusCodes;
import com.restfulbooker.automation.models.Booking;
import com.restfulbooker.automation.models.CreateBookingResponse;
import com.restfulbooker.automation.specs.BaseSpec;

public abstract class BaseTest {

    protected static final String ROOT_PATH = "";
    protected static final String CREATED_BOOKING_PATH = "booking.";

    protected AuthApi authApi;
    protected BookingApi anonymousBookingApi;
    protected BookingApi tokenBookingApi;
    protected BookingApi basicAuthBookingApi;

    private final Set<Integer> createdBookingIds = ConcurrentHashMap.newKeySet();

    @BeforeClass(alwaysRun = true)
    public void setUpApiClients() {
        authApi = new AuthApi();
        anonymousBookingApi = new BookingApi(BaseSpec.getRequestSpec());
        tokenBookingApi = new BookingApi(BaseSpec.getTokenAuthRequestSpec(authApi.generateToken()));
        basicAuthBookingApi = new BookingApi(BaseSpec.getBasicAuthRequestSpec());
    }

    @AfterClass(alwaysRun = true)
    public void deleteCreatedBookings() {
        if (tokenBookingApi == null) {
            return;
        }
        for (int bookingId : createdBookingIds) {
            deleteQuietly(bookingId);
        }
        createdBookingIds.clear();
    }

    protected CreateBookingResponse createTrackedBooking(Booking booking) {
        CreateBookingResponse created = anonymousBookingApi.createBooking(booking)
                .then()
                .spec(BaseSpec.getJsonResponseSpec(StatusCodes.OK))
                .extract()
                .as(CreateBookingResponse.class);
        trackBooking(created.getBookingId());
        return created;
    }

    protected void trackBooking(int bookingId) {
        createdBookingIds.add(bookingId);
    }

    protected Booking fetchBooking(int bookingId) {
        return anonymousBookingApi.getBooking(bookingId)
                .then()
                .spec(BaseSpec.getJsonResponseSpec(StatusCodes.OK))
                .extract()
                .as(Booking.class);
    }

    protected void deleteBookingAndAssertCreated(BookingApi bookingApi, int bookingId) {
        bookingApi.deleteBooking(bookingId)
                .then()
                .spec(BaseSpec.getResponseSpec(StatusCodes.CREATED));
        createdBookingIds.remove(bookingId);
    }

    protected void assertBookingIdsContain(Map<String, String> filters, int bookingId) {
        bookingIdsFor(filters).body("bookingid", hasItem(bookingId));
    }

    protected void assertBookingIdsExclude(Map<String, String> filters, int bookingId) {
        bookingIdsFor(filters).body("bookingid", not(hasItem(bookingId)));
    }

    protected static Map<String, String> nameFilters(Booking booking) {
        return Map.of("firstname", booking.getFirstName(), "lastname", booking.getLastName());
    }

    protected static ValidatableResponse assertBookingFields(ValidatableResponse response, String path,
            Booking expected) {
        return response
                .body(path + "firstname", equalTo(expected.getFirstName()))
                .body(path + "lastname", equalTo(expected.getLastName()))
                .body(path + "totalprice", equalTo(expected.getTotalPrice()))
                .body(path + "depositpaid", equalTo(expected.getDepositPaid()))
                .body(path + "bookingdates.checkin", equalTo(expected.getBookingDates().getCheckIn()))
                .body(path + "bookingdates.checkout", equalTo(expected.getBookingDates().getCheckOut()))
                .body(path + "additionalneeds", equalTo(expected.getAdditionalNeeds()));
    }

    private ValidatableResponse bookingIdsFor(Map<String, String> filters) {
        return anonymousBookingApi.getBookingIds(filters)
                .then()
                .spec(BaseSpec.getResponseSpec(StatusCodes.OK))
                .body(matchesJsonSchemaInClasspath(Schemas.BOOKING_IDS));
    }

    private void deleteQuietly(int bookingId) {
        try {
            int status = tokenBookingApi.deleteBooking(bookingId).statusCode();
            if (status != StatusCodes.CREATED) {
                System.err.println("Cleanup of booking " + bookingId + " returned HTTP " + status);
            }
        } catch (Exception exception) {
            System.err.println("Cleanup of booking " + bookingId + " failed: " + exception.getMessage());
        }
    }
}
