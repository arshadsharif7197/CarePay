package com.carecloud.carepay.patient.checkout;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.fragments.AppointmentDateRangeFragment;
import com.carecloud.carepay.patient.appointments.fragments.AvailableHoursFragment;
import com.carecloud.carepay.patient.appointments.fragments.ChooseProviderFragment;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.patient.payment.androidpay.AndroidPayDialogFragment;
import com.carecloud.carepay.patient.payment.fragments.PatientPaymentMethodFragment;
import com.carecloud.carepay.patient.payment.fragments.PaymentMethodPrepaymentFragment;
import com.carecloud.carepay.patient.payment.fragments.ResponsibilityFragment;
import com.carecloud.carepay.patient.payment.interfaces.PatientPaymentMethodInterface;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.interfaces.AppointmentPrepaymentCallback;
import com.carecloud.carepaylibray.appointments.interfaces.AvailableHoursInterface;
import com.carecloud.carepaylibray.appointments.interfaces.DateRangeInterface;
import com.carecloud.carepaylibray.appointments.interfaces.ProviderInterface;
import com.carecloud.carepaylibray.appointments.interfaces.VisitTypeInterface;
import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.ResourcesToScheduleDTO;
import com.carecloud.carepaylibray.appointments.models.ScheduleAppointmentRequestDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.checkout.CheckOutFormFragment;
import com.carecloud.carepaylibray.checkout.CheckOutInterface;
import com.carecloud.carepaylibray.checkout.NextAppointmentFragment;
import com.carecloud.carepaylibray.checkout.NextAppointmentFragmentInterface;
import com.carecloud.carepaylibray.customcomponents.CustomMessageToast;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.fragments.AddNewCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.ChooseCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.PartialPaymentDialog;
import com.carecloud.carepaylibray.payments.fragments.PaymentConfirmationFragment;
import com.carecloud.carepaylibray.payments.interfaces.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.gson.Gson;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AppointmentCheckoutActivity extends BasePatientActivity implements CheckOutInterface,
        VisitTypeInterface, AvailableHoursInterface, DateRangeInterface, PaymentNavigationCallback,
        AppointmentPrepaymentCallback, ProviderInterface, PatientPaymentMethodInterface {

    private String appointmentId;
    private AppointmentDTO selectedAppointment;

    private AppointmentsResultModel appointmentsResultModel;
    private PaymentsModel paymentsModel;

    private boolean shouldAddBackStack = false;
    private boolean paymentStarted = false;

    private Fragment androidPayTargetFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_appointment);

        Bundle extra = getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO);
        appointmentId = extra.getString(CarePayConstants.APPOINTMENT_ID);

        if (savedInstanceState == null) {
            initDto(getConvertedDTO(WorkflowDTO.class));

            if(getAppointment() != null) {
                //Log Check-out Started
                String[] params = {getString(R.string.param_practice_id), getString(R.string.param_appointment_id), getString(R.string.param_appointment_type), getString(R.string.param_is_guest)};
                Object[] values = {getAppointment().getMetadata().getPracticeId(), getAppointmentId(), getAppointment().getPayload().getVisitType().getName(), false};
                MixPanelUtil.logEvent(getString(R.string.event_checkout_started), params, values);
                MixPanelUtil.startTimer(getString(R.string.timer_checkout));
            }
        }

        shouldAddBackStack = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PaymentConstants.REQUEST_CODE_CHANGE_MASKED_WALLET:
            case PaymentConstants.REQUEST_CODE_MASKED_WALLET:
            case PaymentConstants.REQUEST_CODE_FULL_WALLET:
                forwardAndroidPayResult(requestCode, resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    /**
     * Init current fragment based on the received workflow
     *
     * @param workflowDTO workflow dto
     */
    public void initDto(WorkflowDTO workflowDTO) {
        if (NavigationStateConstants.PATIENT_APP_CHECKOUT.equals(workflowDTO.getState())) {
            appointmentsResultModel = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, workflowDTO);
            showNextAppointmentFragment(appointmentId);
        } else if (NavigationStateConstants.PATIENT_PAY_CHECKOUT.equals(workflowDTO.getState())) {
            paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
            showResponsibilityFragment();
        } else if (NavigationStateConstants.PATIENT_FORM_CHECKOUT.equals(workflowDTO.getState())) {
            appointmentsResultModel = DtoHelper.getConvertedDTO(AppointmentsResultModel.class, workflowDTO);
            showCheckOutFormFragment();
        }
    }

    private void showCheckOutFormFragment() {
        replaceFragment(CheckOutFormFragment.newInstance(appointmentsResultModel), shouldAddBackStack);
    }

    private void showNextAppointmentFragment(String appointmentId) {
        replaceFragment(NextAppointmentFragment.newInstance(appointmentId), shouldAddBackStack);
    }

    private void showResponsibilityFragment() {
        paymentStarted = true;
        replaceFragment(ResponsibilityFragment
                .newInstance(paymentsModel, null, true,
                        Label.getLabel("checkout_responsibility_title")), shouldAddBackStack);
    }

    @Override
    public DTO getDto() {
        return appointmentsResultModel != null ? appointmentsResultModel : paymentsModel;
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
    public void onVisitTypeSelected(VisitTypeDTO visitTypeDTO,
                                    AppointmentResourcesDTO appointmentResourcesDTO,
                                    AppointmentsResultModel appointmentsResultModel) {

        NextAppointmentFragmentInterface fragment = (NextAppointmentFragmentInterface) getSupportFragmentManager()
                .findFragmentByTag(NextAppointmentFragment.class.getCanonicalName());
        if ((fragment != null) && fragment.setVisitType(visitTypeDTO, true)) {
            showAvailableHoursFragment(null, null, appointmentsResultModel,
                    appointmentResourcesDTO.getResource(), visitTypeDTO);
        }
    }

    @Override
    public void onDateRangeSelected(Date startDate, Date endDate, VisitTypeDTO visitTypeDTO,
                                    AppointmentResourcesItemDTO appointmentResource,
                                    AppointmentsResultModel appointmentsResultModel) {
        if (getSupportFragmentManager()
                .findFragmentByTag(AvailableHoursFragment.class.getCanonicalName()) != null) {
            getSupportFragmentManager().popBackStack();//close select date fragment
            getSupportFragmentManager().popBackStack();//close available hours fragment
        }
        AvailableHoursFragment availableHoursFragment = AvailableHoursFragment
                .newInstance(appointmentsResultModel, appointmentResource,
                        startDate, endDate, visitTypeDTO);
        addFragment(availableHoursFragment, true);
    }

    @Override
    public void onHoursAndLocationSelected(AppointmentsSlotsDTO appointmentsSlot,
                                           AppointmentAvailabilityDTO availabilityDTO) {
        getSupportFragmentManager().popBackStack();
        NextAppointmentFragmentInterface fragment = (NextAppointmentFragmentInterface) getSupportFragmentManager()
                .findFragmentByTag(NextAppointmentFragment.class.getCanonicalName());
        if (fragment != null) {
            fragment.setLocationAndTime(appointmentsSlot, true);
        }
    }

    @Override
    public void selectDateRange(Date startDate, Date endDate, VisitTypeDTO visitTypeDTO,
                                AppointmentResourcesItemDTO appointmentResource,
                                AppointmentsResultModel appointmentsResultModel) {
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_START_DATE_BUNDLE, startDate);
        bundle.putSerializable(CarePayConstants.ADD_APPOINTMENT_CALENDAR_END_DATE_BUNDLE, endDate);
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_PROVIDERS_BUNDLE, gson.toJson(appointmentResource));
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_VISIT_TYPE_BUNDLE, gson.toJson(visitTypeDTO));
        bundle.putString(CarePayConstants.ADD_APPOINTMENT_RESOURCE_TO_SCHEDULE_BUNDLE,
                gson.toJson(appointmentsResultModel));

        AppointmentDateRangeFragment appointmentDateRangeFragment = new AppointmentDateRangeFragment();
        appointmentDateRangeFragment.setArguments(bundle);
        replaceFragment(appointmentDateRangeFragment, true);
    }

    @Override
    public void onPayLaterClicked(PendingBalanceDTO pendingBalanceDTO) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", pendingBalanceDTO.getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", pendingBalanceDTO.getMetadata().getPracticeId());
        queryMap.put("appointment_id", appointmentId);
        queryMap.put("patient_id", pendingBalanceDTO.getMetadata().getPatientId());
        Map<String, String> header = getWorkflowServiceHelper().getApplicationStartHeaders();
        header.put("transition", "true");
        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata()
                .getPaymentsTransitions().getContinueTransition();
        getWorkflowServiceHelper().execute(transitionDTO, continueCallback, queryMap, header);

        MixPanelUtil.logEvent(getString(R.string.event_payment_skipped));
    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        replaceFragment(PatientPaymentMethodFragment.newInstance(paymentsModel, amount, false), true);

        MixPanelUtil.logEvent(getString(R.string.event_payment_make_full_payment));
    }

    @Override
    public void onPartialPaymentClicked(double owedAmount, PendingBalanceDTO selectedBalance) {
        new PartialPaymentDialog(getContext(), paymentsModel, selectedBalance).show();

        MixPanelUtil.logEvent(getString(R.string.event_payment_make_partial_payment));
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
    public void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod,
                                      double amount, PaymentsModel paymentsModel) {
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null &&
                !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            Fragment fragment = ChooseCreditCardFragment.newInstance(paymentsModel,
                    selectedPaymentMethod.getLabel(), amount);
            replaceFragment(fragment, true);
        } else {
            showAddCard(amount, paymentsModel);
        }
    }

    @Override
    public void showAddCard(double amount, PaymentsModel paymentsModel) {
        Fragment fragment = AddNewCreditCardFragment.newInstance(paymentsModel, amount);
        replaceFragment(fragment, true);
    }

    @Override
    public void onCreditCardSelected(PaymentCreditCardsPayloadDTO papiPaymentMethod) {
        //Works only when chooseCreditCardFragment is used in selectMode
    }

    @Override
    public void showPaymentConfirmation(WorkflowDTO workflowDTO) {
        String state = workflowDTO.getState();
        if (NavigationStateConstants.PATIENT_FORM_CHECKOUT.equals(state) ||
                (NavigationStateConstants.PATIENT_PAY_CHECKOUT.equals(state) && !paymentStarted)) {
            navigateToWorkflow(workflowDTO);
        } else {
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
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                Bundle args = new Bundle();
                args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, workflowDTO.toString());
                PaymentConfirmationFragment confirmationFragment = new PaymentConfirmationFragment();
                confirmationFragment.setArguments(args);
                displayDialogFragment(confirmationFragment, false);
            }
        }
    }

    @Override
    public void showPaymentPendingConfirmation(PaymentsModel paymentsModel) {
        new CustomMessageToast(this, Label.getLabel("payments_external_pending"), CustomMessageToast.NOTIFICATION_TYPE_SUCCESS).show();

        UserPracticeDTO userPracticeDTO = getPracticeInfo(paymentsModel);
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", userPracticeDTO.getPracticeMgmt());
        queryMap.put("practice_id", userPracticeDTO.getPracticeId());
        queryMap.put("appointment_id", appointmentId);
        queryMap.put("patient_id", userPracticeDTO.getPatientId());
        Map<String, String> header = getWorkflowServiceHelper().getApplicationStartHeaders();
        header.put("transition", "true");

        TransitionDTO continueTransition = paymentsModel.getPaymentsMetadata()
                .getPaymentsTransitions().getContinueTransition();
        getWorkflowServiceHelper().execute(continueTransition, continueCallback, queryMap, header);

    }

    @Override
    public void setAndroidPayTargetFragment(Fragment fragment) {
        androidPayTargetFragment = fragment;
    }

    @Override
    public Fragment getAndroidPayTargetFragment() {
        return androidPayTargetFragment;
    }

    @Override
    public UserPracticeDTO getPracticeInfo(PaymentsModel paymentsModel) {
        String patientId = getAppointment().getMetadata().getPatientId();
        for (UserPracticeDTO userPracticeDTO : paymentsModel.getPaymentPayload().getUserPractices()) {
            if (userPracticeDTO.getPatientId() != null && userPracticeDTO.getPatientId().equals(patientId)) {
                return userPracticeDTO;
            }
        }

        UserPracticeDTO userPracticeDTO = new UserPracticeDTO();
        userPracticeDTO.setPracticeId(getAppointment().getMetadata().getPracticeId());
        userPracticeDTO.setPracticeMgmt(getAppointment().getMetadata().getPracticeMgmt());
        userPracticeDTO.setPatientId(patientId);
        return userPracticeDTO;
    }

    @Nullable
    @Override
    public String getAppointmentId() {
        return appointmentId;
    }

    @Nullable
    @Override
    public AppointmentDTO getAppointment() {
        if (selectedAppointment == null && appointmentsResultModel != null) {
            for (AppointmentDTO appointment : appointmentsResultModel.getPayload().getAppointments()) {
                if (appointment.getPayload().getId().equals(getAppointmentId())) {
                    selectedAppointment = appointment;
                    return selectedAppointment;
                }
            }
        }
        return selectedAppointment;
    }

    @Override
    public void completePaymentProcess(WorkflowDTO workflowDTO) {
        if (paymentStarted) {
            PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
            UserPracticeDTO userPracticeDTO = getPracticeInfo(paymentsModel);
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("practice_mgmt", userPracticeDTO.getPracticeMgmt());
            queryMap.put("practice_id", userPracticeDTO.getPracticeId());
            queryMap.put("appointment_id", appointmentId);
            queryMap.put("patient_id", userPracticeDTO.getPatientId());
            Map<String, String> header = getWorkflowServiceHelper().getApplicationStartHeaders();
            header.put("transition", "true");

            TransitionDTO continueTransition = paymentsModel.getPaymentsMetadata()
                    .getPaymentsTransitions().getContinueTransition();
            getWorkflowServiceHelper().execute(continueTransition, continueCallback, queryMap, header);
        } else {
            PatientNavigationHelper.navigateToWorkflow(getContext(), workflowDTO,
                    getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO));
        }
    }

    @Override
    public void startPaymentProcess(PaymentsModel paymentsModel) {
    }

    @Override
    public void showAvailableHoursFragment(Date startDate, Date endDate,
                                           AppointmentsResultModel appointmentsResultModel,
                                           AppointmentResourcesItemDTO resourcesItemDTO,
                                           VisitTypeDTO visitTypeDTO) {
        AvailableHoursFragment availableHoursFragment = AvailableHoursFragment
                .newInstance(appointmentsResultModel, resourcesItemDTO, startDate, endDate, visitTypeDTO);
        addFragment(availableHoursFragment, true);

    }

    public void showAllDone(WorkflowDTO workflowDTO) {
        AllDoneDialogFragment fragment = AllDoneDialogFragment.newInstance(workflowDTO);
        displayDialogFragment(fragment, true);
    }

    @Override
    public boolean shouldAllowNavigateBack() {
        return true;
    }

    @Override
    public void showChooseProviderFragment() {
        selectedAppointment = getAppointment();
        String practiceMgmt = selectedAppointment.getMetadata().getPracticeMgmt();
        String practiceId = selectedAppointment.getMetadata().getPracticeId();

        ChooseProviderFragment fragment = ChooseProviderFragment.newInstance(appointmentsResultModel,
                practiceMgmt, practiceId);
        addFragment(fragment, true);
    }

    @Override
    public void completeCheckout() {
        //Log Check-out Completed
        if(getAppointment() != null) {
            String[] params = {getString(R.string.param_practice_id), getString(R.string.param_appointment_id), getString(R.string.param_appointment_type), getString(R.string.param_is_guest)};
            Object[] values = {getAppointment().getMetadata().getPracticeId(), getAppointmentId(), getAppointment().getPayload().getVisitType().getName(), false};
            MixPanelUtil.logEvent(getString(R.string.event_checkout_completed), params, values);
            MixPanelUtil.incrementPeopleProperty(getString(R.string.count_checkout_completed), 1);
            MixPanelUtil.endTimer(getString(R.string.timer_checkout));
        }
    }

    WorkflowServiceCallback continueCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            paymentStarted = false;
            hideProgressDialog();
            PatientNavigationHelper
                    .navigateToWorkflow(getContext(), workflowDTO,
                            getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO));

            completeCheckout();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getContext().getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    public void startPrepaymentProcess(ScheduleAppointmentRequestDTO appointmentRequestDTO,
                                       AppointmentsSlotsDTO appointmentSlot, double amount) {

        IntegratedPaymentPostModel postModel = new IntegratedPaymentPostModel();
        postModel.setAmount(amount);
        IntegratedPaymentLineItem paymentLineItem = new IntegratedPaymentLineItem();
        paymentLineItem.setAmount(amount);
        paymentLineItem.setProviderID(appointmentRequestDTO.getAppointment().getProviderGuid());
        paymentLineItem.setLocationID(appointmentSlot.getLocation().getGuid());
        paymentLineItem.setItemType(IntegratedPaymentLineItem.TYPE_PREPAYMENT);
        postModel.addLineItem(paymentLineItem);
        postModel.getMetadata().setAppointmentRequestDTO(appointmentRequestDTO.getAppointment());

        paymentsModel = new PaymentsModel();
        paymentsModel.getPaymentPayload().setPaymentSettings(appointmentsResultModel.getPayload()
                .getPaymentSettings());
        paymentsModel.getPaymentPayload().setMerchantServices(appointmentsResultModel.getPayload()
                .getMerchantServices());
        paymentsModel.getPaymentPayload().setPatientCreditCards(appointmentsResultModel.getPayload()
                .getPatientCreditCards());
        paymentsModel.getPaymentsMetadata().getPaymentsTransitions().setMakePayment(appointmentsResultModel
                .getMetadata().getTransitions().getMakePayment());
        paymentsModel.getPaymentPayload().setPaymentPostModel(postModel);
        PaymentMethodPrepaymentFragment prepaymentFragment = PaymentMethodPrepaymentFragment
                .newInstance(paymentsModel, amount);
        addFragment(prepaymentFragment, true);

        MixPanelUtil.logEvent(getString(R.string.event_payment_start_prepayment));
    }

    @Override
    public void onPaymentDismissed() {

    }

    @Override
    public void onProviderSelected(AppointmentResourcesDTO appointmentResourcesDTO,
                                   AppointmentsResultModel appointmentsResultModel,
                                   ResourcesToScheduleDTO resourcesToScheduleDTO) {
        getSupportActionBar().show();
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().executePendingTransactions();
        if (getSupportFragmentManager().findFragmentById(R.id.fragmentContainer)
                instanceof NextAppointmentFragmentInterface) {
            ((NextAppointmentFragmentInterface) getSupportFragmentManager()
                    .findFragmentById(R.id.fragmentContainer))
                    .setSelectedProvider(appointmentResourcesDTO.getResource().getProvider(), true);
        }

    }

    @Override
    public void createWalletFragment(MaskedWallet maskedWallet, Double amount) {
        replaceFragment(AndroidPayDialogFragment.newInstance(maskedWallet, paymentsModel, amount), true);
    }

    @Override
    public void forwardAndroidPayResult(int requestCode, int resultCode, Intent data) {
        Fragment targetFragment = getAndroidPayTargetFragment();
        if(targetFragment != null) {
            targetFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed(){
        if(getSupportFragmentManager().getBackStackEntryCount() < 1){
            MixPanelUtil.logEvent(getString(R.string.event_checkout_cancelled), getString(R.string.param_last_completed_step), getString(R.string.step_appointment));
        }
        super.onBackPressed();
    }
}
