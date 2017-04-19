package com.carecloud.carepay.practice.clover.models;

import com.orm.SugarRecord;

/**
 * Created by lmenendez on 4/19/17.
 */

public class CloverQueuePaymentRecord extends SugarRecord {

    private String patientID;
    private String practiceID;
    private String practiceMgmt;
    private String queueTransition;
    private String paymentModelJsonEnc;
    private String paymentModelJson;

    public CloverQueuePaymentRecord(){

    }


    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getPracticeID() {
        return practiceID;
    }

    public void setPracticeID(String practiceID) {
        this.practiceID = practiceID;
    }

    public String getPracticeMgmt() {
        return practiceMgmt;
    }

    public void setPracticeMgmt(String practiceMgmt) {
        this.practiceMgmt = practiceMgmt;
    }

    public String getQueueTransition() {
        return queueTransition;
    }

    public void setQueueTransition(String queueTransition) {
        this.queueTransition = queueTransition;
    }

    public String getPaymentModelJsonEnc() {
        return paymentModelJsonEnc;
    }

    public void setPaymentModelJsonEnc(String paymentModelJsonEnc) {
        this.paymentModelJsonEnc = paymentModelJsonEnc;
    }

    public String getPaymentModelJson() {
        return paymentModelJson;
    }

    public void setPaymentModelJson(String paymentModelJson) {
        this.paymentModelJson = paymentModelJson;
    }
}
