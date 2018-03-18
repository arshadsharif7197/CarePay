package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by lmenendez on 3/24/17
 */

public class PatientPaymentPayload {

    @SerializedName("confirmation")
    @Expose
    private String confirmation;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("total")
    @Expose
    private double total;
    @SerializedName("paymentExceptions")
    @Expose
    private List<PaymentExceptionDTO> paymentExceptions;


    public String getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(String confirmation) {
        this.confirmation = confirmation;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<PaymentExceptionDTO> getPaymentExceptions() {
        return paymentExceptions;
    }

    public void setPaymentExceptions(List<PaymentExceptionDTO> paymentExceptions) {
        this.paymentExceptions = paymentExceptions;
    }
}
