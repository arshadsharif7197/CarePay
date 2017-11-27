package com.carecloud.carepaylibray.medications.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/15/17.
 */

public class MedicationsObject extends MedicationsAllergiesObject {

    @SerializedName("dispensableDrugId")
    @Expose
    private String dispensableDrugId;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("genericName")
    @Expose
    private String genericName;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("routeDescription")
    @Expose
    private String routeDescription;

    @SerializedName("routeId")
    @Expose
    private String routeId;

    @SerializedName("tradeName")
    @Expose
    private String tradeName;

    public String getDispensableDrugId() {
        return dispensableDrugId;
    }

    public void setDispensableDrugId(String dispensableDrugId) {
        this.dispensableDrugId = dispensableDrugId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenericName() {
        return genericName;
    }

    public void setGenericName(String genericName) {
        this.genericName = genericName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRouteDescription() {
        return routeDescription;
    }

    public void setRouteDescription(String routeDescription) {
        this.routeDescription = routeDescription;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }
}
