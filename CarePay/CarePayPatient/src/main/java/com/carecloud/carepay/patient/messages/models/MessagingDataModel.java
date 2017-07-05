package com.carecloud.carepay.patient.messages.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 6/30/17
 */

public class MessagingDataModel {

    @SerializedName("messages")
    private Messages messages;

    public Messages getMessages() {
        return messages;
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }
}
