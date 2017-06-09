package com.carecloud.carepaylibray.medications.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/15/17
 */

public class MedicationAllergiesMetadata {

    @SerializedName("links")
    @Expose
    private MedicationAllergiesLinksDTO links = new MedicationAllergiesLinksDTO();

    @SerializedName("transitions")
    @Expose
    private MedicationAllergiesTransitionsDTO transitions = new MedicationAllergiesTransitionsDTO();

    public MedicationAllergiesLinksDTO getLinks() {
        return links;
    }

    public void setLinks(MedicationAllergiesLinksDTO links) {
        this.links = links;
    }

    public MedicationAllergiesTransitionsDTO getTransitions() {
        return transitions;
    }

    public void setTransitions(MedicationAllergiesTransitionsDTO transitions) {
        this.transitions = transitions;
    }
}
