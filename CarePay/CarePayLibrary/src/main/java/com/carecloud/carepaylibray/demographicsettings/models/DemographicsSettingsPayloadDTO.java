
package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DemographicsSettingsPayloadDTO {

    @SerializedName("languages")
    @Expose
    private List<DemographicsSettingsLanguageDTO> demographicsSettingsLanguageDTOs = new ArrayList<>();
    @SerializedName("patient_credit_cards")
    @Expose
    private List<DemographicsSettingsCreditCardsPayloadDTO> patientCreditCards = new ArrayList<>();
    @SerializedName("demographics")
    @Expose
    private DemographicsSettingsDemographicsDTO demographics = new DemographicsSettingsDemographicsDTO();
    @SerializedName("current_email")
    @Expose
    private String currentEmail;
    @SerializedName("papi_accounts")
    @Expose
    private List<DemographicsSettingsPapiAccountsDTO> papiAccounts = new ArrayList<>();
    @SerializedName("merchant_services")
    @Expose
    private List<DemographicsSettingsMerchantServicesDTO> merchantServices = new ArrayList<>();

    /**
     * Gets languages.
     *
     * @return the languages
     */
    public List<DemographicsSettingsLanguageDTO> getLanguages() {
        return demographicsSettingsLanguageDTOs;
    }

    /**
     * Sets languages.
     *
     * @param demographicsSettingsLanguageDTOs the demographics settings language dt os
     */
    public void setLanguages(List<DemographicsSettingsLanguageDTO> demographicsSettingsLanguageDTOs) {
        this.demographicsSettingsLanguageDTOs = demographicsSettingsLanguageDTOs;
    }


    /**
     * Gets demographics.
     *
     * @return the demographics
     */
    public DemographicsSettingsDemographicsDTO getDemographics() {
        return demographics;
    }

    /**
     * Sets demographics.
     *
     * @param demographics the demographics
     */
    public void setDemographics(DemographicsSettingsDemographicsDTO demographics) {
        this.demographics = demographics;
    }

    /**
     * Gets patient credit cards.
     *
     * @return the patient credit cards
     */
    public List<DemographicsSettingsCreditCardsPayloadDTO> getPatientCreditCards() {
        return patientCreditCards;
    }

    /**
     * Sets patient credit cards.
     *
     * @param patientCreditCards the patient credit cards
     */
    public void setPatientCreditCards(List<DemographicsSettingsCreditCardsPayloadDTO> patientCreditCards) {
        this.patientCreditCards = patientCreditCards;
    }

    /**
     * Gets current email.
     *
     * @return the current email
     */
    public String getCurrentEmail() {
        return currentEmail;
    }

    /**
     * Sets current email.
     *
     * @param currentEmail the current email
     */
    public void setCurrentEmail(String currentEmail) {
        this.currentEmail = currentEmail;
    }

    /**
     * Gets papi accounts.
     *
     * @return the papi accounts
     */
    public List<DemographicsSettingsPapiAccountsDTO> getPapiAccounts() {
        return papiAccounts;
    }

    /**
     * Sets papi accounts.
     *
     * @param papiAccounts the papi accounts
     */
    public void setPapiAccounts(List<DemographicsSettingsPapiAccountsDTO> papiAccounts) {
        this.papiAccounts = papiAccounts;
    }

    /**
     * Gets merchant services.
     *
     * @return the merchant services
     */
    public List<DemographicsSettingsMerchantServicesDTO> getMerchantServices() {
        return merchantServices;
    }

    /**
     * Sets merchant services.
     *
     * @param merchantServices the merchant services
     */
    public void setMerchantServices(List<DemographicsSettingsMerchantServicesDTO> merchantServices) {
        this.merchantServices = merchantServices;
    }
}

