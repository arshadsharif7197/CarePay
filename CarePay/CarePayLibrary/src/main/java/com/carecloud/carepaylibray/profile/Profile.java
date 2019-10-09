package com.carecloud.carepaylibray.profile;

import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author pjohnson on 2/19/19.
 */
public class Profile {

    @Expose
    @SerializedName("id")
    private String id;
    @Expose
    @SerializedName("name")
    private String name;
    @Expose
    @SerializedName("delegate_user_id")
    private String delegateUser;
    @Expose
    @SerializedName("delegate_username")
    private String delegateUsername;
    @Expose
    @SerializedName("user_id")
    private String userId;
    @Expose
    @SerializedName("profile_id")
    private String profileId;
    @Expose
    @SerializedName("profile_name")
    private String profileName;
    @Expose
    @SerializedName("breeze_user")
    private boolean breezeUser;
    @Expose
    @SerializedName("demographics")
    private DemographicPayloadInfoDTO demographics;
    @Expose
    @SerializedName("delegate_demographics")
    private DemographicPayloadInfoDTO delegateDemographics;
    @Expose
    @SerializedName("links")
    private List<ProfileLink> links;

    private transient boolean delegate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDelegateUser() {
        return delegateUser;
    }

    public void setDelegateUser(String delegateUser) {
        this.delegateUser = delegateUser;
    }

    public String getDelegateUsername() {
        return delegateUsername;
    }

    public void setDelegateUsername(String delegateUsername) {
        this.delegateUsername = delegateUsername;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public DemographicPayloadInfoDTO getDemographics() {
        return demographics;
    }

    public void setDemographics(DemographicPayloadInfoDTO demographics) {
        this.demographics = demographics;
    }

    public DemographicPayloadInfoDTO getDelegateDemographics() {
        return delegateDemographics;
    }

    public void setDelegateDemographics(DemographicPayloadInfoDTO delegateDemographics) {
        this.delegateDemographics = delegateDemographics;
    }

    public boolean isDelegate() {
        return delegate;
    }

    public void setDelegate(boolean delegate) {
        this.delegate = delegate;
    }

    public boolean isBreezeUser() {
        return breezeUser;
    }

    public void setBreezeUser(boolean breezeUser) {
        this.breezeUser = breezeUser;
    }

    public List<ProfileLink> getLinks() {
        return links;
    }

    public void setLinks(List<ProfileLink> links) {
        this.links = links;
    }

    public ProfileLink getProfileLink(String practiceId) {
        if (links != null) {
            for (ProfileLink link : links) {
                if (link.getPracticeId().equals(practiceId)) {
                    return link;
                }
            }
        }
        return null;
    }
}
