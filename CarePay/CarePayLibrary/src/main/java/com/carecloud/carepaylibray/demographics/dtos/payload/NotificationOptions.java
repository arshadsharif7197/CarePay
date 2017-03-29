package com.carecloud.carepaylibray.demographics.dtos.payload;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 3/28/17.
 */

public class NotificationOptions {

    @SerializedName("push")
    @Expose
    private boolean pushNotification = false;

    @SerializedName("email")
    @Expose
    private boolean emailNotification = false;


    public boolean hasPushNotification() {
        return pushNotification;
    }

    public void setPushNotification(boolean pushNotification) {
        this.pushNotification = pushNotification;
    }

    public boolean hasEmailNotification() {
        return emailNotification;
    }

    public void setEmailNotification(boolean emailNotification) {
        this.emailNotification = emailNotification;
    }
}
