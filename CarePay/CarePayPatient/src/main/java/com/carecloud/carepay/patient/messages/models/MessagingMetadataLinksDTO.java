package com.carecloud.carepay.patient.messages.models;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.SerializedName;

public class MessagingMetadataLinksDTO {

    @SerializedName("self")
    private TransitionDTO self = new TransitionDTO();

    @SerializedName("inbox")
    private TransitionDTO inbox = new TransitionDTO();

    @SerializedName("message")
    private TransitionDTO message = new TransitionDTO();

    @SerializedName("new_message")
    private TransitionDTO newMessage = new TransitionDTO();

    @SerializedName("reply")
    private TransitionDTO reply = new TransitionDTO();

    @SerializedName("mark_read")
    private TransitionDTO markRead = new TransitionDTO();

    @SerializedName("mark_unread")
    private TransitionDTO markUnread = new TransitionDTO();

    @SerializedName("delete_message")
    private TransitionDTO deleteMessage = new TransitionDTO();

    @SerializedName("upload_attachment")
    private TransitionDTO uploadAttachment;

    @SerializedName("fetch_attachment")
    private TransitionDTO fetchAttachment;

    public TransitionDTO getSelf() {
        return self;
    }

    public void setSelf(TransitionDTO self) {
        this.self = self;
    }

    public TransitionDTO getInbox() {
        return inbox;
    }

    public void setInbox(TransitionDTO inbox) {
        this.inbox = inbox;
    }

    public TransitionDTO getMessage() {
        return message;
    }

    public void setMessage(TransitionDTO message) {
        this.message = message;
    }

    public TransitionDTO getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(TransitionDTO newMessage) {
        this.newMessage = newMessage;
    }

    public TransitionDTO getReply() {
        return reply;
    }

    public void setReply(TransitionDTO reply) {
        this.reply = reply;
    }

    public TransitionDTO getMarkRead() {
        return markRead;
    }

    public void setMarkRead(TransitionDTO markRead) {
        this.markRead = markRead;
    }

    public TransitionDTO getMarkUnread() {
        return markUnread;
    }

    public void setMarkUnread(TransitionDTO markUnread) {
        this.markUnread = markUnread;
    }

    public TransitionDTO getDeleteMessage() {
        return deleteMessage;
    }

    public void setDeleteMessage(TransitionDTO deleteMessage) {
        this.deleteMessage = deleteMessage;
    }

    public TransitionDTO getUploadAttachment() {
        return uploadAttachment;
    }

    public void setUploadAttachment(TransitionDTO uploadAttachment) {
        this.uploadAttachment = uploadAttachment;
    }

    public TransitionDTO getFetchAttachment() {
        return fetchAttachment;
    }

    public void setFetchAttachment(TransitionDTO fetchAttachment) {
        this.fetchAttachment = fetchAttachment;
    }
}
