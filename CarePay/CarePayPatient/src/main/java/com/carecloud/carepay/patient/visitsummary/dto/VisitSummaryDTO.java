package com.carecloud.carepay.patient.visitsummary.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 1/8/19.
 */
public class VisitSummaryDTO {

    @Expose
    @SerializedName("payload")
    private VisitSummaryPayload payload;

    public VisitSummaryPayload getPayload() {
        return payload;
    }

    public void setPayload(VisitSummaryPayload payload) {
        this.payload = payload;
    }
}
