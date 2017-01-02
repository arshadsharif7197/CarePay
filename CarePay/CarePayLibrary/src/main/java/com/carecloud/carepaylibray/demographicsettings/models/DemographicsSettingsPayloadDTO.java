
package com.carecloud.carepaylibray.demographicsettings.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicsSettingsPayloadDTO {

    @SerializedName("languages")
    @Expose
    private List<DemographicsSettingsLanguageDTO> demographicsSettingsLanguageDTOs = null;
    @SerializedName("payment_distributions")
    @Expose
    private List<DemographicsSettingsPaymentDistributionDTO> demographicsSettingsPaymentDistributionDTOs = null;
    @SerializedName("payment_methods")
    @Expose
    private List<DemographicsSettingsPaymentMethodDTO> demographicsSettingsPaymentMethodDTOs = null;
    @SerializedName("credit_card_types")
    @Expose
    private List<DemographicsSetiingsCreditCardTypeDTO> demographicsSetiingsCreditCardTypeDTOs = null;
    @SerializedName("payment_settings")
    @Expose
    private DemographicsSettingsPaymentSettingsDTO demographicsSettingsPaymentSettingsDTO;

    public List<DemographicsSettingsLanguageDTO> getLanguages() {
        return demographicsSettingsLanguageDTOs;
    }

    public void setLanguages(List<DemographicsSettingsLanguageDTO> demographicsSettingsLanguageDTOs) {
        this.demographicsSettingsLanguageDTOs = demographicsSettingsLanguageDTOs;
    }

    public List<DemographicsSettingsPaymentDistributionDTO> getPaymentDistributions() {
        return demographicsSettingsPaymentDistributionDTOs;
    }

    public void setPaymentDistributions(List<DemographicsSettingsPaymentDistributionDTO> demographicsSettingsPaymentDistributionDTOs) {
        this.demographicsSettingsPaymentDistributionDTOs = demographicsSettingsPaymentDistributionDTOs;
    }

    public List<DemographicsSettingsPaymentMethodDTO> getPaymentMethods() {
        return demographicsSettingsPaymentMethodDTOs;
    }

    public void setPaymentMethods(List<DemographicsSettingsPaymentMethodDTO> demographicsSettingsPaymentMethodDTOs) {
        this.demographicsSettingsPaymentMethodDTOs = demographicsSettingsPaymentMethodDTOs;
    }

    public List<DemographicsSetiingsCreditCardTypeDTO> getCreditCardTypes() {
        return demographicsSetiingsCreditCardTypeDTOs;
    }

    public void setCreditCardTypes(List<DemographicsSetiingsCreditCardTypeDTO> demographicsSetiingsCreditCardTypeDTOs) {
        this.demographicsSetiingsCreditCardTypeDTOs = demographicsSetiingsCreditCardTypeDTOs;
    }

    public DemographicsSettingsPaymentSettingsDTO getPaymentSettings() {
        return demographicsSettingsPaymentSettingsDTO;
    }

    public void setPaymentSettings(DemographicsSettingsPaymentSettingsDTO demographicsSettingsPaymentSettingsDTO) {
        this.demographicsSettingsPaymentSettingsDTO = demographicsSettingsPaymentSettingsDTO;
    }

}
