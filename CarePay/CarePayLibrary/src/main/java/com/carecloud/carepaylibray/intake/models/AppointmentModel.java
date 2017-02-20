package com.carecloud.carepaylibray.intake.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AppointmentModel {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("visible")
    @Expose
    private Boolean visible;
    @SerializedName("validations")
    @Expose
    private List<ValidationModel> validations = new ArrayList<>();

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

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
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return The visible
     */
    public Boolean getVisible() {
        return visible;
    }

    /**
     * @param visible The visible
     */
    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    /**
     * @return The validations
     */
    public List<ValidationModel> getValidations() {
        return validations;
    }

    /**
     * @param validations The validations
     */
    public void setValidations(List<ValidationModel> validations) {
        this.validations = validations;
    }

}
