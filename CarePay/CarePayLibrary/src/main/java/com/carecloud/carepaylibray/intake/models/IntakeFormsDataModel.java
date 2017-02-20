
package com.carecloud.carepaylibray.intake.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class IntakeFormsDataModel {

    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("properties")
    @Expose
    private PropertiesModel properties = new PropertiesModel();

    /**
     * 
     * @return
     *     The label
     */
    public String getLabel() {
        return label;
    }

    /**
     * 
     * @param label
     *     The label
     */
    public void setLabel(String label) {
        this.label = label;
    }

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
    public PropertiesModel getProperties() {
        return properties;
    }

    /**
     * 
     * @param properties
     *     The properties
     */
    public void setProperties(PropertiesModel properties) {
        this.properties = properties;
    }

}
