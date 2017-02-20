package com.carecloud.carepaylibray.consentforms.models.datamodels.consentforhipaa;

/**
 * Created by Rahul on 10/21/16.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConsentFormConsentHippaPropertiesDTO {

    @SerializedName("signed_by_patient")
    @Expose
    private ConsentFormHippaSignedByPatientDTO signedByPatient = new ConsentFormHippaSignedByPatientDTO();
    @SerializedName("signed_by_legal")
    @Expose
    private ConsentFormHippaSignedByLegalDTO signedByLegal = new ConsentFormHippaSignedByLegalDTO();
    @SerializedName("signature")
    @Expose
    private ConsentFormConsentHippaSignatureDTO signature = new ConsentFormConsentHippaSignatureDTO();

    /**
     * @return The signedByPatient
     */
    public ConsentFormHippaSignedByPatientDTO getSignedByPatient() {
        return signedByPatient;
    }

    /**
     * @param signedByPatient The signed_by_patient
     */
    public void setSignedByPatient(ConsentFormHippaSignedByPatientDTO signedByPatient) {
        this.signedByPatient = signedByPatient;
    }

    /**
     * @return The signedByLegal
     */
    public ConsentFormHippaSignedByLegalDTO getSignedByLegal() {
        return signedByLegal;
    }

    /**
     * @param signedByLegal The signed_by_legal
     */
    public void setSignedByLegal(ConsentFormHippaSignedByLegalDTO signedByLegal) {
        this.signedByLegal = signedByLegal;
    }

    /**
     * @return The signature
     */
    public ConsentFormConsentHippaSignatureDTO getSignature() {
        return signature;
    }

    /**
     * @param signature The signature
     */
    public void setSignature(ConsentFormConsentHippaSignatureDTO signature) {
        this.signature = signature;
    }

}