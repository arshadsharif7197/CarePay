package com.carecloud.carepay.service.library.dtos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 11/20/17
 */

public class UserPracticeAddressDTO {

    @SerializedName("line1")
    private String addressLine1;

    @SerializedName("city")
    private String city;

    @SerializedName("state")
    private String state;

    @SerializedName("zip_code")
    private String zip;


    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        if(zip != null && zip.length() > 5){
            return zip.substring(0, 5)+"-"+zip.substring(5,zip.length());
        }
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    /**
     * get full formatted address
     * @return address
     */
    public String getFullAddress(){
        StringBuilder builder = new StringBuilder(getAddressLine1());
        if(getCity() != null || getState() != null || getZip() != null){
            builder.append("\n");
            if(getCity() != null){
                builder.append(getCity());
            }
            if(getState()!=null){
                builder.append(", ");
                builder.append(getState());
            }
            if(getZip()!=null){
                builder.append(", ");
                builder.append(getZip());
            }
        }
        return builder.toString();
    }
}
