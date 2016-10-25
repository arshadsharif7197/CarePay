package com.carecloud.carepaylibray.demographics.dtos.payload;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 9/26/2016.
 * Model for payload info metadata.
 */
public class DemographicPayloadInfoMetaDataDTO {

    @SerializedName("user_id") @Expose
    private String userId;

    @SerializedName("username") @Expose
    private String username;

    @SerializedName("updated_dt") @Expose
    private String updatedDt;

    @SerializedName("created_dt")@Expose
    private String createdDt;

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
     * The username
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     * The username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return
     * The updatedDt
     */
    public String getUpdatedDt() {
        return updatedDt;
    }

    /**
     *
     * @param updatedDt
     * The updated_dt
     */
    public void setUpdatedDt(String updatedDt) {
        this.updatedDt = updatedDt;
    }

    public String getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(String createdDt) {
        this.createdDt = createdDt;
    }
}
