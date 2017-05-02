package com.carecloud.carepaylibray.signinsignup.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 25/04/17.
 */

public class SignInModelDTO {

    @SerializedName("label")
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
