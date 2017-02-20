
package com.carecloud.carepay.practice.library.signin.dtos;


import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SigninPatientModeTransitionsDTO {

    @SerializedName("action")
    @Expose
    private TransitionDTO action = new TransitionDTO();
    @SerializedName("qrcode")
    @Expose
    private TransitionDTO qrcode = new TransitionDTO();

    public TransitionDTO getAction() {
        return action;
    }

    public void setAction(TransitionDTO action) {
        this.action = action;
    }

    public TransitionDTO getQrcode() {
        return qrcode;
    }

    public void setQrcode(TransitionDTO qrcode) {
        this.qrcode = qrcode;
    }
}
