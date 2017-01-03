
package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicsSettingsPropertiesDTO {

    @SerializedName("card_type")
    @Expose
    private DemographicsSettingsCardTypeDTO demographicsSettingsCardTypeDTO;
    @SerializedName("card_number")
    @Expose
    private DemographicsSettingsCardNumberDTO demographicsSettingsCardNumberDTO;
    @SerializedName("name_on_card")
    @Expose
    private DemographicsSettingsNameOnCardDTO demographicsSettingsNameOnCardDTO;
    @SerializedName("expire_dt")
    @Expose
    private DemographicsSettingsExpireDateDTO demographicsSettingsExpireDateDTO;
    @SerializedName("cvv")
    @Expose
    private DemographicsSettingsCvvDTO demographicsSettingsCvvDTO;
    @SerializedName("token")
    @Expose
    private DemographicsSettingsTokenDTO demographicsSettingsTokenDTO;
    @SerializedName("billing_information")
    @Expose
    private DemographicsSettingsBillingInformationDTO demographicsSettingsBillingInformationDTO;

    public DemographicsSettingsCardTypeDTO getCardType() {
        return demographicsSettingsCardTypeDTO;
    }

    public void setCardType(DemographicsSettingsCardTypeDTO demographicsSettingsCardTypeDTO) {
        this.demographicsSettingsCardTypeDTO = demographicsSettingsCardTypeDTO;
    }

    public DemographicsSettingsCardNumberDTO getCardNumber() {
        return demographicsSettingsCardNumberDTO;
    }

    public void setCardNumber(DemographicsSettingsCardNumberDTO demographicsSettingsCardNumberDTO) {
        this.demographicsSettingsCardNumberDTO = demographicsSettingsCardNumberDTO;
    }

    public DemographicsSettingsNameOnCardDTO getNameOnCard() {
        return demographicsSettingsNameOnCardDTO;
    }

    public void setNameOnCard(DemographicsSettingsNameOnCardDTO demographicsSettingsNameOnCardDTO) {
        this.demographicsSettingsNameOnCardDTO = demographicsSettingsNameOnCardDTO;
    }

    public DemographicsSettingsExpireDateDTO getExpireDt() {
        return demographicsSettingsExpireDateDTO;
    }

    public void setExpireDt(DemographicsSettingsExpireDateDTO demographicsSettingsExpireDateDTO) {
        this.demographicsSettingsExpireDateDTO = demographicsSettingsExpireDateDTO;
    }

    public DemographicsSettingsCvvDTO getCvv() {
        return demographicsSettingsCvvDTO;
    }

    public void setCvv(DemographicsSettingsCvvDTO demographicsSettingsCvvDTO) {
        this.demographicsSettingsCvvDTO = demographicsSettingsCvvDTO;
    }

    public DemographicsSettingsTokenDTO getToken() {
        return demographicsSettingsTokenDTO;
    }

    public void setToken(DemographicsSettingsTokenDTO demographicsSettingsTokenDTO) {
        this.demographicsSettingsTokenDTO = demographicsSettingsTokenDTO;
    }

    public DemographicsSettingsBillingInformationDTO getBillingInformation() {
        return demographicsSettingsBillingInformationDTO;
    }

    public void setBillingInformation(DemographicsSettingsBillingInformationDTO demographicsSettingsBillingInformationDTO) {
        this.demographicsSettingsBillingInformationDTO = demographicsSettingsBillingInformationDTO;
    }

}
