package com.carecloud.carepaylibray.appointments.models;

import com.carecloud.carepaylibray.consentforms.models.ConsentFormDataModelDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Model for appointment metadata.
 */
public class AppointmentMetadataModel implements Serializable {

    @SerializedName("labels")
    @Expose
    private AppointmentLabelDTO label = new AppointmentLabelDTO();
    @SerializedName("links")
    @Expose
    private LinksDTO links = new LinksDTO();
    @SerializedName("transitions")
    @Expose
    private TransitionsDTO transitions = new TransitionsDTO();
    @SerializedName("data_models")
    @Expose
    private ConsentFormDataModelDTO dataModels = new ConsentFormDataModelDTO();

    /**
     * @return The label
     */
    public AppointmentLabelDTO getLabel() {
        return label;
    }

    /**
     * @param label The label
     */
    public void setLabel(AppointmentLabelDTO label) {
        this.label = label;
    }

    /**
     * @return The links
     */
    public LinksDTO getLinks() {
        return links;
    }

    /**
     * @param links The links
     */
    public void setLinks(LinksDTO links) {
        this.links = links;
    }

    /**
     * @return The transitions
     */
    public TransitionsDTO getTransitions() {
        return transitions;
    }

    /**
     * @param transitions The transitions
     */
    public void setTransitions(TransitionsDTO transitions) {
        this.transitions = transitions;
    }

    public ConsentFormDataModelDTO getDataModels() {
        return dataModels;
    }

    public void setDataModels(ConsentFormDataModelDTO dataModels) {
        this.dataModels = dataModels;
    }
}
