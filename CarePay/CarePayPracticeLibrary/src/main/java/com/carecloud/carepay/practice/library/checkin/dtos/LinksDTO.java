package com.carecloud.carepay.practice.library.checkin.dtos;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sudhir_pingale on 11/11/2016.
 * Model for Links
 */
public class LinksDTO {

    @SerializedName("self")
    @Expose
    private TransitionDTO self = new TransitionDTO();
    @SerializedName("checkin_status")
    @Expose
    private TransitionDTO checkinStatus = new TransitionDTO();
    @SerializedName("queue_status")
    @Expose
    private TransitionDTO queueStatus = new TransitionDTO();

    @SerializedName("find_patient")
    @Expose
    private TransitionDTO findPatient = new TransitionDTO();

    @SerializedName("patient_balances")
    @Expose
    private TransitionDTO patientBalances = new TransitionDTO();

    /**
     * @return The self
     */
    public TransitionDTO getSelf() {
        return self;
    }

    /**
     * @param self The self
     */
    public void setSelf(TransitionDTO self) {
        this.self = self;
    }

    /**
     * @return The checkinStatus
     */
    public TransitionDTO getCheckinStatus() {
        return checkinStatus;
    }

    /**
     * @param checkinStatus The checkin_status
     */
    public void setCheckinStatus(TransitionDTO checkinStatus) {
        this.checkinStatus = checkinStatus;
    }

    /**
     * @return The queueStatus
     */
    public TransitionDTO getQueueStatus() {
        return queueStatus;
    }

    /**
     * @param queueStatus the queueStatus to set
     */
    public void setQueueStatus(TransitionDTO queueStatus) {
        this.queueStatus = queueStatus;
    }

    /**
     * @return find patient link
     */
    public TransitionDTO getFindPatient() {
        return findPatient;
    }

    /**
     *
     * @return patient balances link
     */
    public TransitionDTO getPatientBalances() {
        return patientBalances;
    }
}
