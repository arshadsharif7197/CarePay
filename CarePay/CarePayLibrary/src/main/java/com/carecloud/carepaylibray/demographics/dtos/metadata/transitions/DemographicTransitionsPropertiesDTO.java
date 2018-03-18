package com.carecloud.carepaylibray.demographics.dtos.metadata.transitions;

import com.carecloud.carepaylibray.base.models.BaseTransitionsPropertyModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 9/22/2016.
 * Model for transition properties.
 */
class DemographicTransitionsPropertiesDTO {

    @SerializedName("phone")
    @Expose
    private BaseTransitionsPropertyModel phone = new BaseTransitionsPropertyModel();

    public BaseTransitionsPropertyModel getPhone() {
        return phone;
    }

    public void setPhone(BaseTransitionsPropertyModel phone) {
        this.phone = phone;
    }
}
