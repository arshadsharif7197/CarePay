package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harshal_patil on 1/5/2017.
 */
public class DemographicsSettingsInsurancePhotoDTO {

    @SerializedName("insurance_photo")
    @Expose
    private String insurancePhoto;

    public String getInsurancePhoto() {
        return insurancePhoto;
    }

    public void setInsurancePhoto(String insurancePhoto) {
        this.insurancePhoto = insurancePhoto;
    }
}
