package com.carecloud.carepaylibray.payments.models.postmodel;

import com.carecloud.carepaylibray.appointments.models.ScheduleAppointmentRequestDTO;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 2/21/17.
 */

public class PaymentPostModel {

    @SerializedName("amount")
    private double amount = -1;

    @SerializedName("payments")
    private List<PaymentObject> paymentObjects = new ArrayList<>();

    @SerializedName("transaction_response")
    private JsonObject transactionResponse;

    @SerializedName("appointment")
    private ScheduleAppointmentRequestDTO appointmentRequestDTO;

    @SerializedName("appointment_id")
    private String appointmentId;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public List<PaymentObject> getPaymentObjects() {
        return paymentObjects;
    }

    public void setPaymentObjects(List<PaymentObject> paymentObjects) {
        this.paymentObjects = paymentObjects;
    }

    public JsonObject getTransactionResponse() {
        return transactionResponse;
    }

    public void setTransactionResponse(JsonObject transactionResponse) {
        this.transactionResponse = transactionResponse;
    }


    public void addPaymentMethod(PaymentObject paymentObject){
        paymentObjects.add(paymentObject);
    }

    /**
     * Verify validity of payment model
     * @return true if payment model is valid
     */
    public boolean isPaymentModelValid(){
        if(amount <0 || paymentObjects.isEmpty()){
            return false;
        }
        double payAmount = 0;
        for(PaymentObject paymentObject : paymentObjects){
            payAmount = (double) Math.round((payAmount+paymentObject.getAmount())*100)/100;
            if(!paymentObject.isPaymentMethodValid()){
                return false;
            }
            PaymentExecution execution = paymentObject.getExecution();
            switch (execution) {
                case android_pay:
                case apple_pay:
                case clover: {
                    if(transactionResponse == null){
                        return false;
                    }
                    break;
                }
                default: {
                    //nothing
                }
            }
            }
        return payAmount == amount;
    }

    public ScheduleAppointmentRequestDTO getAppointmentRequestDTO() {
        return appointmentRequestDTO;
    }

    public void setAppointmentRequestDTO(ScheduleAppointmentRequestDTO appointmentRequestDTO) {
        this.appointmentRequestDTO = appointmentRequestDTO;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }
}
