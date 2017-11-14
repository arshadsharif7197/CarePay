package com.carecloud.carepay.practice.library.payments.models;

import com.orm.SugarRecord;

/**
 * Created by lmenendez on 11/14/17
 */

public class IntegratedPaymentQueueRecord extends SugarRecord {

    private String patientID;
    private String practiceID;
    private String practiceMgmt;
    private String queueTransition;
    private String deepstreamId;
    private String username;

    public IntegratedPaymentQueueRecord(){

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDeepstreamId() {
        return deepstreamId;
    }

    public void setDeepstreamId(String deepstreamId) {
        this.deepstreamId = deepstreamId;
    }
}
