package com.restfulbooker.automation.models;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Booking {

    @JsonProperty("firstname")
    private String firstName;

    @JsonProperty("lastname")
    private String lastName;

    @JsonProperty("totalprice")
    private Integer totalPrice;

    @JsonProperty("depositpaid")
    private Boolean depositPaid;

    @JsonProperty("bookingdates")
    private BookingDates bookingDates;

    @JsonProperty("additionalneeds")
    private String additionalNeeds;

    public Booking() {
    }

    private Booking(Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.totalPrice = builder.totalPrice;
        this.depositPaid = builder.depositPaid;
        this.bookingDates = builder.bookingDates;
        this.additionalNeeds = builder.additionalNeeds;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        BookingDates datesCopy = bookingDates == null
                ? null
                : new BookingDates(bookingDates.getCheckIn(), bookingDates.getCheckOut());
        return new Builder()
                .firstName(firstName)
                .lastName(lastName)
                .totalPrice(totalPrice)
                .depositPaid(depositPaid)
                .bookingDates(datesCopy)
                .additionalNeeds(additionalNeeds);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Boolean getDepositPaid() {
        return depositPaid;
    }

    public void setDepositPaid(Boolean depositPaid) {
        this.depositPaid = depositPaid;
    }

    public BookingDates getBookingDates() {
        return bookingDates;
    }

    public void setBookingDates(BookingDates bookingDates) {
        this.bookingDates = bookingDates;
    }

    public String getAdditionalNeeds() {
        return additionalNeeds;
    }

    public void setAdditionalNeeds(String additionalNeeds) {
        this.additionalNeeds = additionalNeeds;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Booking that)) {
            return false;
        }
        return Objects.equals(firstName, that.firstName)
                && Objects.equals(lastName, that.lastName)
                && Objects.equals(totalPrice, that.totalPrice)
                && Objects.equals(depositPaid, that.depositPaid)
                && Objects.equals(bookingDates, that.bookingDates)
                && Objects.equals(additionalNeeds, that.additionalNeeds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, totalPrice, depositPaid, bookingDates, additionalNeeds);
    }

    @Override
    public String toString() {
        return "Booking{firstName='" + firstName + "', lastName='" + lastName + "', totalPrice=" + totalPrice
                + ", depositPaid=" + depositPaid + ", bookingDates=" + bookingDates
                + ", additionalNeeds='" + additionalNeeds + "'}";
    }

    public static final class Builder {

        private String firstName;
        private String lastName;
        private Integer totalPrice;
        private Boolean depositPaid;
        private BookingDates bookingDates;
        private String additionalNeeds;

        private Builder() {
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder totalPrice(Integer totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public Builder depositPaid(Boolean depositPaid) {
            this.depositPaid = depositPaid;
            return this;
        }

        public Builder bookingDates(BookingDates bookingDates) {
            this.bookingDates = bookingDates;
            return this;
        }

        public Builder bookingDates(String checkIn, String checkOut) {
            this.bookingDates = new BookingDates(checkIn, checkOut);
            return this;
        }

        public Builder additionalNeeds(String additionalNeeds) {
            this.additionalNeeds = additionalNeeds;
            return this;
        }

        public Booking build() {
            return new Booking(this);
        }
    }
}
