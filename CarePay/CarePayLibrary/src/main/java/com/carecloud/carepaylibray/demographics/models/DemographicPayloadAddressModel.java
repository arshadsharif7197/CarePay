package com.carecloud.carepaylibray.demographics.models;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 9/26/2016.
 */
public class DemographicPayloadAddressModel implements Parcelable {

    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("zipcode")
    @Expose
    private String zipcode;
    @SerializedName("address1")
    @Expose
    private String address1;
    @SerializedName("address2")
    @Expose
    private String address2;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("country")
    @Expose
    private String country;

    public DemographicPayloadAddressModel() {

    }

    protected DemographicPayloadAddressModel(android.os.Parcel in) {
        phone = in.readString();
        zipcode = in.readString();
        address1 = in.readString();
        address2 = in.readString();
        city = in.readString();
        state = in.readString();
        country = in.readString();
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeString(phone);
        dest.writeString(zipcode);
        dest.writeString(address1);
        dest.writeString(address2);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(country);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DemographicPayloadAddressModel> CREATOR = new Creator<DemographicPayloadAddressModel>() {
        @Override
        public DemographicPayloadAddressModel createFromParcel(android.os.Parcel in) {
            return new DemographicPayloadAddressModel(in);
        }

        @Override
        public DemographicPayloadAddressModel[] newArray(int size) {
            return new DemographicPayloadAddressModel[size];
        }
    };

    /**
     *
     * @return
     * The phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     *
     * @param phone
     * The phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     *
     * @return
     * The zipcode
     */
    public String getZipcode() {
        return zipcode;
    }

    /**
     *
     * @param zipcode
     * The zipcode
     */
    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    /**
     *
     * @return
     * The address1
     */
    public String getAddress1() {
        return address1;
    }

    /**
     *
     * @param address1
     * The address1
     */
    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    /**
     *
     * @return
     * The address2
     */
    public String getAddress2() {
        return address2;
    }

    /**
     *
     * @param address2
     * The address2
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     *
     * @return
     * The city
     */
    public String getCity() {
        return city;
    }

    /**
     *
     * @param city
     * The city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     *
     * @return
     * The state
     */
    public String getState() {
        return state;
    }

    /**
     *
     * @param state
     * The state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     *
     * @return
     * The country
     */
    public String getCountry() {
        return country;
    }

    /**
     *
     * @param country
     * The country
     */
    public void setCountry(String country) {
        this.country = country;
    }
}
