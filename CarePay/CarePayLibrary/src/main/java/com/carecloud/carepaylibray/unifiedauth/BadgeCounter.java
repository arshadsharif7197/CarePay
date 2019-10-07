package com.carecloud.carepaylibray.unifiedauth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 13/07/18.
 */
public class BadgeCounter {

    @Expose
    @SerializedName("pending_forms")
    private int pendingForms;
    @Expose
    @SerializedName("messages")
    private int messages;
    @Expose
    @SerializedName("notifications")
    private int notifications;

    public int getPendingForms() {
        return pendingForms;
    }

    public void setPendingForms(int pendingForms) {
        this.pendingForms = pendingForms;
    }

    public int getMessages() {
        return messages;
    }

    public void setMessages(int messages) {
        this.messages = messages;
    }

    public int getNotifications() {
        return notifications;
    }

    public void setNotifications(int notifications) {
        this.notifications = notifications;
    }
}
