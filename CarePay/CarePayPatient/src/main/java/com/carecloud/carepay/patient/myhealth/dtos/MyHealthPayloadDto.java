package com.carecloud.carepay.patient.myhealth.dtos;

import com.carecloud.carepay.patient.patientsplash.dtos.OptionsDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.appointments.models.PracticePatientIdsDTO;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
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
    @SerializedName("demographics")
    @Expose
    private DemographicPayloadInfoDTO demographicDTO = new DemographicPayloadInfoDTO();
    @SerializedName("education_material")
    @Expose
    private EducationMaterial educationMaterial = new EducationMaterial();
    @SerializedName("practice_patient_ids")
    @Expose
    private List<PracticePatientIdsDTO> practicePatientIds = new ArrayList<>();
    @SerializedName("practice_information")
    @Expose
    private List<UserPracticeDTO> practiceInformation = new ArrayList<>();

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

    public DemographicPayloadInfoDTO getDemographicDTO() {
        return demographicDTO;
    }

    public void setDemographicDTO(DemographicPayloadInfoDTO demographicDTO) {
        this.demographicDTO = demographicDTO;
    }

    public List<PracticePatientIdsDTO> getPracticePatientIds() {
        return practicePatientIds;
    }

    public void setPracticePatientIds(List<PracticePatientIdsDTO> practicePatientIds) {
        this.practicePatientIds = practicePatientIds;
    }

    public EducationMaterial getEducationMaterial() {
        return educationMaterial;
    }

    public void setEducationMaterial(EducationMaterial educationMaterial) {
        this.educationMaterial = educationMaterial;
    }

    public List<UserPracticeDTO> getPracticeInformation() {
        return practiceInformation;
    }

    public void setPracticeInformation(List<UserPracticeDTO> practiceInformation) {
        this.practiceInformation = practiceInformation;
    }
}
