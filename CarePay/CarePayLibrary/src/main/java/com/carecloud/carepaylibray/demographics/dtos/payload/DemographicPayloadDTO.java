package com.carecloud.carepaylibray.demographics.dtos.payload;

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
    private DemographicPersDetailsPayloadDTO personalDetails = new DemographicPersDetailsPayloadDTO();

    @SerializedName("identity_documents")
    @Expose
    private List<DemographicIdDocPayloadDTO> idDocuments = new ArrayList<>();

    @SerializedName("insurances")
    @Expose
    private List<DemographicInsurancePayloadDTO> insurances = new ArrayList<>();

    @SerializedName("updates")
    @Expose
    private List<DemographicPayloadUpdateDTO> updates = new ArrayList<>();

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
    public DemographicPersDetailsPayloadDTO getPersonalDetails() {
        return personalDetails;
    }

    /**
     * @param personalDetails The personal_details
     */
    public void setPersonalDetails(DemographicPersDetailsPayloadDTO personalDetails) {
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

    public List<DemographicPayloadUpdateDTO> getUpdates() {
        return updates;
    }

    public void setUpdates(List<DemographicPayloadUpdateDTO> updates) {
        this.updates = updates;
    }
}
