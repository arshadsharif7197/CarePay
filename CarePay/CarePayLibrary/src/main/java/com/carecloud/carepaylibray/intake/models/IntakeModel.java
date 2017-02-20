
package com.carecloud.carepaylibray.intake.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class IntakeModel {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("properties")
    @Expose
    private PropertiesIntakeModel properties = new PropertiesIntakeModel();

    /**
     * 
     * @return
     *     The type
     */
    public String getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 
     * @return
     *     The properties
     */
    public PropertiesIntakeModel getProperties() {
        return properties;
    }

    /**
     * 
     * @param properties
     *     The properties
     */
    public void setProperties(PropertiesIntakeModel properties) {
        this.properties = properties;
    }

}
