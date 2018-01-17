package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 7/04/17.
 */

public class DemographicNotificationPayloadDTO {

    @Expose
    @SerializedName("push")
    private boolean push;

    @Expose
    @SerializedName("email")
    private boolean email;

    public boolean isPush() {
        return push;
    }

    public void setPush(boolean push) {
        this.push = push;
    }

    public boolean isEmail() {
        return email;
    }

    public void setEmail(boolean email) {
        this.email = email;
    }
}
