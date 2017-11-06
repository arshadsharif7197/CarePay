package com.carecloud.carepaylibray.payments.models.history;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 9/28/17
 */

public class PaymentHistoryItemMetadata {

    @SerializedName("user_id")
    private String userId;

    @SerializedName("username")
    private String username;

    @SerializedName("patient_id")
    private String patientId;

    @SerializedName("created_dt")
    private String createdDt;

    @SerializedName("updated_dt")
    private String updatedDt;

    @SerializedName("practice_mgmt")
    private String practiceMgmt;

    @SerializedName("practice_id")
    private String practiceId;

    @SerializedName("payment_made_id")
    private String paymentMadeId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(String createdDt) {
        this.createdDt = createdDt;
    }

    public String getUpdatedDt() {
        return updatedDt;
    }

    public void setUpdatedDt(String updatedDt) {
        this.updatedDt = updatedDt;
    }

    public String getPracticeMgmt() {
        return practiceMgmt;
    }

    public void setPracticeMgmt(String practiceMgmt) {
        this.practiceMgmt = practiceMgmt;
    }

    public String getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(String practiceId) {
        this.practiceId = practiceId;
    }

    public String getPaymentMadeId() {
        return paymentMadeId;
    }

    public void setPaymentMadeId(String paymentMadeId) {
        this.paymentMadeId = paymentMadeId;
    }
}
