
package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DemographicsSettingsPayloadDTO {

    @SerializedName("languages")
    @Expose
    private List<DemographicsSettingsLanguageDTO> demographicsSettingsLanguageDTOs = null;
    @SerializedName("patient_credit_cards")
    @Expose
    private List<DemographicsSettingsCreditCardsPayloadDTO> patientCreditCards;
    @SerializedName("demographics")
    @Expose
    private DemographicsSettingsDemographicsDTO demographics;
    @SerializedName("current_email")
    @Expose
    private String currentEmail;


    public List<DemographicsSettingsLanguageDTO> getLanguages() {
        return demographicsSettingsLanguageDTOs;
    }

    public void setLanguages(List<DemographicsSettingsLanguageDTO> demographicsSettingsLanguageDTOs) {
        this.demographicsSettingsLanguageDTOs = demographicsSettingsLanguageDTOs;
    }


    public DemographicsSettingsDemographicsDTO getDemographics() {
        return demographics;
    }

    public void setDemographics(DemographicsSettingsDemographicsDTO demographics) {
        this.demographics = demographics;
    }

    public List<DemographicsSettingsCreditCardsPayloadDTO> getPatientCreditCards() {
        return patientCreditCards;
    }

    public void setPatientCreditCards(List<DemographicsSettingsCreditCardsPayloadDTO> patientCreditCards) {
        this.patientCreditCards = patientCreditCards;
    }

    public String getCurrentEmail() {
        return currentEmail;
    }

    public void setCurrentEmail(String currentEmail) {
        this.currentEmail = currentEmail;
    }
}

