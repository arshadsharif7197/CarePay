
package com.carecloud.carepay.practice.library.signin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GenderDTO {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("validations")
    @Expose
    private List<GenderValidationDTO> validations = new ArrayList<>();
    @SerializedName("options")
    @Expose
    private List<GenderOptionDTO> options = null;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<GenderValidationDTO> getValidations() {
        return validations;
    }

    public void setValidations(List<GenderValidationDTO> validations) {
        this.validations = validations;
    }

    public List<GenderOptionDTO> getOptions() {
        return options;
    }

    public void setOptions(List<GenderOptionDTO> options) {
        this.options = options;
    }

}
