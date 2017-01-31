package com.carecloud.carepaylibray.consentforms.models.datamodels.consentformedicare;

/**
 * Created by Rahul on 10/23/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ConsentFormMedicareSignByPatientDTO {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("validations")
    @Expose
    private List<ConsentFormMedicareSignByPatientValidationDTO> validations = new ArrayList<ConsentFormMedicareSignByPatientValidationDTO>();

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
    public List<ConsentFormMedicareSignByPatientValidationDTO> getValidations() {
        return validations;
    }

    /**
     * @param validations The validations
     */
    public void setValidations(List<ConsentFormMedicareSignByPatientValidationDTO> validations) {
        this.validations = validations;
    }

}