package com.carecloud.carepay.patient.notifications.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 5/11/17.
 */

public class NotificationsPayload {

    @SerializedName("notifications")
    @Expose
    private List<NotificationItem> notifications = new ArrayList<>();

    public List<NotificationItem> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificationItem> notifications) {
        this.notifications = notifications;
    }
}
