package com.carecloud.carepaylibray.medications.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 2/15/17.
 */

public class MedicationsAllergiesPostObject {

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("properties")
    @Expose
    private List<? extends MedicationsAllergiesObject> items = new ArrayList<>();


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<? extends MedicationsAllergiesObject> getItems() {
        return items;
    }

    public void setItems(List<? extends MedicationsAllergiesObject> items) {
        this.items = items;
    }
}
