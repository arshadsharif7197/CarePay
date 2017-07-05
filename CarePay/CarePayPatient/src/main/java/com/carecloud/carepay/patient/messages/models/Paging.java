package com.carecloud.carepay.patient.messages.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 6/30/17
 */

public class Paging {

    @SerializedName("current_page")
    private long currentPage;

    @SerializedName("per_page")
    private int resultsPerPage;

    @SerializedName("total_pages")
    private long totalPages;

    @SerializedName("total_entries")
    private long totalEntries;

    public long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(long currentPage) {
        this.currentPage = currentPage;
    }

    public int getResultsPerPage() {
        return resultsPerPage;
    }

    public void setResultsPerPage(int resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalEntries() {
        return totalEntries;
    }

    public void setTotalEntries(long totalEntries) {
        this.totalEntries = totalEntries;
    }
}
