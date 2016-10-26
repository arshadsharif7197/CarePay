package com.carecloud.carepaylibray.consentforms.models.datamodels.consentforhipaa;

/**
 * Created by Rahul on 10/21/16.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConsentFormConsentHippaSignatureDTO {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("validation")
    @Expose
    private ConsentFormConsentHippaSignatureValidationDTO validation;

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return The validation
     */
    public ConsentFormConsentHippaSignatureValidationDTO getValidation() {
        return validation;
    }

    /**
     * @param validation The validation
     */
    public void setValidation(ConsentFormConsentHippaSignatureValidationDTO validation) {
        this.validation = validation;
    }

}