package com.carecloud.carepaylibray.demographics.dtos.payload;

import com.carecloud.carepay.service.library.unifiedauth.UnifiedCognitoInfo;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.CheckinSettingsDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPayloadDTO;
import com.carecloud.carepaylibray.unifiedauth.TwoFAuth.Payload;
import com.carecloud.carepaylibray.unifiedauth.TwoFAuth.TwoFactorAuth;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jahirul Bhuiyan on 9/28/2016.
 * Model for payload of response
 */
public class DemographicPayloadResponseDTO extends DemographicsSettingsPayloadDTO {

    @SerializedName("qrcode")
    @Expose
    private String qrCode;

    @SerializedName("otp_sent")
    @Expose
    private Boolean otp_sent;

    @SerializedName("otp_verified")
    @Expose
    private Boolean otp_verified;

    @SerializedName("two_factor_authentication")
    @Expose
    private TwoFactorAuth twoFactorAuth = new TwoFactorAuth();

    @SerializedName("demographics")
    @Expose
    private DemographicPayloadInfoDTO demographics = new DemographicPayloadInfoDTO();
    @SerializedName("appointments")
    @Expose
    private List<AppointmentDTO> appointmentpayloaddto = new ArrayList<>();
    @SerializedName("checkin_settings")
    @Expose
    private CheckinSettingsDTO checkinSettings = new CheckinSettingsDTO();
    @Expose
    @SerializedName("physicians")
    private List<PhysicianDto> physicians = new ArrayList<>();
    @Expose
    @SerializedName("cognito")
    private UnifiedCognitoInfo cognito = new UnifiedCognitoInfo();

    @SerializedName("missingFields")
    private List<DemographicsInfoDto> missingFieldsList = new ArrayList<>();

    public DemographicPayloadInfoDTO getDemographics() {
        return demographics;
    }

    public void setDemographics(DemographicPayloadInfoDTO demographics) {
        this.demographics = demographics;
    }

    public List<AppointmentDTO> getAppointmentpayloaddto() {
        return appointmentpayloaddto;
    }

    public void setAppointmentpayloaddto(List<AppointmentDTO> appointmentpayloaddto) {
        this.appointmentpayloaddto = appointmentpayloaddto;
    }

    /**
     * @return The qrCode
     */
    public String getQrcode() {
        return qrCode;
    }

    /**
     * @param qrCode The qrCode
     */
    public void setQrcode(String qrCode) {
        this.qrCode = qrCode;
    }

    public CheckinSettingsDTO getCheckinSettings() {
        return checkinSettings;
    }

    public void setCheckinSettings(CheckinSettingsDTO checkinSettings) {
        this.checkinSettings = checkinSettings;
    }

    public List<PhysicianDto> getPhysicians() {
        return physicians;
    }

    public void setPhysicians(List<PhysicianDto> physicians) {
        this.physicians = physicians;
    }

    public UnifiedCognitoInfo getCognito() {
        return cognito;
    }

    public void setCognito(UnifiedCognitoInfo cognito) {
        this.cognito = cognito;
    }

    public Boolean getOtp_sent() {
        return otp_sent;
    }

    public void setOtp_sent(Boolean otp_sent) {
        this.otp_sent = otp_sent;
    }

    public Boolean getOtp_verified() {
        return otp_verified;
    }

    public void setOtp_verified(Boolean otp_verified) {
        this.otp_verified = otp_verified;
    }

    public TwoFactorAuth getTwoFactorAuth() {
        return twoFactorAuth;
    }

    public void setTwoFactorAuth(TwoFactorAuth twoFactorAuth) {
        this.twoFactorAuth = twoFactorAuth;
    }

    public List<DemographicsInfoDto> getMissingFieldsList() {
        return missingFieldsList;
    }

    public void setMissingFieldsList(List<DemographicsInfoDto> missingFieldsList) {
        this.missingFieldsList = missingFieldsList;
    }
}
