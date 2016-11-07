package com.carecloud.carepay.practice.library.signin.dtos;

/**
 * Created by Rahul on 11/2/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignInModelDTO {
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
        return label;
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
