
package com.carecloud.breezemini.payments.postmodel.credittransaction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreditTransactionTypeDTO {

    @SerializedName("sortCode")
    @Expose
    private String sortCode;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("creatorName")
    @Expose
    private String creatorName;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("abstractBoolean")
    @Expose
    private boolean abstractBoolean;
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("updatedBy")
    @Expose
    private String updatedBy;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("updaterNameApplication")
    @Expose
    private String updaterNameApplication;
    @SerializedName("creatorNameApplication")
    @Expose
    private String creatorNameApplication;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updaterName")
    @Expose
    private String updaterName;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("createdBy")
    @Expose
    private String createdBy;
    @SerializedName("normalBalance")
    @Expose
    private String normalBalance;
    @SerializedName("cid")
    @Expose
    private String cid;

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getAbstractBoolean() {
        return abstractBoolean;
    }

    public void setAbstractBoolean(boolean abstractBoolean) {
        this.abstractBoolean = abstractBoolean;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUpdaterNameApplication() {
        return updaterNameApplication;
    }

    public void setUpdaterNameApplication(String updaterNameApplication) {
        this.updaterNameApplication = updaterNameApplication;
    }

    public String getCreatorNameApplication() {
        return creatorNameApplication;
    }

    public void setCreatorNameApplication(String creatorNameApplication) {
        this.creatorNameApplication = creatorNameApplication;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdaterName() {
        return updaterName;
    }

    public void setUpdaterName(String updaterName) {
        this.updaterName = updaterName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getNormalBalance() {
        return normalBalance;
    }

    public void setNormalBalance(String normalBalance) {
        this.normalBalance = normalBalance;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

}
