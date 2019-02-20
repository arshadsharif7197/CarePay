package com.carecloud.carepay.service.library.unifiedauth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 3/9/17.
 */

public class UnifiedAuthorizationModel {

    @SerializedName("cognito")
    @Expose
    private UnifiedCognitoInfo cognito = new UnifiedCognitoInfo();

    @SerializedName("user_links")
    @Expose
    private UserLinks userLinks = new UserLinks();

    public UnifiedCognitoInfo getCognito() {
        return cognito;
    }

    public void setCognito(UnifiedCognitoInfo cognito) {
        this.cognito = cognito;
    }

    public UserLinks getUserLinks() {
        return userLinks;
    }

    public void setUserLinks(UserLinks userLinks) {
        this.userLinks = userLinks;
    }
}
