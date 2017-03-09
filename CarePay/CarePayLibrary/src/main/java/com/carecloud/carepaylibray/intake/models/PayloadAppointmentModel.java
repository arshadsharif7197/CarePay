
package com.carecloud.carepaylibray.intake.models;


import com.carecloud.carepaylibray.base.models.PatientModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PayloadAppointmentModel {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("appointment_status")
    @Expose
    private AppointmentStatusModel appointmentStatus;
    @SerializedName("patient")
    @Expose
    private PatientModel patient;
    @SerializedName("location")
    @Expose
    private LocationModel location;
    @SerializedName("cancellation_details")
    @Expose
    private CancellationDetailsModel cancellationDetails;
    @SerializedName("chief_complaint")
    @Expose
    private Object chiefComplaint;
    @SerializedName("task_id")
    @Expose
    private Object taskId;
    @SerializedName("updated_by_application")
    @Expose
    private String updatedByApplication;
    @SerializedName("appointment_cancellation_reason_id")
    @Expose
    private Object appointmentCancellationReasonId;
    @SerializedName("arrived_at")
    @Expose
    private String arrivedAt;
    @SerializedName("business_entity_id")
    @Expose
    private String businessEntityId;
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
    private Integer providerId;
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
    private Integer encounterId;
    @SerializedName("admit_diagnosis_id")
    @Expose
    private Object admitDiagnosisId;
    @SerializedName("note_set_id")
    @Expose
    private Integer noteSetId;
    @SerializedName("document_set_id")
    @Expose
    private Integer documentSetId;
    @SerializedName("contrast")
    @Expose
    private Object contrast;
    @SerializedName("laterality")
    @Expose
    private Object laterality;
    @SerializedName("created_by_application")
    @Expose
    private Object createdByApplication;
    @SerializedName("patient_instructions")
    @Expose
    private Object patientInstructions;
    @SerializedName("preferred_confirmation_method")
    @Expose
    private String preferredConfirmationMethod;
    @SerializedName("provider")
    @Expose
    private ProviderModel provider = new ProviderModel();

    /**
     * 
     * @return
     *     The id
     */
    public String getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The appointmentStatus
     */
    public AppointmentStatusModel getAppointmentStatus() {
        return appointmentStatus;
    }

    /**
     * 
     * @param appointmentStatus
     *     The appointment_status
     */
    public void setAppointmentStatus(AppointmentStatusModel appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    /**
     * 
     * @return
     *     The patient
     */
    public PatientModel getPatient() {
        return patient;
    }

    /**
     * 
     * @param patient
     *     The patient
     */
    public void setPatient(PatientModel patient) {
        this.patient = patient;
    }

    /**
     * 
     * @return
     *     The location
     */
    public LocationModel getLocation() {
        return location;
    }

    /**
     * 
     * @param location
     *     The location
     */
    public void setLocation(LocationModel location) {
        this.location = location;
    }

    /**
     * 
     * @return
     *     The cancellationDetails
     */
    public CancellationDetailsModel getCancellationDetails() {
        return cancellationDetails;
    }

    /**
     * 
     * @param cancellationDetails
     *     The cancellation_details
     */
    public void setCancellationDetails(CancellationDetailsModel cancellationDetails) {
        this.cancellationDetails = cancellationDetails;
    }

    /**
     * 
     * @return
     *     The chiefComplaint
     */
    public Object getChiefComplaint() {
        return chiefComplaint;
    }

    /**
     * 
     * @param chiefComplaint
     *     The chief_complaint
     */
    public void setChiefComplaint(Object chiefComplaint) {
        this.chiefComplaint = chiefComplaint;
    }

    /**
     * 
     * @return
     *     The taskId
     */
    public Object getTaskId() {
        return taskId;
    }

    /**
     * 
     * @param taskId
     *     The task_id
     */
    public void setTaskId(Object taskId) {
        this.taskId = taskId;
    }

    /**
     * 
     * @return
     *     The updatedByApplication
     */
    public String getUpdatedByApplication() {
        return updatedByApplication;
    }

    /**
     * 
     * @param updatedByApplication
     *     The updated_by_application
     */
    public void setUpdatedByApplication(String updatedByApplication) {
        this.updatedByApplication = updatedByApplication;
    }

    /**
     * 
     * @return
     *     The appointmentCancellationReasonId
     */
    public Object getAppointmentCancellationReasonId() {
        return appointmentCancellationReasonId;
    }

    /**
     * 
     * @param appointmentCancellationReasonId
     *     The appointment_cancellation_reason_id
     */
    public void setAppointmentCancellationReasonId(Object appointmentCancellationReasonId) {
        this.appointmentCancellationReasonId = appointmentCancellationReasonId;
    }

    /**
     * 
     * @return
     *     The arrivedAt
     */
    public String getArrivedAt() {
        return arrivedAt;
    }

    /**
     * 
     * @param arrivedAt
     *     The arrived_at
     */
    public void setArrivedAt(String arrivedAt) {
        this.arrivedAt = arrivedAt;
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
     *     The comments
     */
    public Object getComments() {
        return comments;
    }

    /**
     * 
     * @param comments
     *     The comments
     */
    public void setComments(Object comments) {
        this.comments = comments;
    }

    /**
     * 
     * @return
     *     The confirmationDetails
     */
    public Object getConfirmationDetails() {
        return confirmationDetails;
    }

    /**
     * 
     * @param confirmationDetails
     *     The confirmation_details
     */
    public void setConfirmationDetails(Object confirmationDetails) {
        this.confirmationDetails = confirmationDetails;
    }

    /**
     * 
     * @return
     *     The departedAt
     */
    public Object getDepartedAt() {
        return departedAt;
    }

    /**
     * 
     * @param departedAt
     *     The departed_at
     */
    public void setDepartedAt(Object departedAt) {
        this.departedAt = departedAt;
    }

    /**
     * 
     * @return
     *     The examRoomId
     */
    public Object getExamRoomId() {
        return examRoomId;
    }

    /**
     * 
     * @param examRoomId
     *     The exam_room_id
     */
    public void setExamRoomId(Object examRoomId) {
        this.examRoomId = examRoomId;
    }

    /**
     * 
     * @return
     *     The isConfirmed
     */
    public Boolean getIsConfirmed() {
        return isConfirmed;
    }

    /**
     * 
     * @param isConfirmed
     *     The is_confirmed
     */
    public void setIsConfirmed(Boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    /**
     * 
     * @return
     *     The isForceOverbook
     */
    public Boolean getIsForceOverbook() {
        return isForceOverbook;
    }

    /**
     * 
     * @param isForceOverbook
     *     The is_force_overbook
     */
    public void setIsForceOverbook(Boolean isForceOverbook) {
        this.isForceOverbook = isForceOverbook;
    }

    /**
     * 
     * @return
     *     The patientContacted
     */
    public Object getPatientContacted() {
        return patientContacted;
    }

    /**
     * 
     * @param patientContacted
     *     The patient_contacted
     */
    public void setPatientContacted(Object patientContacted) {
        this.patientContacted = patientContacted;
    }

    /**
     * 
     * @return
     *     The recurrenceId
     */
    public Object getRecurrenceId() {
        return recurrenceId;
    }

    /**
     * 
     * @param recurrenceId
     *     The recurrence_id
     */
    public void setRecurrenceId(Object recurrenceId) {
        this.recurrenceId = recurrenceId;
    }

    /**
     * 
     * @return
     *     The recurrenceIndex
     */
    public Object getRecurrenceIndex() {
        return recurrenceIndex;
    }

    /**
     * 
     * @param recurrenceIndex
     *     The recurrence_index
     */
    public void setRecurrenceIndex(Object recurrenceIndex) {
        this.recurrenceIndex = recurrenceIndex;
    }

    /**
     * 
     * @return
     *     The referringPhysicianNpi
     */
    public Object getReferringPhysicianNpi() {
        return referringPhysicianNpi;
    }

    /**
     * 
     * @param referringPhysicianNpi
     *     The referring_physician_npi
     */
    public void setReferringPhysicianNpi(Object referringPhysicianNpi) {
        this.referringPhysicianNpi = referringPhysicianNpi;
    }

    /**
     * 
     * @return
     *     The visitReasonId
     */
    public Integer getVisitReasonId() {
        return visitReasonId;
    }

    /**
     * 
     * @param visitReasonId
     *     The visit_reason_id
     */
    public void setVisitReasonId(Integer visitReasonId) {
        this.visitReasonId = visitReasonId;
    }

    /**
     * 
     * @return
     *     The resourceId
     */
    public Integer getResourceId() {
        return resourceId;
    }

    /**
     * 
     * @param resourceId
     *     The resource_id
     */
    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    /**
     * 
     * @return
     *     The providerId
     */
    public Integer getProviderId() {
        return providerId;
    }

    /**
     * 
     * @param providerId
     *     The provider_id
     */
    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    /**
     * 
     * @return
     *     The startTime
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * 
     * @param startTime
     *     The start_time
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * 
     * @return
     *     The endTime
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * 
     * @param endTime
     *     The end_time
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
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
     *     The encounterId
     */
    public Integer getEncounterId() {
        return encounterId;
    }

    /**
     * 
     * @param encounterId
     *     The encounter_id
     */
    public void setEncounterId(Integer encounterId) {
        this.encounterId = encounterId;
    }

    /**
     * 
     * @return
     *     The admitDiagnosisId
     */
    public Object getAdmitDiagnosisId() {
        return admitDiagnosisId;
    }

    /**
     * 
     * @param admitDiagnosisId
     *     The admit_diagnosis_id
     */
    public void setAdmitDiagnosisId(Object admitDiagnosisId) {
        this.admitDiagnosisId = admitDiagnosisId;
    }

    /**
     * 
     * @return
     *     The noteSetId
     */
    public Integer getNoteSetId() {
        return noteSetId;
    }

    /**
     * 
     * @param noteSetId
     *     The note_set_id
     */
    public void setNoteSetId(Integer noteSetId) {
        this.noteSetId = noteSetId;
    }

    /**
     * 
     * @return
     *     The documentSetId
     */
    public Integer getDocumentSetId() {
        return documentSetId;
    }

    /**
     * 
     * @param documentSetId
     *     The document_set_id
     */
    public void setDocumentSetId(Integer documentSetId) {
        this.documentSetId = documentSetId;
    }

    /**
     * 
     * @return
     *     The contrast
     */
    public Object getContrast() {
        return contrast;
    }

    /**
     * 
     * @param contrast
     *     The contrast
     */
    public void setContrast(Object contrast) {
        this.contrast = contrast;
    }

    /**
     * 
     * @return
     *     The laterality
     */
    public Object getLaterality() {
        return laterality;
    }

    /**
     * 
     * @param laterality
     *     The laterality
     */
    public void setLaterality(Object laterality) {
        this.laterality = laterality;
    }

    /**
     * 
     * @return
     *     The createdByApplication
     */
    public Object getCreatedByApplication() {
        return createdByApplication;
    }

    /**
     * 
     * @param createdByApplication
     *     The created_by_application
     */
    public void setCreatedByApplication(Object createdByApplication) {
        this.createdByApplication = createdByApplication;
    }

    /**
     * 
     * @return
     *     The patientInstructions
     */
    public Object getPatientInstructions() {
        return patientInstructions;
    }

    /**
     * 
     * @param patientInstructions
     *     The patient_instructions
     */
    public void setPatientInstructions(Object patientInstructions) {
        this.patientInstructions = patientInstructions;
    }

    /**
     * 
     * @return
     *     The preferredConfirmationMethod
     */
    public String getPreferredConfirmationMethod() {
        return preferredConfirmationMethod;
    }

    /**
     * 
     * @param preferredConfirmationMethod
     *     The preferred_confirmation_method
     */
    public void setPreferredConfirmationMethod(String preferredConfirmationMethod) {
        this.preferredConfirmationMethod = preferredConfirmationMethod;
    }

    /**
     * 
     * @return
     *     The provider
     */
    public ProviderModel getProvider() {
        return provider;
    }

    /**
     * 
     * @param provider
     *     The provider
     */
    public void setProvider(ProviderModel provider) {
        this.provider = provider;
    }

}
