package com.carecloud.carepaylibray.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 3/4/19.
 */
public class ProfileLink {

    @SerializedName("practice_id")
    @Expose
    private String practiceId;
    @SerializedName("patient_id")
    @Expose
    private String patientId;
    @SerializedName("permissions")
    @Expose
    private PermissionDto permissionDto;


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
}
