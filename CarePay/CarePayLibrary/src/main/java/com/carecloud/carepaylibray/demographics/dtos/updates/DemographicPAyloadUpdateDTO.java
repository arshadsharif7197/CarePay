package com.carecloud.carepaylibray.demographics.dtos.updates;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/1/2016.
 * Model for demographic update.
 */
public class DemographicPAyloadUpdateDTO {

    @SerializedName("update") @Expose private String update;

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }
}
