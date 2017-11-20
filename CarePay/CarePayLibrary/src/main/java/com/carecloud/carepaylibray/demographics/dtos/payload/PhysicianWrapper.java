package com.carecloud.carepaylibray.demographics.dtos.payload;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pjohnson on 14/11/17.
 */

public class PhysicianWrapper {

    @Expose
    @SerializedName("physicians")
    private List<PhysicianDto> physicians = new ArrayList<>();

    public List<PhysicianDto> getPhysicians() {
        return physicians;
    }

    public void setPhysicians(List<PhysicianDto> physicians) {
        this.physicians = physicians;
    }
}
