package com.carecloud.carepay.practice.library.signin.dtos;

/**
 * Created by sudhir_pingale on 11/17/2016.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@Deprecated
public class SignInPatientModeDataModelDTO {
    @SerializedName("login")
    @Expose
    private SignInPatientModeModelDTO login = new SignInPatientModeModelDTO();

    @SerializedName("personal_info")
    @Expose
    private PersonalInfoDTO personalInfo = new PersonalInfoDTO();

    /**
     * @return The login
     */
    public SignInPatientModeModelDTO getLogin() {
        return login;
    }

    /**
     * @param login The login
     */
    public void setLogin(SignInPatientModeModelDTO login) {
        this.login = login;
    }

    public PersonalInfoDTO getPersonalInfo() {
        return personalInfo;
    }

    public void setPersonalInfo(PersonalInfoDTO personalInfo) {
        this.personalInfo = personalInfo;
    }

}
