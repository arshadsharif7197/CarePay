package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 5/16/17
 */

public class DemographicsAddressSection {

    @SerializedName("properties")
    @Expose
    private Properties properties = new Properties();

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public class Properties {

        @SerializedName("phone")
        @Expose
        private DemographicsField phone = new DemographicsField();

        @SerializedName("zipcode")
        @Expose
        private DemographicsField zipcode = new DemographicsField();

        @SerializedName("address1")
        @Expose
        private DemographicsField address1 = new DemographicsField();

        @SerializedName("address2")
        @Expose
        private DemographicsField address2 = new DemographicsField();

        @SerializedName("city")
        @Expose
        private DemographicsField city = new DemographicsField();

        @SerializedName("state")
        @Expose
        private DemographicsField state = new DemographicsField();

        @SerializedName("country")
        @Expose
        private DemographicsField country = new DemographicsField();

        public DemographicsField getPhone() {
            return phone;
        }

        public void setPhone(DemographicsField phone) {
            this.phone = phone;
        }

        public DemographicsField getZipcode() {
            return zipcode;
        }

        public void setZipcode(DemographicsField zipcode) {
            this.zipcode = zipcode;
        }

        public DemographicsField getAddress1() {
            return address1;
        }

        public void setAddress1(DemographicsField address1) {
            this.address1 = address1;
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
