package com.carecloud.shamrocksdk.payment.models;

import com.google.gson.annotations.SerializedName;

public class RequestError {

    @SerializedName("id")
    private String id;

    @SerializedName("error")
    private String error;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
