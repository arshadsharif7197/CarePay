package com.carecloud.carepay.mini.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 6/23/17
 */

public class MetadataDTO {

    @SerializedName("transitions")
    private TransitionsDTO transitions;

    public TransitionsDTO getTransitions() {
        return transitions;
    }

    public void setTransitions(TransitionsDTO transitions) {
        this.transitions = transitions;
    }

    void merge(MetadataDTO metadataDTO){
        this.transitions.merge(metadataDTO.getTransitions());
    }
}
