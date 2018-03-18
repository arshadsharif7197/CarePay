package com.carecloud.carepay.practice.library.patientmodecheckin.models;

import com.carecloud.carepaylibray.signinsignup.dto.OptionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 4/25/17.
 */

public class PatientModeCheckinPayloadDTO {

    @SerializedName("checkin_patient_mode")
    @Expose
    private CheckinModeDTO checkinModeDTO = new CheckinModeDTO();

    @SerializedName("languages")
    @Expose
    private List<OptionDTO> languages = new ArrayList<>();


    public CheckinModeDTO getCheckinModeDTO() {
        return checkinModeDTO;
    }

    public void setCheckinModeDTO(CheckinModeDTO checkinModeDTO) {
        this.checkinModeDTO = checkinModeDTO;
    }

    public List<OptionDTO> getLanguages() {
        return languages;
    }

    public void setLanguages(List<OptionDTO> languages) {
        this.languages = languages;
    }
}
