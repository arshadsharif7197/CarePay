package com.carecloud.carepaylibray.consentforms.models.datamodels.consentforauthorization;

/**
 * Created by Rahul on 10/21/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;




public class ConsentFormMinorFirstNameDTO {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("validations")
    @Expose
    private List<ConsentFormMinorFirstNameValidationDTO> validations = new ArrayList<>();

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
     * @return The label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label The label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return The validations
     */
    public List<ConsentFormMinorFirstNameValidationDTO> getValidations() {
        return validations;
    }

    /**
     * @param validations The validations
     */
    public void setValidations(List<ConsentFormMinorFirstNameValidationDTO> validations) {
        this.validations = validations;
    }

}