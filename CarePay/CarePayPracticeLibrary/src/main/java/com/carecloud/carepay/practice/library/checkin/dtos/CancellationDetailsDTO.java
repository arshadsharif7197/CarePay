package com.carecloud.carepay.practice.library.checkin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by Jahirul Bhuiyan on 10/27/2016.
 */

public class CancellationDetailsDTO {

    @SerializedName("id")
    @Expose
    private Object id;
    @SerializedName("detail")
    @Expose
    private Object detail;
    @SerializedName("comments")
    @Expose
    private Object comments;

    /**
     *
     * @return
     * The id
     */
    public Object getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Object id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The detail
     */
    public Object getDetail() {
        return detail;
    }

    /**
     *
     * @param detail
     * The detail
     */
    public void setDetail(Object detail) {
        this.detail = detail;
    }

    /**
     *
     * @return
     * The comments
     */
    public Object getComments() {
        return comments;
    }

    /**
     *
     * @param comments
     * The comments
     */
    public void setComments(Object comments) {
        this.comments = comments;
    }
}