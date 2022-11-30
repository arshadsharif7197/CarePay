package com.carecloud.carepaylibray.appointments.models;

import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CheckBoxDto implements Serializable {

    @SerializedName("providersameaslast")
    @Expose
    private Boolean providerSameAsLast;
    @SerializedName("providerpicklist")
    @Expose
    private Boolean providerPickList;
    @SerializedName("locationsameaslast")
    @Expose
    private Boolean locationSameAsLast;
    @SerializedName("locationpicklist")
    @Expose
    private Boolean locationPickList;

    public Boolean getProviderSameAsLast() {
        return providerSameAsLast;
    }

    public void setProviderSameAsLast(Boolean providerSameAsLast) {
        this.providerSameAsLast = providerSameAsLast;
    }

    public Boolean getProviderPickList() {
        return providerPickList;
    }

    public void setProviderPickList(Boolean providerPickList) {
        this.providerPickList = providerPickList;
    }

    public Boolean getLocationSameAsLast() {
        return locationSameAsLast;
    }

    public void setLocationSameAsLast(Boolean locationSameAsLast) {
        this.locationSameAsLast = locationSameAsLast;
    }

    public Boolean getLocationPickList() {
        return locationPickList;
    }

    public void setLocationPickList(Boolean locationPickList) {
        this.locationPickList = locationPickList;
    }
}