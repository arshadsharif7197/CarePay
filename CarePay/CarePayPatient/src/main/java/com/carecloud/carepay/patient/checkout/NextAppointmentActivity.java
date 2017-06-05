package com.carecloud.carepay.patient.checkout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.fragments.AppointmentDateRangeFragment;
import com.carecloud.carepay.patient.appointments.fragments.AvailableHoursFragment;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.payment.fragments.PatientPaymentMethodFragment;
import com.carecloud.carepay.patient.payment.fragments.ResponsibilityFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.interfaces.AvailableHoursInterface;
import com.carecloud.carepaylibray.appointments.interfaces.DateRangeInterface;
import com.carecloud.carepaylibray.appointments.interfaces.VisitTypeInterface;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.fragments.AddNewCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.ChooseCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentConfirmationFragment;
import com.carecloud.carepaylibray.payments.interfaces.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.google.gson.Gson;

import java.util.Date;

public class NextAppointmentActivity extends BasePatientActivity implements CheckOutInterface,
        VisitTypeInterface, AvailableHoursInterface, DateRangeInterface, PaymentNavigationCallback {

    private DTO checkOutDto;
    public static final String APPOINTMENT_ID = "appointmentId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_appointment);
        Bundle extra = getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO);
        String appointmentId = extra.getString(APPOINTMENT_ID);
        if (savedInstanceState == null) {
            if (NavigationStateConstants.PATIENT_APP_CHECKOUT.equals(extra.getString("state"))) {
                checkOutDto = getConvertedDTO(AppointmentsResultModel.class);
                showNextAppointmentFragment(appointmentId);
            } else if (NavigationStateConstants.PATIENT_PAY_CHECKOUT.equals(extra.getString("state"))) {
                checkOutDto = getConvertedDTO(PaymentsModel.class);
                showResponsibilityFragment();
            } else if (NavigationStateConstants.PATIENT_FORM_CHECKOUT.equals(extra.getString("state"))) {
                checkOutDto = getConvertedDTO(AppointmentsResultModel.class);
                showCheckOutFormFragment();
            }
        }
    }

    private void showCheckOutFormFragment() {
        replaceFragment(CheckOutFormFragment.newInstance((AppointmentsResultModel) checkOutDto), false);
    }

    private void showNextAppointmentFragment(String appointmentId) {
        replaceFragment(NextAppointmentFragment.newInstance(appointmentId), false);
    }

    private void showResponsibilityFragment() {
        replaceFragment(ResponsibilityFragment
                        .newInstance(getConvertedDTO(PaymentsModel.class), null, false, "checkout_responsibility_title"),
                false);
    }

    @Override
    public DTO getDto() {
        return checkOutDto;
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.fragmentContainer, fragment, addToBackStack);
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        addFragment(R.id.fragmentContainer, fragment, addToBackStack);
    }

    @Override
    public void onVisitTypeSelected(VisitTypeDTO visitTypeDTO, AppointmentResourcesDTO appointmentResourcesDTO, AppointmentsResultModel appointmentsResultModel) {
        NextAppointmentFragment fragment = (NextAppointmentFragment) getSupportFragmentManager()
                .findFragmentByTag(NextAppointmentFragment.class.getCanonicalName());
        if (fragment != null) {
            if (fragment.setVisitType(visitTypeDTO)) {
                fragment.showAvailableHoursFragment();
            }
        }
    }

    @Override
    public void onDateRangeSelected(Date startDate, Date endDate, VisitTypeDTO visitTypeDTO, AppointmentResourcesItemDTO appointmentResource, AppointmentsResultModel appointmentsResultModel) {
        if (getSupportFragmentManager().findFragmentByTag(AvailableHoursFragment.class.getCanonicalName()) != null) {
            getSupportFragmentManager().popBackStack();//close select date fragment
            getSupportFragmentManager().popBackStack();//close available hours fragment
        }
        AvailableHoursFragment availableHoursFragment = AvailableHoursFragment
                .newInstance(appointmentsResultModel, appointmentResource,
                        startDate, endDate, visitTypeDTO);
        addFragment(availableHoursFragment, true);
    }

    @Override
    public void onHoursAndLocationSelected(AppointmentsSlotsDTO appointmentsSlot, AppointmentAvailabilityDTO availabilityDTO) {
        getSupportFragmentManager().popBackStack();
        NextAppointmentFragment fragment = (NextAppointmentFragment) getSupportFragmentManager()
                .findFragmentByTag(NextAppointmentFragment.class.getCanonicalName());
        if (fragment != null) {
            fragment.setLocationAndTime(appointmentsSlot);
        }
    }

    @Override
    public void selectDateRange(Date startDate, Date endDate, VisitTypeDTO visitTypeDTO, AppointmentResourcesItemDTO appointmentResource, AppointmentsResultModel appointmentsResultModel) {
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_START_DATE_BUNDLE, startDate);
        bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_END_DATE_BUNDLE, endDate);
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_PROVIDERS_BUNDLE, gson.toJson(appointmentResource));
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_VISIT_TYPE_BUNDLE, gson.toJson(visitTypeDTO));
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_RESOURCE_TO_SCHEDULE_BUNDLE, gson.toJson(appointmentsResultModel));

        AppointmentDateRangeFragment appointmentDateRangeFragment = new AppointmentDateRangeFragment();
        appointmentDateRangeFragment.setArguments(bundle);
        replaceFragment(appointmentDateRangeFragment, true);
    }

    @Override
    public void onPayLaterClicked(PaymentsModel paymentsModel) {
        //Does not apply here due to business logic
    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        replaceFragment(PatientPaymentMethodFragment.newInstance(paymentsModel, amount), true);
    }

    @Override
    public void onPartialPaymentClicked(double owedAmount) {

    }

    @Override
    public void onDetailCancelClicked(PaymentsModel paymentsModel) {
        showResponsibilityFragment();
    }

    @Override
    public void onPaymentPlanAction(PaymentsModel paymentsModel) {
        //NOT Yet
    }

    @Override
    public void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod, double amount, PaymentsModel paymentsModel) {
        PaymentsModel paymentsDTO = (PaymentsModel) checkOutDto;
        if (paymentsDTO.getPaymentPayload().getPatientCreditCards() != null &&
                !paymentsDTO.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            Fragment fragment = ChooseCreditCardFragment.newInstance(paymentsDTO, selectedPaymentMethod.getLabel(), amount);
            replaceFragment(fragment, true);
        } else {
            showAddCard(amount, paymentsModel);
        }
    }

    @Override
    public void showAddCard(double amount, PaymentsModel paymentsModel) {
        Gson gson = new Gson();
        Bundle args = new Bundle();
        Fragment fragment;
        String paymentsDTOString = gson.toJson(paymentsModel);
        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, paymentsDTOString);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        fragment = new AddNewCreditCardFragment();


        fragment.setArguments(args);
        replaceFragment(fragment, true);
    }

    @Override
    public void showPaymentConfirmation(WorkflowDTO workflowDTO) {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Bundle args = new Bundle();
        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, workflowDTO.toString());

        PaymentConfirmationFragment confirmationFragment = new PaymentConfirmationFragment();
        confirmationFragment.setArguments(args);

        displayDialogFragment(confirmationFragment, true);
    }

    @Override
    public UserPracticeDTO getPracticeInfo(PaymentsModel paymentsModel) {
        String patientId = paymentsModel.getPaymentPayload().getPatientBalances().get(0).getDemographics().getMetadata().getPatientId();
        for (UserPracticeDTO userPracticeDTO : paymentsModel.getPaymentPayload().getUserPractices()) {
            if (userPracticeDTO.getPatientId() != null && userPracticeDTO.getPatientId().equals(patientId)) {
                return userPracticeDTO;
            }
        }
        return null;
    }

    @Override
    public void completePaymentProcess(WorkflowDTO workflowDTO) {
        PatientNavigationHelper.navigateToWorkflow(getContext(), workflowDTO,
                getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO));
    }

    @Override
    public void startPaymentProcess(PaymentsModel paymentsModel) {
    }

    public void showAllDoneFragment(WorkflowDTO workflowDTO) {
        AllDoneDialogFragment fragment = AllDoneDialogFragment.newInstance(workflowDTO);
        displayDialogFragment(fragment, true);
    }
}
