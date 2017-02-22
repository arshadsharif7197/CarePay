package com.carecloud.carepaylibray.consentforms.models.datamodels.consentforauthorization;

/**
 * Created by Rahul on 10/21/16.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConsentFormConsentAuthPropertiesDTO {

    @SerializedName("minor_first_name")
    @Expose
    private ConsentFormMinorFirstNameDTO minorFirstName = new ConsentFormMinorFirstNameDTO();
    @SerializedName("minor_last_name")
    @Expose
    private ConsentFormMinorLastNameDTO minorLastName = new ConsentFormMinorLastNameDTO();
    @SerializedName("minor_date_of_birth")
    @Expose
    private ConsentFormMinorDateofBirthDTO minorDateOfBirth = new ConsentFormMinorDateofBirthDTO();
    @SerializedName("minor_gender")
    @Expose
    private ConsentFormMinorGenderDTO minorGender = new ConsentFormMinorGenderDTO();
    @SerializedName("signed_by_patient")
    @Expose
    private ConsentFormAuthorizationSignByPatientDTO signedByPatient = new ConsentFormAuthorizationSignByPatientDTO();
    @SerializedName("signed_by_legal")
    @Expose
    private ConsentFormAuthorizationSignByLegalDTO signedByLegal = new ConsentFormAuthorizationSignByLegalDTO();
    @SerializedName("signature")
    @Expose
    private ConsentFormConsentAuthSignatureDTO signature = new ConsentFormConsentAuthSignatureDTO();

    /**
     * @return The minorFirstName
     */
    public ConsentFormMinorFirstNameDTO getMinorFirstName() {
        return minorFirstName;
    }

    /**
     * @param minorFirstName The minor_first_name
     */
    public void setMinorFirstName(ConsentFormMinorFirstNameDTO minorFirstName) {
        this.minorFirstName = minorFirstName;
    }

    /**
     * @return The minorLastName
     */
    public ConsentFormMinorLastNameDTO getMinorLastName() {
        return minorLastName;
    }

    /**
     * @param minorLastName The minor_last_name
     */
    public void setMinorLastName(ConsentFormMinorLastNameDTO minorLastName) {
        this.minorLastName = minorLastName;
    }

    /**
     * @return The minorDateOfBirth
     */
    public ConsentFormMinorDateofBirthDTO getMinorDateOfBirth() {
        return minorDateOfBirth;
    }

    /**
     * @param minorDateOfBirth The minor_date_of_birth
     */
    public void setMinorDateOfBirth(ConsentFormMinorDateofBirthDTO minorDateOfBirth) {
        this.minorDateOfBirth = minorDateOfBirth;
    }

    /**
     * @return The minorGender
     */
    public ConsentFormMinorGenderDTO getMinorGender() {
        return minorGender;
    }

    /**
     * @param minorGender The minor_gender
     */
    public void setMinorGender(ConsentFormMinorGenderDTO minorGender) {
        this.minorGender = minorGender;
    }

    /**
     * @return The signedByPatient
     */
    public ConsentFormAuthorizationSignByPatientDTO getSignedByPatient() {
        return signedByPatient;
    }

    /**
     * @param signedByPatient The signed_by_patient
     */
    public void setSignedByPatient(ConsentFormAuthorizationSignByPatientDTO signedByPatient) {
        this.signedByPatient = signedByPatient;
    }

    /**
     * @return The signedByLegal
     */
    public ConsentFormAuthorizationSignByLegalDTO getSignedByLegal() {
        return signedByLegal;
    }

    /**
     * @param signedByLegal The signed_by_legal
     */
    public void setSignedByLegal(ConsentFormAuthorizationSignByLegalDTO signedByLegal) {
        this.signedByLegal = signedByLegal;
    }

    /**
     * @return The signature
     */
    public ConsentFormConsentAuthSignatureDTO getSignature() {
        return signature;
    }

    /**
     * @param signature The signature
     */
    public void setSignature(ConsentFormConsentAuthSignatureDTO signature) {
        this.signature = signature;
    }

}