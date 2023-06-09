
package com.carecloud.carepaylibray.appointments.models;

import androidx.annotation.NonNull;

import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.adhoc.AdhocFormsPatientModeInfo;
import com.carecloud.carepaylibray.base.dtos.DelegatePermissionBasePayloadDto;
import com.carecloud.carepaylibray.base.models.PagingDto;
import com.carecloud.carepaylibray.demographics.dtos.payload.ConsentFormUserResponseDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicsDTO;
import com.carecloud.carepaylibray.payments.models.MerchantServicesDTO;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientsCreditCardsPayloadListDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsPayloadSettingsDTO;
import com.carecloud.carepaylibray.signinsignup.dto.OptionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model for appointment payload.
 */
public class AppointmentPayloadModel extends DelegatePermissionBasePayloadDto implements Serializable {

    @SerializedName("languages")
    @Expose
    private List<OptionDTO> languages = new ArrayList<>();
    @SerializedName("appointments")
    @Expose
    private List<AppointmentDTO> appointments = new ArrayList<>();
    @SerializedName("practice_patient_ids")
    @Expose
    private List<PracticePatientIdsDTO> practicePatientIds = new ArrayList<>();
    @SerializedName("providers")
    @Expose
    private List<AppointmentProvidersDTO> providers = new ArrayList<>();
    @SerializedName("locations")
    @Expose
    private List<LocationDTO> locations = new ArrayList<>();
    @SerializedName("providers_schedule")
    @Expose
    private List<ProvidersScheduleDTO> providersSchedule = new ArrayList<>();
    @SerializedName("cancellation_reasons")
    @Expose
    private List<CancellationReasonDTO> cancellationReasons = new ArrayList<CancellationReasonDTO>();
    @SerializedName("appointment_availability")
    @Expose
    private AppointmentAvailabilityDataDTO appointmentAvailability = new AppointmentAvailabilityDataDTO();
    @SerializedName("resources_to_schedule")
    @Expose
    private List<ResourcesToScheduleDTO> resourcesToSchedule = new ArrayList<>();
    @SerializedName("appointments_settings")
    @Expose
    private List<AppointmentsSettingDTO> appointmentsSettings = new ArrayList<>();
    @SerializedName("demographics")
    @Expose
    private DemographicsSettingsDemographicsDTO demographicDTO = new DemographicsSettingsDemographicsDTO();
    @SerializedName("patient_balances")
    @Expose
    private List<PatientBalanceDTO> patientBalances = new ArrayList<>();
    @Expose
    @SerializedName("checkout_forms_user_response")
    private List<ConsentFormUserResponseDTO> responses = new ArrayList<>();
    @Expose
    @SerializedName("patient_forms_response")
    private List<ConsentFormUserResponseDTO> patientFormsResponse = new ArrayList<>();
    @Expose
    @SerializedName("patient_forms_filled")
    private List<ConsentFormUserResponseDTO> patientFormsFilled = new ArrayList<>();
    @Expose
    @SerializedName(value = "practice_information", alternate = "user_practices")
    private List<UserPracticeDTO> userPractices = new ArrayList<>();
    @Expose
    @SerializedName("filled_forms")
    private List<String> filledForms = new ArrayList<>();
    @SerializedName("payment_settings")
    @Expose
    private List<PaymentsPayloadSettingsDTO> paymentSettings = new ArrayList<>();
    @SerializedName("merchant_services")
    @Expose
    private List<MerchantServicesDTO> merchantServices = new ArrayList<>();
    @SerializedName("patient_credit_cards")
    @Expose
    private List<PaymentsPatientsCreditCardsPayloadListDTO> patientCreditCards = new ArrayList<>();
    @SerializedName("adhoc_forms_patient_mode")
    @Expose
    private AdhocFormsPatientModeInfo adhocFormsPatientModeInfo = new AdhocFormsPatientModeInfo();
    @SerializedName("portal_settings")
    @Expose
    private List<PortalSettingDTO> portalSettings = new ArrayList<>();
    @SerializedName("appointment_page_details")
    @Expose
    private List<PagingDto> pagingInfo = new ArrayList<>();
    @SerializedName("video_visit")
    @Expose
    private VideoVisitModel videoVisitModel = new VideoVisitModel();
    @SerializedName("intelligent_scheduler")
    @Expose
    private List<IntelligentSchedulerDTO> intelligent_scheduler;

