package com.carecloud.carepaylibray.retail.models;

import com.google.gson.annotations.SerializedName;

public class RetailBillingPerson {
    private static final String DEFAULT_COUNTRY_CODE = "US";

    @SerializedName("name")
    private String name;

    @SerializedName("street")
    private String street;

    @SerializedName("city")
    private String city;

    @SerializedName("country_code")
    private String countryCode = DEFAULT_COUNTRY_CODE;

    @SerializedName("postal_code")
    private String postalCode;

    @SerializedName("state_or_province_code")
    private String stateCode;

    @SerializedName("phone")
    private String phone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
