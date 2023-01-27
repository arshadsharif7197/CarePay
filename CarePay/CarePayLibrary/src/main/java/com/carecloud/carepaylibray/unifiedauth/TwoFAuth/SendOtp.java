package com.carecloud.carepaylibray.unifiedauth.TwoFAuth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendOtp {

    @SerializedName("otp_type")
    @Expose
    private String otpType;
    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("phone_number")
    @Expose
    private String phone_number;

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getOtpType() {
        return otpType;
    }

    public void setOtpType(String otpType) {
        this.otpType = otpType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}