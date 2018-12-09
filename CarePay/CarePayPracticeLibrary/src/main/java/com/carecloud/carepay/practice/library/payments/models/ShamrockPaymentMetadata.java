package com.carecloud.carepay.practice.library.payments.models;

import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentMetadata;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 11/9/17
 */

public class ShamrockPaymentMetadata extends IntegratedPaymentMetadata {

    @SerializedName("practice_mgmt")
    private String practiceMgmt;

    @SerializedName("business_entity_id")
    private String practiceId;

    @SerializedName("patient_id")
    private String patientId;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("breeze_user_id")
    private String breezeUserId;


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

    public String getBreezeUserId() {
        return breezeUserId;
    }

    public void setBreezeUserId(String breezeUserId) {
        this.breezeUserId = breezeUserId;
    }

    /**
     * Fill base fields
     * @param metadata metadata
     * @return this
     */
    public ShamrockPaymentMetadata setIntegratedPaymentMetadata(IntegratedPaymentMetadata metadata){
        super.setAppointmentId(metadata.getAppointmentId());
        super.setAppointmentRequestDTO(metadata.getAppointmentRequestDTO());
        super.setCancellationReasonId(metadata.getCancellationReasonId());
        super.setOrder(metadata.getOrder());


        return this;
    }
}
