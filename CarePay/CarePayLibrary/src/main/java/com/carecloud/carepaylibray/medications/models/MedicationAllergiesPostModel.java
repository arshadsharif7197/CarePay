package com.carecloud.carepaylibray.medications.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 2/15/17.
 */

public class MedicationAllergiesPostModel {

    @SerializedName("$schema")
    @Expose
    private String schema;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("items")
    @Expose
    private List<MedicationsAllergiesPostObject> items = new ArrayList<>();

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<MedicationsAllergiesPostObject> getItems() {
        return items;
    }

    public void setItems(List<MedicationsAllergiesPostObject> items) {
        this.items = items;
    }
}
