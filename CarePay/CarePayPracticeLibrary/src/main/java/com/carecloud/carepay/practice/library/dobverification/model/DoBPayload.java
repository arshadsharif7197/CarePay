package com.carecloud.carepay.practice.library.dobverification.model;

import com.carecloud.carepay.practice.library.patientmodecheckin.models.CheckinModeDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicsDTO;
import com.carecloud.carepaylibray.signinsignup.dto.OptionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pjohnson on 4/4/19.
 */
public class DoBPayload {

    @SerializedName("demographics")
    @Expose
    private DemographicsSettingsDemographicsDTO demographicDTO = new DemographicsSettingsDemographicsDTO();
    @SerializedName("languages")
    @Expose
    private List<OptionDTO> languages = new ArrayList<>();
    @SerializedName("appointments")
    @Expose
    private List<AppointmentDTO> appointments = new ArrayList<>();
    @SerializedName("checkin_patient_mode")
    @Expose
    private CheckinModeDTO checkinModeDTO = new CheckinModeDTO();

    public DemographicsSettingsDemographicsDTO getDemographicDTO() {
        return demographicDTO;
    }

    public void setDemographicDTO(DemographicsSettingsDemographicsDTO demographicDTO) {
        this.demographicDTO = demographicDTO;
    }

    public List<OptionDTO> getLanguages() {
        return languages;
    }

    public void setLanguages(List<OptionDTO> languages) {
        this.languages = languages;
    }

    public List<AppointmentDTO> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<AppointmentDTO> appointments) {
        this.appointments = appointments;
    }

    public CheckinModeDTO getCheckinModeDTO() {
        return checkinModeDTO;
    }

    public void setCheckinModeDTO(CheckinModeDTO checkinModeDTO) {
        this.checkinModeDTO = checkinModeDTO;
    }
}
