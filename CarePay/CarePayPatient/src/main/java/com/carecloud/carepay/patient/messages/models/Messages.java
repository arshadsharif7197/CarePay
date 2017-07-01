package com.carecloud.carepay.patient.messages.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 6/30/17
 */

public class Messages {

    @SerializedName("data")
    private Reply data;

    @SerializedName("paging")
    private Paging paging;

    public Reply getData() {
        return data;
    }

    public void setData(Reply data) {
        this.data = data;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }


    private class Reply {

        @SerializedName("id")
        private String id;

        @SerializedName("author")
        private Participant author = new Participant();

        @SerializedName("read")
        private boolean read;

        @SerializedName("updated_at")
        private String lastUpdate;

        @SerializedName("subject")
        private String subject;

        @SerializedName("participants")
        private List<Participant> participants = new ArrayList<>();

        @SerializedName("body")
        private String body;

        @SerializedName("replies")
        private List<Reply> replies = new ArrayList<>();

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Participant getAuthor() {
            return author;
        }

        public void setAuthor(Participant author) {
            this.author = author;
        }

        public boolean isRead() {
            return read;
        }

        public void setRead(boolean read) {
            this.read = read;
        }

        public String getLastUpdate() {
            return lastUpdate;
        }

        public void setLastUpdate(String lastUpdate) {
            this.lastUpdate = lastUpdate;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public List<Participant> getParticipants() {
            return participants;
        }

        public void setParticipants(List<Participant> participants) {
            this.participants = participants;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public List<Reply> getReplies() {
            return replies;
        }

        public void setReplies(List<Reply> replies) {
            this.replies = replies;
        }
    }

    private class Participant {

        @SerializedName("name")
        private String name;

        @SerializedName("user_id")
        private String userId;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}
