package com.carecloud.carepay.mini.models.metadata;

import com.google.gson.annotations.SerializedName;

public class DeviceMetadata {

    @SerializedName("practice_id")
    private String practiceID;

    @SerializedName("practice_name")
    private String practiceName;

    @SerializedName("location_id")
    private String locationID;

    public String getPracticeID() {
        return practiceID;
    }

    public void setPracticeID(String practiceID) {
        this.practiceID = practiceID;
    }

    public String getPracticeName() {
        return practiceName;
    }

    public void setPracticeName(String practiceName) {
        this.practiceName = practiceName;
    }

    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }
}
