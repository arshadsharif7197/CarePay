package com.carecloud.carepay.mini.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 6/24/17
 */

public class UserPracticeDTO {

    @SerializedName("user_id")
    private String userId;

    @SerializedName("practice_mgmt")
    private String practiceMgmt;

//    @SerializedName("practice_id")
    @SerializedName("id")
    private String practiceId;

//    @SerializedName("practice_name")
    @SerializedName("name")
    private String practiceName;

    @SerializedName("name_alias")
    private String practiceAliasName;

    @SerializedName("papi_organization_id")
    private String organizationId;

    @SerializedName("practice_photo")
    private String practicePhoto;

    @SerializedName("practice_phone")
    private String practicePhone;

    @SerializedName("locations")
    private List<LocationsDTO> locationsDTOList = new ArrayList<>();

    @Expose(deserialize = false, serialize = false)
    private String practiceInitials;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPracticeMgmt() {
        return practiceMgmt;
    }

    public void setPracticeMgmt(String practiceMgmt) {
        this.practiceMgmt = practiceMgmt;
    }

    public String getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(String practiceId) {
        this.practiceId = practiceId;
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

    public String getPracticePhone() {
        return practicePhone;
    }

    public void setPracticePhone(String practicePhone) {
        this.practicePhone = practicePhone;
    }

    public String getPracticeInitials() {
        return practiceInitials;
    }

    public void setPracticeInitials(String practiceInitials) {
        this.practiceInitials = practiceInitials;
    }

    public String getPracticeAliasName() {
        return practiceAliasName;
    }

    public void setPracticeAliasName(String practiceAliasName) {
        this.practiceAliasName = practiceAliasName;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public List<LocationsDTO> getLocationsDTOList() {
        return locationsDTOList;
    }

    public void setLocationsDTOList(List<LocationsDTO> locationsDTOList) {
        this.locationsDTOList = locationsDTOList;
    }

    /**
     * Find a location for this practice by its id
     * @param id location id
     * @return location if found or null
     */
    public LocationsDTO findLocationById(String id){
        for (LocationsDTO locationsDTO : locationsDTOList){
            if(locationsDTO.getGuid().equals(id)){
                return locationsDTO;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object object){
        try{
            UserPracticeDTO compare = (UserPracticeDTO) object;
            return this.getPracticeId().equals(compare.getPracticeId());
        }catch (ClassCastException cce){
            return false;
        }
    }
}

