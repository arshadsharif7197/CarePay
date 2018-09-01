package com.carecloud.carepay.service.library.unifiedauth;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 13/07/18.
 */
public class UnifiedSignInTransitions {

    @Expose
    @SerializedName("badge_counters")
    TransitionDTO badgeCounter = new TransitionDTO();

    public TransitionDTO getBadgeCounter() {
        return badgeCounter;
    }

    public void setBadgeCounter(TransitionDTO badgeCounter) {
        this.badgeCounter = badgeCounter;
    }
}
