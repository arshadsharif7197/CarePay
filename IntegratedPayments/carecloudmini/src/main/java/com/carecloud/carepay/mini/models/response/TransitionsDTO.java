package com.carecloud.carepay.mini.models.response;

import com.carecloud.carepay.mini.services.ServiceRequestDTO;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 6/23/17
 */

public class TransitionsDTO {

    @SerializedName("authenticate")
    private ServiceRequestDTO authenticate = new ServiceRequestDTO();

    @SerializedName("sign_in")
    private ServiceRequestDTO signIn = new ServiceRequestDTO();

    @SerializedName("sign_out")
    private ServiceRequestDTO signOut = new ServiceRequestDTO();

    public ServiceRequestDTO getAuthenticate() {
        return authenticate;
    }

    public void setAuthenticate(ServiceRequestDTO authenticate) {
        this.authenticate = authenticate;
    }

    public ServiceRequestDTO getSignIn() {
        return signIn;
    }

    public void setSignIn(ServiceRequestDTO signIn) {
        this.signIn = signIn;
    }

    public ServiceRequestDTO getSignOut() {
        return signOut;
    }

    public void setSignOut(ServiceRequestDTO signOut) {
        this.signOut = signOut;
    }

    void merge(TransitionsDTO transitionsDTO){
        if(transitionsDTO.getAuthenticate().getUrl()!=null){
            setAuthenticate(transitionsDTO.getAuthenticate());
        }
        if(transitionsDTO.getSignIn().getUrl()!=null){
            setSignIn(transitionsDTO.getSignIn());
        }
        if(transitionsDTO.getSignOut().getUrl()!=null){
            setSignOut(transitionsDTO.getSignOut());
        }
    }
}
