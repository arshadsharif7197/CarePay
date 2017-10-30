package com.carecloud.carepay.patient.purchases.models.sso;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 10/30/17
 */

public class Person {

    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
