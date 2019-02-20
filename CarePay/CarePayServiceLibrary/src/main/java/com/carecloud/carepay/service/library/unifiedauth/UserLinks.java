package com.carecloud.carepay.service.library.unifiedauth;

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
    private LoggedInUser loggedInUser;
    @Expose
    @SerializedName("represented_users")
    private List<Profile> representedUsers = new ArrayList<>();

    public LoggedInUser getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(LoggedInUser loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public List<Profile> getRepresentedUsers() {
        return representedUsers;
    }

    public void setRepresentedUsers(List<Profile> representedUsers) {
        this.representedUsers = representedUsers;
    }
}
