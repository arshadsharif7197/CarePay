package com.carecloud.carepaylibray.unifiedauth.TwoFAuth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Payload {

    @SerializedName("enabled")
    @Expose
    private Boolean enabled;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    @SerializedName("otp")
    @Expose
    private String otp;

    @SerializedName("settingsList")
    @Expose
    private List<SettingsList> settingsList = null;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<SettingsList> getSettingsList() {
        return settingsList;
    }

    public void setSettingsList(List<SettingsList> settingsList) {
        this.settingsList = settingsList;
    }

}