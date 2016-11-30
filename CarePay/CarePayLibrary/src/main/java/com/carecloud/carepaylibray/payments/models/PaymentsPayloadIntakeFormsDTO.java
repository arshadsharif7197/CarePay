package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rahul on 11/30/16.
 */

public class PaymentsPayloadIntakeFormsDTO {
    @SerializedName("ETag")
    @Expose
    private String eTag;
    @SerializedName("ServerSideEncryption")
    @Expose
    private String serverSideEncryption;

    /**
     *
     * @return
     * The eTag
     */
    public String getETag() {
        return eTag;
    }

    /**
     *
     * @param eTag
     * The ETag
     */
    public void setETag(String eTag) {
        this.eTag = eTag;
    }

    /**
     *
     * @return
     * The serverSideEncryption
     */
    public String getServerSideEncryption() {
        return serverSideEncryption;
    }

    /**
     *
     * @param serverSideEncryption
     * The ServerSideEncryption
     */
    public void setServerSideEncryption(String serverSideEncryption) {
        this.serverSideEncryption = serverSideEncryption;
    }
}
