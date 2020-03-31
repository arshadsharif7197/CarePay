package com.carecloud.carepay.service.library.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServerErrorDTO {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private MessageErrorDTO message = new MessageErrorDTO();
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

    public MessageErrorDTO getMessage() {
        return message;
    }

    public void setMessage(MessageErrorDTO message) {
        this.message = message;
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
