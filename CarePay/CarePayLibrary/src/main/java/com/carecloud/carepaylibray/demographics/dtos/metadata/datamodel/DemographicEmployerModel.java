package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 11/4/17
 */

public class DemographicEmployerModel {

    @SerializedName("properties")
    private Properties properties;

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public class Properties {

        @SerializedName("name")
        private DemographicsField employerName;

        @SerializedName("address")
        private Address address;

        public DemographicsField getEmployerName() {
            return employerName;
        }

        public void setEmployerName(DemographicsField employerName) {
            this.employerName = employerName;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }
    }

    public class Address {

        @SerializedName("properties")
        private Properties properties;

        public Properties getProperties() {
            return properties;
        }

        public void setProperties(Properties properties) {
            this.properties = properties;
        }

        public class Properties {

            @SerializedName("phone_number")
            private DemographicsField phoneNumber;

            @SerializedName("zipcode")
            private DemographicsField zipCode;

            @SerializedName("address1")
            private DemographicsField address;

            @SerializedName("address2")
            private DemographicsField address2;

            @SerializedName("city")
            private DemographicsField city;

            @SerializedName("state")
            private DemographicsField state;

            @SerializedName("country")
            private DemographicsField country;

            public DemographicsField getPhoneNumber() {
                return phoneNumber;
            }

            public void setPhoneNumber(DemographicsField phoneNumber) {
                this.phoneNumber = phoneNumber;
            }

            public DemographicsField getZipCode() {
                return zipCode;
            }

            public void setZipCode(DemographicsField zipCode) {
                this.zipCode = zipCode;
            }

            public DemographicsField getAddress() {
                return address;
            }

            public void setAddress(DemographicsField address) {
                this.address = address;
            }

            public DemographicsField getAddress2() {
                return address2;
            }

            public void setAddress2(DemographicsField address2) {
                this.address2 = address2;
            }

            public DemographicsField getCity() {
                return city;
            }

            public void setCity(DemographicsField city) {
                this.city = city;
            }

            public DemographicsField getState() {
                return state;
            }

            public void setState(DemographicsField state) {
                this.state = state;
            }

            public DemographicsField getCountry() {
                return country;
            }

            public void setCountry(DemographicsField country) {
                this.country = country;
            }
        }
    }
}
