package com.carecloud.carepaylibray.demographics.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/1/2016.
 */
public class DemIdDocPhotoDto {

    @SerializedName("identity_document_photo") @Expose
    private String idDocPhoto;

    public String getIdDocPhoto() {
        return idDocPhoto;
    }

    public void setIdDocPhoto(String idDocPhoto) {
        this.idDocPhoto = idDocPhoto;
    }
}
