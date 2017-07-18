package com.carecloud.carepay.mini.models.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by lmenendez on 6/24/17
 */

public class LocationsDTO {

//    @SerializedName("id")
//    private Integer id;

    @SerializedName("name")
    private String name;

    @SerializedName("is_visible_appointment_scheduler")
    private Boolean isVisibleAppointmentScheduler;

//    @SerializedName("guid")
    @SerializedName("id")
    private String guid;

    @SerializedName("address")
    private Address address;

    @SerializedName("phones")
    private List<Phone> phones = null;

//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsVisibleAppointmentScheduler() {
        return isVisibleAppointmentScheduler;
    }

    public void setIsVisibleAppointmentScheduler(Boolean isVisibleAppointmentScheduler) {
        this.isVisibleAppointmentScheduler = isVisibleAppointmentScheduler;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }


    @Override
    public boolean equals(Object object){
        try{
            LocationsDTO compare = (LocationsDTO) object;
            return this.getGuid().equals(compare.getGuid());
        }catch (ClassCastException cce){
            return false;
        }
    }

    @Override
    public int hashCode(){
        if(getGuid() != null){
            return getGuid().hashCode();
        }
        return super.hashCode();
    }


    public class Address {

        @SerializedName("line1")
        private String line1;

        @SerializedName("city")
        private String city;

        @SerializedName("zip_code")
        private String zipCode;

        @SerializedName("latitude")
        private Double latitude;

        @SerializedName("longitude")
        private Double longitude;

        @SerializedName("state_name")
        private String stateName;

        @SerializedName("country_name")
        private String countryName;

        public String getLine1() {
            return line1;
        }

        public void setLine1(String line1) {
            this.line1 = line1;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getZipCode() {
            return zipCode;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public String getStateName() {
            return stateName;
        }

        public void setStateName(String stateName) {
            this.stateName = stateName;
        }

        public String getCountryName() {
            return countryName;
        }

        public void setCountryName(String countryName) {
            this.countryName = countryName;
        }

    }

    private class Phone {

        @SerializedName("phone_number")
        private String phoneNumber;

        @SerializedName("phone_type")
        private String phoneType;

        @SerializedName("is_primary")
        private Boolean isPrimary;

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getPhoneType() {
            return phoneType;
        }

        public void setPhoneType(String phoneType) {
            this.phoneType = phoneType;
        }

        public Boolean getIsPrimary() {
            return isPrimary;
        }

        public void setIsPrimary(Boolean isPrimary) {
            this.isPrimary = isPrimary;
        }

    }
}
