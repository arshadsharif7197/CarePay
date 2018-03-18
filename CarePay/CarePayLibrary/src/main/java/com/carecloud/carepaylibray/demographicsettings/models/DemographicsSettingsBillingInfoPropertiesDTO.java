
package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicsSettingsBillingInfoPropertiesDTO {

    @SerializedName("same_as_patient")
    @Expose
    private DemographicsSettingsSameAsPatientDTO demographicsSettingsSameAsPatientDTO = new DemographicsSettingsSameAsPatientDTO();
    @SerializedName("line1")
    @Expose
    private DemographicsSettingsCardTypeLineDTO demographicsSettingsCardTypeLineDTO = new DemographicsSettingsCardTypeLineDTO();
    @SerializedName("line2")
    @Expose
    private DemographicsSettingsCardTypeLineDTO line2 = new DemographicsSettingsCardTypeLineDTO();
    @SerializedName("city")
    @Expose
    private DemographicsSettingsCityDTO demographicsSettingsCityDTO = new DemographicsSettingsCityDTO();
    @SerializedName("state")
    @Expose
    private DemographicsSettingsStateDTO demographicsSettingsStateDTO = new DemographicsSettingsStateDTO();
    @SerializedName("zip")
    @Expose
    private DemographicsSettingsZipDTO demographicsSettingsZipDTO = new DemographicsSettingsZipDTO();
    @SerializedName("country")
    @Expose
    private DemographicsSettingsCountryDTO demographicsSettingsCountryDTO = new DemographicsSettingsCountryDTO();

    public DemographicsSettingsSameAsPatientDTO getSameAsPatient() {
        return demographicsSettingsSameAsPatientDTO;
    }

    public void setSameAsPatient(DemographicsSettingsSameAsPatientDTO demographicsSettingsSameAsPatientDTO) {
        this.demographicsSettingsSameAsPatientDTO = demographicsSettingsSameAsPatientDTO;
    }

    public DemographicsSettingsCardTypeLineDTO getLine1() {
        return demographicsSettingsCardTypeLineDTO;
    }

    public void setLine1(DemographicsSettingsCardTypeLineDTO demographicsSettingsCardTypeLineDTO) {
        this.demographicsSettingsCardTypeLineDTO = demographicsSettingsCardTypeLineDTO;
    }

    public DemographicsSettingsCardTypeLineDTO getLine2() {
        return line2;
    }

    public void setLine2(DemographicsSettingsCardTypeLineDTO line2) {
        this.line2 = line2;
    }

    public DemographicsSettingsCityDTO getCity() {
        return demographicsSettingsCityDTO;
    }

    public void setCity(DemographicsSettingsCityDTO demographicsSettingsCityDTO) {
        this.demographicsSettingsCityDTO = demographicsSettingsCityDTO;
    }

    public DemographicsSettingsStateDTO getState() {
        return demographicsSettingsStateDTO;
    }

    public void setState(DemographicsSettingsStateDTO demographicsSettingsStateDTO) {
        this.demographicsSettingsStateDTO = demographicsSettingsStateDTO;
    }

    public DemographicsSettingsZipDTO getZip() {
        return demographicsSettingsZipDTO;
    }

    public void setZip(DemographicsSettingsZipDTO demographicsSettingsZipDTO) {
        this.demographicsSettingsZipDTO = demographicsSettingsZipDTO;
    }

    public DemographicsSettingsCountryDTO getCountry() {
        return demographicsSettingsCountryDTO;
    }

    public void setCountry(DemographicsSettingsCountryDTO demographicsSettingsCountryDTO) {
        this.demographicsSettingsCountryDTO = demographicsSettingsCountryDTO;
    }

}
