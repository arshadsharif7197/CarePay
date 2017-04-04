package com.carecloud.breezemini.payments.postmodel;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 2/21/17.
 */

public class PaymentPostModel {

    @SerializedName("amount")
    private double amount = -1;

    @SerializedName("payment_methods")
    private List<PaymentMethod> paymentMethods = new ArrayList<>();

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<PaymentMethod> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public void addPaymentMethod(PaymentMethod paymentMethod){
        paymentMethods.add(paymentMethod);
    }

    /**
     * Verify validity of payment model
     * @return true if payment model is valid
     */
    public boolean isPaymentModelValid(){
        if(amount <0 || paymentMethods.isEmpty()){
            return false;
        }
        double payAmount = 0;
        for(PaymentMethod paymentMethod : paymentMethods){
            payAmount += paymentMethod.getAmount();
            if(!paymentMethod.isPaymentMethodValid()){
                return false;
            }
        }
        return payAmount == amount;
    }
}
