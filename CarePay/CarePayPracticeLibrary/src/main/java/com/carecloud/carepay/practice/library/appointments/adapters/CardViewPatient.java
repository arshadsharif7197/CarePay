package com.carecloud.carepay.practice.library.appointments.adapters;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.CheckinStatusDTO;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.payments.models.LocationIndexDTO;
import com.carecloud.carepaylibray.payments.models.ProviderIndexDTO;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.text.NumberFormat;
import java.util.Date;

/**
 * Created by cocampo on 3/17/17
 */
public class CardViewPatient {
    public Object raw;
    public String id;
    public String appointmentId;
    public String initials;
    public String name;
    public String providerName;
    public String balance;
    public String photoUrl;
    public String providerId;
    public String locationId;
    public Date appointmentStartTime;
    Boolean isAppointmentOver;
    Boolean isRequested;
    public Boolean isCheckedIn;
    public Boolean isCheckingIn;
    public Boolean isPending;
    public Boolean isCheckingOut;
    public Boolean isCheckedOut;
    public CheckinStatusDTO checkinStatus;
    public Date lastUpdate;
    int headCount;

    CardViewPatient(Object raw, String id, ProviderIndexDTO provider, LocationIndexDTO location, Double balance, PatientModel dto) {
        this.raw = raw;
        this.id = id;
        this.name = dto.getFullName();
        this.initials = dto.getShortName();
        this.photoUrl = dto.getProfilePhoto();
        this.providerName = StringUtil.getLabelForView(provider.getName());
        this.providerId = provider.getId();
        this.balance = getFormattedBalance(balance);
        this.locationId = location.getId();
        this.isRequested = false;
        this.isCheckedIn = false;
        this.isCheckingIn = false;
        this.isCheckingOut = false;
        this.isCheckedOut = false;
        this.isPending = false;
    }

    /**
     * @param dto     Appointment Payload DTO
     * @param balance owed
     */
    public CardViewPatient(AppointmentsPayloadDTO dto, Double balance) {
        this(dto, dto, balance);
    }

    /**
     * @param raw     main DTO
     * @param dto     Appointment Payload DTO
     * @param balance owed
     */
    public CardViewPatient(Object raw, AppointmentsPayloadDTO dto, Double balance) {
        this.raw = raw;
        PatientModel patientModel = dto.getPatient();
        this.id = patientModel.getPatientId();
        this.name = patientModel.getFullName();
        this.initials = patientModel.getShortName();
        this.photoUrl = patientModel.getProfilePhoto();
        this.providerId = dto.getProvider().getId().toString();
        this.providerName = dto.getProvider().getName();
        this.appointmentStartTime = DateUtil.getInstance().setDateRaw(dto.getStartTime()).getDate();
        this.isAppointmentOver = dto.isAppointmentOver();
        this.balance = getFormattedBalance(balance);
        this.locationId = dto.getLocation().getId().toString();
        String code = dto.getAppointmentStatus().getCode();
        this.isRequested = code.equalsIgnoreCase(CarePayConstants.REQUESTED);
        this.isCheckedIn = code.equalsIgnoreCase(CarePayConstants.CHECKED_IN) ||
                code.equalsIgnoreCase(CarePayConstants.IN_PROGRESS_IN_ROOM) ||
                code.equalsIgnoreCase(CarePayConstants.IN_PROGRESS_OUT_ROOM);
        this.isCheckingIn = code.equalsIgnoreCase(CarePayConstants.CHECKING_IN);
        this.isPending = code.equalsIgnoreCase(CarePayConstants.PENDING);
        this.isCheckingOut = code.equalsIgnoreCase(CarePayConstants.CHECKING_OUT);
        this.isCheckedOut = code.equalsIgnoreCase(CarePayConstants.CHECKED_OUT) ||
                code.equalsIgnoreCase(CarePayConstants.BILLED) ||
                code.equalsIgnoreCase(CarePayConstants.MANUALLY_BILLED);
        this.checkinStatus = dto.getAppointmentStatus().getCheckinStatusDTO();
        if (dto.getAppointmentStatus().getLastUpdated() != null) {
            this.lastUpdate = DateUtil.getInstance().setDateRaw(dto.getAppointmentStatus().getLastUpdated().replaceAll("\\.\\d\\d\\dZ", "-00:00")).getDate();
        }
    }

    /**
     * @param appointmentTime header date
     */
    public CardViewPatient(Date appointmentTime) {
        this.appointmentStartTime = appointmentTime;
    }

    public CardViewPatient() {

    }

    private String getFormattedBalance(Double balance) {
        NumberFormat numForm = NumberFormat.getCurrencyInstance();

        return numForm.format(balance == null ? 0 : balance);
    }
}
