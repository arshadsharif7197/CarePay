package com.carecloud.carepaylibray.payments.models.postmodel;

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
            payAmount += paymentObject.getAmount();
            if(!paymentObject.isPaymentMethodValid()){
                return false;
            }
        }
        return payAmount == amount;
    }
}
