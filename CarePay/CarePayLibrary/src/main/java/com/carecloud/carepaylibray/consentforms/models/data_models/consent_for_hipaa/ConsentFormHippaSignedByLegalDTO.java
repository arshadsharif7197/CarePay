package com.carecloud.carepaylibray.consentforms.models.data_models.consent_for_hipaa;

/**
 * Created by Rahul on 10/23/16.
 */



import java.util.ArrayList;
        import java.util.List;
        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

public class ConsentFormHippaSignedByLegalDTO {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("validations")
    @Expose
    private List<ConsentFormHippaSignedByLegalValidationDTO> validations =
            new ArrayList<ConsentFormHippaSignedByLegalValidationDTO>();

    /**
     *
     * @return
     * The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     * The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return
     * The validations
     */
    public List<ConsentFormHippaSignedByLegalValidationDTO> getValidations() {
        return validations;
    }

    /**
     *
     * @param validations
     * The validations
     */
    public void setValidations(List<ConsentFormHippaSignedByLegalValidationDTO> validations) {
        this.validations = validations;
    }

}