package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 27/10/17.
 */

public class DemographicEmergencyContactSection {

    @Expose
    @SerializedName("display")
    private boolean display;

    @Expose
    @SerializedName("required")
    private boolean required;

    @Expose
    @SerializedName("label")
    private String label;

    @Expose
    @SerializedName("order")
    private int order;

    @Expose
    @SerializedName("properties")
    private DemographicEmergencyContactSection.Properties properties = new DemographicEmergencyContactSection
            .Properties();

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public DemographicEmergencyContactSection.Properties getProperties() {
        return properties;
    }

    public void setProperties(DemographicEmergencyContactSection.Properties properties) {
        this.properties = properties;
    }

    public class Properties {

        @SerializedName("first_name")
        @Expose
        private DemographicsField firstName = new DemographicsField();

        @SerializedName("last_name")
        @Expose
        private DemographicsField lastName = new DemographicsField();

        @SerializedName("email")
        @Expose
        private DemographicsField emailAddress = new DemographicsField();

        @SerializedName("date_of_birth")
        @Expose
        private DemographicsField dateOfBirth = new DemographicsField();

        @SerializedName("gender")
        @Expose
        private DemographicsField gender = new DemographicsField();

        @SerializedName("relationship_type")
        @Expose
        private DemographicsField relationshipType = new DemographicsField();

        @SerializedName("address")
        @Expose
        private DemographicsAddressSection address = new DemographicsAddressSection();


        public DemographicsField getFirstName() {
            return firstName;
        }

        public void setFirstName(DemographicsField firstName) {
            this.firstName = firstName;
        }

        public DemographicsField getLastName() {
            return lastName;
        }

        public void setLastName(DemographicsField lastName) {
            this.lastName = lastName;
        }

        public DemographicsField getEmailAddress() {
            return emailAddress;
        }

        public void setEmailAddress(DemographicsField emailAddress) {
            this.emailAddress = emailAddress;
        }

        public DemographicsField getDateOfBirth() {
            return dateOfBirth;
        }

        public void setDateOfBirth(DemographicsField dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
        }

        public DemographicsField getGender() {
            return gender;
        }

        public void setGender(DemographicsField gender) {
            this.gender = gender;
        }

        public DemographicsField getRelationshipType() {
            return relationshipType;
        }

        public void setRelationshipType(DemographicsField relationshipType) {
            this.relationshipType = relationshipType;
        }

        public DemographicsAddressSection getAddress() {
            return address;
        }

        public void setAddress(DemographicsAddressSection address) {
            this.address = address;
        }
    }
}
