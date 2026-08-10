package com.example.lightshop.models;

import com.google.gson.annotations.SerializedName;

public class AddressModel {
    @SerializedName("user_email")
    private String userEmail;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("phone")
    private String phone;

    @SerializedName("pincode")
    private String pincode;

    @SerializedName("state")
    private String state;

    @SerializedName("city")
    private String city;

    @SerializedName("house_no")
    private String houseNo;

    @SerializedName("road_name")
    private String roadName;

    @SerializedName("landmark")
    private String landmark;

    @SerializedName("address_type")
    private String addressType;

    @SerializedName("latitude")
    private Double latitude;

    @SerializedName("longitude")
    private Double longitude;

    public AddressModel(String userEmail, String fullName, String phone, String pincode, String state, String city, String houseNo, String roadName, String landmark, String addressType) {
        this.userEmail = userEmail;
        this.fullName = fullName;
        this.phone = phone;
        this.pincode = pincode;
        this.state = state;
        this.city = city;
        this.houseNo = houseNo;
        this.roadName = roadName;
        this.landmark = landmark;
        this.addressType = addressType;
    }

    // Getters and Setters (Optional but good practice)
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getUserEmail() { return userEmail; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getPincode() { return pincode; }
    public String getState() { return state; }
    public String getCity() { return city; }
    public String getHouseNo() { return houseNo; }
    public String getRoadName() { return roadName; }
    public String getLandmark() { return landmark; }
    public String getAddressType() { return addressType; }
}
