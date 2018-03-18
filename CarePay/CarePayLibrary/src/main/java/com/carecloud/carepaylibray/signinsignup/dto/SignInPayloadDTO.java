package com.carecloud.carepaylibray.signinsignup.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pjohnson on 25/04/17.
 */

public class SignInPayloadDTO {

    @SerializedName("login")
    @Expose
    private SignInPayloadPayloadDTO payload = new SignInPayloadPayloadDTO();

    @SerializedName("personal_info_check")
    @Expose
    private SignInPayloadPayloadDTO patientModePersonalInfoCheck = new SignInPayloadPayloadDTO();

    @SerializedName("languages")
    @Expose
    private List<OptionDTO> languages = new ArrayList<>();

    @SerializedName("state")
    @Expose
    private String state;

    public SignInPayloadPayloadDTO getPayload() {
        return payload;
    }

    public void setPayload(SignInPayloadPayloadDTO payload) {
        this.payload = payload;
    }

    public SignInPayloadPayloadDTO getPatientModePersonalInfoCheck() {
        return patientModePersonalInfoCheck;
    }

    public void setPatientModePersonalInfoCheck(SignInPayloadPayloadDTO patientModePersonalInfoCheck) {
        this.patientModePersonalInfoCheck = patientModePersonalInfoCheck;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<OptionDTO> getLanguages() {
        return languages;
    }

    public void setLanguages(List<OptionDTO> languages) {
        this.languages = languages;
    }
}