    /**
     * @return languages
     */
    public List<OptionDTO> getLanguages() {
        return languages;
    }

    /**
     * @param languages languages
     */
    public void setLanguages(List<OptionDTO> languages) {
        this.languages = languages;
    }

    /**
     * @return The appointments
     */
    public List<AppointmentDTO> getAppointments() {
        return appointments;
    }

    /**
     * @param appointments The appointments
     */
    public void setAppointments(List<AppointmentDTO> appointments) {
        this.appointments = appointments;
    }

    /**
     * @return The providers
     */
    public List<AppointmentProvidersDTO> getProviders() {
        return providers;
    }

    /**
     * @param providers The providers
     */
    public void setProviders(List<AppointmentProvidersDTO> providers) {
        this.providers = providers;
    }

    /**
     * @return The locations
     */
    public List<LocationDTO> getLocations() {
        return locations;
    }

    /**
     * @param locations The locations
     */
    public void setLocations(List<LocationDTO> locations) {
        this.locations = locations;
    }

    /**
     * @return The providersSchedule
     */
    public List<ProvidersScheduleDTO> getProvidersSchedule() {
        return providersSchedule;
    }

    /**
     * @param providersSchedule The providers_schedule
     */
    public void setProvidersSchedule(List<ProvidersScheduleDTO> providersSchedule) {
        this.providersSchedule = providersSchedule;
    }

    /**
     * @return The cancellationReasons
     */
    public List<CancellationReasonDTO> getCancellationReasons() {
        return cancellationReasons;
    }

    /**
     * @param cancellationReasons The cancellation_reasons
     */
    public void setCancellationReasons(List<CancellationReasonDTO> cancellationReasons) {
        this.cancellationReasons = cancellationReasons;
    }

    /**
     * Gets appointmentAvailability.
     *
     * @return the appointmentAvailability
     */
    public AppointmentAvailabilityDataDTO getAppointmentAvailability() {
        return appointmentAvailability;
    }

    /**
     * Sets appointmentAvailability.
     *
     * @param appointmentAvailability the appointmentAvailability
     */
    public void setAppointmentAvailability(AppointmentAvailabilityDataDTO appointmentAvailability) {
        this.appointmentAvailability = appointmentAvailability;
    }

    /**
     * @return resourcesToSchedule
     */
    public List<ResourcesToScheduleDTO> getResourcesToSchedule() {
        return resourcesToSchedule;
    }

    /**
     * @param resourcesToSchedule resourcesToSchedule
     */
    public void setResourcesToSchedule(List<ResourcesToScheduleDTO> resourcesToSchedule) {
        this.resourcesToSchedule = resourcesToSchedule;
    }

    /**
     * @return practice_patent_ids
     */
    public List<PracticePatientIdsDTO> getPracticePatientIds() {
        return practicePatientIds;
    }

    /**
     * @param practicePatientIds practicePatientIds
     */
    public void setPracticePatientIds(List<PracticePatientIdsDTO> practicePatientIds) {
        this.practicePatientIds = practicePatientIds;
    }

    /**
     * @return appointmentsSettings
     */
    public List<AppointmentsSettingDTO> getAppointmentsSettings() {
        return appointmentsSettings;
    }

    /**
     * @param appointmentsSettings appointmentsSettings
     */
    public void setAppointmentsSettings(List<AppointmentsSettingDTO> appointmentsSettings) {
        this.appointmentsSettings = appointmentsSettings;
    }

    public DemographicsSettingsDemographicsDTO getDemographicDTO() {
        return demographicDTO;
    }

    public void setDemographicDTO(DemographicsSettingsDemographicsDTO demographicDTO) {
        this.demographicDTO = demographicDTO;
    }

    public List<PatientBalanceDTO> getPatientBalances() {
        return patientBalances;
    }

    public void setPatientBalances(List<PatientBalanceDTO> patientBalances) {
        this.patientBalances = patientBalances;
    }

    public List<ConsentFormUserResponseDTO> getResponses() {
        return responses;
    }

    public void setResponses(List<ConsentFormUserResponseDTO> responses) {
        this.responses = responses;
    }

