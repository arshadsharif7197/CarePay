package com.carecloud.carepaylibray.demographics.dtos.payload;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rahul on 9/30/16.
 * MOdel for insurance photo.
 */

public class DemographicInsurancePhotoDTO {
    @SerializedName("insurance_photo")
    @Expose
    private String insurancePhoto;

    @SerializedName("page")
    @Expose
    private int page;

    @SerializedName("delete")
    @Expose
    private boolean delete = false;

    @Expose(serialize = false, deserialize = false)
    private boolean newPhoto = false;


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

    public boolean isNewPhoto() {
        return newPhoto;
    }

    public void setNewPhoto(boolean newPhoto) {
        this.newPhoto = newPhoto;
    }
}
