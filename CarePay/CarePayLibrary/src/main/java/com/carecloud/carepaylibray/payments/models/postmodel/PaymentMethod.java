package com.carecloud.carepaylibray.payments.models.postmodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/21/17.
 */

public class PaymentMethod {

    @SerializedName("type")
    private PaymentType type;

    @SerializedName("execution")
    private PaymentExecution execution;

    @SerializedName("amount")
    private double amount;

    @SerializedName("card_id")
    private String cardID;

    @SerializedName("credit_card")
    private CreditCardModel creditCard;

    @SerializedName("transaction_response")
    private TransactionResponse transactionResponse;

    @SerializedName("bank_account_token")
    private String bankAccountToken;

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

    public String getCardID() {
        return cardID;
    }

    public void setCardID(String cardID) {
        this.cardID = cardID;
    }

    public CreditCardModel getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCardModel creditCard) {
        this.creditCard = creditCard;
    }

    public TransactionResponse getTransactionResponse() {
        return transactionResponse;
    }

    public void setTransactionResponse(TransactionResponse transactionResponse) {
        this.transactionResponse = transactionResponse;
    }

    public String getBankAccountToken() {
        return bankAccountToken;
    }

    public void setBankAccountToken(String bankAccountToken) {
        this.bankAccountToken = bankAccountToken;
    }

    /**
     * Verify Payment Method Validity
     * @return true is payment method is valid
     */
    public boolean isPaymentMethodValid(){
        switch (type){
            case credit_card:{
                return  amount >0 &&
                        isExecutionValid();
            }
            default:
                return amount > 0 &&
                        type != null;
        }
    }

    private boolean isExecutionValid(){
        switch (execution){
            case android_pay:
            case apple_pay:
            case clover:
            {
                return hasValidPaymentOption() &&
                        transactionResponse != null &&
                        transactionResponse.isValid();
            }
            default:{
                return  execution != null &&
                        hasValidPaymentOption();
            }
        }
    }

    private boolean hasValidPaymentOption(){
        return (cardID != null) ||
                 (creditCard != null && creditCard.isValidCreditCard());
    }




}
