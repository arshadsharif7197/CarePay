package com.carecloud.carepaylibray.medications.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/16/17.
 */

public class MedicationAllergiesPayload {

    @SerializedName("medications")
    @Expose
    private MedicationsPayload medications =  new MedicationsPayload();

    public MedicationsPayload getMedications() {
        return medications;
    }

    public void setMedications(MedicationsPayload medications) {
        this.medications = medications;
    }
}
