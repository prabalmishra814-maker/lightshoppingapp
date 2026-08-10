package com.example.lightshop.models;

import com.google.gson.annotations.SerializedName;

public class LocationIQResponse {
    @SerializedName("address")
    public Address address;

    public static class Address {
        @SerializedName("house_number")
        public String houseNumber;
        @SerializedName("road")
        public String road;
        @SerializedName("city")
        public String city;
        @SerializedName("state")
        public String state;
        @SerializedName("postcode")
        public String postcode;
        @SerializedName("suburb")
        public String suburb;
    }
}
