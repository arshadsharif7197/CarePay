package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.properties;

import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataEntityDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/24/2016.
 * Specific metadata DTO for 'address'
 */

public class DemographicMetadataPropertiesAddressDTO {
    @SerializedName("phone")
    @Expose
    private MetadataEntityDTO phone = new MetadataEntityDTO();

    @SerializedName("zipcode")
    @Expose
    private MetadataEntityDTO zipcode = new MetadataEntityDTO();

    @SerializedName("address1")
    @Expose
    private MetadataEntityDTO address1 = new MetadataEntityDTO();

    @SerializedName("address2")
    @Expose
    private MetadataEntityDTO address2 = new MetadataEntityDTO();

    @SerializedName("city")
    @Expose
    private MetadataEntityDTO city = new MetadataEntityDTO();

    @SerializedName("state")
    @Expose
    private MetadataEntityDTO state = new MetadataEntityDTO();

    @SerializedName("country")
    @Expose
    private MetadataEntityDTO country = new MetadataEntityDTO();

    public MetadataEntityDTO getPhone() {
        return phone;
    }

    public void setPhone(MetadataEntityDTO phone) {
        this.phone = phone;
    }

    public MetadataEntityDTO getZipcode() {
        return zipcode;
    }

    public void setZipcode(MetadataEntityDTO zipcode) {
        this.zipcode = zipcode;
    }

    public MetadataEntityDTO getAddress1() {
        return address1;
    }

    public void setAddress1(MetadataEntityDTO address1) {
        this.address1 = address1;
    }

    public MetadataEntityDTO getAddress2() {
        return address2;
    }

    public void setAddress2(MetadataEntityDTO address2) {
        this.address2 = address2;
    }

    public MetadataEntityDTO getCity() {
        return city;
    }

    public void setCity(MetadataEntityDTO city) {
        this.city = city;
    }

    public MetadataEntityDTO getState() {
        return state;
    }

    public void setState(MetadataEntityDTO state) {
        this.state = state;
    }

    public MetadataEntityDTO getCountry() {
        return country;
    }

    public void setCountry(MetadataEntityDTO country) {
        this.country = country;
    }
}