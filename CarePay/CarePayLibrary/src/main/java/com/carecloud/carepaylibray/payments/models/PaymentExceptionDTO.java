package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by leo on 7/23/17
 */

public class PaymentExceptionDTO {

    @SerializedName("ExceptionMessage")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
