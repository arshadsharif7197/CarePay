package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;

/**
 * @author pjohnson on 7/04/17.
 */

public class DemographicNotificationPayloadDTO {

    @Expose
    private boolean push;

    @Expose
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
