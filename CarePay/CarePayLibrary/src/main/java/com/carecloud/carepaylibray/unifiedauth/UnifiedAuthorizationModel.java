package com.carecloud.carepaylibray.unifiedauth;

import com.carecloud.carepay.service.library.unifiedauth.UnifiedCognitoInfo;
import com.carecloud.carepaylibray.profile.UserLinks;
import com.carecloud.carepaylibray.unifiedauth.TwoFAuth.TwoFactorAuth;
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
    @SerializedName("two_factor_authentication")
    @Expose
    private TwoFactorAuth twoFactorAuth = new TwoFactorAuth();

    public TwoFactorAuth getTwoFactorAuth() {
        return twoFactorAuth;
    }

    public void setTwoFactorAuth(TwoFactorAuth twoFactorAuth) {
        this.twoFactorAuth = twoFactorAuth;
    }

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
