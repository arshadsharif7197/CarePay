package com.carecloud.carepaylibray.googleapis.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Jahirul Bhuiyan on 9/15/2016.
 */
public class GoogleAddressModel {
    @SerializedName("results")
    @Expose
    private List<GoogleAddressResultModel> results = new ArrayList<>();
    @SerializedName("status")
    @Expose
    private String status;

    /**
     *
     * @return
     * The results
     */
    public List<GoogleAddressResultModel> getResults() {
        return results;
    }

    /**
     *
     * @param results
     * The results
     */
    public void setResults(List<GoogleAddressResultModel> results) {
        this.results = results;
    }

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

}
