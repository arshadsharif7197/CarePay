package com.carecloud.carepaylibray.signinsignup.dtos.signup;

/**
 * Created by Rahul on 11/7/16.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RepeatPasswordValidationDTO {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("value")
    @Expose
    private Boolean value;
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
    public Boolean getValue() {
        return value;
    }

    /**
     * @param value The value
     */
    public void setValue(Boolean value) {
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