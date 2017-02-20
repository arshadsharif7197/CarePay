package com.carecloud.carepay.practice.library.signin.dtos;

/**
 * Created by Rahul on 11/2/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignInPropertiesDTO {
    @SerializedName("email")
    @Expose
    private SignInEmailDTO email = new SignInEmailDTO();
    @SerializedName("password")
    @Expose
    private SignInPasswordDTO password = new SignInPasswordDTO();

    /**
     * @return The email
     */
    public SignInEmailDTO getEmail() {
        return email;
    }

    /**
     * @param email The email
     */
    public void setEmail(SignInEmailDTO email) {
        this.email = email;
    }

    /**
     * @return The password
     */
    public SignInPasswordDTO getPassword() {
        return password;
    }

    /**
     * @param password The password
     */
    public void setPassword(SignInPasswordDTO password) {
        this.password = password;
    }

}
