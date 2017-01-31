package com.carecloud.carepaylibray.consentforms.models.datamodels.consentforauthorization;

/**
 * Created by Rahul on 10/23/16.
 */


import com.carecloud.carepaylibray.consentforms.models.ConsentFormValueDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConsentFormMinorGenderValidationDTO {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("value")
    @Expose
    private ConsentFormValueDTO value;
    @SerializedName("error_message")
    @Expose
    private String errorMessage;

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
     * @return The value
     */
    public ConsentFormValueDTO getValue() {
        return value;
    }

    /**
     * @param value The value
     */
    public void setValue(ConsentFormValueDTO value) {
        this.value = value;
    }

    /**
     * @return The errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage The error_message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}