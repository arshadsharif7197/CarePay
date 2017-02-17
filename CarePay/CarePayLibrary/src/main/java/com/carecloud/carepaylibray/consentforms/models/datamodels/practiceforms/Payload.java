
package com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class Payload {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("form_category")
    @Expose
    private String formCategory;
    @SerializedName("all_providers")
    @Expose
    private Boolean allProviders;
    @SerializedName("all_locations")
    @Expose
    private Boolean allLocations;
    @SerializedName("all_visit_types")
    @Expose
    private Boolean allVisitTypes;
    @SerializedName("enabled")
    @Expose
    private Boolean enabled;
    @SerializedName("fields")
    @Expose
    private Fields fields;
    @SerializedName("providers")
    @Expose
    private List<Object> providers = new ArrayList<>();
    @SerializedName("locations")
    @Expose
    private List<Object> locations = new ArrayList<>();
    @SerializedName("visit_types")
    @Expose
    private List<Object> visitTypes = new ArrayList<>();
    @SerializedName("uuid")
    @Expose
    private String uuid;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormCategory() {
        return formCategory;
    }

    public void setFormCategory(String formCategory) {
        this.formCategory = formCategory;
    }

    public Boolean getAllProviders() {
        return allProviders;
    }

    public void setAllProviders(Boolean allProviders) {
        this.allProviders = allProviders;
    }

    public Boolean getAllLocations() {
        return allLocations;
    }

    public void setAllLocations(Boolean allLocations) {
        this.allLocations = allLocations;
    }

    public Boolean getAllVisitTypes() {
        return allVisitTypes;
    }

    public void setAllVisitTypes(Boolean allVisitTypes) {
        this.allVisitTypes = allVisitTypes;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Fields getFields() {
        return fields;
    }

    public void setFields(Fields fields) {
        this.fields = fields;
    }

    public List<Object> getProviders() {
        return providers;
    }

    public void setProviders(List<Object> providers) {
        this.providers = providers;
    }

    public List<Object> getLocations() {
        return locations;
    }

    public void setLocations(List<Object> locations) {
        this.locations = locations;
    }

    public List<Object> getVisitTypes() {
        return visitTypes;
    }

    public void setVisitTypes(List<Object> visitTypes) {
        this.visitTypes = visitTypes;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

}
