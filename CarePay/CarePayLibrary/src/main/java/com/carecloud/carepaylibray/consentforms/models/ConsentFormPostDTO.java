package com.carecloud.carepaylibray.consentforms.models;

/**
 * Created by Rahul on 10/21/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConsentFormPostDTO {

    @SerializedName("consent_for_medicare")
    @Expose
    private ConsentFormConsentMedicareDTO consentForMedicare;
    @SerializedName("consent_for_authorization")
    @Expose
    private ConsentFormConsentAuthorizationDTO consentForAuthorization;
    @SerializedName("consent_for_hipaa")
    @Expose
    private ConsentFormConsentHippaDTO consentForHipaa;

    /**
     * @return The consentForMedicare
     */
    public ConsentFormConsentMedicareDTO getConsentForMedicare() {
        return consentForMedicare;
    }

    /**
     * @param consentForMedicare The consent_for_medicare
     */
    public void setConsentForMedicare(ConsentFormConsentMedicareDTO consentForMedicare) {
        this.consentForMedicare = consentForMedicare;
    }

    /**
     * @return The consentForAuthorization
     */
    public ConsentFormConsentAuthorizationDTO getConsentForAuthorization() {
        return consentForAuthorization;
    }

    /**
     * @param consentForAuthorization The consent_for_authorization
     */
    public void setConsentForAuthorization(ConsentFormConsentAuthorizationDTO consentForAuthorization) {
        this.consentForAuthorization = consentForAuthorization;
    }

    /**
     * @return The consentForHipaa
     */
    public ConsentFormConsentHippaDTO getConsentForHipaa() {
        return consentForHipaa;
    }

    /**
     * @param consentForHipaa The consent_for_hipaa
     */
    public void setConsentForHipaa(ConsentFormConsentHippaDTO consentForHipaa) {
        this.consentForHipaa = consentForHipaa;
    }

}