package com.carecloud.carepay.practice.library.signin.dtos;

/**
 * Created by Rahul on 11/2/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class SignInEmailDTO {
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("validations")
    @Expose
    private List<SignInEmailValidationDTO> validations = new ArrayList<>();

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
    public List<SignInEmailValidationDTO> getValidations() {
        return validations;
    }

    /**
     * @param validations The validations
     */
    public void setValidations(List<SignInEmailValidationDTO> validations) {
        this.validations = validations;
    }

}
