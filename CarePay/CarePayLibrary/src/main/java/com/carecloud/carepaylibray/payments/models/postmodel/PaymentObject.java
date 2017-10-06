package com.carecloud.carepaylibray.payments.models.postmodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/21/17
 */

public class PaymentObject {

    @SerializedName("type")
    private PaymentType type;

    @SerializedName("execution")
    private PaymentExecution execution;

    @SerializedName("amount")
    private double amount;

    @SerializedName("provider_id")
    private String providerID;

    @SerializedName("location_id")
    private String locationID;

    @SerializedName("papi_payment_method")
    private PapiPaymentMethod papiPaymentMethod;

    @SerializedName("credit_card")
    private CreditCardModel creditCard;

    @SerializedName("bank_account_token")
    private String bankAccountToken;

    @SerializedName("responsibility_type")
    private String responsibilityType;

    @SerializedName("application")
    private PaymentApplication paymentApplication;

    @SerializedName("new_charge")
    private PaymentNewCharge paymentNewCharge;

    @SerializedName("description")
    private String description;

    public PaymentType getType() {
        return type;
    }

    public void setType(PaymentType type) {
        this.type = type;
    }

    public PaymentExecution getExecution() {
        return execution;
    }

    public void setExecution(PaymentExecution execution) {
        this.execution = execution;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public CreditCardModel getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCardModel creditCard) {
        this.creditCard = creditCard;
    }

    public String getBankAccountToken() {
        return bankAccountToken;
    }

    public void setBankAccountToken(String bankAccountToken) {
        this.bankAccountToken = bankAccountToken;
    }

    public String getProviderID() {
        return providerID;
    }

    public void setProviderID(String providerID) {
        this.providerID = providerID;
    }

    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }

    public String getResponsibilityType() {
        return responsibilityType;
    }

    public void setResponsibilityType(String responsibilityType) {
        this.responsibilityType = responsibilityType;
    }

    public PaymentApplication getPaymentApplication() {
        return paymentApplication;
    }

    public void setPaymentApplication(PaymentApplication paymentApplication) {
        this.paymentApplication = paymentApplication;
    }

    public PapiPaymentMethod getPapiPaymentMethod() {
        return papiPaymentMethod;
    }

    public void setPapiPaymentMethod(PapiPaymentMethod papiPaymentMethod) {
        this.papiPaymentMethod = papiPaymentMethod;
    }

    /**
     * Verify Payment Method Validity
     * @return true is payment method is valid
     */
    public boolean isPaymentMethodValid(){
        if(type == null){
            return false;
        }
        switch (type){
            case credit_card:{
                return  amount > 0 &&
                        isExecutionValid();
            }
            default:
                return amount > 0;
        }
    }

    private boolean isExecutionValid(){
        if(execution == null){
            return false;
        }
            return hasValidPaymentOption();
    }

    private boolean hasValidPaymentOption(){
        return (papiPaymentMethod != null && papiPaymentMethod.isValid()) ||
                 (creditCard != null && creditCard.isValidCreditCard());
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PaymentNewCharge getPaymentNewCharge() {
        return paymentNewCharge;
    }

    public void setPaymentNewCharge(PaymentNewCharge paymentNewCharge) {
        this.paymentNewCharge = paymentNewCharge;
    }
}
