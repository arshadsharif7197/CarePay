package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harshal_patil on 1/7/2017.
 */

public class DemographicsSettingsMetadataPropertiesDTO {
    @SerializedName("phone")
    @Expose
    private DemographicsSettingsPhoneDTO phone;
    @SerializedName("zipcode")
    @Expose
    private DemographicsSettingsZipDTO zipcode;
    @SerializedName("address1")
    @Expose
    private DemographicsSettingsAddressDTO address1;
    @SerializedName("address2")
    @Expose
    private DemographicsSettingsAddressDTO address2;
    @SerializedName("city")
    @Expose
    private DemographicsSettingsCityDTO city;
    @SerializedName("state")
    @Expose
    private DemographicsSettingsStateDTO state;
    @SerializedName("country")
    @Expose
    private DemographicsSettingsCountryDTO country;

    public DemographicsSettingsPhoneDTO getPhone() {
        return phone;
    }

    public void setPhone(DemographicsSettingsPhoneDTO phone) {
        this.phone = phone;
    }

    public DemographicsSettingsZipDTO getZipcode() {
        return zipcode;
    }

    public void setZipcode(DemographicsSettingsZipDTO zipcode) {
        this.zipcode = zipcode;
    }

    public DemographicsSettingsAddressDTO getAddress1() {
        return address1;
    }

    public void setAddress1(DemographicsSettingsAddressDTO address1) {
        this.address1 = address1;
    }

    public DemographicsSettingsAddressDTO getAddress2() {
        return address2;
    }

    public void setAddress2(DemographicsSettingsAddressDTO address2) {
        this.address2 = address2;
    }

    public DemographicsSettingsCityDTO getCity() {
        return city;
    }

    public void setCity(DemographicsSettingsCityDTO city) {
        this.city = city;
    }

    public DemographicsSettingsStateDTO getState() {
        return state;
    }

    public void setState(DemographicsSettingsStateDTO state) {
        this.state = state;
    }

    public DemographicsSettingsCountryDTO getCountry() {
        return country;
    }

    public void setCountry(DemographicsSettingsCountryDTO country) {
        this.country = country;
    }

}
