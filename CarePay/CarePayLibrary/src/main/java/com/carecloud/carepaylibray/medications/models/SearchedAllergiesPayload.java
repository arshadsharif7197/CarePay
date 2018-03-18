package com.carecloud.carepaylibray.medications.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 2/16/17.
 */

public class SearchedAllergiesPayload {

    @SerializedName("payload")
    @Expose
    private List<AllergiesObject> allergies = new ArrayList<>();

    public List<AllergiesObject> getAllergies() {
        return allergies;
    }

    public void setAllergies(List<AllergiesObject> allergies) {
        this.allergies = allergies;
    }
}
