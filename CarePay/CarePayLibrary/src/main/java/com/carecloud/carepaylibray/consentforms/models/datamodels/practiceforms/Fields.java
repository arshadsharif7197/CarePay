
package com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class Fields {

    @SerializedName("properties")
    @Expose
    private Properties properties = new Properties();
    @SerializedName("required")
    @Expose
    private List<String> required = new ArrayList<>();

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public List<String> getRequired() {
        return required;
    }

    public void setRequired(List<String> required) {
        this.required = required;
    }

}
