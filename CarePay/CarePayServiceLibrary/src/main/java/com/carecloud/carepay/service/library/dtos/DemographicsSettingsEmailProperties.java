package com.carecloud.carepay.service.library.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harshal_patil on 1/21/2017.
 */

public class DemographicsSettingsEmailProperties {
    @SerializedName("login_email")
    @Expose
    private DemographicsSettingsLoginEmailDTO loginEmail = new DemographicsSettingsLoginEmailDTO();
    @SerializedName("proposed_email")
    @Expose
    private DemographicsSettingsProposedEmailDTO proposedEmail = new DemographicsSettingsProposedEmailDTO();

    public DemographicSettingsCurrentPasswordDTO getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(DemographicSettingsCurrentPasswordDTO currentPassword) {
        this.currentPassword = currentPassword;
    }

    public DemographicsSettingsProposedPasswordDTO getProposedPassword() {
        return proposedPassword;
    }

    public void setProposedPassword(DemographicsSettingsProposedPasswordDTO proposedPassword) {
        this.proposedPassword = proposedPassword;
    }

    @SerializedName("current_password")
    @Expose
    private DemographicSettingsCurrentPasswordDTO currentPassword;
    @SerializedName("proposed_password")
    @Expose
    private DemographicsSettingsProposedPasswordDTO proposedPassword;

    public DemographicsSettingsLoginEmailDTO getLoginEmail() {
        return loginEmail;
    }

    public void setLoginEmail(DemographicsSettingsLoginEmailDTO loginEmail) {
        this.loginEmail = loginEmail;
    }

    public DemographicsSettingsProposedEmailDTO getProposedEmail() {
        return proposedEmail;
    }

    public void setProposedEmail(DemographicsSettingsProposedEmailDTO proposedEmail) {
        this.proposedEmail = proposedEmail;
    }

}
