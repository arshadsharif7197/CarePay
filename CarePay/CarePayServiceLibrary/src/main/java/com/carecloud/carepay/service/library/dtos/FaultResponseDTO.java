package com.carecloud.carepay.service.library.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FaultResponseDTO {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("exception")
    @Expose
    private ExceptionDTO exception;
    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("module")
    @Expose
    private String module;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ExceptionDTO getException() {
        return exception;
    }

    public void setException(ExceptionDTO exception) {
        this.exception = exception;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

}
