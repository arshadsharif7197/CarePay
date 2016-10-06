package com.carecloud.carepaylibray.demographics.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jahirul Bhuiyan on 9/19/2016.
 */
public class DemPayloadPojo {

    @SerializedName("address")
    @Expose
    private DemAddressPayloadPojo address;

    @SerializedName("personal_details")
    @Expose
    private DemPersDetailsPayloadPojo personalDetails;

    @SerializedName("identity_document")
    @Expose
    private DemIdDocPayloadPojo idDocument;

    @SerializedName("insurances")
    @Expose
    private List<DemInsurancePayloadPojo> insurances = new ArrayList<DemInsurancePayloadPojo>();

    @SerializedName("updates")
    @Expose
    private List<DemUpdatePojo> updates = new ArrayList<>();

    /**
     *
     * @return
     * The address
     */
    public DemAddressPayloadPojo getAddress() {
        return address;
    }

    /**
     *
     * @param address
     * The address
     */
    public void setAddress(DemAddressPayloadPojo address) {
        this.address = address;
    }

    /**
     *
     * @return
     * The personalDetails
     */
    public DemPersDetailsPayloadPojo getPersonalDetails() {
        return personalDetails;
    }

    /**
     *
     * @param personalDetails
     * The personal_details
     */
    public void setPersonalDetails(DemPersDetailsPayloadPojo personalDetails) {
        this.personalDetails = personalDetails;
    }

    /**
     *
     * @return
     * The idDocument
     */
    public DemIdDocPayloadPojo getIdDocument() {
        return idDocument;
    }

    /**
     *
     * @param idDocument
     * The drivers_license
     */
    public void setIdDocument(DemIdDocPayloadPojo idDocument) {
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

    public List<DemUpdatePojo> getUpdates() {
        return updates;
    }

    public void setUpdates(List<DemUpdatePojo> updates) {
        this.updates = updates;
    }
}
