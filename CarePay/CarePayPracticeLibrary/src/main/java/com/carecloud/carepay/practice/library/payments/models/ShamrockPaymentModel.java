package com.carecloud.carepay.practice.library.payments.models;

import com.google.gson.annotations.SerializedName;

public class ShamrockPaymentModel {

    @SerializedName("payload")
    private ShamrockPaymentPayload payload = new ShamrockPaymentPayload();

    public ShamrockPaymentPayload getPayload() {
        return payload;
    }

    public void setPayload(ShamrockPaymentPayload payload) {
        this.payload = payload;
    }

    public static class ShamrockPaymentPayload {

        @SerializedName("patient_payments")
        private ShamrockPaymentsPostModel postModel = new ShamrockPaymentsPostModel();

        public ShamrockPaymentsPostModel getPostModel() {
            return postModel;
        }

        public void setPostModel(ShamrockPaymentsPostModel postModel) {
            this.postModel = postModel;
        }
    }
}
