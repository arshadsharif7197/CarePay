package com.carecloud.carepay.service.library.unifiedauth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 3/8/17.
 */

public class UnifiedSignUpDto {

    @SerializedName("user")
    @Expose
    private UnifiedSignUpUser user = new UnifiedSignUpUser();

    public UnifiedSignUpUser getUser() {
        return user;
    }

    public void setUser(UnifiedSignUpUser user) {
        this.user = user;
    }

    /**
     * Check if all required fields are added
     * @return true if valid
     */
    public boolean isValidUser(){
        return user.getEmail()!=null &&
                user.getPassword() != null &&
                user.getFirstName() != null &&
                user.getLastName() != null &&
                user.getBirthDate() != null &&
                user.getInviteCode() != null;
    }
}
