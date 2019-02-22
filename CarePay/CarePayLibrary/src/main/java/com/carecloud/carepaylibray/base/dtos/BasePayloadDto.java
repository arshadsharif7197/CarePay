package com.carecloud.carepaylibray.base.dtos;

import com.carecloud.carepaylibray.profile.Profile;
import com.carecloud.carepaylibray.profile.UserLinks;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 2/20/19.
 */
public class BasePayloadDto {

    @SerializedName("user_links")
    @Expose
    private UserLinks userLinks = new UserLinks();
    @SerializedName("delegate")
    @Expose
    private Profile delegate;

    public UserLinks getUserLinks() {
        return userLinks;
    }

    public void setUserLinks(UserLinks userLinks) {
        this.userLinks = userLinks;
    }

    public Profile getDelegate() {
        return delegate;
    }

    public void setDelegate(Profile delegate) {
        this.delegate = delegate;
    }
}
