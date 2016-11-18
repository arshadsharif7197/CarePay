package com.carecloud.carepay.practice.library.signin.dtos;

/**
 * Created by sudhir_pingale on 11/17/2016.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignInPatientModeModelDTO {
    @SerializedName("label")
    @Expose
    private String labels;
    @SerializedName("properties")
    @Expose
    private SignInPropertiesDTO properties;

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
