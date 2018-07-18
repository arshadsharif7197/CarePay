package com.carecloud.carepaylibray.payments.models.history;

import com.carecloud.carepaylibray.payments.models.PaymentPlanPayloadDTO;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 9/28/17
 */

public class PaymentHistoryItemPayloadMetadata {

    @SerializedName("practice_mgmt")
    private String practiceMgmt;

    @SerializedName("business_entity_id")
    private String businessEntityId;

    @SerializedName("patient_id")
    private String patientId;

    @SerializedName("breeze_user_id")
    private String userId;

    @SerializedName("carepay")
    private boolean carepay = false;

    @SerializedName("prepped_for_processing")
    private boolean prepped = false;

    @SerializedName("externally_processed")
    private boolean externallyProcessed = false;

    @SerializedName("payment_plan")
    private PaymentPlanPayloadDTO paymentPlan;


    public String getPracticeMgmt() {
        return practiceMgmt;
    }

    public void setPracticeMgmt(String practiceMgmt) {
        this.practiceMgmt = practiceMgmt;
    }

    public String getBusinessEntityId() {
        return businessEntityId;
    }

    public void setBusinessEntityId(String businessEntityId) {
        this.businessEntityId = businessEntityId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isCarepay() {
        return carepay;
    }

    public void setCarepay(boolean carepay) {
        this.carepay = carepay;
    }

    public boolean isPrepped() {
        return prepped;
    }

    public void setPrepped(boolean prepped) {
        this.prepped = prepped;
    }

    public boolean isExternallyProcessed() {
        return externallyProcessed;
    }

    public void setExternallyProcessed(boolean externallyProcessed) {
        this.externallyProcessed = externallyProcessed;
    }

    public PaymentPlanPayloadDTO getPaymentPlan() {
        return paymentPlan;
    }

    public void setPaymentPlan(PaymentPlanPayloadDTO paymentPlan) {
        this.paymentPlan = paymentPlan;
    }
}
