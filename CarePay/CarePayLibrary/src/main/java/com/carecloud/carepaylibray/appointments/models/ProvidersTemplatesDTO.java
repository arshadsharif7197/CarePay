
package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProvidersTemplatesDTO {

    @SerializedName("appointment_template")
    @Expose
    private ProvidersTemplateDTO appointmentTemplate = new ProvidersTemplateDTO();

    /**
     * 
     * @return
     *     The appointmentTemplate
     */
    public ProvidersTemplateDTO getAppointmentTemplate() {
        return appointmentTemplate;
    }

    /**
     * 
     * @param appointmentTemplate
     *     The appointment_template
     */
    public void setAppointmentTemplate(ProvidersTemplateDTO appointmentTemplate) {
        this.appointmentTemplate = appointmentTemplate;
    }

}
