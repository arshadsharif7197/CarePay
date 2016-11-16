package com.carecloud.carepaylibray.consentforms.models.payload;

import com.carecloud.carepaylibray.consentforms.models.datamodels.consentforauthorization.ConsentFormAuthorizationPayloadDTO;
import com.carecloud.carepaylibray.consentforms.models.datamodels.consentforhipaa.ConsentFormHippaPayloadDTO;
import com.carecloud.carepaylibray.consentforms.models.datamodels.consentformedicare.ConsentFormMedicarePayloadDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rahul on 11/14/16.
 */

public class ConseFormsPayloadDTO {
    @SerializedName("consent_for_medicare")
    @Expose
    private ConsentFormMedicarePayloadDTO consentFormMedicarePayload;
    @SerializedName("consent_for_authorization")
    @Expose
    private ConsentFormAuthorizationPayloadDTO consentFormAuthorizationPayloadDTO;
    @SerializedName("consent_for_hipaa")
    @Expose
    private ConsentFormHippaPayloadDTO consentFormHippaPayload;

    public ConsentFormMedicarePayloadDTO getConsentFormMedicarePayload() {
        return consentFormMedicarePayload;
    }

    public void setConsentFormMedicarePayload(ConsentFormMedicarePayloadDTO consentFormMedicarePayload) {
        this.consentFormMedicarePayload = consentFormMedicarePayload;
    }

    public ConsentFormAuthorizationPayloadDTO getConsentFormAuthorizationPayloadDTO() {
        return consentFormAuthorizationPayloadDTO;
    }

    public void setConsentFormAuthorizationPayloadDTO(ConsentFormAuthorizationPayloadDTO consentFormAuthorizationPayloadDTO) {
        this.consentFormAuthorizationPayloadDTO = consentFormAuthorizationPayloadDTO;
    }

    public ConsentFormHippaPayloadDTO getConsentFormHippaPayload() {
        return consentFormHippaPayload;
    }

    public void setConsentFormHippaPayload(ConsentFormHippaPayloadDTO consentFormHippaPayload) {
        this.consentFormHippaPayload = consentFormHippaPayload;
    }
}
