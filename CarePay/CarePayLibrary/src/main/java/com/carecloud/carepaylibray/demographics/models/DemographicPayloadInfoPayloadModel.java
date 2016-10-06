package com.carecloud.carepaylibray.demographics.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Jahirul Bhuiyan on 9/26/2016.
 */
public class DemographicPayloadInfoPayloadModel {

    @SerializedName("address")
    @Expose
    private DemographicPayloadAddressModel address;

    @SerializedName("personal_details")
    @Expose
    private DemographicPayloadPersonalDetailsModel personalDetails;

    @SerializedName("identity_document")
    @Expose
    private DemographicPayloadIdDocumentModel idDocument;

    @SerializedName("insurances")
    @Expose
    private List<DemographicPayloadInsuranceModel> insurances = new ArrayList<DemographicPayloadInsuranceModel>();

    @SerializedName("updates")
    @Expose
    private List<DemographicUpdateModel> updates = new ArrayList<>();

    /**
     *
     * @return
     * The address
     */
    public DemographicPayloadAddressModel getAddress() {
        return address;
    }

    /**
     *
     * @param address
     * The address
     */
    public void setAddress(DemographicPayloadAddressModel address) {
        this.address = address;
    }

    /**
     *
     * @return
     * The personalDetails
     */
    public DemographicPayloadPersonalDetailsModel getPersonalDetails() {
        return personalDetails;
    }

    /**
     *
     * @param personalDetails
     * The personal_details
     */
    public void setPersonalDetails(DemographicPayloadPersonalDetailsModel personalDetails) {
        this.personalDetails = personalDetails;
    }

    /**
     *
     * @return
     * The idDocument
     */
    public DemographicPayloadIdDocumentModel getIdDocument() {
        return idDocument;
    }

    /**
     *
     * @param idDocument
     * The drivers_license
     */
    public void setIdDocument(DemographicPayloadIdDocumentModel idDocument) {
        this.idDocument = idDocument;
    }

    /**
     *
     * @return
     * The insurances
     */
    public List<DemographicPayloadInsuranceModel> getInsurances() {
        return insurances;
    }

    /**
     *
     * @param insurances
     * The insurances
     */
    public void setInsurances(List<DemographicPayloadInsuranceModel> insurances) {
        this.insurances = insurances;
    }

    /**
     *
     * @return
     * The updates
     */
    public List<DemographicUpdateModel> getUpdates() {
        return updates;
    }

    /**
     *
     * @param updates
     * The updates
     */
    public void setUpdates(List<DemographicUpdateModel> updates) {
        this.updates = updates;
    }
}
