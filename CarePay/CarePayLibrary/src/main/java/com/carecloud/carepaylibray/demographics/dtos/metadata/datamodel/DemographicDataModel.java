package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel;

import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityInsurancesDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 5/16/17
 */

public class DemographicDataModel {

    @SerializedName("demographic")
    @Expose
    private Demographic demographic = new Demographic();

    public Demographic getDemographic() {
        return demographic;
    }

    public void setDemographic(Demographic demographic) {
        this.demographic = demographic;
    }


    public class Demographic{

        @SerializedName("address")
        @Expose
        private DemographicsAddressSection address = new DemographicsAddressSection();

        @SerializedName("personal_details")
        @Expose
        private DemographicsPersonalSection personalDetails = new DemographicsPersonalSection();

        @SerializedName("insurances")
        @Expose
        private DemographicMetadataEntityInsurancesDTO insurances = new DemographicMetadataEntityInsurancesDTO();

        public DemographicsAddressSection getAddress() {
            return address;
        }

        public void setAddress(DemographicsAddressSection address) {
            this.address = address;
        }

        public DemographicsPersonalSection getPersonalDetails() {
            return personalDetails;
        }

        public void setPersonalDetails(DemographicsPersonalSection personalDetails) {
            this.personalDetails = personalDetails;
        }

        public DemographicMetadataEntityInsurancesDTO getInsurances() {
            return insurances;
        }

        public void setInsurances(DemographicMetadataEntityInsurancesDTO insurances) {
            this.insurances = insurances;
        }
    }
}
