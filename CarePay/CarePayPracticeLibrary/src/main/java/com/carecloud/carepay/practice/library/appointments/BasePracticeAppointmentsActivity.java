package com.carecloud.carepay.practice.library.appointments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.dialogs.PracticeAvailableHoursDialogFragment;
import com.carecloud.carepay.practice.library.appointments.dialogs.PracticeChooseProviderDialog;
import com.carecloud.carepay.practice.library.appointments.dialogs.PracticeModeRequestAppointmentDialog;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.customdialog.DateRangePickerDialog;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeAddNewCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeChooseCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentMethodPrepaymentFragment;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.interfaces.AppointmentNavigationCallback;
import com.carecloud.carepaylibray.appointments.interfaces.AppointmentPrepaymentCallback;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSettingDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.LinksDTO;
import com.carecloud.carepaylibray.appointments.models.ResourcesToScheduleDTO;
import com.carecloud.carepaylibray.appointments.models.ScheduleAppointmentRequestDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.customdialogs.VisitTypeFragmentDialog;
import com.carecloud.carepaylibray.payments.fragments.PaymentConfirmationFragment;
import com.carecloud.carepaylibray.payments.interfaces.PaymentMethodDialogInterface;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.squareup.timessquare.CalendarPickerView;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cocampo on 3/13/17
 */

