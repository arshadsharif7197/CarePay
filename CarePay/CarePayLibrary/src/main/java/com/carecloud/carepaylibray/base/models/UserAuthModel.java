package com.carecloud.carepaylibray.base.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 1/17/18
 */

public class UserAuthModel {

    @SerializedName("role")
    private String role;

    @SerializedName("permissions")
    private UserAuthPermissions userAuthPermissions = new UserAuthPermissions();

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public UserAuthPermissions getUserAuthPermissions() {
        return userAuthPermissions;
    }

    public void setUserAuthPermissions(UserAuthPermissions userAuthPermissions) {
        this.userAuthPermissions = userAuthPermissions;
    }
}
