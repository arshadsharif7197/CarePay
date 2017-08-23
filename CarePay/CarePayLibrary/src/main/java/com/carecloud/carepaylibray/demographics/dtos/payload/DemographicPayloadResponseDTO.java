package com.carecloud.carepaylibray.demographics.dtos.payload;

import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.CheckinSettingsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPayloadDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jahirul Bhuiyan on 9/28/2016.
 * Model for payload of response
 */
public class DemographicPayloadResponseDTO extends DemographicsSettingsPayloadDTO{

    @SerializedName("qrcode")
    @Expose
    private String qrCode;
    @SerializedName("demographics")
    @Expose
    private DemographicPayloadInfoDTO demographics = new DemographicPayloadInfoDTO();
    @SerializedName("appointments")
    @Expose
    private List<AppointmentDTO> appointmentpayloaddto = new ArrayList<>();
    @SerializedName("checkin_settings")
    @Expose
    private CheckinSettingsDTO checkinSettings = new CheckinSettingsDTO();

    public DemographicPayloadInfoDTO getDemographics() {
        return demographics;
    }

    public void setDemographics(DemographicPayloadInfoDTO demographics) {
        this.demographics = demographics;
    }

    public List<AppointmentDTO> getAppointmentpayloaddto() {
        return appointmentpayloaddto;
    }

    public void setAppointmentpayloaddto(List<AppointmentDTO> appointmentpayloaddto) {
        this.appointmentpayloaddto = appointmentpayloaddto;
    }

    /**
     * @return The qrCode
     */
    public String getQrcode() {
        return qrCode;
    }

    /**
     * @param qrCode The qrCode
     */
    public void setQrcode(String qrCode) {
        this.qrCode = qrCode;
    }

    public CheckinSettingsDTO getCheckinSettings() {
        return checkinSettings;
    }

    public void setCheckinSettings(CheckinSettingsDTO checkinSettings) {
        this.checkinSettings = checkinSettings;
    }
}
