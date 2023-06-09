package com.carecloud.carepay.service.library.dtos;


import com.carecloud.carepay.service.library.base.OptionNameInterface;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jahirul Bhuiyan on 10/27/2016
 */

public class UserPracticeDTO implements OptionNameInterface {

    @SerializedName("practice_mgmt")
    @Expose
    private String practiceMgmt;
    @SerializedName("practice_id")
    @Expose
    private String practiceId;
    @SerializedName("practice_name")
    @Expose
    private String practiceName;
    @SerializedName("practice_photo")
    @Expose
    private String practicePhoto;
    @SerializedName("practice_phone")
    @Expose
    private String practicePhone;
    @SerializedName("practice_address")
    @Expose
    private UserPracticeAddress addressDTO = new UserPracticeAddress();
    @SerializedName("prefix")
    @Expose
    private String prefix;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("patient_id")
    @Expose
    private String patientId;
    @SerializedName("username")
    @Expose
    private String userName;
    @SerializedName("breeze_practice")
    @Expose
    private boolean breezePractice = false;
    @SerializedName("payeezy")
    @Expose
    private boolean payeezyEnabled = false;
    @SerializedName("clover")
    @Expose
    private boolean cloverEnabled = false;
    @SerializedName("is_retail_enabled")
    @Expose
    private boolean isRetailEnabled = false;
    @SerializedName("locations")
    @Expose
    private List<AvailableLocationDTO> locations;
    @SerializedName("visit_summary_enabled")
    @Expose
    private boolean visitSummaryEnabled = false;

    /**
     * @return The prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @param prefix The prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * @return The userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId The user_id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return The practiceMgmt
     */
    public String getPracticeMgmt() {
        return practiceMgmt;
    }

    /**
     * @param practiceMgmt The practice_mgmt
     */
    public void setPracticeMgmt(String practiceMgmt) {
        this.practiceMgmt = practiceMgmt;
    }

    /**
     * @return The practiceId
     */
    public String getPracticeId() {
        return practiceId;
    }

    /**
     * @param practiceId The practice_id
     */
    public void setPracticeId(String practiceId) {
        this.practiceId = practiceId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPracticeName() {
        return practiceName;
    }

    public void setPracticeName(String practiceName) {
        this.practiceName = practiceName;
    }

    public String getPracticePhoto() {
        return practicePhoto;
    }

    public void setPracticePhoto(String practicePhoto) {
        this.practicePhoto = practicePhoto;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPracticePhone() {
        return practicePhone;
    }

    public void setPracticePhone(String practicePhone) {
        this.practicePhone = practicePhone;
    }

    public UserPracticeAddress getAddressDTO() {
        return addressDTO;
    }

    public void setAddressDTO(UserPracticeAddress addressDTO) {
        this.addressDTO = addressDTO;
    }

    public boolean isBreezePractice() {
        return breezePractice;
    }

    public void setBreezePractice(boolean breezePractice) {
        this.breezePractice = breezePractice;
    }

    public boolean isPayeezyEnabled() {
        return payeezyEnabled;
    }

    public void setPayeezyEnabled(boolean payeezyEnabled) {
        this.payeezyEnabled = payeezyEnabled;
    }

    public boolean isCloverEnabled() {
        return cloverEnabled;
    }

    public void setCloverEnabled(boolean cloverEnabled) {
        this.cloverEnabled = cloverEnabled;
    }

    public boolean isRetailEnabled() {
        return isRetailEnabled;
    }

    public void setRetailEnabled(boolean retailEnabled) {
        isRetailEnabled = retailEnabled;
    }

    public List<AvailableLocationDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<AvailableLocationDTO> locations) {
        this.locations = locations;
    }

    public boolean isVisitSummaryEnabled() {
        return visitSummaryEnabled;
    }

    public void setVisitSummaryEnabled(boolean visitSummaryEnabled) {
        this.visitSummaryEnabled = visitSummaryEnabled;
    }

    @Override
    public String getDisplayName() {
        return practiceName;
    }
}
