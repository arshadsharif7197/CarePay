package com.carecloud.carepay.practice.library.payments.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by lmenendez on 11/14/17
 */

@Entity(tableName = "IntegratedPaymentQueueRecord")
public class IntegratedPaymentQueueRecord {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String patientID;
    private String practiceID;
    private String practiceMgmt;
    private String queueTransition;
    private String deepstreamId;
    private String username;
    private boolean recordOnly;


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

    public boolean isRecordOnly() {
        return recordOnly;
    }

    public void setRecordOnly(boolean recordOnly) {
        this.recordOnly = recordOnly;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
