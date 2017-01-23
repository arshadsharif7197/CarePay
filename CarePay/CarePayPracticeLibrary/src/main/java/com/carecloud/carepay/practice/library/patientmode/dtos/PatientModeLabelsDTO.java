package com.carecloud.carepay.practice.library.patientmode.dtos;

/**
 * Created by Rahul on 10/27/16.
 */


import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientModeLabelsDTO  extends PatientModeSwitchPinDTO{

    @SerializedName("welcome_heading")
    @Expose
    private String welcomeHeading;
    @SerializedName("get_started_heading")
    @Expose
    private String getStartedHeading;


    /**
     * @return The welcomeHeading
     */
    public String getWelcomeHeading() {
        return StringUtil.isNullOrEmpty(welcomeHeading) ?
                CarePayConstants.NOT_DEFINED : welcomeHeading;
    }

    /**
     * @param welcomeHeading The welcome_heading
     */
    public void setWelcomeHeading(String welcomeHeading) {
        this.welcomeHeading = welcomeHeading;
    }

    /**
     * @return The getStartedHeading
     */
    public String getGetStartedHeading() {
        return StringUtil.isNullOrEmpty(getStartedHeading) ?
                CarePayConstants.NOT_DEFINED : getStartedHeading;
    }

    /**
     * @param getStartedHeading The get_started_heading
     */
    public void setGetStartedHeading(String getStartedHeading) {
        this.getStartedHeading = getStartedHeading;
    }


}
