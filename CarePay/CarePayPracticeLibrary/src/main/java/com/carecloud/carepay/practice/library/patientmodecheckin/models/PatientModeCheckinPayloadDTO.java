package com.carecloud.carepay.practice.library.patientmodecheckin.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 4/25/17.
 */

public class PatientModeCheckinPayloadDTO {

    @SerializedName("checkin_patient_mode")
    @Expose
    private CheckinModeDTO checkinModeDTO = new CheckinModeDTO();


    public CheckinModeDTO getCheckinModeDTO() {
        return checkinModeDTO;
    }

    public void setCheckinModeDTO(CheckinModeDTO checkinModeDTO) {
        this.checkinModeDTO = checkinModeDTO;
    }
}
