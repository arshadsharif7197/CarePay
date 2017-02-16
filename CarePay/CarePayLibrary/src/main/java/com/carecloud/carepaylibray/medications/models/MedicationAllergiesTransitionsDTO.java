package com.carecloud.carepaylibray.medications.models;

import com.carecloud.carepaylibray.appointments.models.TransitionsDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/15/17.
 */

public class MedicationAllergiesTransitionsDTO {

    @SerializedName("medications")
    @Expose
    private TransitionsDTO medications =  new TransitionsDTO();


    public TransitionsDTO getMedications() {
        return medications;
    }

    public void setMedications(TransitionsDTO medications) {
        this.medications = medications;
    }
}
