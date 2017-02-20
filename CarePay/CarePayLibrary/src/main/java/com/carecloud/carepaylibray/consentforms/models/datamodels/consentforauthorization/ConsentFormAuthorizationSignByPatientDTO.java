package com.carecloud.carepaylibray.consentforms.models.datamodels.consentforauthorization;

/**
 * Created by Rahul on 10/23/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class ConsentFormAuthorizationSignByPatientDTO {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("validations")
    @Expose
    private List<ConsentFormAuthorizationSignByPatientValidationDTO> validations = new ArrayList<>();

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
     * @return The validations
     */
    public List<ConsentFormAuthorizationSignByPatientValidationDTO> getValidations() {
        return validations;
    }

    /**
     * @param validations The validations
     */
    public void setValidations(List<ConsentFormAuthorizationSignByPatientValidationDTO> validations) {
        this.validations = validations;
    }

}