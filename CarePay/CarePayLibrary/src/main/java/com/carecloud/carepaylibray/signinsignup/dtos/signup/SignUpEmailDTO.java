package com.carecloud.carepaylibray.signinsignup.dtos.signup;

/**
 * Created by Rahul on 11/7/16.
 */


import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class SignUpEmailDTO {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("validations")
    @Expose
    private List<SignUpEmailValidationDTO> validations = new ArrayList<>();

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
        return  StringUtil.isNullOrEmpty(label) ?
                CarePayConstants.NOT_DEFINED : label;
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
    public List<SignUpEmailValidationDTO> getValidations() {
        return validations;
    }

    /**
     * @param validations The validations
     */
    public void setValidations(List<SignUpEmailValidationDTO> validations) {
        this.validations = validations;
    }

}