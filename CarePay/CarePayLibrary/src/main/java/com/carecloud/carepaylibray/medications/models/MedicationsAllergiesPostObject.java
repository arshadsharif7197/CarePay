package com.carecloud.carepaylibray.medications.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/15/17.
 */

public class MedicationsAllergiesPostObject {

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("properties")
    @Expose
    private MedicationsAllergiesObject properties;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MedicationsAllergiesObject getProperties() {
        return properties;
    }

    public void setProperties(MedicationsAllergiesObject properties) {
        this.properties = properties;
    }
}
