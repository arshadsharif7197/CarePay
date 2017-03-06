
package com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PracticeForm {

    @SerializedName("payload")
    @Expose
//    private Payload payload = new Payload();
//
//    public Payload getPayload() {
//        return payload;
//    }
//
//    public void setPayload(Payload payload) {
//        this.payload = payload;
//    }
    private JsonObject payload;

    public JsonObject getPayload() {
        return payload;
    }

    public void setPayload(JsonObject payload) {
        this.payload = payload;
    }
}
