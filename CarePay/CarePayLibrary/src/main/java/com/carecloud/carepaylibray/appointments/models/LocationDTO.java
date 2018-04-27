
package com.carecloud.carepaylibray.appointments.models;

import com.carecloud.carepay.service.library.dtos.AvailableLocationDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for appointment locations.
 */
public class LocationDTO extends AvailableLocationDTO {

    @Expose(serialize = false)
    private boolean error = false;

    @SerializedName("address")
    @Expose
    private AppointmentAddressDTO address = new AppointmentAddressDTO();
    @SerializedName("phones")
    @Expose
    private List<PhoneDTO> phoneDTOs = new ArrayList<>();


    /**
     * @return The address
     */
    public AppointmentAddressDTO getAddress() {
        return address;
    }

    /**
     * @param address The address
     */
    public void setAddress(AppointmentAddressDTO address) {
        this.address = address;
    }

    public boolean hasError() {
        return error;
    }

    public void setError(boolean hasError) {
        this.error = hasError;
    }

    public List<PhoneDTO> getPhoneDTOs() {
        return phoneDTOs;
    }

    public void setPhoneDTOs(List<PhoneDTO> phoneDTOs) {
        this.phoneDTOs = phoneDTOs;
    }

    public class PhoneDTO {

        @SerializedName("phone_number")
        private String phoneNumber;

        @SerializedName("phone_type")
        private String phoneType;

        @SerializedName("phone_ext")
        private String extension;

        @SerializedName("is_primary")
        private boolean primary;

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

        public String getExtension() {
            return extension;
        }

        public void setExtension(String extension) {
            this.extension = extension;
        }

        public boolean isPrimary() {
            return primary;
        }

        public void setPrimary(boolean primary) {
            this.primary = primary;
        }
    }
}
