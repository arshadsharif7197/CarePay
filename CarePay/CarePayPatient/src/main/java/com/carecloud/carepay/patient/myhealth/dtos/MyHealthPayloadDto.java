package com.carecloud.carepay.patient.myhealth.dtos;

import com.carecloud.carepay.patient.patientsplash.dtos.OptionsDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pjohnson on 17/07/17.
 */
public class MyHealthPayloadDto {

    @SerializedName("languages")
    @Expose
    private List<OptionsDTO> languages = new ArrayList<>();
    @SerializedName("myhealth")
    @Expose
    private MyHealthDataDto myHealthData = new MyHealthDataDto();

    public List<OptionsDTO> getLanguages() {
        return languages;
    }

    public void setLanguages(List<OptionsDTO> languages) {
        this.languages = languages;
    }

    public MyHealthDataDto getMyHealthData() {
        return myHealthData;
    }

    public void setMyHealthData(MyHealthDataDto myHealthData) {
        this.myHealthData = myHealthData;
    }
}
