
package com.carecloud.carepaylibray.demographicsettings.models;

import com.carecloud.carepaylibray.base.dtos.DelegatePermissionBasePayloadDto;
import com.carecloud.carepaylibray.payments.models.MerchantServicesDTO;
import com.carecloud.carepaylibray.payments.models.PapiAccountsDTO;
import com.carecloud.carepaylibray.signinsignup.dto.OptionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DemographicsSettingsPayloadDTO extends DelegatePermissionBasePayloadDto {

    @SerializedName("languages")
    @Expose
    private List<OptionDTO> demographicsSettingsLanguageDTOs = new ArrayList<>();
    @SerializedName("notifications")
    @Expose
    private DemographicSettingsNotificationDTO demographicSettingsNotificationDTO = new DemographicSettingsNotificationDTO();
    @SerializedName("patient_credit_cards")
    @Expose
    private List<DemographicsSettingsCreditCardsPayloadDTO> patientCreditCards = new ArrayList<>();
    @SerializedName("current_email")
    @Expose
    private String currentEmail;
    @SerializedName("papi_accounts")
    @Expose
    private List<PapiAccountsDTO> papiAccounts = new ArrayList<>();
    @SerializedName("merchant_services")
    @Expose
    private List<MerchantServicesDTO> merchantServices = new ArrayList<>();

    /**
     * Gets languages.
     *
     * @return the languages
     */
    public List<OptionDTO> getLanguages() {
        return demographicsSettingsLanguageDTOs;
    }

    /**
     * Sets languages.
     *
     * @param demographicsSettingsLanguageDTOs the demographics settings language dt os
     */
    public void setLanguages(List<OptionDTO> demographicsSettingsLanguageDTOs) {
        this.demographicsSettingsLanguageDTOs = demographicsSettingsLanguageDTOs;
    }

    public List<OptionDTO> getDemographicsSettingsLanguageDTOs() {
        return demographicsSettingsLanguageDTOs;
    }

    public void setDemographicsSettingsLanguageDTOs(List<OptionDTO> demographicsSettingsLanguageDTOs) {
        this.demographicsSettingsLanguageDTOs = demographicsSettingsLanguageDTOs;
    }

    public DemographicSettingsNotificationDTO getDemographicSettingsNotificationDTO() {
        return demographicSettingsNotificationDTO;
    }

    public void setDemographicSettingsNotificationDTO(DemographicSettingsNotificationDTO demographicSettingsNotificationDTO) {
        this.demographicSettingsNotificationDTO = demographicSettingsNotificationDTO;
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
    public List<PapiAccountsDTO> getPapiAccounts() {
        return papiAccounts;
    }

    /**
     * Sets papi accounts.
     *
     * @param papiAccounts the papi accounts
     */
    public void setPapiAccounts(List<PapiAccountsDTO> papiAccounts) {
        this.papiAccounts = papiAccounts;
    }

    /**
     * Gets merchant services.
     *
     * @return the merchant services
     */
    public List<MerchantServicesDTO> getMerchantServices() {
        return merchantServices;
    }

    /**
     * Sets merchant services.
     *
     * @param merchantServices the merchant services
     */
    public void setMerchantServices(List<MerchantServicesDTO> merchantServices) {
        this.merchantServices = merchantServices;
    }
}

