package com.carecloud.carepay.practice.library.practicesetting.models;

/**
 * Created by prem_mourya on 10/24/2016.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PracticeSettingLabelDTO {

    @SerializedName("practice_setting_pin_practice_mode")
    @Expose
    private String practiceSettingPinPracticeMode;
    @SerializedName("practice_setting_pin_enter_unlock_practice_mode")
    @Expose
    private String practiceSettingPinEnterUnlockPracticeMode;
    @SerializedName("practice_setting_pin_cancel")
    @Expose
    private String practiceSettingPinCancel;

    /**
     *
     * @return
     *     The practiceSettingPinPracticeMode
     */
    public String getPracticeSettingPinPracticeMode() {
        return practiceSettingPinPracticeMode;
    }

    /**
     *
     * @param practiceSettingPinPracticeMode
     *     The practice_setting_pin_practice_mode
     */
    public void setPracticeSettingPinPracticeMode(String practiceSettingPinPracticeMode) {
        this.practiceSettingPinPracticeMode = practiceSettingPinPracticeMode;
    }

    /**
     *
     * @return
     *     The practiceSettingPinEnterUnlockPracticeMode
     */
    public String getPracticeSettingPinEnterUnlockPracticeMode() {
        return practiceSettingPinEnterUnlockPracticeMode;
    }

    /**
     *
     * @param practiceSettingPinEnterUnlockPracticeMode
     *     The practice_setting_pin_enter_unlock_practice_mode
     */
    public void setPracticeSettingPinEnterUnlockPracticeMode(String practiceSettingPinEnterUnlockPracticeMode) {
        this.practiceSettingPinEnterUnlockPracticeMode = practiceSettingPinEnterUnlockPracticeMode;
    }

    /**
     *
     * @return
     *     The practiceSettingPinCancel
     */
    public String getPracticeSettingPinCancel() {
        return practiceSettingPinCancel;
    }

    /**
     *
     * @param practiceSettingPinCancel
     *     The practice_setting_pin_cancel
     */
    public void setPracticeSettingPinCancel(String practiceSettingPinCancel) {
        this.practiceSettingPinCancel = practiceSettingPinCancel;
    }

}

