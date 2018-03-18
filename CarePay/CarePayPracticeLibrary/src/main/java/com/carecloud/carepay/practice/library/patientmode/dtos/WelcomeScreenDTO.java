package com.carecloud.carepay.practice.library.patientmode.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 3/6/17.
 */

public class WelcomeScreenDTO {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("welcome_photo")
    @Expose
    private String welcomePhoto;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getWelcomePhoto() {
        return welcomePhoto;
    }

    public void setWelcomePhoto(String welcomePhoto) {
        this.welcomePhoto = welcomePhoto;
    }

}
