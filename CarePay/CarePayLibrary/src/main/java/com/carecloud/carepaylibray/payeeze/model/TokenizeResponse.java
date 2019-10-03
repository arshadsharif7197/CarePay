package com.carecloud.carepaylibray.payeeze.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 2019-06-21.
 */
public class TokenizeResponse {

    @SerializedName("correlation_id")
    private String correlationId;
    @SerializedName("status")
    private String status;
    @SerializedName("type")
    private String type;
    @SerializedName("token")
    private Token token;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }
}
