package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.SerializedName;

public class VideoVisitModel {
    @SerializedName("urls")
    private Urls urls;

    public Urls getUrls() {
        return urls;
    }

    public void setUrls(Urls urls) {
        this.urls = urls;
    }

    public class Urls {
        private String web;
        private String ios;
        private String android;
        private String apple_store;
        private String google_play;

        public String getWeb() {
            return web;
        }

        public String getIos() {
            return ios;
        }

        public String getAndroid() {
            return android;
        }

        public String getApple_store() {
            return apple_store;
        }

        public String getGoogle_play() {
            return google_play;
        }

        public void setWeb(String web) {
            this.web = web;
        }

        public void setIos(String ios) {
            this.ios = ios;
        }

        public void setAndroid(String android) {
            this.android = android;
        }

        public void setApple_store(String apple_store) {
            this.apple_store = apple_store;
        }

        public void setGoogle_play(String google_play) {
            this.google_play = google_play;
        }
    }
}
