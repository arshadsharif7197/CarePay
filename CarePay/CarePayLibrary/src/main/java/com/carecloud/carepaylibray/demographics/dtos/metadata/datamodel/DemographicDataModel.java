package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel;

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


    public class Demographic {

        @SerializedName("address")
        @Expose
        private DemographicsAddressSection address = new DemographicsAddressSection();

        @SerializedName("personal_details")
        @Expose
        private DemographicsPersonalSection personalDetails = new DemographicsPersonalSection();

        @SerializedName("insurances")
        @Expose
        private DemographicInsuranceSection insurances = new DemographicInsuranceSection();

        @SerializedName("emergency_contact")
        @Expose
        private DemographicEmergencyContactSection emergencyContact = new DemographicEmergencyContactSection();

        @SerializedName("employment_info")
        @Expose
        private DemographicEmploymentInfoSection employmentInfo = new DemographicEmploymentInfoSection();

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

        public DemographicInsuranceSection getInsurances() {
            return insurances;
        }

        public void setInsurances(DemographicInsuranceSection insurances) {
            this.insurances = insurances;
        }

        public DemographicEmergencyContactSection getEmergencyContact() {
            return emergencyContact;
        }

        public void setEmergencyContact(DemographicEmergencyContactSection emergencyContact) {
            this.emergencyContact = emergencyContact;
        }

        public DemographicEmploymentInfoSection getEmploymentInfo() {
            return employmentInfo;
        }

        public void setEmploymentInfo(DemographicEmploymentInfoSection employmentInfo) {
            this.employmentInfo = employmentInfo;
        }
    }
}