public abstract class BasePracticeAppointmentsActivity extends BasePracticeActivity
        implements AppointmentNavigationCallback, AppointmentPrepaymentCallback,
        PaymentMethodDialogInterface,
        DateRangePickerDialog.DateRangePickerDialogListener {

    private Date startDate;
    private Date endDate;

    protected AppointmentResourcesDTO appointmentResourcesDTO;
    protected AppointmentsResultModel appointmentsResultModel;
    private AppointmentsResultModel resourcesToSchedule;
    protected VisitTypeDTO visitTypeDTO;
    private AppointmentsSlotsDTO appointmentSlot;

    private PaymentsModel paymentsModel;
    private PatientModel patientModel;

    private ScheduleAppointmentRequestDTO scheduleAppointmentRequestDTO;
    private double prepayAmount;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        appointmentsResultModel = getConvertedDTO(AppointmentsResultModel.class);
        paymentsModel = getConvertedDTO(PaymentsModel.class);
    }

    /**
     * Shows Confirmation after Appointment Created
     */
    public void showAppointmentConfirmation() {
        if (isVisible()) {
            ApplicationMode.ApplicationType applicationType = getApplicationMode().getApplicationType();
            boolean autoScheduleAppointments = getAppointmentsSettings().getRequests().getAutomaticallyApproveRequests();

            SystemUtil.showSuccessToast(getContext(),
                    Label.getLabel(applicationType == ApplicationMode.ApplicationType.PRACTICE || autoScheduleAppointments ?
                            "appointment_schedule_success_message_HTML" : "appointment_request_success_message_HTML"));
        }

        onAppointmentRequestSuccess();
        logAppointmentRequestedMixpanel();
    }

    @Override
    public void onHoursAndLocationSelected(AppointmentsSlotsDTO appointmentsSlot,
                                           AppointmentAvailabilityDTO availabilityDTO) {
        // Call Request appointment Summary dialog from here
        String cancelString = Label.getLabel("available_hours_back");
        new PracticeModeRequestAppointmentDialog(
                this,
                cancelString,
                appointmentsSlot,
                appointmentResourcesDTO,
                visitTypeDTO,
                this,
                patientModel
        ).show();
    }

    @Override
    public void onRangeSelected(Date start, Date end) {
        this.startDate = start;
        this.endDate = end;

        onDateRangeCancelled();
    }

    @Override
    public void onDateRangeCancelled() {
        onDateRangeSelected(startDate, endDate, visitTypeDTO, appointmentResourcesDTO.getResource(),
                resourcesToSchedule);
    }

    @Override
    public void requestAppointment(AppointmentsSlotsDTO appointmentSlot, String reasonForVisit) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", getApplicationMode().getUserPracticeDTO().getPracticeMgmt());
        queryMap.put("practice_id", getApplicationMode().getUserPracticeDTO().getPracticeId());


        scheduleAppointmentRequestDTO = new ScheduleAppointmentRequestDTO();
        ScheduleAppointmentRequestDTO.Appointment appointment = scheduleAppointmentRequestDTO.getAppointment();
        appointment.setStartTime(appointmentSlot.getStartTime());
        appointment.setEndTime(appointmentSlot.getEndTime());
        appointment.setLocationId(appointmentSlot.getLocation().getId());
        appointment.setLocationGuid(appointmentSlot.getLocation().getGuid());
        appointment.setProviderId(appointmentResourcesDTO.getResource().getProvider().getId());
        appointment.setProviderGuid(appointmentResourcesDTO.getResource().getProvider().getGuid());
        appointment.setVisitReasonId(visitTypeDTO.getId());
        appointment.setResourceId(appointmentResourcesDTO.getResource().getId());
        appointment.setComplaint(reasonForVisit);
        appointment.setComments(reasonForVisit);

        appointment.getPatient().setId(patientModel.getPatientId());

        prepayAmount = visitTypeDTO.getAmount();
        if (prepayAmount > 0 && paymentsModel != null) {
            startPrepaymentProcess(scheduleAppointmentRequestDTO, appointmentSlot, prepayAmount);
        } else {
            Gson gson = new Gson();
            getWorkflowServiceHelper().execute(getMakeAppointmentTransition(),
                    getMakeAppointmentCallback, gson.toJson(scheduleAppointmentRequestDTO), queryMap);
        }
    }

    @Override
    public void onAppointmentUnconfirmed() {
        onDateRangeCancelled();
    }

    private WorkflowServiceCallback getMakeAppointmentCallback = new WorkflowServiceCallback() {

        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            showAppointmentConfirmation();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.doDefaultFailureBehavior((BaseActivity) getContext(), exceptionMessage);
        }
    };

    public PatientModel getPatient() {
        return patientModel;
    }

    public void setPatient(PatientModel patient) {
        this.patientModel = patient;
    }

    @Override
    public void newAppointment() {
        getWorkflowServiceHelper().execute(getLinks().getResourcesToSchedule(),
                scheduleResourcesCallback, null, null, null);
    }

    @Override
    public void rescheduleAppointment(AppointmentDTO appointmentDTO) {

    }

    @Override
    public void onProviderSelected(AppointmentResourcesDTO appointmentResourcesDTO,
                                   AppointmentsResultModel appointmentsResultModel,
                                   ResourcesToScheduleDTO resourcesToScheduleDTO) {
        resourcesToSchedule = appointmentsResultModel;
        String tag = VisitTypeFragmentDialog.class.getName();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        VisitTypeFragmentDialog dialog = VisitTypeFragmentDialog
                .newInstance(appointmentResourcesDTO, appointmentsResultModel, getAppointmentsSettings());
        dialog.show(ft, tag);
    }

    /**
     * what to do with the selected visit type and provider
     *
     * @param visitTypeDTO selected visit type from dialog
     */
    @Override
    public void onVisitTypeSelected(VisitTypeDTO visitTypeDTO,
                                    AppointmentResourcesDTO appointmentResourcesDTO,
                                    AppointmentsResultModel appointmentsResultModel) {
        this.appointmentResourcesDTO = appointmentResourcesDTO;
        this.visitTypeDTO = visitTypeDTO;
        PracticeAvailableHoursDialogFragment fragment = PracticeAvailableHoursDialogFragment
                .newInstance(appointmentsResultModel, appointmentResourcesDTO.getResource(),
                        null, null, visitTypeDTO);
        displayDialogFragment(fragment, true);
    }

    @Override
    public void onDateRangeSelected(Date startDate, Date endDate, VisitTypeDTO visitTypeDTO,
                                    AppointmentResourcesItemDTO appointmentResource,
                                    AppointmentsResultModel appointmentsResultModel) {
        PracticeAvailableHoursDialogFragment fragment = PracticeAvailableHoursDialogFragment
                .newInstance(appointmentsResultModel, appointmentResourcesDTO.getResource(),
                        startDate, endDate, visitTypeDTO);
        fragment.show(getSupportFragmentManager(), fragment.getClass().getName());
    }

    @Override
    public void selectDateRange(Date startDate, Date endDate, VisitTypeDTO visitTypeDTO,
                                AppointmentResourcesItemDTO appointmentResource,
                                AppointmentsResultModel appointmentsResultModel) {
        DateUtil dateUtil = DateUtil.getInstance().setToCurrent();
        DateRangePickerDialog dialog = DateRangePickerDialog.newInstance(
                Label.getLabel("date_range_picker_dialog_title"),
                Label.getLabel("datepicker_cancel_option"),
                false,
                startDate,
                endDate,
                dateUtil.getDate(),
                dateUtil.addDays(92).getDate(),
                this,
                CalendarPickerView.SelectionMode.RANGE.name());

        displayDialogFragment(dialog, false);
    }

    private WorkflowServiceCallback scheduleResourcesCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            AppointmentsResultModel resourcesToScheduleModel = DtoHelper
                    .getConvertedDTO(AppointmentsResultModel.class, workflowDTO);

            if (resourcesToScheduleModel != null && resourcesToScheduleModel.getPayload() != null
                    && resourcesToScheduleModel.getPayload().getResourcesToSchedule() != null
                    && resourcesToScheduleModel.getPayload().getResourcesToSchedule().size() > 0) {
                PracticeChooseProviderDialog fragment = PracticeChooseProviderDialog
                        .newInstance(resourcesToScheduleModel,
                                Label.getLabel("practice_list_select_a_provider"),
                                Label.getLabel("practice_list_continue"));
                fragment.show(getSupportFragmentManager(), fragment.getClass().getName());
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.doDefaultFailureBehavior((BaseActivity) getContext(), exceptionMessage);
        }
    };

    protected abstract TransitionDTO getMakeAppointmentTransition();

    protected abstract LinksDTO getLinks();

    @Override
    public AppointmentsSettingDTO getAppointmentsSettings() {
        List<AppointmentsSettingDTO> appointmentsSettingDTOList = appointmentsResultModel
                .getPayload().getAppointmentsSettings();
        if (!appointmentsSettingDTOList.isEmpty()) {
            return appointmentsSettingDTOList.get(0);
        }
        return new AppointmentsSettingDTO();
    }

    @Override
    public void startPrepaymentProcess(ScheduleAppointmentRequestDTO appointmentRequestDTO,
                                       AppointmentsSlotsDTO appointmentSlot, double amount) {
        this.appointmentSlot = appointmentSlot;
        IntegratedPaymentPostModel postModel = new IntegratedPaymentPostModel();
        postModel.setAmount(amount);

        IntegratedPaymentLineItem paymentLineItem = new IntegratedPaymentLineItem();
        paymentLineItem.setAmount(amount);
        paymentLineItem.setProviderID(appointmentRequestDTO.getAppointment().getProviderGuid());
        paymentLineItem.setLocationID(appointmentRequestDTO.getAppointment().getLocationGuid());
        paymentLineItem.setItemType(IntegratedPaymentLineItem.TYPE_PREPAYMENT);

        postModel.addLineItem(paymentLineItem);
        postModel.getMetadata().setAppointmentRequestDTO(appointmentRequestDTO.getAppointment());

        paymentsModel.getPaymentPayload().setPaymentPostModel(postModel);

        onPayButtonClicked(amount, paymentsModel);
    }

    @Override
    public void onPaymentDismissed() {
        onHoursAndLocationSelected(appointmentSlot, null);
    }

    @Override
    public void showAddCard(double amount, PaymentsModel paymentsModel) {
        PracticeAddNewCreditCardFragment fragment = PracticeAddNewCreditCardFragment.newInstance(paymentsModel, amount);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        PracticePaymentMethodPrepaymentFragment prepaymentFragment = PracticePaymentMethodPrepaymentFragment
                .newInstance(paymentsModel, amount);
        displayDialogFragment(prepaymentFragment, true);

        MixPanelUtil.logEvent(getString(R.string.event_payment_start_prepayment));
    }

    @Override
    public void onPaymentPlanAction(PaymentsModel paymentsModel) {

    }

    @Override
    public void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod, double amount,
                                      PaymentsModel paymentsModel) {
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null
                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            DialogFragment fragment = PracticeChooseCreditCardFragment
                    .newInstance(paymentsModel, selectedPaymentMethod.getLabel(), amount);
            displayDialogFragment(fragment, false);
        } else {
            showAddCard(amount, paymentsModel);
        }
    }

    @Nullable
    @Override
    public String getAppointmentId() {
        return null;
    }

    @Nullable
    @Override
    public AppointmentDTO getAppointment() {
        return null;
    }

    @Override
    public void showPaymentConfirmation(WorkflowDTO workflowDTO) {
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        IntegratedPatientPaymentPayload payload = paymentsModel.getPaymentPayload()
                .getPatientPayments().getPayload();
        if (!payload.getProcessingErrors().isEmpty() && payload.getTotalPaid() == 0D) {
            StringBuilder builder = new StringBuilder();
            for (IntegratedPatientPaymentPayload.ProcessingError processingError
                    : payload.getProcessingErrors()) {
                builder.append(processingError.getError());
                builder.append("\n");
            }
            int last = builder.lastIndexOf("\n");
            builder.replace(last, builder.length(), "");
            showErrorNotification(builder.toString());
        } else {
            PaymentConfirmationFragment confirmationFragment = PaymentConfirmationFragment
                    .newInstance(workflowDTO, Label.getLabel("appointment.confirmationScreen.type.label.paymentType"),
                            Label.getLabel("add_appointment_back_to_appointments_button"));
            displayDialogFragment(confirmationFragment, false);

            //this is a prepayment
            MixPanelUtil.incrementPeopleProperty(getString(R.string.count_prepayments_completed), 1);
        }
    }

    @Override
    public UserPracticeDTO getPracticeInfo(PaymentsModel paymentsModel) {
        UserPracticeDTO userPracticeDTO = paymentsModel.getPaymentPayload().getUserPractices().get(0);
        userPracticeDTO.setPatientId(patientModel.getPatientId());
        return userPracticeDTO;
    }

    @Override
    public void completePaymentProcess(WorkflowDTO workflowDTO) {
        onAppointmentRequestSuccess();
        logAppointmentRequestedMixpanel();
    }

    @Override
    public void onDismissPaymentMethodDialog(PaymentsModel paymentsModel) {
        onHoursAndLocationSelected(appointmentSlot, null);
    }

    private void logAppointmentRequestedMixpanel() {
        ApplicationMode.ApplicationType applicationType = getApplicationMode().getApplicationType();
        String[] params = {getString(R.string.param_appointment_type),
                getString(R.string.param_practice_id),
                getString(R.string.param_practice_name),
                getString(R.string.param_provider_id),
                getString(R.string.param_patient_id),
                getString(R.string.param_location_id),
                getString(R.string.param_reason_visit),
                //make sure this is the last item in case we need to null it out to prevent it from sending
                getString(R.string.param_payment_made)
        };
        Object[] values = {visitTypeDTO.getName(),
                getApplicationMode().getUserPracticeDTO().getPracticeId(),
                getApplicationMode().getUserPracticeDTO().getPracticeName(),
                scheduleAppointmentRequestDTO.getAppointment().getProviderGuid(),
                patientModel.getPatientId(),
                scheduleAppointmentRequestDTO.getAppointment().getLocationGuid(),
                scheduleAppointmentRequestDTO.getAppointment().getComments(),
                prepayAmount
        };
        if(prepayAmount <= 0D){
            params[params.length-1] = null;
        }
        MixPanelUtil.logEvent(getString(applicationType == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE ?
                R.string.event_appointment_requested : R.string.event_appointment_scheduled), params, values);
        if (applicationType == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE) {
            MixPanelUtil.incrementPeopleProperty(getString(R.string.count_appointment_requested), 1);
        }

    }
}