    public List<ConsentFormUserResponseDTO> getPatientFormsResponse() {
        return patientFormsResponse;
    }

    public void setPatientFormsResponse(List<ConsentFormUserResponseDTO> patientFormsResponse) {
        this.patientFormsResponse = patientFormsResponse;
    }

    public List<ConsentFormUserResponseDTO> getPatientFormsFilled() {
        return patientFormsFilled;
    }

    public void setPatientFormsFilled(List<ConsentFormUserResponseDTO> patientFormsFilled) {
        this.patientFormsFilled = patientFormsFilled;
    }

    public List<UserPracticeDTO> getUserPractices() {
        return userPractices;
    }

    public void setUserPractices(List<UserPracticeDTO> userPractices) {
        this.userPractices = userPractices;
    }

    public List<String> getFilledForms() {
        return filledForms;
    }

    public void setFilledForms(List<String> filledForms) {
        this.filledForms = filledForms;
    }

    public List<PaymentsPayloadSettingsDTO> getPaymentSettings() {
        return paymentSettings;
    }

    public void setPaymentSettings(List<PaymentsPayloadSettingsDTO> paymentSettings) {
        this.paymentSettings = paymentSettings;
    }

    public List<MerchantServicesDTO> getMerchantServices() {
        return merchantServices;
    }

    public void setMerchantServices(List<MerchantServicesDTO> merchantServices) {
        this.merchantServices = merchantServices;
    }

    public List<PaymentsPatientsCreditCardsPayloadListDTO> getPatientCreditCards() {
        return patientCreditCards;
    }

    public void setPatientCreditCards(List<PaymentsPatientsCreditCardsPayloadListDTO> patientCreditCards) {
        this.patientCreditCards = patientCreditCards;
    }

    public AdhocFormsPatientModeInfo getAdhocFormsPatientModeInfo() {
        return adhocFormsPatientModeInfo;
    }

    public void setAdhocFormsPatientModeInfo(AdhocFormsPatientModeInfo adhocFormsPatientModeInfo) {
        this.adhocFormsPatientModeInfo = adhocFormsPatientModeInfo;
    }

    public List<PortalSettingDTO> getPortalSettings() {
        return portalSettings;
    }

    public void setPortalSettings(List<PortalSettingDTO> portalSettings) {
        this.portalSettings = portalSettings;
    }

    public List<PagingDto> getPagingInfo() {
        return pagingInfo;
    }

    public void setPagingInfo(List<PagingDto> pagingInfo) {
        this.pagingInfo = pagingInfo;
    }

    public VideoVisitModel getVideoVisitModel() {
        return videoVisitModel;
    }

    public void setVideoVisitModel(VideoVisitModel videoVisitModel) {
        this.videoVisitModel = videoVisitModel;
    }

    public UserPracticeDTO getPractice(@NonNull String practiceId) {
        for (UserPracticeDTO practice : getUserPractices()) {
            if (practice.getPracticeId().equals(practiceId)) {
                return practice;
            }
        }
        return null;
    }

    public AppointmentsSettingDTO getAppointmentsSetting(String practiceId) {
        for (AppointmentsSettingDTO settingDTO : appointmentsSettings) {
            if (settingDTO.getPracticeId().equals(practiceId)) {
                return settingDTO;
            }
        }
        return null;
    }

    public boolean isRescheduleEnabled(String practiceId) {
        boolean rescheduleEnabled = false;
        for (PortalSettingDTO portalSettingsDto : portalSettings) {
            if (portalSettingsDto.getMetadata().getPracticeId().equals(practiceId)) {
                for (PortalSetting portalSetting : portalSettingsDto.getPayload()) {
                    if ("scheduling".equals(portalSetting.getTypeName().toLowerCase())
                            && "appointments".equals(portalSetting.getLabel().toLowerCase())) {
                        rescheduleEnabled = "A".equals(portalSetting.getStatus());
                    }
                }
            }
        }

        if (!rescheduleEnabled) {
            return false;
        }

        return canScheduleAppointments(practiceId);
    }

    public List<IntelligentSchedulerDTO> getIntelligent_scheduler() {
        return intelligent_scheduler;
    }

    public void setIntelligent_scheduler(List<IntelligentSchedulerDTO> intelligent_scheduler) {
        this.intelligent_scheduler = intelligent_scheduler;
    }
}
