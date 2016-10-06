package com.carecloud.carepaylibray.demographics.models;

import com.carecloud.carepaylibray.base.models.BasePersonModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 9/26/2016.
 */
public class DemographicPayloadPersonalDetailsModel extends BasePersonModel {

    @SerializedName("first_name") @Expose
    private String firstName;

    @SerializedName("middle_name") @Expose
    private String middleName;

    @SerializedName("last_name") @Expose
    private String lastName;

    @SerializedName("date_of_birth") @Expose
    private String dateOfBirth;

    @SerializedName("gender") @Expose
    private String gender;

    @SerializedName("primary_race") @Expose
    private String primaryRace;

    @SerializedName("secondary_race") @Expose
    private String secondaryRace;

    @SerializedName("ethnicity") @Expose
    private String ethnicity;

    @SerializedName("preferred_language") @Expose
    private String preferredLanguage;



    /**
     * @return The primaryRace
     */
    public String getPrimaryRace() {
        return primaryRace;
    }

    /**
     * @param primaryRace The primary_race
     */
    public void setPrimaryRace(String primaryRace) {
        this.primaryRace = primaryRace;
    }

    /**
     * @return The secondaryRace
     */
    public String getSecondaryRace() {
        return secondaryRace;
    }

    /**
     * @param secondaryRace The secondary_race
     */
    public void setSecondaryRace(String secondaryRace) {
        this.secondaryRace = secondaryRace;
    }

    /**
     * @return The ethnicity
     */
    public String getEthnicity() {
        return ethnicity;
    }

    /**
     * @param ethnicity The ethnicity
     */
    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    /**
     * @return The preferredLanguage
     */
    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    /**
     * @param preferredLanguage The preferred_language
     */
    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getMiddleName() {
        return middleName;
    }

    @Override
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    @Override
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public String getGender() {
        return gender;
    }

    @Override
    public void setGender(String gender) {
        this.gender = gender;
    }
}
