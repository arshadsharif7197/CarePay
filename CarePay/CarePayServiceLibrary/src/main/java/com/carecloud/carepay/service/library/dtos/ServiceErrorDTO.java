
package com.carecloud.carepay.service.library.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServiceErrorDTO {

    @SerializedName("error_code")
    @Expose
    private String errorCode;
    @SerializedName("error_type")
    @Expose
    private String errorType;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("details")
    @Expose
    private String details;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

}
