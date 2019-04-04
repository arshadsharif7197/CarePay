
package com.carecloud.carepaylibray.appointments.models;

import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Model for appointment address.
 */
public class AppointmentAddressDTO {

    @SerializedName("line1")
    @Expose
    private String line1;
    @SerializedName("line2")
    @Expose
    private String line2;
    @SerializedName("line3")
    @Expose
    private String line3;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("zip_code")
    @Expose
    private String zipCode;
    @SerializedName("county_name")
    @Expose
    private String countyName;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("state_name")
    @Expose
    private String stateName;

    /**
     * @return The line1
     */
    public String getLine1() {
        return line1;
    }

    /**
     * @param line1 The line1
     */
    public void setLine1(String line1) {
        this.line1 = line1;
    }

    /**
     * @return The line2
     */
    public String getLine2() {
        return line2;
    }

    /**
     * @param line2 The line2
     */
    public void setLine2(String line2) {
        this.line2 = line2;
    }

    /**
     * @return The line3
     */
    public Object getLine3() {
        return line3;
    }

    /**
     * @param line3 The line3
     */
    public void setLine3(String line3) {
        this.line3 = line3;
    }

    /**
     * @return The city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city The city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return The zipCode
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * @param zipCode The zip_code
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * @return The countyName
     */
    public Object getCountyName() {
        return countyName;
    }

    /**
     * @param countyName The county_name
     */
    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    /**
     * @return The latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * @param latitude The latitude
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * @return The longitude
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * @param longitude The longitude
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * @return The stateName
     */
    public String getStateName() {
        return stateName;
    }

    /**
     * @param stateName The state_name
     */
    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    /**
     * Returns full address.
     *
     * @return Full address
     */
    public String getPlaceAddressString() {
        return StringUtil.capitalize((StringUtil.isNullOrEmpty(line1) ? "" : line1 + " ")
                + (StringUtil.isNullOrEmpty(line2) ? "" : line2 + " ")
                + (StringUtil.isNullOrEmpty(city) ? "" : city + " ")
                + (line3 == null ? "" : line3 + " ")
                + (StringUtil.isNullOrEmpty(stateName) ? "" : stateName + " ")
                + (StringUtil.isNullOrEmpty(zipCode) ? "" : zipCode + " ")
                + (countyName == null ? "" : countyName));
    }

    public String geAddressStringWithShortZipWOCounty() {
        return StringUtil.capitalize((StringUtil.isNullOrEmpty(line1) ? "" : line1 + ", ")
                + (StringUtil.isNullOrEmpty(line2) ? "" : line2 + ", ")
                + (StringUtil.isNullOrEmpty(city) ? "" : city + ", ")
                + (line3 == null ? "" : line3 + ", ")
                + (StringUtil.isNullOrEmpty(stateName) ? "" : stateName + ", ")
                + (StringUtil.isNullOrEmpty(zipCode) ? "" : zipCode.substring(0, 5) + " "));
    }

    public String geAddressStringWithShortZipWOCounty2Lines() {
        return StringUtil.capitalize((StringUtil.isNullOrEmpty(line1) ? "" : line1 + ", ")
                + (StringUtil.isNullOrEmpty(line2) ? "" : line2 + ", ")
                + (StringUtil.isNullOrEmpty(city) ? "\n" : "\n" + city + ", ")
                + (line3 == null ? "" : line3 + ", ")
                + (StringUtil.isNullOrEmpty(stateName) ? "" : stateName + ", ")
                + (StringUtil.isNullOrEmpty(zipCode) ? "" : zipCode.substring(0, 5) + " "));
    }

    @Override
    public String toString() {
        // Example: 2645 SW 37th Ave Suite 502
        String firstAddressHalf = "";

        if (!StringUtil.isNullOrEmpty(line1)) {
            firstAddressHalf = (firstAddressHalf + line1.trim()).trim();
        }

        if (!StringUtil.isNullOrEmpty(line2)) {
            firstAddressHalf = (firstAddressHalf + " " + line2.trim()).trim();
        }

        if (!StringUtil.isNullOrEmpty(line3)) {
            firstAddressHalf = (firstAddressHalf + " " + line3.trim()).trim();
        }

        // Example: Miami, FL 33133, USA
        String secondAddressHalf = "";

        if (!StringUtil.isNullOrEmpty(city)) {
            secondAddressHalf = (secondAddressHalf + city.trim()).trim();
        }

        if (!StringUtil.isNullOrEmpty(stateName)) {
            if (!StringUtil.isNullOrEmpty(secondAddressHalf)) {
                secondAddressHalf += ",";
            }

            secondAddressHalf = (secondAddressHalf + " " + stateName.trim()).trim();
        }

        if (!StringUtil.isNullOrEmpty(zipCode)) {
            secondAddressHalf = (secondAddressHalf + " " + zipCode.trim()).trim();
        }

        if (!StringUtil.isNullOrEmpty(countyName)) {
            if (!StringUtil.isNullOrEmpty(secondAddressHalf)) {
                secondAddressHalf += ",";
            }

            secondAddressHalf = (secondAddressHalf + " " + countyName.trim()).trim();
        }

        if (StringUtil.isNullOrEmpty(firstAddressHalf)) {
            return secondAddressHalf;
        }

        if (StringUtil.isNullOrEmpty(secondAddressHalf)) {
            return firstAddressHalf;
        }

        return firstAddressHalf.trim() + ",\n" + secondAddressHalf.trim();
    }
}
