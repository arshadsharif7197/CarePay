package com.carecloud.carepay.patient.visitsummary.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 1/8/19.
 */
public class VisitSummaryRequest {

    @SerializedName("status")
    @Expose
    private VisitSummaryStatus status;

    @SerializedName("job_id")
    @Expose
    private String jobId;

    public VisitSummaryStatus getStatus() {
        return status;
    }

    public void setStatus(VisitSummaryStatus status) {
        this.status = status;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}
