package com.carecloud.carepaylibray.signinsignup.dto;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 25/04/17.
 */

public class SignInLinksDTO {
    @SerializedName("self")
    @Expose
    private TransitionDTO self = new TransitionDTO();
    @SerializedName("login")
    @Expose
    private TransitionDTO login = new TransitionDTO();
    @SerializedName("personal_info")
    @Expose
    private TransitionDTO personalInfo = new TransitionDTO();
    @SerializedName("language_metadata")
    @Expose
    private TransitionDTO language = new TransitionDTO();

    public TransitionDTO getSelf() {
        return self;
    }

    public void setSelf(TransitionDTO self) {
        this.self = self;
    }

    public TransitionDTO getLogin() {
        return login;
    }

    public void setLogin(TransitionDTO login) {
        this.login = login;
    }

    public TransitionDTO getPersonalInfo() {
        return personalInfo;
    }

    public void setPersonalInfo(TransitionDTO personalInfo) {
        this.personalInfo = personalInfo;
    }

    public TransitionDTO getLanguage() {
        return language;
    }

    public void setLanguage(TransitionDTO language) {
        this.language = language;
    }
}
