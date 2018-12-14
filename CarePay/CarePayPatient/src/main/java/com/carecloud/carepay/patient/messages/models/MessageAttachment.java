package com.carecloud.carepay.patient.messages.models;

import com.google.gson.annotations.SerializedName;

public class MessageAttachment {


    @SerializedName("document")
    private AttachmentDocument document = new AttachmentDocument();

    public AttachmentDocument getDocument() {
        return document;
    }

    public void setDocument(AttachmentDocument document) {
        this.document = document;
    }


    public static class AttachmentDocument {

        @SerializedName("alias")
        private String alias;

        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("created_by")
        private String createdBy;

        @SerializedName("description")
        private String description;

        @SerializedName("document_format")
        private String documentFormat;

        @SerializedName("document_handler")
        private String documentHandler;

        @SerializedName("document_set_id")
        private String documentSetId;

        @SerializedName("document_source_id")
        private String documentSourceId;

        @SerializedName("effective_date")
        private String effectiveDate;

        @SerializedName("id")
        private String id;

        @SerializedName("name")
        private String name;

        @SerializedName("page_count")
        private String pageCount;

        @SerializedName("status")
        private String status;

        @SerializedName("updated_at")
        private String updatedAt;

        @SerializedName("updated_by")
        private String updatedBy;

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDocumentFormat() {
            return documentFormat;
        }

        public void setDocumentFormat(String documentFormat) {
            this.documentFormat = documentFormat;
        }

        public String getDocumentHandler() {
            return documentHandler;
        }

        public void setDocumentHandler(String documentHandler) {
            this.documentHandler = documentHandler;
        }

        public String getDocumentSetId() {
            return documentSetId;
        }

        public void setDocumentSetId(String documentSetId) {
            this.documentSetId = documentSetId;
        }

        public String getDocumentSourceId() {
            return documentSourceId;
        }

        public void setDocumentSourceId(String documentSourceId) {
            this.documentSourceId = documentSourceId;
        }

        public String getEffectiveDate() {
            return effectiveDate;
        }

        public void setEffectiveDate(String effectiveDate) {
            this.effectiveDate = effectiveDate;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPageCount() {
            return pageCount;
        }

        public void setPageCount(String pageCount) {
            this.pageCount = pageCount;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getUpdatedBy() {
            return updatedBy;
        }

        public void setUpdatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
        }
    }

}
