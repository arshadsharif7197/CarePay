package com.carecloud.carepaylibray.payments.models.postmodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/21/17.
 */

public class PaymentLineItemMetadata {

    @SerializedName("practice_id")
    private String practiceID;

    @SerializedName("provider_id")
    private String providerID;

    @SerializedName("location_id")
    private String locationID;

    @SerializedName("patient_id")
    private String patientID;


    public String getPracticeID() {
        return practiceID;
    }

    public void setPracticeID(String practiceID) {
        this.practiceID = practiceID;
    }

    public String getProviderID() {
        return providerID;
    }

    public void setProviderID(String providerID) {
        this.providerID = providerID;
    }

    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }
}
