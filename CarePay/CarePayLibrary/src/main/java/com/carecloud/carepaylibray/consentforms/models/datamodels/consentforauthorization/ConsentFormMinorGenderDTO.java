package com.carecloud.carepaylibray.consentforms.models.datamodels.consentforauthorization;

/**
 * Created by Rahul on 10/21/16.
 */

import com.carecloud.carepaylibray.consentforms.models.ConsentFormOptionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class ConsentFormMinorGenderDTO {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("validations")
    @Expose
    private List<ConsentFormMinorGenderValidationDTO> validations =
            new ArrayList<ConsentFormMinorGenderValidationDTO>();
    @SerializedName("options")
    @Expose
    private List<ConsentFormOptionDTO> options = new ArrayList<ConsentFormOptionDTO>();

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

   /* */

    /**
     * @return The validations
     */
    public List<ConsentFormMinorGenderValidationDTO> getValidations() {
        return validations;
    }

    /**
     * @param validations The validations
     */
    public void setValidations(List<ConsentFormMinorGenderValidationDTO> validations) {
        this.validations = validations;
    }

    /**
     * @return The options
     */
    public List<ConsentFormOptionDTO> getOptions() {
        return options;
    }

    /**
     * @param options The options
     */
    public void setOptions(List<ConsentFormOptionDTO> options) {
        this.options = options;
    }

}