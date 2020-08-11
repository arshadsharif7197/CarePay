package com.carecloud.carepaylibray.fcm;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationResponse {
    @SerializedName("metadata")
    @Expose
    private Metadata metadata;
    @SerializedName("payload")
    @Expose
    private Payload payload;

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    public class Metadata {
        @SerializedName("event_source_guid")
        @Expose
        private String eventSourceGuid;
        @SerializedName("generated_at")
        @Expose
        private String generatedAt;
        @SerializedName("business_entity_guid")
        @Expose
        private String businessEntityGuid;
        @SerializedName("event_guid")
        @Expose
        private String eventGuid;
        @SerializedName("practice_id")
        @Expose
        private String practiceId;
        @SerializedName("event_name")
        @Expose
        private String eventName;
        @SerializedName("message_type")
        @Expose
        private String messageType;
        @SerializedName("notification_id")
        @Expose
        private String notificationId;
        @SerializedName("business_entity_id")
        @Expose
        private Integer businessEntityId;
        @SerializedName("published_at")
        @Expose
        private String publishedAt;
        @SerializedName("deliveredAt")
        @Expose
        private String deliveredAt;
        @SerializedName("practice_mgmt")
        @Expose
        private String practiceMgmt;

        public String getEventSourceGuid() {
            return eventSourceGuid;
        }

        public void setEventSourceGuid(String eventSourceGuid) {
            this.eventSourceGuid = eventSourceGuid;
        }

        public String getGeneratedAt() {
            return generatedAt;
        }

        public void setGeneratedAt(String generatedAt) {
            this.generatedAt = generatedAt;
        }

        public String getBusinessEntityGuid() {
            return businessEntityGuid;
        }

        public void setBusinessEntityGuid(String businessEntityGuid) {
            this.businessEntityGuid = businessEntityGuid;
        }

        public String getEventGuid() {
            return eventGuid;
        }

        public void setEventGuid(String eventGuid) {
            this.eventGuid = eventGuid;
        }

        public String getPracticeId() {
            return practiceId;
        }

        public void setPracticeId(String practiceId) {
            this.practiceId = practiceId;
        }

        public String getEventName() {
            return eventName;
        }

        public void setEventName(String eventName) {
            this.eventName = eventName;
        }

        public String getMessageType() {
            return messageType;
        }

        public void setMessageType(String messageType) {
            this.messageType = messageType;
        }

        public String getNotificationId() {
            return notificationId;
        }

        public void setNotificationId(String notificationId) {
            this.notificationId = notificationId;
        }

        public Integer getBusinessEntityId() {
            return businessEntityId;
        }

        public void setBusinessEntityId(Integer businessEntityId) {
            this.businessEntityId = businessEntityId;
        }

        public String getPublishedAt() {
            return publishedAt;
        }

        public void setPublishedAt(String publishedAt) {
            this.publishedAt = publishedAt;
        }

        public String getDeliveredAt() {
            return deliveredAt;
        }

        public void setDeliveredAt(String deliveredAt) {
            this.deliveredAt = deliveredAt;
        }

        public String getPracticeMgmt() {
            return practiceMgmt;
        }

        public void setPracticeMgmt(String practiceMgmt) {
            this.practiceMgmt = practiceMgmt;
        }

    }

    public class Payload {
        @SerializedName("event_source_guid")
        @Expose
        private String eventSourceGuid;
        @SerializedName("sender")
        @Expose
        private String sender;
        @SerializedName("generated_at")
        @Expose
        private String generatedAt;
        @SerializedName("event_name")
        @Expose
        private String eventName;
        @SerializedName("patient_guid")
        @Expose
        private String patientGuid;
        @SerializedName("message_id")
        @Expose
        private String messageId;
        @SerializedName("sender_name")
        @Expose
        private String senderName;

        public String getEventSourceGuid() {
            return eventSourceGuid;
        }

        public void setEventSourceGuid(String eventSourceGuid) {
            this.eventSourceGuid = eventSourceGuid;
        }

        public String getSender() {
            return sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public String getGeneratedAt() {
            return generatedAt;
        }

        public void setGeneratedAt(String generatedAt) {
            this.generatedAt = generatedAt;
        }

        public String getEventName() {
            return eventName;
        }

        public void setEventName(String eventName) {
            this.eventName = eventName;
        }

        public String getPatientGuid() {
            return patientGuid;
        }

        public void setPatientGuid(String patientGuid) {
            this.patientGuid = patientGuid;
        }

        public String getMessageId() {
            return messageId;
        }

        public void setMessageId(String messageId) {
            this.messageId = messageId;
        }

        public String getSenderName() {
            return senderName;
        }

        public void setSenderName(String senderName) {
            this.senderName = senderName;
        }

    }
}
