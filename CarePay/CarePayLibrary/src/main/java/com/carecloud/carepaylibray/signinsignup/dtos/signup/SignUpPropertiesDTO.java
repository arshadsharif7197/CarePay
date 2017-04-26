package com.carecloud.carepaylibray.signinsignup.dtos.signup;

/**
 * Created by Rahul on 11/7/16.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@Deprecated
public class SignUpPropertiesDTO {

    @SerializedName("email")
    @Expose
    private SignUpEmailDTO email = new SignUpEmailDTO();
    @SerializedName("create_password")
    @Expose
    private CreatePasswordDTO createPassword = new CreatePasswordDTO();
    @SerializedName("repeat_password")
    @Expose
    private RepeatPasswordDTO repeatPassword = new RepeatPasswordDTO();

    /**
     * @return The email
     */
    public SignUpEmailDTO getEmail() {
        return email;
    }

    /**
     * @param email The email
     */
    public void setEmail(SignUpEmailDTO email) {
        this.email = email;
    }

    /**
     * @return The createPassword
     */
    public CreatePasswordDTO getCreatePassword() {
        return createPassword;
    }

    /**
     * @param createPassword The create_password
     */
    public void setCreatePassword(CreatePasswordDTO createPassword) {
        this.createPassword = createPassword;
    }

    /**
     * @return The repeatPassword
     */
    public RepeatPasswordDTO getRepeatPassword() {
        return repeatPassword;
    }

    /**
     * @param repeatPassword The repeat_password
     */
    public void setRepeatPassword(RepeatPasswordDTO repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

}