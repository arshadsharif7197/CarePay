
package com.carecloud.carepaylibray.intake.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class DataModelsMetadataModel {

    @SerializedName("intake_forms")
    @Expose
    private IntakeFormsDataModel intakeForms;

    /**
     * 
     * @return
     *     The intakeForms
     */
    public IntakeFormsDataModel getIntakeForms() {
        return intakeForms;
    }

    /**
     * 
     * @param intakeForms
     *     The intake_forms
     */
    public void setIntakeForms(IntakeFormsDataModel intakeForms) {
        this.intakeForms = intakeForms;
    }

}
