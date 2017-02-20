package com.carecloud.carepay.practice.library.checkin.dtos;

import com.carecloud.carepaylibray.payments.models.LocationDTO;
import com.carecloud.carepaylibray.payments.models.ProviderDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/27/2016.
 */

public class AppointmentPayloadDTO {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("appointment_status")
    @Expose
    private AppointmentStatusDTO appointmentStatus = new AppointmentStatusDTO();
    @SerializedName("patient")
    @Expose
    private PatientDTO patient = new PatientDTO();
    @SerializedName("location")
    @Expose
    private LocationDTO location = new LocationDTO();
    @SerializedName("cancellation_details")
    @Expose
    private CancellationDetailsDTO cancellationDetails = new CancellationDetailsDTO();
    @SerializedName("chief_complaint")
    @Expose
    private Object chiefComplaint;
    @SerializedName("task_id")
    @Expose
    private Object taskId;
    @SerializedName("updated_by_application")
    @Expose
    private Object updatedByApplication;
    @SerializedName("appointment_cancellation_reason_id")
    @Expose
    private Object appointmentCancellationReasonId;
    @SerializedName("arrived_at")
    @Expose
    private Object arrivedAt;
    @SerializedName("business_entity_id")
    @Expose
    private Integer businessEntityId;
    @SerializedName("comments")
    @Expose
    private Object comments;
    @SerializedName("confirmation_details")
    @Expose
    private Object confirmationDetails;
    @SerializedName("departed_at")
    @Expose
    private Object departedAt;
    @SerializedName("exam_room_id")
    @Expose
    private Object examRoomId;
    @SerializedName("is_confirmed")
    @Expose
    private Boolean isConfirmed;
    @SerializedName("is_force_overbook")
    @Expose
    private Boolean isForceOverbook;
    @SerializedName("patient_contacted")
    @Expose
    private Object patientContacted;
    @SerializedName("recurrence_id")
    @Expose
    private Object recurrenceId;
    @SerializedName("recurrence_index")
    @Expose
    private Object recurrenceIndex;
    @SerializedName("referring_physician_npi")
    @Expose
    private Object referringPhysicianNpi;
    @SerializedName("visit_reason_id")
    @Expose
    private Integer visitReasonId;
    @SerializedName("resource_id")
    @Expose
    private Integer resourceId;
    @SerializedName("provider_id")
    @Expose
    private String providerId;
    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("end_time")
    @Expose
    private String endTime;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("encounter_id")
    @Expose
    private Object encounterId;
    @SerializedName("provider")
    @Expose
    private ProviderDTO provider = new ProviderDTO();
    @SerializedName("resource")
    @Expose
    private ResourceDTO resource = new ResourceDTO();

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The appointmentStatus
     */
    public AppointmentStatusDTO getAppointmentStatus() {
        return appointmentStatus;
    }

