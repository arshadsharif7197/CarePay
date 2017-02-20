package com.carecloud.carepaylibray.consentforms.models.datamodels.consentformedicare;

/**
 * Created by Rahul on 10/21/16.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConsentFormsConsentMedicareSignatureDTO {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("validation")
    @Expose
    private ConsentFormsConsentMedicareSignatureValidationDTO validation = new ConsentFormsConsentMedicareSignatureValidationDTO();

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
    public ConsentFormsConsentMedicareSignatureValidationDTO getValidation() {
        return validation;
    }

    /**
     * @param validation The validation
     */
    public void setValidation(ConsentFormsConsentMedicareSignatureValidationDTO validation) {
        this.validation = validation;
    }

}
