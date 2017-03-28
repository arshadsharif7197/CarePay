package com.carecloud.carepaylibray.demographics.dtos.payload;

import com.carecloud.carepaylibray.base.models.PatientModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jahirul Bhuiyan on 9/19/2016.
 * Model for Demographics payload
 */
public class DemographicPayloadDTO {
    @SerializedName("personal_details")
    @Expose
    private PatientModel personalDetails = new PatientModel();

    @SerializedName("insurances")
    @Expose
    private List<DemographicInsurancePayloadDTO> insurances = new ArrayList<>();

    @SerializedName("identity_documents")
    @Expose
    private List<DemographicIdDocPayloadDTO> idDocuments = new ArrayList<>();

    @SerializedName("address")
    @Expose
    private DemographicAddressPayloadDTO address = new DemographicAddressPayloadDTO();

    @SerializedName("notifications")
    @Expose
    private NotificationSettings notificationSettings = new NotificationSettings();

    /**
     * @return The address
     */
    public DemographicAddressPayloadDTO getAddress() {
        return address;
    }

    /**
     * @param address The address
     */
    public void setAddress(DemographicAddressPayloadDTO address) {
        this.address = address;
    }

    /**
     * @return The personalDetails
     */
    public PatientModel getPersonalDetails() {
        return personalDetails;
    }

    /**
     * @param personalDetails The personal_details
     */
    public void setPersonalDetails(PatientModel personalDetails) {
        this.personalDetails = personalDetails;
    }

    /**
     * Getter
     *
     * @return The list of id docs
     */
    public List<DemographicIdDocPayloadDTO> getIdDocuments() {
        return idDocuments;
    }

    /**
     * Setter
     * @param idDocuments The id documents
     */
    public void setIdDocuments(List<DemographicIdDocPayloadDTO> idDocuments) {
        this.idDocuments = idDocuments;
    }

    /**
     * @return The insurances
     */
    public List<DemographicInsurancePayloadDTO> getInsurances() {
        return insurances;
    }

    /**
     * @param insurances The insurances
     */
    public void setInsurances(List<DemographicInsurancePayloadDTO> insurances) {
        this.insurances = insurances;
    }

    public NotificationSettings getNotificationSettings() {
        return notificationSettings;
    }

    public void setNotificationSettings(NotificationSettings notificationSettings) {
        this.notificationSettings = notificationSettings;
    }
}
