
package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for appointment locations.
 */
public class LocationDTO {
    @Expose(serialize = false)
    private boolean error = false;


    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("guid")
    @Expose
    private String guid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("is_visible_appointment_scheduler")
    @Expose
    private Boolean isVisibleAppointmentScheduler;
    @SerializedName("address")
    @Expose
    private AppointmentAddressDTO address = new AppointmentAddressDTO();
    @SerializedName("phones")
    @Expose
    private List<PhoneDTO> phoneDTOs = new ArrayList<>();

    /**
     * 
     * @return
     *     The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The isVisibleAppointmentScheduler
     */
    public Boolean getIsVisibleAppointmentScheduler() {
        return isVisibleAppointmentScheduler;
    }

    /**
     * 
     * @param isVisibleAppointmentScheduler
     *     The is_visible_appointment_scheduler
     */
    public void setIsVisibleAppointmentScheduler(Boolean isVisibleAppointmentScheduler) {
        this.isVisibleAppointmentScheduler = isVisibleAppointmentScheduler;
    }

    /**
     * 
     * @return
     *     The address
     */
    public AppointmentAddressDTO getAddress() {
        return address;
    }

    /**
     * 
     * @param address
     *     The address
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

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
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
