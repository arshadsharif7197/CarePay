package com.carecloud.carepay.practice.library.signin.dtos;

/**
 * Created by Rahul on 11/2/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignInModelDTO {
    @SerializedName("labels")
    @Expose
    private String labels;
    @SerializedName("properties")
    @Expose
    private SignInPropertiesDTO properties = new SignInPropertiesDTO();

    /**
     * @return The labels
     */
    public String getLabels() {
        return labels;
    }

    /**
     * @param labels The labels
     */
    public void setLabels(String labels) {
        this.labels = labels;
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
