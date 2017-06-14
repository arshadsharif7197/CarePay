package com.carecloud.carepay.practice.library.patientmodecheckin.activities;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.dialogs.PracticeAvailableHoursDialogFragment;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.customdialog.DateRangePickerDialog;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.ResponsibilityCheckOutFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeAddNewCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeChooseCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePartialPaymentDialogFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentMethodDialogFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
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
import com.carecloud.carepaylibray.checkout.CheckOutFormFragment;
import com.carecloud.carepaylibray.checkout.CheckOutInterface;
import com.carecloud.carepaylibray.checkout.NextAppointmentFragment;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.fragments.PaymentConfirmationFragment;
import com.carecloud.carepaylibray.payments.interfaces.PaymentMethodDialogInterface;
import com.carecloud.carepaylibray.payments.interfaces.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lmenendez on 6/13/17
 */

public class PatientModeCheckoutActivity extends BasePracticeActivity implements CheckOutInterface, VisitTypeInterface,
        AvailableHoursInterface, DateRangeInterface, PaymentNavigationCallback,
        PaymentMethodDialogInterface, DateRangePickerDialog.DateRangePickerDialogListener {

    private AppointmentsResultModel appointmentsResultModel;
    private PaymentsModel paymentsModel;
    private String appointmentId;

    private Date startDate;
    private Date endDate;
    private AppointmentResourcesDTO appointmentResourcesDTO;
    private VisitTypeDTO visitTypeDTO;

    private boolean shouldAddBackStack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_checkout);
        initViews();

        Bundle extra = getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO);
        appointmentId = extra.getString(CarePayConstants.APPOINTMENT_ID);
        if (savedInstanceState == null) {
            WorkflowDTO workflowDTO = getConvertedDTO(WorkflowDTO.class);
            initDto(workflowDTO);
        }

        shouldAddBackStack = true;
    }

    /**
     * Init current fragment based on the received workflow
     * @param workflowDTO workflow dto
     */
    public void initDto(WorkflowDTO workflowDTO){
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

    private void initViews(){
        View logout = findViewById(R.id.logoutTextview);
        logout.setOnClickListener(homeClick);

        View home = findViewById(R.id.btnHome);
        home.setOnClickListener(homeClick);
    }

    private void showCheckOutFormFragment() {
        replaceFragment(CheckOutFormFragment.newInstance(appointmentsResultModel), shouldAddBackStack);
    }

    private void showNextAppointmentFragment(String appointmentId) {
        replaceFragment(NextAppointmentFragment.newInstance(appointmentId), shouldAddBackStack);
    }

    private void showResponsibilityFragment() {
        ResponsibilityCheckOutFragment responsibilityFragment = new ResponsibilityCheckOutFragment();
        Bundle bundle = new Bundle();
        DtoHelper.bundleDto(bundle, paymentsModel);
        responsibilityFragment.setArguments(bundle);

        replaceFragment(responsibilityFragment, shouldAddBackStack);
    }

    @Override
    public void onBackPressed(){
        if(shouldAllowNavigateBack()){
            super.onBackPressed();
        }
    }


    @Override
    public void onDetailCancelClicked(PaymentsModel paymentsModel) {
        //can be ignored
    }

    @Override
    public void showAddCard(double amount, PaymentsModel paymentsModel) {
        Gson gson = new Gson();
        Bundle args = new Bundle();
        String paymentsDTOString = gson.toJson(paymentsModel);
        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, paymentsDTOString);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        DialogFragment fragment = new PracticeAddNewCreditCardFragment();
        fragment.setArguments(args);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onPaymentPlanAction(PaymentsModel paymentsModel) {
        //todo whenever payment plans are ready
    }

    @Override
    public void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod, double amount, PaymentsModel paymentsModel) {
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            DialogFragment fragment = PracticeChooseCreditCardFragment.newInstance(paymentsModel,
                    selectedPaymentMethod.getLabel(), amount);
            displayDialogFragment(fragment, false);
        } else {
            showAddCard(amount, paymentsModel);
        }
    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        PracticePaymentMethodDialogFragment fragment = PracticePaymentMethodDialogFragment
                .newInstance(paymentsModel, amount);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onVisitTypeSelected(VisitTypeDTO visitTypeDTO, AppointmentResourcesDTO appointmentResourcesDTO, AppointmentsResultModel appointmentsResultModel) {
        this.visitTypeDTO = visitTypeDTO;
        this.appointmentResourcesDTO = appointmentResourcesDTO;
        this.appointmentsResultModel = appointmentsResultModel;
        NextAppointmentFragment fragment = (NextAppointmentFragment) getSupportFragmentManager()
                .findFragmentByTag(NextAppointmentFragment.class.getCanonicalName());
        if ((fragment != null) && fragment.setVisitType(visitTypeDTO)) {
            showAvailableHoursFragment(null, null, appointmentsResultModel, appointmentResourcesDTO.getResource(), visitTypeDTO);
        }
    }

    @Override
    public void completePaymentProcess(WorkflowDTO workflowDTO) {
        PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO,
                getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO));
    }

    @Override
    public void onDateRangeSelected(Date startDate, Date endDate, VisitTypeDTO visitTypeDTO, AppointmentResourcesItemDTO appointmentResource, AppointmentsResultModel appointmentsResultModel) {
        if (getSupportFragmentManager().findFragmentByTag(PracticeAvailableHoursDialogFragment.class.getCanonicalName()) != null) {
            getSupportFragmentManager().popBackStack();//close select date fragment
            getSupportFragmentManager().popBackStack();//close available hours fragment
        }
        showAvailableHoursFragment(startDate, endDate, appointmentsResultModel, appointmentResource, visitTypeDTO);
    }

    @Override
    public void showPaymentConfirmation(WorkflowDTO workflowDTO) {
        Bundle args = new Bundle();
        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, workflowDTO.toString());

        PaymentConfirmationFragment confirmationFragment = new PaymentConfirmationFragment();
        confirmationFragment.setArguments(args);

        displayDialogFragment(confirmationFragment, true);
    }

    @Override
    public UserPracticeDTO getPracticeInfo(PaymentsModel paymentsModel) {
        return null;
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
        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getContinueTransition();
        getWorkflowServiceHelper().execute(transitionDTO, continueCallback, queryMap, header);
    }

    @Override
    public void onPartialPaymentClicked(double owedAmount) {
        PracticePartialPaymentDialogFragment dialog = PracticePartialPaymentDialogFragment
                .newInstance(paymentsModel, owedAmount);
        displayDialogFragment(dialog, false);
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
        DateUtil dateUtil = DateUtil.getInstance().setToCurrent();
        DateRangePickerDialog dialog = DateRangePickerDialog.newInstance(
                Label.getLabel("date_range_picker_dialog_title"),
                Label.getLabel("datepicker_cancel_option"),
                false,
                startDate,
                endDate,
                dateUtil.getDate(),
                dateUtil.addDays(92).getDate(),
                this
        );

        displayDialogFragment(dialog, false);
    }

    @Override
    public void startPaymentProcess(PaymentsModel paymentsModel) {

    }

    private View.OnClickListener homeClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AppointmentsResultModel appointmentsResultModel = getConvertedDTO(AppointmentsResultModel.class);
            goToHome(appointmentsResultModel.getMetadata().getTransitions().getLogout());
        }
    };

    @Override
    public DTO getDto() {
        return appointmentsResultModel != null ? appointmentsResultModel : paymentsModel;
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.root_layout, fragment, addToBackStack);
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        addFragment(R.id.root_layout, fragment, addToBackStack);
    }

    @Override
    public void showAvailableHoursFragment(Date start, Date end, AppointmentsResultModel appointmentsResultModel, AppointmentResourcesItemDTO resourcesItemDTO, VisitTypeDTO visitTypeDTO) {
        PracticeAvailableHoursDialogFragment fragment = PracticeAvailableHoursDialogFragment.newInstance(appointmentsResultModel, resourcesItemDTO, start, end, visitTypeDTO);
        fragment.show(getSupportFragmentManager(), fragment.getClass().getName());
    }

    @Override
    public void showAllDone(WorkflowDTO workflowDTO) {

    }

    @Override
    public boolean shouldAllowNavigateBack() {
        return getSupportFragmentManager().getBackStackEntryCount() > 0;
    }

    @Override
    public void onRangeSelected(Date start, Date end) {
        this.startDate = start;
        this.endDate = end;

        onDateRangeCancelled();
    }

    @Override
    public void onDateRangeCancelled() {
        onDateRangeSelected(startDate, endDate, visitTypeDTO, appointmentResourcesDTO.getResource(), appointmentsResultModel);
    }

    WorkflowServiceCallback continueCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            Log.e(getContext().getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Override
    public void onDismissPaymentMethodDialog(PaymentsModel paymentsModel) {

    }
}
