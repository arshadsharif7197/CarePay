package com.carecloud.carepaylibray.consentforms.models.transitions;

/**
 * Created by Rahul on 10/21/16.
 */


        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

public class ConsentFormTransitionsDTO {

    @SerializedName("update_consent")
    @Expose
    private ConsentFormUpdateConsentDTO updateConsent;

    /**
     *
     * @return
     * The updateConsent
     */
    public ConsentFormUpdateConsentDTO getUpdateConsent() {
        return updateConsent;
    }

    /**
     *
     * @param updateConsent
     * The update_consent
     */
    public void setUpdateConsent(ConsentFormUpdateConsentDTO updateConsent) {
        this.updateConsent = updateConsent;
    }

}