package com.carecloud.carepaylibray.demographics.models;

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
    private DemographicAddressPayloadDTO address;

    @SerializedName("personal_details")
    @Expose
    private DemographicPersDetailsPayloadDTO personalDetails;

    @SerializedName("identity_document")
    @Expose
    private DemographicIdDocPayloadDTO idDocument;

    @SerializedName("insurances")
    @Expose
    private List<DemographicInsurancePayloadDTO> insurances = new ArrayList<DemographicInsurancePayloadDTO>();

    @SerializedName("updates")
    @Expose
    private List<DemographicUpdateDTO> updates = new ArrayList<>();

    /**
     *
     * @return
     * The address
     */
    public DemographicAddressPayloadDTO getAddress() {
        return address;
    }

    /**
     *
     * @param address
     * The address
     */
    public void setAddress(DemographicAddressPayloadDTO address) {
        this.address = address;
    }

    /**
     *
     * @return
     * The personalDetails
     */
    public DemographicPersDetailsPayloadDTO getPersonalDetails() {
        return personalDetails;
    }

    /**
     *
     * @param personalDetails
     * The personal_details
     */
    public void setPersonalDetails(DemographicPersDetailsPayloadDTO personalDetails) {
        this.personalDetails = personalDetails;
    }

    /**
     *
     * @return
     * The idDocument
     */
    public DemographicIdDocPayloadDTO getIdDocument() {
        return idDocument;
    }

    /**
     *
     * @param idDocument
     * The drivers_license
     */
    public void setIdDocument(DemographicIdDocPayloadDTO idDocument) {
        this.idDocument = idDocument;
    }

    /**
     *
     * @return
     * The insurances
     */
    public List<DemographicInsurancePayloadDTO> getInsurances() {
        return insurances;
    }

    /**
     *
     * @param insurances
     * The insurances
     */
    public void setInsurances(List<DemographicInsurancePayloadDTO> insurances) {
        this.insurances = insurances;
    }

    public List<DemographicUpdateDTO> getUpdates() {
        return updates;
    }

    public void setUpdates(List<DemographicUpdateDTO> updates) {
        this.updates = updates;
    }
}
