package com.carecloud.carepay.service.library.dtos;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/27/2016.
 */

public class UserPracticeDTO {
    @SerializedName("prefix")
    @Expose
    private String prefix;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("practice_mgmt")
    @Expose
    private String practiceMgmt;
    @SerializedName("practice_id")
    @Expose
    private String practiceId;

    /**
     *
     * @return
     * The prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     *
     * @param prefix
     * The prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     *
     * @return
     * The userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     *
     * @param userId
     * The user_id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     *
     * @return
     * The practiceMgmt
     */
    public String getPracticeMgmt() {
        return practiceMgmt;
    }

    /**
     *
     * @param practiceMgmt
     * The practice_mgmt
     */
    public void setPracticeMgmt(String practiceMgmt) {
        this.practiceMgmt = practiceMgmt;
    }

    /**
     *
     * @return
     * The practiceId
     */
    public String getPracticeId() {
        return practiceId;
    }

    /**
     *
     * @param practiceId
     * The practice_id
     */
    public void setPracticeId(String practiceId) {
        this.practiceId = practiceId;
    }

}
