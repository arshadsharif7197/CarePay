
package com.carecloud.carepaylibray.demographicsettings.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicsSettingsPayloadDTO {

    @SerializedName("languages")
    @Expose
    private List<DemographicsSettingsLanguageDTO> demographicsSettingsLanguageDTOs = null;
    //@SerializedName("patient_credit_cards")
//    @Expose
  //  private List<DemographicsSettingsPatientCreditCardsDTO> patientCreditCards;
    @SerializedName("demographics")
    @Expose
    private DemographicsSettingsDemographicsDTO demographics;

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

  /*  public List<DemographicsSettingsPatientCreditCardsDTO> getPatientCreditCards() {
        return patientCreditCards;
    }

    public void setPatientCreditCards(List<DemographicsSettingsPatientCreditCardsDTO> patientCreditCards) {
        this.patientCreditCards = patientCreditCards;
    }*/


}
