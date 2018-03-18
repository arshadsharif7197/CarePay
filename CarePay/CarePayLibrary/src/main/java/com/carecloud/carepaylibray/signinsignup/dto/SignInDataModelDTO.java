package com.carecloud.carepaylibray.signinsignup.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 25/04/17.
 */
public class SignInDataModelDTO {

    @SerializedName("login")
    @Expose
    private SignInModelDTO login = new SignInModelDTO();

    @SerializedName("personal_info")
    @Expose
    private SignInModelDTO personalInfo = new SignInModelDTO();

    @SerializedName("signin")
    @Expose
    private SignInModelDTO signin = new SignInModelDTO();
    @SerializedName("signup")
    @Expose
    private SignInModelDTO signup = new SignInModelDTO();

    public SignInModelDTO getLogin() {
        return login;
    }

    public void setLogin(SignInModelDTO login) {
        this.login = login;
    }

    public SignInModelDTO getPersonalInfo() {
        return personalInfo;
    }

    public void setPersonalInfo(SignInModelDTO personalInfo) {
        this.personalInfo = personalInfo;
    }

    public SignInModelDTO getSignin() {
        return signin;
    }

    public void setSignin(SignInModelDTO signin) {
        this.signin = signin;
    }

    public SignInModelDTO getSignup() {
        return signup;
    }

    public void setSignup(SignInModelDTO signup) {
        this.signup = signup;
    }
}
