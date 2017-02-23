
package com.carecloud.carepaylibray.intake.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AddressModel {

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
     * 
     * @return
     *     The line1
     */
    public String getLine1() {
        return line1;
    }

    /**
     * 
     * @param line1
     *     The line1
     */
    public void setLine1(String line1) {
        this.line1 = line1;
    }

    /**
     * 
     * @return
     *     The line2
     */
    public String getLine2() {
        return line2;
    }

    /**
     * 
     * @param line2
     *     The line2
     */
    public void setLine2(String line2) {
        this.line2 = line2;
    }

    /**
     * 
     * @return
     *     The line3
     */
    public String getLine3() {
        return line3;
    }

    /**
     * 
     * @param line3
     *     The line3
     */
    public void setLine3(String line3) {
        this.line3 = line3;
    }

    /**
     * 
     * @return
     *     The city
     */
    public String getCity() {
        return city;
    }

    /**
     * 
     * @param city
     *     The city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * 
     * @return
     *     The zipCode
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * 
     * @param zipCode
     *     The zip_code
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * 
     * @return
     *     The countyName
     */
    public String getCountyName() {
        return countyName;
    }

    /**
     * 
     * @param countyName
     *     The county_name
     */
    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    /**
     * 
     * @return
     *     The latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * 
     * @param latitude
     *     The latitude
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * 
     * @return
     *     The longitude
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * 
     * @param longitude
     *     The longitude
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * 
     * @return
     *     The stateName
     */
    public String getStateName() {
        return stateName;
    }

    /**
     * 
     * @param stateName
     *     The state_name
     */
    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

}
