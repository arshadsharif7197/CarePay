package com.carecloud.carepay.practice.library.patientmode.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 3/6/17.
 */

public class PracticeWelcomeDTO {
    @SerializedName("practice_mgmt")
    @Expose
    private String practiceMgmt;
    @SerializedName("practice_id")
    @Expose
    private String practiceId;
    @SerializedName("welcome_screen")
    @Expose
    private WelcomeScreenDTO welcomeScreen = new WelcomeScreenDTO();

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

    public WelcomeScreenDTO getWelcomeScreen() {
        return welcomeScreen;
    }

    public void setWelcomeScreen(WelcomeScreenDTO welcomeScreen) {
        this.welcomeScreen = welcomeScreen;
    }

}
