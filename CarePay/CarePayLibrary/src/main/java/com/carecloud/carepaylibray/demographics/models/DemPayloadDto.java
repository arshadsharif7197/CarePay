package com.carecloud.carepaylibray.demographics.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jahirul Bhuiyan on 9/19/2016.
 */
public class DemPayloadDto {

    @SerializedName("address")
    @Expose
    private DemAddressPayloadDto address;

    @SerializedName("personal_details")
    @Expose
    private DemPersDetailsPayloadDto personalDetails;

    @SerializedName("identity_document")
    @Expose
    private DemIdDocPayloadDto idDocument;

    @SerializedName("insurances")
    @Expose
    private List<DemInsurancePayloadPojo> insurances = new ArrayList<DemInsurancePayloadPojo>();

    @SerializedName("updates")
    @Expose
    private List<DemUpdateDto> updates = new ArrayList<>();

    /**
     *
     * @return
     * The address
     */
    public DemAddressPayloadDto getAddress() {
        return address;
    }

    /**
     *
     * @param address
     * The address
     */
    public void setAddress(DemAddressPayloadDto address) {
        this.address = address;
    }

    /**
     *
     * @return
     * The personalDetails
     */
    public DemPersDetailsPayloadDto getPersonalDetails() {
        return personalDetails;
    }

    /**
     *
     * @param personalDetails
     * The personal_details
     */
    public void setPersonalDetails(DemPersDetailsPayloadDto personalDetails) {
        this.personalDetails = personalDetails;
    }

    /**
     *
     * @return
     * The idDocument
     */
    public DemIdDocPayloadDto getIdDocument() {
        return idDocument;
    }

    /**
     *
     * @param idDocument
     * The drivers_license
     */
    public void setIdDocument(DemIdDocPayloadDto idDocument) {
        this.idDocument = idDocument;
    }

    /**
     *
     * @return
     * The insurances
     */
    public List<DemInsurancePayloadPojo> getInsurances() {
        return insurances;
    }

    /**
     *
     * @param insurances
     * The insurances
     */
    public void setInsurances(List<DemInsurancePayloadPojo> insurances) {
        this.insurances = insurances;
    }

    public List<DemUpdateDto> getUpdates() {
        return updates;
    }

    public void setUpdates(List<DemUpdateDto> updates) {
        this.updates = updates;
    }
}
