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
    @SerializedName("address")
    @Expose
    private DemographicAddressPayloadDTO address = new DemographicAddressPayloadDTO();

    @SerializedName("personal_details")
    @Expose
    private PatientModel personalDetails = new PatientModel();

    @SerializedName("emergency_contact")
    @Expose
    private PatientModel emergencyContact;

    @SerializedName("insurances")
    @Expose
    private List<DemographicInsurancePayloadDTO> insurances = new ArrayList<>();

    @SerializedName("identity_document")
    @Expose
    private DemographicIdDocPayloadDTO idDocument = new DemographicIdDocPayloadDTO();

    @SerializedName("notifications")
    @Expose
    private NotificationOptions notificationOptions = new NotificationOptions();

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
    public DemographicIdDocPayloadDTO getIdDocument() {
        return idDocument;
    }

    /**
     * Setter
     *
     * @param idDocument The id documents
     */
    public void setIdDocument(DemographicIdDocPayloadDTO idDocument) {
        this.idDocument = idDocument;
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

    public NotificationOptions getNotificationOptions() {
        return notificationOptions;
    }

    public void setNotificationOptions(NotificationOptions notificationOptions) {
        this.notificationOptions = notificationOptions;
    }

    public PatientModel getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(PatientModel emergencyContact) {
        this.emergencyContact = emergencyContact;
    }
}
