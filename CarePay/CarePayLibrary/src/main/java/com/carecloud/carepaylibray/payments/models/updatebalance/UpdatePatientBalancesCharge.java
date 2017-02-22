package com.carecloud.carepaylibray.payments.models.updatebalance;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/22/17.
 */

public class UpdatePatientBalancesCharge {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("customer_id")
    @Expose
    private String customerId;
    @SerializedName("account_id")
    @Expose
    private String accountId;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("refunded_total")
    @Expose
    private String refundedTotal;
    @SerializedName("refundable_balance")
    @Expose
    private String refundableBalance;
    @SerializedName("merchant_service_transaction_id")
    @Expose
    private String merchantServiceTransactionId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getRefundedTotal() {
        return refundedTotal;
    }

    public void setRefundedTotal(String refundedTotal) {
        this.refundedTotal = refundedTotal;
    }

    public String getRefundableBalance() {
        return refundableBalance;
    }

    public void setRefundableBalance(String refundableBalance) {
        this.refundableBalance = refundableBalance;
    }

    public String getMerchantServiceTransactionId() {
        return merchantServiceTransactionId;
    }

    public void setMerchantServiceTransactionId(String merchantServiceTransactionId) {
        this.merchantServiceTransactionId = merchantServiceTransactionId;
    }


}
