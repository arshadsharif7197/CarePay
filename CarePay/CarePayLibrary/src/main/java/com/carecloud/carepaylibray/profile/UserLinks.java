package com.carecloud.carepaylibray.profile;

import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pjohnson on 2/19/19.
 */
public class UserLinks {

    @Expose
    @SerializedName("logged_in_user")
    private Profile loggedInUser;
    @Expose
    @SerializedName("represented_users")
    private List<ProfileDto> representedUsers = new ArrayList<>();
    @SerializedName("practice_information")
    @Expose
    private List<UserPracticeDTO> delegatePracticeInformation = new ArrayList<>();
    @Expose
    @SerializedName("authorized_delegates")
    private List<ProfileDto> delegates = new ArrayList<>();

    public Profile getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(Profile loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public List<ProfileDto> getRepresentedUsers() {
        return representedUsers;
    }

    public void setRepresentedUsers(List<ProfileDto> representedUsers) {
        this.representedUsers = representedUsers;
    }

    public List<UserPracticeDTO> getDelegatePracticeInformation() {
        return delegatePracticeInformation;
    }

    public void setDelegatePracticeInformation(List<UserPracticeDTO> delegatePracticeInformation) {
        this.delegatePracticeInformation = delegatePracticeInformation;
    }

    public List<ProfileDto> getDelegates() {
        return delegates;
    }

    public void setDelegates(List<ProfileDto> delegates) {
        this.delegates = delegates;
    }
}
