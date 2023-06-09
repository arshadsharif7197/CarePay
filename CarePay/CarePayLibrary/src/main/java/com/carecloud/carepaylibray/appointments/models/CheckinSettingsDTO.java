package com.carecloud.carepaylibray.appointments.models;

import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicsInfoDto;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 8/23/17
 */

public class CheckinSettingsDTO {

    @SerializedName("ask_medications")
    private boolean showMedications = true;

    @SerializedName("ask_allergies")
    private boolean showAllergies = true;

    @SerializedName("identity_documents_required")
    private boolean showIdentityDocs = true;

    @SerializedName("health_insurance_required")
    private boolean showHealthInsurance = true;

    @SerializedName("allow_patients_upload_medications")
    private boolean allowMedicationPicture = true;

    @SerializedName("demographic_info")
    private List<DemographicsInfoDto> demographicsInfoDtoList = new ArrayList<>();

    public boolean shouldShowMedications() {
        return showMedications;
    }

    public void setShowMedications(boolean showMedications) {
        this.showMedications = showMedications;
    }

    public boolean shouldShowAllergies() {
        return showAllergies;
    }

    public void setShowAllergies(boolean showAllergies) {
        this.showAllergies = showAllergies;
    }

    public boolean shouldShowIdentityDocs() {
        return showIdentityDocs;
    }

    public void setShowIdentityDocs(boolean showIdentityDocs) {
        this.showIdentityDocs = showIdentityDocs;
    }

    public boolean shouldShowHealthInsurance() {
        return showHealthInsurance;
    }

    public void setShowHealthInsurance(boolean showHealthInsurance) {
        this.showHealthInsurance = showHealthInsurance;
    }

    public boolean isAllowMedicationPicture() {
        return allowMedicationPicture;
    }

    public void setAllowMedicationPicture(boolean allowMedicationPicture) {
        this.allowMedicationPicture = allowMedicationPicture;
    }

    public List<DemographicsInfoDto> getDemographicsInfoDtoList() {
        return demographicsInfoDtoList;
    }

    public void setDemographicsInfoDtoList(List<DemographicsInfoDto> demographicsInfoDtoList) {
        this.demographicsInfoDtoList = demographicsInfoDtoList;
    }
}
