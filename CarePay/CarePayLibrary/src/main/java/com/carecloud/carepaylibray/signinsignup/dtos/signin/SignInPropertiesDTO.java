package com.carecloud.carepaylibray.signinsignup.dtos.signin;

/**
 * Created by Rahul on 11/7/16.
 */


import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

public class SignInPropertiesDTO {

    @SerializedName("email")
    @Expose
    private SignInEmailDTO email;
    @SerializedName("password")
    @Expose
    private SignInPasswordDTO password;

    /**
     *
     * @return
     * The email
     */
    public SignInEmailDTO getEmail() {
        return email;
    }

    /**
     *
     * @param email
     * The email
     */
    public void setEmail(SignInEmailDTO email) {
        this.email = email;
    }

    /**
     *
     * @return
     * The password
     */
    public SignInPasswordDTO getPassword() {
        return password;
    }

    /**
     *
     * @param password
     * The password
     */
    public void setPassword(SignInPasswordDTO password) {
        this.password = password;
    }

}