package com.carecloud.carepay.patient.appointments.models;

import com.carecloud.carepaylibray.appointments.models.AppointmentsSettingDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pjohnson on 2019-12-10.
 */
public class PracticeInformationMiniPayload {

    @Expose
    @SerializedName("practice_information")
    List<AppointmentsSettingDTO> appointmentsSettingDTOList = new ArrayList<>();

    public List<AppointmentsSettingDTO> getAppointmentsSettingDTOList() {
        return appointmentsSettingDTOList;
    }

    public void setAppointmentsSettingDTOList(List<AppointmentsSettingDTO> appointmentsSettingDTOList) {
        this.appointmentsSettingDTOList = appointmentsSettingDTOList;
    }
}
