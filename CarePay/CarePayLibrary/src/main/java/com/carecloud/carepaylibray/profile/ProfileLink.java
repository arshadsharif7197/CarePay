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
    @SerializedName("relationship_type")
    @Expose
    private String relationType;
    @SerializedName("permissions")
    @Expose
    private PermissionDto permissionDto;
    @SerializedName("practice_name")
    @Expose
    private String practiceName;
    @SerializedName("practice_image")
    @Expose
    private String practiceImage;
    @SerializedName("expiration_dt")
    @Expose
    private String expirationDate;


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

    public String getPracticeName() {
        return practiceName;
    }

    public void setPracticeName(String practiceName) {
        this.practiceName = practiceName;
    }

    public String getPracticeImage() {
        return practiceImage;
    }

    public void setPracticeImage(String practiceImage) {
        this.practiceImage = practiceImage;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
}
