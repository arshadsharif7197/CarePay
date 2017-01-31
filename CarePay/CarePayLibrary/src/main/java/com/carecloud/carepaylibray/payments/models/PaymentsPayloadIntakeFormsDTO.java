package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Rahul on 11/30/16.
 */

public class PaymentsPayloadIntakeFormsDTO implements Serializable{
    @SerializedName("ETag")
    @Expose
    private String etag;
    @SerializedName("ServerSideEncryption")
    @Expose
    private String serverSideEncryption;

    /**
     * @return The eTag
     */
    public String getETag() {
        return etag;
    }

    /**
     * @param etag The ETag
     */
    public void setETag(String etag) {
        this.etag = etag;
    }

    /**
     * @return The serverSideEncryption
     */
    public String getServerSideEncryption() {
        return serverSideEncryption;
    }

    /**
     * @param serverSideEncryption The ServerSideEncryption
     */
    public void setServerSideEncryption(String serverSideEncryption) {
        this.serverSideEncryption = serverSideEncryption;
    }
}