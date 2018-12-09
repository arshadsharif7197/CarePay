package com.carecloud.carepay.patient.notifications.models;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CustomNotificationItem extends NotificationItem {
    public static final int TYPE_APP_UPDATED = 0x55;
    public static final int TYPE_UPDATE_REQUIRED = 0x66;

    @IntDef({TYPE_APP_UPDATED, TYPE_UPDATE_REQUIRED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CustomNotificationType{}

    private @CustomNotificationType int notificationType;

    public @CustomNotificationType int getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(@CustomNotificationType int notificationType) {
        this.notificationType = notificationType;
    }


}
