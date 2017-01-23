package com.carecloud.carepaylibray.signinsignup.dtos.signin;

/**
 * Created by Rahul on 11/7/16.
 */


import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignInDTO {

    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("properties")
    @Expose
    private SignInPropertiesDTO properties;

    /**
     * @return The label
     */
    public String getLabel() {
        return StringUtil.isNullOrEmpty(label) ?
                CarePayConstants.NOT_DEFINED : label ;
    }

    /**
     * @param label The label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return The properties
     */
    public SignInPropertiesDTO getProperties() {
        return properties;
    }

    /**
     * @param properties The properties
     */
    public void setProperties(SignInPropertiesDTO properties) {
        this.properties = properties;
    }

}