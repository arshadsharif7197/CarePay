package com.carecloud.carepaylibray.consentforms.models.transitions;

/**
 * Created by Rahul on 10/23/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class ConsentFormsPracticeMgmtDTO {


    @Expose
    private String name;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("validations")
    @Expose
    private List<ConsentFormsPracticeMgmtValidationDTO> validations = new ArrayList<ConsentFormsPracticeMgmtValidationDTO>();

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
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
    public List<ConsentFormsPracticeMgmtValidationDTO> getValidations() {
        return validations;
    }

    /**
     * @param validations The validations
     */
    public void setValidations(List<ConsentFormsPracticeMgmtValidationDTO> validations) {
        this.validations = validations;
    }

}