package com.carecloud.carepaylibray.unifiedauth.TwoFAuth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TwoFactorAuth {

@SerializedName("settings")
@Expose
private Settings settings;
@SerializedName("verified")
@Expose
private Boolean verified;
@SerializedName("otp_sent")
@Expose
private Boolean otpSent;
@SerializedName("enable_2fa_popup")
@Expose
private Boolean enable2faPopup;

public Settings getSettings() {
return settings;
}

public void setSettings(Settings settings) {
this.settings = settings;
}

public Boolean getVerified() {
return verified;
}

public void setVerified(Boolean verified) {
this.verified = verified;
}

public Boolean getOtpSent() {
return otpSent;
}

public void setOtpSent(Boolean otpSent) {
this.otpSent = otpSent;
}

public Boolean getEnable2faPopup() {
return enable2faPopup;
}

public void setEnable2faPopup(Boolean enable2faPopup) {
this.enable2faPopup = enable2faPopup;
}

}