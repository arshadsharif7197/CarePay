
package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Model for appointment metadata.
 */
public class AppointmentMetadataModel implements Serializable {

    @SerializedName("label")
    @Expose
    private AppointmentLabelDTO label;

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
}
