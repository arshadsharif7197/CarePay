package com.carecloud.carepaylibray.medications.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/15/17.
 */

public class MedicationsObject extends MedicationsAllergiesObject {

    @SerializedName("dispensableDrugId")
    @Expose
    private String dispensableDrugId;

    public String getDispensableDrugId() {
        return dispensableDrugId;
    }

    public void setDispensableDrugId(String dispensableDrugId) {
        this.dispensableDrugId = dispensableDrugId;
    }
}
