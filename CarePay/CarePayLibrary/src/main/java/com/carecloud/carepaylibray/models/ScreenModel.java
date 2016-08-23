package com.carecloud.carepaylibray.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Jahirul Bhuiyan on 8/23/2016.
 */
public class ScreenModel {

    @SerializedName("name")
    private String Name;
    @SerializedName("data")
    private ArrayList<ScreenComponentModel> componentModels;

    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }

    public ArrayList<ScreenComponentModel> getComponentModels() {
        return componentModels;
    }

    public void setComponentModels(ArrayList<ScreenComponentModel> componentModels) {
        this.componentModels = componentModels;
    }
}
