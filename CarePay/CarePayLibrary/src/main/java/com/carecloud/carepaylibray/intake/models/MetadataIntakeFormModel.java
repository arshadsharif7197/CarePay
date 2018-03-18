
package com.carecloud.carepaylibray.intake.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class MetadataIntakeFormModel {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("practice_mgmt")
    @Expose
    private String practiceMgmt;
    @SerializedName("practice_id")
    @Expose
    private String practiceId;
    @SerializedName("created_dt")
    @Expose
    private String createdDt;

    /**
     * 
     * @return
     *     The userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 
     * @param userId
     *     The user_id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 
     * @return
     *     The practiceMgmt
     */
    public String getPracticeMgmt() {
        return practiceMgmt;
    }

    /**
     * 
     * @param practiceMgmt
     *     The practice_mgmt
     */
    public void setPracticeMgmt(String practiceMgmt) {
        this.practiceMgmt = practiceMgmt;
    }

    /**
     * 
     * @return
     *     The practiceId
     */
    public String getPracticeId() {
        return practiceId;
    }

    /**
     * 
     * @param practiceId
     *     The practice_id
     */
    public void setPracticeId(String practiceId) {
        this.practiceId = practiceId;
    }

    /**
     * 
     * @return
     *     The createdDt
     */
    public String getCreatedDt() {
        return createdDt;
    }

    /**
     * 
     * @param createdDt
     *     The created_dt
     */
    public void setCreatedDt(String createdDt) {
        this.createdDt = createdDt;
    }

}
