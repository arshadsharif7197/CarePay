package com.carecloud.carepaylibray.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 2/19/19.
 */
public class ProfileDto {

    @Expose
    @SerializedName("profile")
    private Profile profile;


    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return profile != null ? profile.getDemographics().getPayload().getPersonalDetails().getFullName() : "";
    }
}
