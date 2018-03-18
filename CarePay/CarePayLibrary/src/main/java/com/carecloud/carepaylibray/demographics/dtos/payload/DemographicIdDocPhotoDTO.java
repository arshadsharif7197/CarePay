package com.carecloud.carepaylibray.demographics.dtos.payload;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/1/2016.
 * Mdel for ID doc photo.
 */
public class DemographicIdDocPhotoDTO {

    @SerializedName("identity_document_photo") @Expose
    private String idDocPhoto;

    @SerializedName("page")
    @Expose
    private int page;

    @SerializedName("delete")
    @Expose
    private boolean delete = false;

    public String getIdDocPhoto() {
        return idDocPhoto;
    }

    public void setIdDocPhoto(String idDocPhoto) {
        this.idDocPhoto = idDocPhoto;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }
}
