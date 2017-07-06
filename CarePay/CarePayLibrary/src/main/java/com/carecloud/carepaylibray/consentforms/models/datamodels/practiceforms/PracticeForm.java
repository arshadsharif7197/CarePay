
package com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PracticeForm implements Serializable{

    @SerializedName("payload")
    @Expose
    private JsonObject payload;

    private transient String lastModifiedDate;

    public JsonObject getPayload() {
        return payload;
    }

    public void setPayload(JsonObject payload) {
        this.payload = payload;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
