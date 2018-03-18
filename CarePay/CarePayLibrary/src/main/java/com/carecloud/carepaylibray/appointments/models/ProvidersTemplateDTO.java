
package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ProvidersTemplateDTO {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("locations")
    @Expose
    private List<ProvidersTemplateLocationDTO> locations = new ArrayList<>();
    @SerializedName("business_entity_id")
    @Expose
    private String businessEntityId;
    @SerializedName("description")
    @Expose
    private Object description;
    @SerializedName("effective_from")
    @Expose
    private String effectiveFrom;
    @SerializedName("effective_to")
    @Expose
    private String effectiveTo;
    @SerializedName("max_appointments_allowed")
    @Expose
    private Integer maxAppointmentsAllowed;
    @SerializedName("start_at")
    @Expose
    private String startAt;
    @SerializedName("end_at")
    @Expose
    private String endAt;
    @SerializedName("timezone_name")
    @Expose
    private String timezoneName;
    @SerializedName("occurrences")
    @Expose
    private List<ProvidersOccurrenceDTO> occurrences = new ArrayList<>();

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
     *     The locations
     */
    public List<ProvidersTemplateLocationDTO> getLocations() {
        return locations;
    }

    /**
     * 
     * @param locations
     *     The locations
     */
    public void setLocations(List<ProvidersTemplateLocationDTO> locations) {
        this.locations = locations;
    }

    /**
     * 
     * @return
     *     The businessEntityId
     */
    public String getBusinessEntityId() {
        return businessEntityId;
    }

    /**
     * 
     * @param businessEntityId
     *     The business_entity_id
     */
    public void setBusinessEntityId(String businessEntityId) {
        this.businessEntityId = businessEntityId;
    }

    /**
     * 
     * @return
     *     The description
     */
    public Object getDescription() {
        return description;
    }

    /**
     * 
     * @param description
     *     The description
     */
    public void setDescription(Object description) {
        this.description = description;
    }

    /**
     * 
     * @return
     *     The effectiveFrom
     */
    public String getEffectiveFrom() {
        return effectiveFrom;
    }

    /**
     * 
     * @param effectiveFrom
     *     The effective_from
     */
    public void setEffectiveFrom(String effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    /**
     * 
     * @return
     *     The effectiveTo
     */
    public String getEffectiveTo() {
        return effectiveTo;
    }

    /**
     * 
     * @param effectiveTo
     *     The effective_to
     */
    public void setEffectiveTo(String effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    /**
     * 
     * @return
     *     The maxAppointmentsAllowed
     */
    public Integer getMaxAppointmentsAllowed() {
        return maxAppointmentsAllowed;
    }

    /**
     * 
     * @param maxAppointmentsAllowed
     *     The max_appointments_allowed
     */
    public void setMaxAppointmentsAllowed(Integer maxAppointmentsAllowed) {
        this.maxAppointmentsAllowed = maxAppointmentsAllowed;
    }

    /**
     * 
     * @return
     *     The startAt
     */
    public String getStartAt() {
        return startAt;
    }

    /**
     * 
     * @param startAt
     *     The start_at
     */
    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    /**
     * 
     * @return
     *     The endAt
     */
    public String getEndAt() {
        return endAt;
    }

    /**
     * 
     * @param endAt
     *     The end_at
     */
    public void setEndAt(String endAt) {
        this.endAt = endAt;
    }

    /**
     * 
     * @return
     *     The timezoneName
     */
    public String getTimezoneName() {
        return timezoneName;
    }

    /**
     * 
     * @param timezoneName
     *     The timezone_name
     */
    public void setTimezoneName(String timezoneName) {
        this.timezoneName = timezoneName;
    }

    /**
     * 
     * @return
     *     The occurrences
     */
    public List<ProvidersOccurrenceDTO> getOccurrences() {
        return occurrences;
    }

    /**
     * 
     * @param occurrences
     *     The occurrences
     */
    public void setOccurrences(List<ProvidersOccurrenceDTO> occurrences) {
        this.occurrences = occurrences;
    }

}
