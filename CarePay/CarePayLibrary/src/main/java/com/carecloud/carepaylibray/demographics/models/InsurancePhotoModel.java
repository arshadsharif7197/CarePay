package com.carecloud.carepaylibray.demographics.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rahul on 9/30/16.
 */

public class InsurancePhotoModel {
    @SerializedName("insurance_photo")
    @Expose
    private String insurancePhoto;

    /**
     *
     * @return
     * The insurancePhoto
     */
    public String getInsurancePhoto() {
        return insurancePhoto;
    }

    /**
     *
     * @param insurancePhoto
     * The insurance_photo
     */
    public void setInsurancePhoto(String insurancePhoto) {
        this.insurancePhoto = insurancePhoto;
    }
}
