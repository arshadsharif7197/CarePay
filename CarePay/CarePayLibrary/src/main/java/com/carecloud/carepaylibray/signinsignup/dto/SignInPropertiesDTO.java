package com.carecloud.carepaylibray.signinsignup.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 25/04/17.
 */

public class SignInPropertiesDTO {

    @SerializedName("email")
    @Expose
    private SignInPropertyDTO email = new SignInPropertyDTO();
    @SerializedName("password")
    @Expose
    private SignInPropertyDTO password = new SignInPropertyDTO();
    @SerializedName("gender")
    @Expose
    private GenderPropertyDTO gender = new GenderPropertyDTO();
    @SerializedName("create_password")
    @Expose
    private SignInPropertyDTO createPassword = new SignInPropertyDTO();
    @SerializedName("repeat_password")
    @Expose
    private SignInPropertyDTO repeatPassword = new SignInPropertyDTO();


    public SignInPropertyDTO getEmail() {
        return email;
    }

    public void setEmail(SignInPropertyDTO email) {
        this.email = email;
    }

    public SignInPropertyDTO getPassword() {
        return password;
    }

    public void setPassword(SignInPropertyDTO password) {
        this.password = password;
    }

    public GenderPropertyDTO getGender() {
        return gender;
    }

    public void setGender(GenderPropertyDTO gender) {
        this.gender = gender;
    }

    public SignInPropertyDTO getCreatePassword() {
        return createPassword;
    }

    public void setCreatePassword(SignInPropertyDTO createPassword) {
        this.createPassword = createPassword;
    }

    public SignInPropertyDTO getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(SignInPropertyDTO repeatPassword) {
        this.repeatPassword = repeatPassword;
    }
}
