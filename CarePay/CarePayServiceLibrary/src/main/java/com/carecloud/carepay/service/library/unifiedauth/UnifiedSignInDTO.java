package com.carecloud.carepay.service.library.unifiedauth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 3/8/17.
 */

public class UnifiedSignInDTO {

    @SerializedName("user")
    @Expose
    private UnifiedSignInUser user = new UnifiedSignInUser();

    public UnifiedSignInUser getUser() {
        return user;
    }

    public void setUser(UnifiedSignInUser user) {
        this.user = user;
    }

    /**
     * Check if required fields are provided
     *
     * @return true if valid user
     */
    public boolean isValidUser() {
        return user.getEmail() != null &&
                user.getPassword() != null;
    }
}
