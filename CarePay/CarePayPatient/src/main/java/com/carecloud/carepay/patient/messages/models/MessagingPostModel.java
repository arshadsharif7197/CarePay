package com.carecloud.carepay.patient.messages.models;

import com.google.gson.annotations.SerializedName;

public class MessagingPostModel {

    @SerializedName("recipient")
    private Messages.Participant participant = new Messages.Participant();

    @SerializedName("body")
    private String message;

    @SerializedName("subject")
    private String subject;

    public Messages.Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Messages.Participant participant) {
        this.participant = participant;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
