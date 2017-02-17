package com.carecloud.carepaylibray.demographics.dtos.payload;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jahirul Bhuiyan on 9/26/2016.
 * Model for Id doc payload.
 */
public class DemographicIdDocPayloadDTO {

    @SerializedName("identity_document_photos")
    @Expose
    private List<DemographicIdDocPhotoDTO> idDocPhothos = new ArrayList<>();

    @SerializedName("identity_document_number")
    @Expose
    private String idNumber;

    @SerializedName("identity_document_state")
    @Expose
    private String idState;

    @SerializedName("identity_document_country")
    @Expose
    private String idCountry;

    @SerializedName("identity_document_type")
    @Expose
    private String idType;

    public List<DemographicIdDocPhotoDTO> getIdDocPhothos() {
        return idDocPhothos;
    }

    public void setIdDocPhothos(List<DemographicIdDocPhotoDTO> idDocPhothos) {
        this.idDocPhothos = idDocPhothos;
    }

    /**
     * @return The idNumber
     */
    public String getIdNumber() {
        return idNumber;
    }

    /**
     * @param idNumber The license_number
     */
    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    /**
     * @return The idState
     */
    public String getIdState() {
        return idState;
    }

    /**
     * @param idState The license_state
     */
    public void setIdState(String idState) {
        this.idState = idState;
    }

    public String getIdCountry() {
        return idCountry;
    }

    public void setIdCountry(String idCountry) {
        this.idCountry = idCountry;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }
}
