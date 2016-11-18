package com.carecloud.carepaylibray.consentforms.models.datamodels.consentforhipaa;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rahul on 11/14/16.
 */

public class ConsentFormHippaPayloadDTO {
    @SerializedName("signed_by_patient")
    @Expose
    private boolean signedByPatient;
    @SerializedName("signed_by_legal")
    @Expose
    private boolean signedByLegal;
    @SerializedName("signature")
    @Expose
    private String signature;

    public boolean getSignedByPatient() {
        return signedByPatient;
    }

    public void setSignedByPatient(boolean signedByPatient) {
        this.signedByPatient = signedByPatient;
    }

    public boolean getSignedByLegal() {
        return signedByLegal;
    }

    public void setSignedByLegal(boolean signedByLegal) {
        this.signedByLegal = signedByLegal;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
