package com.carecloud.carepaylibray.consentforms.models.transitions;

/**
 * Created by Rahul on 10/21/16.
 */


import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConsentFormTransitionsDTO {

    @SerializedName("update_consent")
    @Expose
    private TransitionDTO updateConsent = new TransitionDTO();

    /**
     * @return The updateConsent
     */
    public TransitionDTO getUpdateConsent() {
        return updateConsent;
    }

    /**
     * @param updateConsent The update_consent
     */
    public void setUpdateConsent(TransitionDTO updateConsent) {
        this.updateConsent = updateConsent;
    }

}