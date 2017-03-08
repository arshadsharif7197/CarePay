package com.carecloud.carepaylibray.base.models;

import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientModel {

    @SerializedName("patient_id")
    @Expose
    private String patientId;

    @SerializedName("first_name")
    @Expose
    protected String firstName;

    @SerializedName("middle_name")
    @Expose
    protected String middleName;

    @SerializedName("last_name")
    @Expose
    protected String lastName;

    @SerializedName("date_of_birth")
    @Expose
    protected String dateOfBirth;

    @SerializedName("gender")
    @Expose
    protected String gender;

    @SerializedName("ethnicity")
    @Expose
    protected String ethnicity;

    @SerializedName("primary_race")
    @Expose
    protected String primaryRace;

    @SerializedName("secondary_race")
    @Expose
    protected String secondaryRace;

    @SerializedName("profile_photo")
    @Expose
    protected String profilePhoto;

    public String getPatientId() {
        return patientId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getPrimaryRace() {
        return primaryRace;
    }

    public void setPrimaryRace(String primaryRace) {
        this.primaryRace = primaryRace;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getSecondaryRace() {
        return secondaryRace;
    }

    public void setSecondaryRace(String secondaryRace) {
        this.secondaryRace = secondaryRace;
    }

    public String getFormattedDateOfBirth() {
        if (StringUtil.isNullOrEmpty(dateOfBirth)) {
            return "";
        }

        return DateUtil.getInstance().setDateRaw(dateOfBirth).toStringWithFormatMmSlashDdSlashYyyy();
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
}