    /**
     *
     * @param appointmentStatus
     * The appointment_status
     */
    public void setAppointmentStatus(AppointmentStatusDTO appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    /**
     *
     * @return
     * The patient
     */
    public PatientDTO getPatient() {
        return patient;
    }

    /**
     *
     * @param patient
     * The patient
     */
    public void setPatient(PatientDTO patient) {
        this.patient = patient;
    }

    /**
     *
     * @return
     * The location
     */
    public LocationDTO getLocation() {
        return location;
    }

    /**
     *
     * @param location
     * The location
     */
    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    /**
     *
     * @return
     * The cancellationDetails
     */
    public CancellationDetailsDTO getCancellationDetails() {
        return cancellationDetails;
    }

    /**
     *
     * @param cancellationDetails
     * The cancellation_details
     */
    public void setCancellationDetails(CancellationDetailsDTO cancellationDetails) {
        this.cancellationDetails = cancellationDetails;
    }

    /**
     *
     * @return
     * The chiefComplaint
     */
    public Object getChiefComplaint() {
        return chiefComplaint;
    }

    /**
     *
     * @param chiefComplaint
     * The chief_complaint
     */
    public void setChiefComplaint(Object chiefComplaint) {
        this.chiefComplaint = chiefComplaint;
    }

    /**
     *
     * @return
     * The taskId
     */
    public Object getTaskId() {
        return taskId;
    }

    /**
     *
     * @param taskId
     * The task_id
     */
    public void setTaskId(Object taskId) {
        this.taskId = taskId;
    }

    /**
     *
     * @return
     * The updatedByApplication
     */
    public Object getUpdatedByApplication() {
        return updatedByApplication;
    }

    /**
     *
     * @param updatedByApplication
     * The updated_by_application
     */
    public void setUpdatedByApplication(Object updatedByApplication) {
        this.updatedByApplication = updatedByApplication;
    }

    /**
     *
     * @return
     * The appointmentCancellationReasonId
     */
    public Object getAppointmentCancellationReasonId() {
        return appointmentCancellationReasonId;
    }

    /**
     *
     * @param appointmentCancellationReasonId
     * The appointment_cancellation_reason_id
     */
    public void setAppointmentCancellationReasonId(Object appointmentCancellationReasonId) {
        this.appointmentCancellationReasonId = appointmentCancellationReasonId;
    }

    /**
     *
     * @return
     * The arrivedAt
     */
    public Object getArrivedAt() {
        return arrivedAt;
    }

    /**
     *
     * @param arrivedAt
     * The arrived_at
     */
    public void setArrivedAt(Object arrivedAt) {
        this.arrivedAt = arrivedAt;
    }

    /**
     *
     * @return
     * The businessEntityId
     */
    public Integer getBusinessEntityId() {
        return businessEntityId;
    }

    /**
     *
     * @param businessEntityId
     * The business_entity_id
     */
    public void setBusinessEntityId(Integer businessEntityId) {
        this.businessEntityId = businessEntityId;
    }

    /**
     *
     * @return
     * The comments
     */
    public Object getComments() {
        return comments;
    }

    /**
     *
     * @param comments
     * The comments
     */
    public void setComments(Object comments) {
        this.comments = comments;
    }

    /**
     *
     * @return
     * The confirmationDetails
     */
    public Object getConfirmationDetails() {
        return confirmationDetails;
    }

    /**
     *
     * @param confirmationDetails
     * The confirmation_details
     */
    public void setConfirmationDetails(Object confirmationDetails) {
        this.confirmationDetails = confirmationDetails;
    }

    /**
     *
     * @return
     * The departedAt
     */
    public Object getDepartedAt() {
        return departedAt;
    }

    /**
     *
     * @param departedAt
     * The departed_at
     */
    public void setDepartedAt(Object departedAt) {
        this.departedAt = departedAt;
    }

    /**
     *
     * @return
     * The examRoomId
     */
    public Object getExamRoomId() {
        return examRoomId;
    }

    /**
     *
     * @param examRoomId
     * The exam_room_id
     */
    public void setExamRoomId(Object examRoomId) {
        this.examRoomId = examRoomId;
    }

    /**
     *
     * @return
     * The isConfirmed
     */
    public Boolean getIsConfirmed() {
        return isConfirmed;
    }

    /**
     *
     * @param isConfirmed
     * The is_confirmed
     */
    public void setIsConfirmed(Boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    /**
     *
     * @return
     * The isForceOverbook
     */
    public Boolean getIsForceOverbook() {
        return isForceOverbook;
    }

    /**
     *
     * @param isForceOverbook
     * The is_force_overbook
     */
    public void setIsForceOverbook(Boolean isForceOverbook) {
        this.isForceOverbook = isForceOverbook;
    }

    /**
     *
     * @return
     * The patientContacted
     */
    public Object getPatientContacted() {
        return patientContacted;
    }

    /**
     *
     * @param patientContacted
     * The patient_contacted
     */
    public void setPatientContacted(Object patientContacted) {
        this.patientContacted = patientContacted;
    }

    /**
     *
     * @return
     * The recurrenceId
     */
    public Object getRecurrenceId() {
        return recurrenceId;
    }

    /**
     *
     * @param recurrenceId
     * The recurrence_id
     */
    public void setRecurrenceId(Object recurrenceId) {
        this.recurrenceId = recurrenceId;
    }

    /**
     *
     * @return
     * The recurrenceIndex
     */
    public Object getRecurrenceIndex() {
        return recurrenceIndex;
    }

    /**
     *
     * @param recurrenceIndex
     * The recurrence_index
     */
    public void setRecurrenceIndex(Object recurrenceIndex) {
        this.recurrenceIndex = recurrenceIndex;
    }

    /**
     *
     * @return
     * The referringPhysicianNpi
     */
    public Object getReferringPhysicianNpi() {
        return referringPhysicianNpi;
    }

    /**
     *
     * @param referringPhysicianNpi
     * The referring_physician_npi
     */
    public void setReferringPhysicianNpi(Object referringPhysicianNpi) {
        this.referringPhysicianNpi = referringPhysicianNpi;
    }

    /**
     *
     * @return
     * The visitReasonId
     */
    public Integer getVisitReasonId() {
        return visitReasonId;
    }

    /**
     *
     * @param visitReasonId
     * The visit_reason_id
     */
    public void setVisitReasonId(Integer visitReasonId) {
        this.visitReasonId = visitReasonId;
    }

    /**
     *
     * @return
     * The resourceId
     */
    public Integer getResourceId() {
        return resourceId;
    }

    /**
     *
     * @param resourceId
     * The resource_id
     */
    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    /**
     *
     * @return
     * The providerId
     */
    public String getProviderId() {
        return providerId;
    }

    /**
     *
     * @param providerId
     * The provider_id
     */
    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    /**
     *
     * @return
     * The startTime
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     *
     * @param startTime
     * The start_time
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     *
     * @return
     * The endTime
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     *
     * @param endTime
     * The end_time
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     *
     * @return
     * The createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     *
     * @param createdAt
     * The created_at
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     *
     * @return
     * The updatedAt
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     *
     * @param updatedAt
     * The updated_at
     */
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     *
     * @return
     * The encounterId
     */
    public Object getEncounterId() {
        return encounterId;
    }

    /**
     *
     * @param encounterId
     * The encounter_id
     */
    public void setEncounterId(Object encounterId) {
        this.encounterId = encounterId;
    }

    /**
     *
     * @return
     * The provider
     */
    public ProviderDTO getProvider() {
        return provider;
    }

    /**
     *
     * @param provider
     * The provider
     */
    public void setProvider(ProviderDTO provider) {
        this.provider = provider;
    }

    /**
     *
     * @return
     * The resource
     */
    public ResourceDTO getResource() {
        return resource;
    }

    /**
     *
     * @param resource
     * The resource
     */
    public void setResource(ResourceDTO resource) {
        this.resource = resource;
    }

    @Override
    public boolean equals(Object payloadObj) {
        if (this == payloadObj) {return true;}
        if (!(payloadObj instanceof AppointmentPayloadDTO)) {return false;}

        AppointmentPayloadDTO appointmentPayloadDTO = (AppointmentPayloadDTO) payloadObj;

        return getId().equals(appointmentPayloadDTO.getId());

    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
