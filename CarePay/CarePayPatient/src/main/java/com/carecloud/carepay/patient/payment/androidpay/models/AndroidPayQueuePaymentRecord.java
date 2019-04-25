package com.carecloud.carepay.patient.payment.androidpay.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by lmenendez on 4/19/17.
 */
@Entity(tableName = "AndroidPayQueuePaymentRecord")
public class AndroidPayQueuePaymentRecord {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String patientID;
    private String practiceID;
    private String practiceMgmt;
    private String queueTransition;
    private String paymentModelJsonEnc;
    private String paymentModelJson;
    private String username;


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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
