
package com.carecloud.carepay.practice.library.signin.dtos;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SigninPatientModeLinksDTO {

    @SerializedName("self")
    @Expose
    private TransitionDTO self = new TransitionDTO();
    @SerializedName("login")
    @Expose
    private TransitionDTO login = new TransitionDTO();
    @SerializedName("personal_info")
    @Expose
    private TransitionDTO personalInfo = new TransitionDTO();

    /**
     * 
     * @return
     *     The self
     */
    public TransitionDTO getSelf() {
        return self;
    }

    /**
     * 
     * @param self
     *     The self
     */
    public void setSelf(TransitionDTO self) {
        this.self = self;
    }


    /**
     * 
     * @return
     *     The personalInfo
     */
    public TransitionDTO getPersonalInfo() {
        return personalInfo;
    }

    /**
     * 
     * @param personalInfo
     *     The personal_info
     */
    public void setPersonalInfo(TransitionDTO personalInfo) {
        this.personalInfo = personalInfo;
    }

    public TransitionDTO getLogin() {
        return login;
    }

    public void setLogin(TransitionDTO login) {
        this.login = login;
    }
}
