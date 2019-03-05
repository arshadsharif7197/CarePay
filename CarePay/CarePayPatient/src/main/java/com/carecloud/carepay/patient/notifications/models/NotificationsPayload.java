package com.carecloud.carepay.patient.notifications.models;

import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.appointments.models.PracticePatientIdsDTO;
import com.carecloud.carepaylibray.base.dtos.BasePayloadDto;
import com.carecloud.carepaylibray.base.models.Paging;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.profile.ProfileLink;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 5/11/17.
 */

public class NotificationsPayload extends BasePayloadDto {

    @SerializedName("notifications")
    @Expose
    private List<NotificationItem> notifications = new ArrayList<>();
    @SerializedName("practice_patient_ids")
    @Expose
    private List<PracticePatientIdsDTO> practicePatientIds = new ArrayList<>();
    @SerializedName("demographics")
    @Expose
    private DemographicPayloadDTO demographicDTO = new DemographicPayloadDTO();
    @SerializedName("practice_information")
    @Expose
    private List<UserPracticeDTO> practiceInformation = new ArrayList<>();

    @SerializedName("page_details")
    @Expose
    private Paging paging;

    public List<NotificationItem> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificationItem> notifications) {
        this.notifications = notifications;
    }

    public List<PracticePatientIdsDTO> getPracticePatientIds() {
        return practicePatientIds;
    }

    public void setPracticePatientIds(List<PracticePatientIdsDTO> practicePatientIds) {
        this.practicePatientIds = practicePatientIds;
    }

    public DemographicPayloadDTO getDemographicDTO() {
        return demographicDTO;
    }

    public void setDemographicDTO(DemographicPayloadDTO demographicDTO) {
        this.demographicDTO = demographicDTO;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }

    public List<UserPracticeDTO> getPracticeInformation() {
        return practiceInformation;
    }

    public void setPracticeInformation(List<UserPracticeDTO> practiceInformation) {
        this.practiceInformation = practiceInformation;
    }

    public boolean canViewSurveyNotifications(String practiceId) {
        if (getDelegate() == null) {
            return true;
        }

        ProfileLink profileLink = getDelegate().getProfileLink(practiceId);
        if (profileLink == null) {
            return false;
        }

        return profileLink.getPermissionDto().getPermissions().getViewAndSubmitSurveys().isEnabled();
    }
}
