package com.carecloud.carepaylibray.demographics.models;

import com.carecloud.carepaylibray.base.models.BaseTransitionsPropertyModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 9/22/2016.
 * Model for transition properties.
 */
public class DemographicTransitionsPropertiesDTO {

    @SerializedName("phone")
    @Expose
    private BaseTransitionsPropertyModel phone;

    public BaseTransitionsPropertyModel getPhone() {
        return phone;
    }

    public void setPhone(BaseTransitionsPropertyModel phone) {
        this.phone = phone;
    }
}
