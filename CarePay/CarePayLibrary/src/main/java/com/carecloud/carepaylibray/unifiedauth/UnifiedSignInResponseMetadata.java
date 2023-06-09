package com.carecloud.carepaylibray.unifiedauth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 13/07/18.
 */
public class UnifiedSignInResponseMetadata {

    @Expose
    @SerializedName("transitions")
    private UnifiedSignInTransitions transitions = new UnifiedSignInTransitions();

    public UnifiedSignInTransitions getTransitions() {
        return transitions;
    }

    public void setTransitions(UnifiedSignInTransitions transitions) {
        this.transitions = transitions;
    }
}
