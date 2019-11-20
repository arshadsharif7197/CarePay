package com.carecloud.carepaylibray.unifiedauth;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 13/07/18.
 */
public class UnifiedSignInTransitions {

    @Expose
    @SerializedName("badge_counters")
    private TransitionDTO badgeCounter = new TransitionDTO();
    @Expose
    @SerializedName("accept_connect_invite")
    private TransitionDTO acceptConnectInvite = new TransitionDTO();

    public TransitionDTO getBadgeCounter() {
        return badgeCounter;
    }

    public void setBadgeCounter(TransitionDTO badgeCounter) {
        this.badgeCounter = badgeCounter;
    }

    public TransitionDTO getAcceptConnectInvite() {
        return acceptConnectInvite;
    }

    public void setAcceptConnectInvite(TransitionDTO acceptConnectInvite) {
        this.acceptConnectInvite = acceptConnectInvite;
    }
}
