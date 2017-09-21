package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rahul on 11/30/16
 */

public class CreditCardBillingInformationDTO {

        @SerializedName("same_as_patient")
        @Expose
        private Boolean sameAsPatient;
        @SerializedName("line1")
        @Expose
        private String line1;
        @SerializedName("line2")
        @Expose
        private String line2;
        @SerializedName("city")
        @Expose
        private String city;
        @SerializedName("state")
        @Expose
        private String state;
        @SerializedName("zip")
        @Expose
        private String zip;
        @SerializedName("country")
        @Expose
        private String country;

        /**
         *
         * @return
         * The sameAsPatient
         */
        public Boolean getSameAsPatient() {
            return sameAsPatient;
        }

        /**
         *
         * @param sameAsPatient
         * The same_as_patient
         */
        public void setSameAsPatient(Boolean sameAsPatient) {
            this.sameAsPatient = sameAsPatient;
        }

        /**
         *
         * @return
         * The line1
         */
        public String getLine1() {
            return line1;
        }

        /**
         *
         * @param line1
         * The line1
         */
        public void setLine1(String line1) {
            this.line1 = line1;
        }

        /**
         *
         * @return
         * The line2
         */
        public String getLine2() {
            return line2;
        }

        /**
         *
         * @param line2
         * The line2
         */
        public void setLine2(String line2) {
            this.line2 = line2;
        }

        /**
         *
         * @return
         * The city
         */
        public String getCity() {
            return city;
        }

        /**
         *
         * @param city
         * The city
         */
        public void setCity(String city) {
            this.city = city;
        }

        /**
         *
         * @return
         * The state
         */
        public String getState() {
            return state;
        }

        /**
         *
         * @param state
         * The state
         */
        public void setState(String state) {
            this.state = state;
        }

        /**
         *
         * @return
         * The zip
         */
        public String getZip() {
            return zip;
        }

        /**
         *
         * @param zip
         * The zip
         */
        public void setZip(String zip) {
            this.zip = zip;
        }

        /**
         *
         * @return
         * The country
         */
        public String getCountry() {
            return country;
        }

        /**
         *
         * @param country
         * The country
         */
        public void setCountry(String country) {
            this.country = country;
        }
}
