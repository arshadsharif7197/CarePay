package com.carecloud.carepaylibray.signinsignup.dtos.signup;

/**
 * Created by Rahul on 11/7/16.
 */

import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

@Deprecated
public class SignUpDTO {

    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("properties")
    @Expose
    private SignUpPropertiesDTO properties = new SignUpPropertiesDTO();

    /**
     *
     * @return
     * The label
     */
    public String getLabel() {
        return label;
    }

    /**
     *
     * @param label
     * The label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     *
     * @return
     * The properties
     */
    public SignUpPropertiesDTO getProperties() {
        return properties;
    }

    /**
     *
     * @param properties
     * The properties
     */
    public void setProperties(SignUpPropertiesDTO properties) {
        this.properties = properties;
    }

}