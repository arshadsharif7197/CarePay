package com.carecloud.carepay.patient.myhealth.dtos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 9/27/17
 */

public class BusinessEntity {

    @SerializedName("guid")
    private String guid;

    @SerializedName("id")
    private String id;

    @SerializedName("name_alias")
    private String practiceName;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPracticeName() {
        return practiceName;
    }

    public void setPracticeName(String practiceName) {
        this.practiceName = practiceName;
    }
}
