package com.carecloud.carepaylibray.medications.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/15/17.
 */

public abstract class MedicationsAllergiesObject implements Comparable<MedicationsAllergiesObject> {

    @SerializedName("action")
    @Expose
    private MedicationAllergiesAction action;

    @SerializedName("displayName")
    @Expose
    protected String displayName;

    @SerializedName("uuid")
    @Expose
    private String uuid;

    public MedicationAllergiesAction getAction() {
        return action;
    }

    public void setAction(MedicationAllergiesAction action) {
        this.action = action;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public int compareTo(MedicationsAllergiesObject object){
        return this.displayName.compareTo(object.displayName);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
