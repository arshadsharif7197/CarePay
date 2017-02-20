
package com.carecloud.carepay.practice.library.signin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PersonalInfoDTO {

    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("properties")
    @Expose
    private GenderPropertiesDTO properties = new GenderPropertiesDTO();

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public GenderPropertiesDTO getProperties() {
        return properties;
    }

    public void setProperties(GenderPropertiesDTO properties) {
        this.properties = properties;
    }

}
