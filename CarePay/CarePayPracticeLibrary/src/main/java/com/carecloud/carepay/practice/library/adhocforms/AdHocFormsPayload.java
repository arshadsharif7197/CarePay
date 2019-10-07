package com.carecloud.carepay.practice.library.adhocforms;

import com.carecloud.carepay.practice.library.signin.dtos.PracticeSelectionUserPractice;
import com.carecloud.carepaylibray.adhoc.AdhocFormsPatientModeInfo;
import com.carecloud.carepaylibray.demographics.dtos.payload.ConsentFormUserResponseDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicsDTO;
import com.carecloud.carepaylibray.signinsignup.dto.OptionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 12/15/17
 */

public class AdHocFormsPayload {

    @SerializedName("languages")
    @Expose
    private List<OptionDTO> languages = new ArrayList<>();

    @SerializedName("user_practices")
    @Expose
    private List<PracticeSelectionUserPractice> userPracticesList = new ArrayList<>();

    @Expose
    @SerializedName("patient_forms_response")
    private List<ConsentFormUserResponseDTO> patientFormsResponse = new ArrayList<>();

    @SerializedName("adhoc_forms_patient_mode")
    @Expose
    private AdhocFormsPatientModeInfo adhocFormsPatientModeInfo = new AdhocFormsPatientModeInfo();

    @Expose
    @SerializedName("filled_forms")
    private List<String> filledForms = new ArrayList<>();

    @SerializedName("demographics")
    @Expose
    private DemographicsSettingsDemographicsDTO demographicDTO = new DemographicsSettingsDemographicsDTO();


    public List<OptionDTO> getLanguages() {
        return languages;
    }

    public void setLanguages(List<OptionDTO> languages) {
        this.languages = languages;
    }

    public List<PracticeSelectionUserPractice> getUserPracticesList() {
        return userPracticesList;
    }

    public void setUserPracticesList(List<PracticeSelectionUserPractice> userPracticesList) {
        this.userPracticesList = userPracticesList;
    }

    public List<ConsentFormUserResponseDTO> getPatientFormsResponse() {
        return patientFormsResponse;
    }

    public void setPatientFormsResponse(List<ConsentFormUserResponseDTO> patientFormsResponse) {
        this.patientFormsResponse = patientFormsResponse;
    }

    public AdhocFormsPatientModeInfo getAdhocFormsPatientModeInfo() {
        return adhocFormsPatientModeInfo;
    }

    public void setAdhocFormsPatientModeInfo(AdhocFormsPatientModeInfo adhocFormsPatientModeInfo) {
        this.adhocFormsPatientModeInfo = adhocFormsPatientModeInfo;
    }

    public List<String> getFilledForms() {
        return filledForms;
    }

    public void setFilledForms(List<String> filledForms) {
        this.filledForms = filledForms;
    }

    public DemographicsSettingsDemographicsDTO getDemographicDTO() {
        return demographicDTO;
    }

    public void setDemographicDTO(DemographicsSettingsDemographicsDTO demographicDTO) {
        this.demographicDTO = demographicDTO;
    }
}
