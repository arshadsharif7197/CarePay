package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.SerializedName;

public class VideoVisitModel {

    @SerializedName("payload")
    private VideoVisitPayload payload = new VideoVisitPayload();

    public VideoVisitPayload getPayload() {
        return payload;
    }

    public void setPayload(VideoVisitPayload payload) {
        this.payload = payload;
    }

    public class VideoVisitPayload {

        @SerializedName("visit_url")
        private String visitUrl;

        public String getVisitUrl() {
            return visitUrl;
        }

        public void setVisitUrl(String visitUrl) {
            this.visitUrl = visitUrl;
        }
    }
}
