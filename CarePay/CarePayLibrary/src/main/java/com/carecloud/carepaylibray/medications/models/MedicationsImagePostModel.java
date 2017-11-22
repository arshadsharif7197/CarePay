package com.carecloud.carepaylibray.medications.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 11/16/17
 */

public class MedicationsImagePostModel {

    @SerializedName("photo")
    private String photo;

    @SerializedName("delete")
    private boolean delete = false;

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }
}
