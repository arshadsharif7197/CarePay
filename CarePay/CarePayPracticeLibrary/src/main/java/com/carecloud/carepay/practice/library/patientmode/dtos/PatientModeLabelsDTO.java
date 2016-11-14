package com.carecloud.carepay.practice.library.patientmode.dtos;

/**
 * Created by Rahul on 10/27/16.
 */


import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientModeLabelsDTO {

    @SerializedName("welcome_heading")
    @Expose
    private String welcomeHeading;
    @SerializedName("get_started_heading")
    @Expose
    private String getStartedHeading;
    @SerializedName("practice_mode_switch_pin_header")
    @Expose
    private String practiceModeSwitchPinHeader;
    @SerializedName("practice_mode_switch_pin_enter_unlock")
    @Expose
    private String practiceModeSwitchPinEnterUnlock;
    @SerializedName("practice_mode_switch_pin_cancel")
    @Expose
    private String practiceModeSwitchPinCancel;
    @SerializedName("practice_mode_switch_pin_zero")
    @Expose
    private String practiceModeSwitchPinZero;
    @SerializedName("practice_mode_switch_pin_one")
    @Expose
    private String practiceModeSwitchPinOne;
    @SerializedName("practice_mode_switch_pin_two")
    @Expose
    private String practiceModeSwitchPinTwo;
    @SerializedName("practice_mode_switch_pin_three")
    @Expose
    private String practiceModeSwitchPinThree;
    @SerializedName("practice_mode_switch_pin_four")
    @Expose
    private String practiceModeSwitchPinFour;
    @SerializedName("practice_mode_switch_pin_five")
    @Expose
    private String practiceModeSwitchPinFive;
    @SerializedName("practice_mode_switch_pin_six")
    @Expose
    private String practiceModeSwitchPinSix;
    @SerializedName("practice_mode_switch_pin_seven")
    @Expose
    private String practiceModeSwitchPinSeven;
    @SerializedName("practice_mode_switch_pin_eight")
    @Expose
    private String practiceModeSwitchPinEight;
    @SerializedName("practice_mode_switch_pin_nine")
    @Expose
    private String practiceModeSwitchPinNine;

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

    public String getPracticeModeSwitchPinHeader() {
        return StringUtil.getLabelForView(practiceModeSwitchPinHeader) ;
    }

    public void setPracticeModeSwitchPinHeader(String practiceModeSwitchPinHeader) {
        this.practiceModeSwitchPinHeader = practiceModeSwitchPinHeader;
    }

    public String getPracticeModeSwitchPinEnterUnlock() {
        return StringUtil.getLabelForView(practiceModeSwitchPinEnterUnlock) ;
    }

    public void setPracticeModeSwitchPinEnterUnlock(String practiceModeSwitchPinEnterUnlock) {
        this.practiceModeSwitchPinEnterUnlock = practiceModeSwitchPinEnterUnlock;
    }

    public String getPracticeModeSwitchPinCancel() {
        return StringUtil.getLabelForView(practiceModeSwitchPinCancel) ;
    }

    public void setPracticeModeSwitchPinCancel(String practiceModeSwitchPinCancel) {
        this.practiceModeSwitchPinCancel = practiceModeSwitchPinCancel;
    }

    public String getPracticeModeSwitchPinZero() {
        return StringUtil.getLabelForView(practiceModeSwitchPinZero) ;
    }

    public void setPracticeModeSwitchPinZero(String practiceModeSwitchPinZero) {
        this.practiceModeSwitchPinZero = practiceModeSwitchPinZero;
    }

    public String getPracticeModeSwitchPinOne() {
        return StringUtil.getLabelForView(practiceModeSwitchPinOne) ;
    }

    public void setPracticeModeSwitchPinOne(String practiceModeSwitchPinOne) {
        this.practiceModeSwitchPinOne = practiceModeSwitchPinOne;
    }

    public String getPracticeModeSwitchPinTwo() {
        return StringUtil.getLabelForView(practiceModeSwitchPinTwo) ;
    }

    public void setPracticeModeSwitchPinTwo(String practiceModeSwitchPinTwo) {
        this.practiceModeSwitchPinTwo = practiceModeSwitchPinTwo;
    }

    public String getPracticeModeSwitchPinThree() {
        return StringUtil.getLabelForView(practiceModeSwitchPinThree) ;
    }

    public void setPracticeModeSwitchPinThree(String practiceModeSwitchPinThree) {
        this.practiceModeSwitchPinThree = practiceModeSwitchPinThree;
    }

    public String getPracticeModeSwitchPinFour() {
        return StringUtil.getLabelForView(practiceModeSwitchPinFour) ;
    }

    public void setPracticeModeSwitchPinFour(String practiceModeSwitchPinFour) {
        this.practiceModeSwitchPinFour = practiceModeSwitchPinFour;
    }

    public String getPracticeModeSwitchPinFive() {
        return StringUtil.getLabelForView(practiceModeSwitchPinFive) ;
    }

    public void setPracticeModeSwitchPinFive(String practiceModeSwitchPinFive) {
        this.practiceModeSwitchPinFive = practiceModeSwitchPinFive;
    }

    public String getPracticeModeSwitchPinSix() {
        return StringUtil.getLabelForView(practiceModeSwitchPinSix) ;
    }

    public void setPracticeModeSwitchPinSix(String practiceModeSwitchPinSix) {
        this.practiceModeSwitchPinSix = practiceModeSwitchPinSix;
    }

    public String getPracticeModeSwitchPinSeven() {
        return StringUtil.getLabelForView(practiceModeSwitchPinSeven) ;
    }

    public void setPracticeModeSwitchPinSeven(String practiceModeSwitchPinSeven) {
        this.practiceModeSwitchPinSeven = practiceModeSwitchPinSeven;
    }

    public String getPracticeModeSwitchPinEight() {
        return StringUtil.getLabelForView(practiceModeSwitchPinEight) ;
    }

    public void setPracticeModeSwitchPinEight(String practiceModeSwitchPinEight) {
        this.practiceModeSwitchPinEight = practiceModeSwitchPinEight;
    }

    public String getPracticeModeSwitchPinNine() {
        return StringUtil.getLabelForView(practiceModeSwitchPinNine) ;
    }

    public void setPracticeModeSwitchPinNine(String practiceModeSwitchPinNine) {
        this.practiceModeSwitchPinNine = practiceModeSwitchPinNine;
    }
}
