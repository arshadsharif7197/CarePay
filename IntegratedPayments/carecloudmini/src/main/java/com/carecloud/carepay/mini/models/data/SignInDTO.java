package com.carecloud.carepay.mini.models.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 6/23/17
 */

public class SignInDTO {

    @SerializedName("user")
    private UserDTO user = new UserDTO();

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}
