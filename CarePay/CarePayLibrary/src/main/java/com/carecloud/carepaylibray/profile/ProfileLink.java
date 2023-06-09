package com.carecloud.carepaylibray.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author pjohnson on 3/4/19.
 */
public class ProfileLink {

    @SerializedName("practice_id")
    @Expose
    private String practiceId;
    @SerializedName("practice_mgmt")
    @Expose
    private String practiceMgmt;
    @SerializedName("patient_id")
    @Expose
    private String patientId;
    @SerializedName("relationship_type")
    @Expose
    private String relationType;
    @SerializedName("permissions")
    @Expose
    private PermissionDto permissionDto;
    @SerializedName("expiration_dt")
    @Expose
    private String expirationDate;
    @SerializedName("delegate_user_id")
    @Expose
    private String delegateUserId;
    @SerializedName("permissions_hierarchy")
    @Expose
    private List<Permission> permissionsHierarchy;


    public PermissionDto getPermissionDto() {
        return permissionDto;
    }

    public void setPermissionDto(PermissionDto permissionDto) {
        this.permissionDto = permissionDto;
    }

    public String getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(String practiceId) {
        this.practiceId = practiceId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getPracticeMgmt() {
        return practiceMgmt;
    }

    public void setPracticeMgmt(String practiceMgmt) {
        this.practiceMgmt = practiceMgmt;
    }

    public String getDelegateUserId() {
        return delegateUserId;
    }

    public void setDelegateUserId(String delegateUserId) {
        this.delegateUserId = delegateUserId;
    }

    public List<Permission> getPermissionsHierarchy() {
        return permissionsHierarchy;
    }

    public void setPermissionsHierarchy(List<Permission> permissionsHierarchy) {
        this.permissionsHierarchy = permissionsHierarchy;
    }
}
