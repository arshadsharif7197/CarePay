package com.carecloud.carepaylibray.demographics.dtos.payload;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jahirul Bhuiyan on 9/28/2016.
 * Model for payload of response
 */
public class DemographicPayloadResponseDTO {
    @SerializedName("demographics")
    @Expose
    private DemographicPayloadInfoDTO demographics;



    @SerializedName("appointments")
    @Expose
    private List<AppointmentPayloadDTO> appointmentpayloaddto= new ArrayList<>();

    public DemographicPayloadInfoDTO getDemographics() {
        return demographics;
    }

    public void setDemographics(DemographicPayloadInfoDTO demographics) {
        this.demographics = demographics;
    }
    public List<AppointmentPayloadDTO>getAppointmentpayloaddto() {
        return appointmentpayloaddto;
    }

    public void setAppointmentpayloaddto(List<AppointmentPayloadDTO> appointmentpayloaddto) {
        this.appointmentpayloaddto = appointmentpayloaddto;
    }


}
