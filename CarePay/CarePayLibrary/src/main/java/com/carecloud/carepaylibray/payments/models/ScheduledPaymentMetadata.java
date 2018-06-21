package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.SerializedName;

public class ScheduledPaymentMetadata extends PaymentPlanMetadataDTO {

    @SerializedName("one_time_payment_id")
    private String oneTimePaymentId;

    public String getOneTimePaymentId() {
        return oneTimePaymentId;
    }

    public void setOneTimePaymentId(String oneTimePaymentId) {
        this.oneTimePaymentId = oneTimePaymentId;
    }
}
