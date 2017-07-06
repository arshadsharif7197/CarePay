package com.carecloud.carepay.patient.messages.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 6/30/17
 */

public class MessagingDataModel {

    @SerializedName("messages")
    private Messages messages = new Messages();

    @SerializedName("inbox")
    private Inbox inbox = new Inbox();

    public Messages getMessages() {
        return messages;
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }

    public Inbox getInbox() {
        return inbox;
    }

    public void setInbox(Inbox inbox) {
        this.inbox = inbox;
    }
}
