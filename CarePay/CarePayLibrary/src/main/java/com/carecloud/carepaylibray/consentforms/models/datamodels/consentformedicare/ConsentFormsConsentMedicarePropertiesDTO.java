package com.carecloud.carepaylibray.consentforms.models.datamodels.consentformedicare;

/**
 * Created by Rahul on 10/21/16.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConsentFormsConsentMedicarePropertiesDTO {

    @SerializedName("signed_by_patient")
    @Expose
    private ConsentFormMedicareSignByPatientDTO signedByPatient;
    @SerializedName("signed_by_legal")
    @Expose
    private ConsentFormMedicareSignByLegalDTO signedByLegal;
    @SerializedName("signature")
    @Expose
    private ConsentFormsConsentMedicareSignatureDTO signature;

    /**
     * @return The signedByPatient
     */
    public ConsentFormMedicareSignByPatientDTO getSignedByPatient() {
        return signedByPatient;
    }

    /**
     * @param signedByPatient The signed_by_patient
     */
    public void setSignedByPatient(ConsentFormMedicareSignByPatientDTO signedByPatient) {
        this.signedByPatient = signedByPatient;
    }

    /**
     * @return The signedByLegal
     */
    public ConsentFormMedicareSignByLegalDTO getSignedByLegal() {
        return signedByLegal;
    }

    /**
     * @param signedByLegal The signed_by_legal
     */
    public void setSignedByLegal(ConsentFormMedicareSignByLegalDTO signedByLegal) {
        this.signedByLegal = signedByLegal;
    }

    /**
     * @return The signature
     */
    public ConsentFormsConsentMedicareSignatureDTO getSignature() {
        return signature;
    }

    /**
     * @param signature The signature
     */
    public void setSignature(ConsentFormsConsentMedicareSignatureDTO signature) {
        this.signature = signature;
    }

}
