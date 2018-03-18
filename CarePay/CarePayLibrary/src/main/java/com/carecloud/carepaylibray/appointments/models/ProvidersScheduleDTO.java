
package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProvidersScheduleDTO implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("npi")
    @Expose
    private Object npi;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("appointments")
    @Expose
    private List<Object> appointments = new ArrayList<>();
    @SerializedName("specialty")
    @Expose
    private String specialty;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("resource")
    @Expose
    private AppointmentResourceDTO resource = new AppointmentResourceDTO();
    @SerializedName("reasons")
    @Expose
    private List<ProvidersReasonDTO> reasons = new ArrayList<>();
    @SerializedName("templates")
    @Expose
    private List<ProvidersTemplatesDTO> templates = new ArrayList<>();

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
     *     The npi
     */
    public Object getNpi() {
        return npi;
    }

    /**
     * 
     * @param npi
     *     The npi
     */
    public void setNpi(Object npi) {
        this.npi = npi;
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
     *     The appointments
     */
    public List<Object> getAppointments() {
        return appointments;
    }

    /**
     * 
     * @param appointments
     *     The appointments
     */
    public void setAppointments(List<Object> appointments) {
        this.appointments = appointments;
    }

    /**
     * 
     * @return
     *     The specialty
     */
    public String getSpecialty() {
        return specialty;
    }

    /**
     * 
     * @param specialty
     *     The specialty
     */
    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    /**
     * 
     * @return
     *     The phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 
     * @param phone
     *     The phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 
     * @return
     *     The resource
     */
    public AppointmentResourceDTO getResource() {
        return resource;
    }

    /**
     * 
     * @param resource
     *     The resource
     */
    public void setResource(AppointmentResourceDTO resource) {
        this.resource = resource;
    }

    /**
     * 
     * @return
     *     The reasons
     */
    public List<ProvidersReasonDTO> getReasons() {
        return reasons;
    }

    /**
     * 
     * @param reasons
     *     The reasons
     */
    public void setReasons(List<ProvidersReasonDTO> reasons) {
        this.reasons = reasons;
    }

    /**
     * 
     * @return
     *     The templates
     */
    public List<ProvidersTemplatesDTO> getTemplates() {
        return templates;
    }

    /**
     * 
     * @param templates
     *     The templates
     */
    public void setTemplates(List<ProvidersTemplatesDTO> templates) {
        this.templates = templates;
    }

}
