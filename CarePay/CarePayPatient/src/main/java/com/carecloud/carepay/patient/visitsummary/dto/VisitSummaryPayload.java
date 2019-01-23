package com.carecloud.carepay.patient.visitsummary.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 1/8/19.
 */
public class VisitSummaryPayload {

    @Expose
    @SerializedName("visit_summary_request")
    private VisitSummaryRequest visitSummaryRequest;
    @Expose
    @SerializedName("visit_summary")
    private VisitSummaryStatusResponse visitSummary;

    public VisitSummaryRequest getVisitSummaryRequest() {
        return visitSummaryRequest;
    }

    public void setVisitSummaryRequest(VisitSummaryRequest visitSummaryRequest) {
        this.visitSummaryRequest = visitSummaryRequest;
    }

    public VisitSummaryStatusResponse getVisitSummary() {
        return visitSummary;
    }

    public void setVisitSummary(VisitSummaryStatusResponse visitSummary) {
        this.visitSummary = visitSummary;
    }
}
