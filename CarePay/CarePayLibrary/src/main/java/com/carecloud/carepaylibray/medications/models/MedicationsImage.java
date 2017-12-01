package com.carecloud.carepaylibray.medications.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 11/16/17
 */

public class MedicationsImage {

    @SerializedName("payload")
    private MedicationsImagePayload payload = new MedicationsImagePayload();

    public MedicationsImagePayload getPayload() {
        return payload;
    }

    public void setPayload(MedicationsImagePayload payload) {
        this.payload = payload;
    }


    public class MedicationsImagePayload {

        @SerializedName("url")
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

}
