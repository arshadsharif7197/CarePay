package com.carecloud.carepaylibray.medications.models;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/15/17.
 */

public class MedicationAllergiesTransitionsDTO {

    @SerializedName("medications")
    @Expose
    private TransitionDTO medications =  new TransitionDTO();


    public TransitionDTO getMedications() {
        return medications;
    }

    public void setMedications(TransitionDTO medications) {
        this.medications = medications;
    }
}
