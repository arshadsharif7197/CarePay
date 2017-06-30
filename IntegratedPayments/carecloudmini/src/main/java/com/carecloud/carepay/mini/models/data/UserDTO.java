package com.carecloud.carepay.mini.models.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 6/23/17
 */

public class UserDTO {

    @SerializedName("alias")
    private String alias;

    @SerializedName("password")
    private String password;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
