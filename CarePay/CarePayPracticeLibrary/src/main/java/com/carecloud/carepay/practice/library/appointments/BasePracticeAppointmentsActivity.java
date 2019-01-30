package com.carecloud.carepay.practice.library.appointments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.createappointment.AvailabilityHourFragment;
import com.carecloud.carepay.practice.library.appointments.createappointment.CreateAppointmentFragment;
import com.carecloud.carepay.practice.library.appointments.dialogs.PracticeModeRequestAppointmentDialog;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.customdialog.DateRangePickerDialog;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeAddNewCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeChooseCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentMethodPrepaymentFragment;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.createappointment.CreateAppointmentFragmentInterface;
import com.carecloud.carepaylibray.appointments.interfaces.AppointmentNavigationCallback;
import com.carecloud.carepaylibray.appointments.interfaces.AppointmentPrepaymentCallback;
import com.carecloud.carepaylibray.appointments.interfaces.DateCalendarRangeInterface;
import com.carecloud.carepaylibray.appointments.interfaces.ScheduleAppointmentInterface;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.LinksDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.ResourcesToScheduleDTO;
import com.carecloud.carepaylibray.appointments.models.ScheduleAppointmentRequestDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.base.models.PatientModel;
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
import com.squareup.timessquare.CalendarPickerView;

import java.util.Date;

/**
 * Created by cocampo on 3/13/17
 */

public abstract class BasePracticeAppointmentsActivity extends BasePracticeActivity
        implements AppointmentNavigationCallback, AppointmentPrepaymentCallback,
        PaymentMethodDialogInterface, ScheduleAppointmentInterface,
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

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        appointmentsResultModel = getConvertedDTO(AppointmentsResultModel.class);
        paymentsModel = getConvertedDTO(PaymentsModel.class);
    }

    @Override
    public void onHoursAndLocationSelected(AppointmentsSlotsDTO appointmentsSlot,
                                           AppointmentAvailabilityDTO availabilityDTO) {
        //TODO: Refactor this (take out Appointment Navigation Interface) after SHMRK-8014
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

    public PatientModel getPatient() {
        return patientModel;
    }

    public void setPatient(PatientModel patient) {
        this.patientModel = patient;
    }

    @Override
    public void newAppointment() {
        CreateAppointmentFragment fragment = CreateAppointmentFragment.newInstance(getPatient());
        showFragment(fragment);
    }

    @Override
    public void rescheduleAppointment(AppointmentDTO appointmentDTO) {
        //Not apply for this flow. Need refactor
    }

    @Override
    public void onProviderSelected(AppointmentResourcesDTO appointmentResourcesDTO,
                                   AppointmentsResultModel appointmentsResultModel,
                                   ResourcesToScheduleDTO resourcesToScheduleDTO) {
        //TODO: Refactor this (take out Appointment Navigation Interface) after SHMRK-8014
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
        //TODO: Refactor this (take out Appointment Navigation Interface) after SHMRK-8014
    }

    @Override
    public void onDateRangeSelected(Date startDate, Date endDate, VisitTypeDTO visitTypeDTO,
                                    AppointmentResourcesItemDTO appointmentResource,
                                    AppointmentsResultModel appointmentsResultModel) {
        //TODO: Refactor this (take out Appointment Navigation Interface) after SHMRK-8014
    }

    @Override
    public void selectDateRange(Date startDate, Date endDate, VisitTypeDTO visitTypeDTO,
                                AppointmentResourcesItemDTO appointmentResource,
                                AppointmentsResultModel appointmentsResultModel) {
        //TODO: Refactor this (take out Appointment Navigation Interface) after SHMRK-8014
    }

    protected abstract LinksDTO getLinks();

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
    }

    @Override
    public void onDismissPaymentMethodDialog(PaymentsModel paymentsModel) {
        onHoursAndLocationSelected(appointmentSlot, null);
    }

    @Override
    public void showFragment(DialogFragment fragment) {
        String tag = fragment.getClass().getName();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(tag);
        fragment.show(ft, tag);
    }

    @Override
    public void displayToolbar(boolean display, String title) {

    }

    @Override
    public void setResourceProvider(AppointmentResourcesItemDTO resource) {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag(CreateAppointmentFragment.class.getName());
        if (fragment instanceof CreateAppointmentFragmentInterface) {
            ((CreateAppointmentFragmentInterface) fragment).setResourceProvider(resource);
        }
    }

    @Override
    public void setVisitType(VisitTypeDTO visitTypeDTO) {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag(CreateAppointmentFragment.class.getName());
        if (fragment instanceof CreateAppointmentFragmentInterface) {
            ((CreateAppointmentFragmentInterface) fragment).setVisitType(visitTypeDTO);
        }
    }

    @Override
    public void setLocation(LocationDTO locationDTO) {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag(CreateAppointmentFragment.class.getName());
        if (fragment instanceof CreateAppointmentFragmentInterface) {
            ((CreateAppointmentFragmentInterface) fragment).setLocation(locationDTO);
        }
    }

    @Override
    public void showAvailabilityHourFragment() {
        showFragment(AvailabilityHourFragment.newInstance());
    }

    @Override
    public void showAppointmentConfirmationFragment(AppointmentDTO appointmentDTO) {
        showFragment(PracticeModeRequestAppointmentDialog.newInstance(appointmentDTO, getPatient()));
    }

    @Override
    public void startPrepaymentProcess(ScheduleAppointmentRequestDTO appointmentRequestDto, double amount, String practiceId) {

    }

    @Override
    public void setDateRange(Date newStartDate, Date newEndDate) {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag(AvailabilityHourFragment.class.getName());
        if (fragment instanceof DateCalendarRangeInterface) {
            ((DateCalendarRangeInterface) fragment).setDateRange(newStartDate, newEndDate);
        }
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        //NOT VALID
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        //NOT VALID
    }
}
