
package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for appointment resources item.
 */
public class AppointmentResourcesItemDTO {

    @SerializedName("appointment_confirmation")
    @Expose
    private String appointmentConfirmation;
    @SerializedName("business_entity_id")
    @Expose
    private String businessEntityId;
    @SerializedName("code")
    @Expose
    private Object code;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("created_by")
    @Expose
    private Integer createdBy;
    @SerializedName("description")
    @Expose
    private Object description;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("is_for_requests")
    @Expose
    private Boolean isForRequests;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("sort_code")
    @Expose
    private Integer sortCode;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("updated_by")
    @Expose
    private Integer updatedBy;
    @SerializedName("visit_reasons")
    @Expose
    private List<VisitTypeDTO> visitReasons = new ArrayList<>();
    @SerializedName("provider")
    @Expose
    private ProviderDTO provider = new ProviderDTO();

    /**
     * 
     * @return
     *     The appointmentConfirmation
     */
    public String getAppointmentConfirmation() {
        return appointmentConfirmation;
    }

    /**
     * 
     * @param appointmentConfirmation
     *     The appointment_confirmation
     */
    public void setAppointmentConfirmation(String appointmentConfirmation) {
        this.appointmentConfirmation = appointmentConfirmation;
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
     *     The code
     */
    public Object getCode() {
        return code;
    }

    /**
     * 
     * @param code
     *     The code
     */
    public void setCode(Object code) {
        this.code = code;
    }

    /**
     * 
     * @return
     *     The createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * 
     * @param createdAt
     *     The created_at
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 
     * @return
     *     The createdBy
     */
    public Integer getCreatedBy() {
        return createdBy;
    }

    /**
     * 
     * @param createdBy
     *     The created_by
     */
    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
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
     *     The isForRequests
     */
    public Boolean getIsForRequests() {
        return isForRequests;
    }

    /**
     * 
     * @param isForRequests
     *     The is_for_requests
     */
    public void setIsForRequests(Boolean isForRequests) {
        this.isForRequests = isForRequests;
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
     *     The sortCode
     */
    public Integer getSortCode() {
        return sortCode;
    }

    /**
     * 
     * @param sortCode
     *     The sort_code
     */
    public void setSortCode(Integer sortCode) {
        this.sortCode = sortCode;
    }

    /**
     * 
     * @return
     *     The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * 
     * @param status
     *     The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 
     * @return
     *     The updatedAt
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 
     * @param updatedAt
     *     The updated_at
     */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 
     * @return
     *     The updatedBy
     */
    public Integer getUpdatedBy() {
        return updatedBy;
    }

    /**
     * 
     * @param updatedBy
     *     The updated_by
     */
    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     *
     * @return visitReasons
     */
    public List<VisitTypeDTO> getVisitReasons() {
        return visitReasons;
    }

    /**
     *
     * @param visitReasons visitReasons
     */
    public void setVisitReasons(List<VisitTypeDTO> visitReasons) {
        this.visitReasons = visitReasons;
    }

    /**
     *
     * @return provider
     */
    public ProviderDTO getProvider() {
        return provider;
    }

    /**
     *
     * @param provider provider
     */
    public void setProvider(ProviderDTO provider) {
        this.provider = provider;
    }
}
