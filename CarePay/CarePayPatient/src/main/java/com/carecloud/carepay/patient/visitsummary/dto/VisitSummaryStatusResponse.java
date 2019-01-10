package com.carecloud.carepay.patient.visitsummary.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 1/9/19.
 */
public class VisitSummaryStatusResponse {

    @Expose
    @SerializedName("job_id")
    private String jobId;
    @Expose
    @SerializedName("status")
    private String status;
    @Expose
    @SerializedName("status_code")
    private String statusCode;
    @Expose
    @SerializedName("queued")
    private boolean queued;
    @Expose
    @SerializedName("working")
    private boolean working;

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public boolean isQueued() {
        return queued;
    }

    public void setQueued(boolean queued) {
        this.queued = queued;
    }

    public boolean isWorking() {
        return working;
    }

    public void setWorking(boolean working) {
        this.working = working;
    }
}
