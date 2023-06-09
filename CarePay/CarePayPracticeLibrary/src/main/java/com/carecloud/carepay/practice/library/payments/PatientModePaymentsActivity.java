package com.carecloud.carepay.practice.library.payments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.models.ResponsibilityHeaderModel;
import com.carecloud.carepay.practice.library.payments.adapter.PaymentBalancesAdapter;
import com.carecloud.carepay.practice.library.payments.dialogs.PatientModePaymentPlanDetailsDialogFragment;
import com.carecloud.carepay.practice.library.payments.dialogs.PaymentQueuedDialogFragment;
import com.carecloud.carepay.practice.library.payments.dialogs.ResponsibilityFragmentDialog;
import com.carecloud.carepay.practice.library.payments.fragments.PatientModePaymentPlanEditFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePartialPaymentDialogFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentMethodDialogFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentPlanAmountFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentPlanConfirmationFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.common.ConfirmationCallback;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.fragments.PaymentConfirmationFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanConfirmationFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanEditFragment;
import com.carecloud.carepaylibray.payments.interfaces.PaymentMethodDialogInterface;
import com.carecloud.carepaylibray.payments.interfaces.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanEditInterface;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentListItem;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentModel;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentPayload;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentExecution;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.payments.viewModel.PatientResponsibilityViewModel;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by pjohnson on 16/03/17
 */
public class PatientModePaymentsActivity extends BasePracticeActivity
        implements PaymentBalancesAdapter.PaymentRecyclerViewCallback,
        PaymentNavigationCallback, ResponsibilityFragmentDialog.PayResponsibilityCallback,
        PaymentMethodDialogInterface, PaymentPlanEditInterface {

    private PaymentsModel paymentResultModel;
    private PatientBalanceDTO selectedBalance;
    private PatientResponsibilityViewModel patientResponsibilityViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_payment);
        paymentResultModel = getConvertedDTO(PaymentsModel.class);
        patientResponsibilityViewModel = new ViewModelProvider(this).get(PatientResponsibilityViewModel.class);
        patientResponsibilityViewModel.setPaymentsModel(paymentResultModel);
        setUpUI();
    }

    private void setUpUI() {
        if (hasNoPayments()) {
            showNoPaymentsImage();
        } else {
            showPayments(paymentResultModel);
        }
        findViewById(R.id.btnHome).setOnClickListener(view -> goToHome(paymentResultModel.getPaymentsMetadata().getPaymentsTransitions().getLogout()));
        TextView logoutTextview = findViewById(R.id.logoutTextview);
        logoutTextview.setOnClickListener(view -> goToHome(paymentResultModel.getPaymentsMetadata().getPaymentsTransitions().getLogout()));
        logoutTextview.setText(Label.getLabel("practice_app_logout_text"));
    }

    private boolean hasNoPayments() {
        boolean hasNoPayments = true;
        Iterator<PatientBalanceDTO> iterator = paymentResultModel.getPaymentPayload().getPatientBalances().iterator();
        while (iterator.hasNext()) {
            PatientBalanceDTO patientBalanceDTO = iterator.next();
            if (Double.parseDouble(patientBalanceDTO.getPendingRepsonsibility()) > 0) {
                if (hasNoPayments) {
                    hasNoPayments = false;
                }
            } else {
                iterator.remove();//remove all 0 balances to prevent them from showing in the list
            }
        }
        if (!paymentResultModel.getPaymentPayload().getActivePlans(getApplicationMode()
                .getUserPracticeDTO().getPracticeId()).isEmpty()) {
            hasNoPayments = false;
        }
        return hasNoPayments;
    }

    private void showPayments(PaymentsModel paymentsModel) {
        RecyclerView recyclerView = findViewById(R.id.appointmentsRecyclerView);
        recyclerView.setVisibility(View.VISIBLE);
        findViewById(R.id.emptyPaymentsImageView).setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        PaymentBalancesAdapter paymentBalancesAdapter = new PaymentBalancesAdapter(this,
                paymentsModel, getBalances(paymentsModel),
                paymentsModel.getPaymentPayload().getUserPractices().get(0), this);
        recyclerView.setAdapter(paymentBalancesAdapter);

        ((TextView) findViewById(R.id.title)).setText(Label.getLabel("select_pending_payment_title"));
        ((TextView) findViewById(R.id.subtitle)).setText(Label.getLabel("no_pending_payment_description"));

    }

    private List<PaymentListItem> getBalances(PaymentsModel paymentsModel) {
        List<PaymentListItem> output = new ArrayList<>();
        output.addAll(paymentsModel.getPaymentPayload().getPatientBalances());
        output.addAll(paymentsModel.getPaymentPayload().getActivePlans(getApplicationMode()
                .getUserPracticeDTO().getPracticeId()));
        return output;
    }

    private void showNoPaymentsImage() {
        findViewById(R.id.emptyPaymentsImageView).setVisibility(View.VISIBLE);
        findViewById(R.id.appointmentsRecyclerView).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.title)).setText(Label.getLabel("no_payment_title"));
        ((TextView) findViewById(R.id.subtitle)).setText(Label.getLabel("no_pending_payment_description"));
    }

    @Override
    public void onBalancePayButtonClicked(PatientBalanceDTO patientBalanceDTO) {
        selectedBalance = patientBalanceDTO;
        startPaymentProcess(paymentResultModel);
    }

    @Override
    public void onPaymentPlanButtonClicked(PaymentPlanDTO paymentPlanDTO) {
        PatientModePaymentPlanDetailsDialogFragment fragment = PatientModePaymentPlanDetailsDialogFragment
                .newInstance(paymentResultModel, paymentPlanDTO, true);
        displayDialogFragment(fragment, true);
    }

    @Override
    public void startPaymentProcess(PaymentsModel paymentsModel) {
        if (selectedBalance == null) {
            if (paymentsModel.getPaymentPayload().getPatientBalances().isEmpty()) {
                return;
            }
            selectedBalance = paymentsModel.getPaymentPayload().getPatientBalances().get(0);
        }
        ResponsibilityHeaderModel headerModel = ResponsibilityHeaderModel.newClinicHeader(paymentsModel);
        ResponsibilityFragmentDialog dialog = ResponsibilityFragmentDialog
                .newInstance(paymentsModel, headerModel, selectedBalance);
        displayDialogFragment(dialog, false);
    }

    @Override
    public void onPartialPaymentClicked(double owedAmount, PendingBalanceDTO selectedBalance) {
        PracticePartialPaymentDialogFragment fragment = PracticePartialPaymentDialogFragment
                .newInstance(paymentResultModel, owedAmount);
        fragment.setOnCancelListener(dialogInterface -> startPaymentProcess(paymentResultModel));
        displayDialogFragment(fragment, true);

        MixPanelUtil.logEvent(getString(R.string.event_payment_make_partial_payment),
                getString(R.string.param_practice_id),
                selectedBalance.getMetadata().getPracticeId());
    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        PracticePaymentMethodDialogFragment fragment = PracticePaymentMethodDialogFragment
                .newInstance(paymentsModel, amount);
        fragment.setOnCancelListener(dialogInterface -> startPaymentProcess(paymentResultModel));
        displayDialogFragment(fragment, true);

        String[] params = {getString(R.string.param_balance_amount),
                getString(R.string.param_practice_id)
        };
        Object[] values = {amount,
                paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0)
                        .getMetadata().getPracticeId()
        };
        MixPanelUtil.logEvent(getString(R.string.event_payment_make_full_payment), params, values);
    }

    @Override
    public void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod,
                                      double amount,
                                      PaymentsModel paymentsModel) {
        //TODO: Delete this when refactor. This code is not used anymore
//        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null
//                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
//            PracticeChooseCreditCardFragment fragment = PracticeChooseCreditCardFragment
//                    .newInstance(paymentsModel, selectedPaymentMethod.getLabel(), amount);
//            displayDialogFragment(fragment, false);
//        } else {
//            showAddCard(amount, paymentsModel);
//        }
    }

    public void onPaymentPlanAction(final PaymentsModel paymentsModel) {
        PendingBalanceDTO selectedPendingBalance = selectedBalance.getBalances().get(0);
        selectedPendingBalance = paymentsModel.getPaymentPayload()
                .reduceBalanceItems(selectedPendingBalance, false);
        PracticePaymentPlanAmountFragment fragment = PracticePaymentPlanAmountFragment
                .newInstance(paymentsModel, selectedPendingBalance, true);
        fragment.setOnCancelListener(dialog -> startPaymentProcess(paymentsModel));
        displayDialogFragment(fragment, true);
    }

    @Override
    public void navigateToFragment(Fragment fragment, boolean addToBackStack) {
        //NA
    }

    @Override
    public void showPaymentConfirmation(WorkflowDTO workflowDTO) {
        showPaymentConfirmation(workflowDTO, false);
    }

    @Override
    public void showAddCard(double amount, PaymentsModel paymentsModel) {
        //TODO: Delete this when refactor. This code is not used anymore
//        PracticeAddNewCreditCardFragment fragment = PracticeAddNewCreditCardFragment
//                .newInstance(paymentsModel, amount);
//        displayDialogFragment(fragment, false);
    }

    @Override
    public void onCreditCardSelected(PaymentCreditCardsPayloadDTO creditCard) {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag(PatientModePaymentPlanEditFragment.class.getName());
        if (fragment instanceof PatientModePaymentPlanEditFragment) {
            ((PaymentPlanEditFragment) fragment).replacePaymentMethod(creditCard);
        }
    }

    @Override
    public void completePaymentProcess(WorkflowDTO workflowDTO) {
        popBackStackImmediate(PatientModePaymentPlanDetailsDialogFragment.class.getName());
        popBackStackImmediate(PracticePartialPaymentDialogFragment.class.getName());
        popBackStackImmediate(PracticePaymentMethodDialogFragment.class.getName());
        refreshBalance();
    }

    private void popBackStackImmediate(String className) {
        getSupportFragmentManager().popBackStackImmediate(className, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onPayLaterClicked(PendingBalanceDTO pendingBalanceDTO) {
        //NA
    }

    @Override
    public UserPracticeDTO getPracticeInfo(PaymentsModel paymentsModel) {
        if (paymentsModel != null && !paymentsModel.getPaymentPayload().getUserPractices().isEmpty()) {
            return paymentsModel.getPaymentPayload().getUserPractices().get(0);
        }
        return null;
    }

    @Override
    public void onPaymentCashFinished() {
        //NA
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
    public void onLeftActionTapped(PaymentsModel paymentsModel, double owedAmount) {
        onPaymentPlanAction(paymentsModel);
    }

    @Override
    public void onRightActionTapped(PaymentsModel paymentsModel, double amount) {
        onPayButtonClicked(amount, paymentsModel);
    }

    @Override
    public void onMiddleActionTapped(PaymentsModel paymentsModel, double amount) {
        PendingBalanceDTO pendingBalanceDTO = paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances()
                .get(0);
        onPartialPaymentClicked(amount, pendingBalanceDTO);
    }

    private void refreshBalance() {
        Map<String, String> queryMap = createQueryMap();
        TransitionDTO transitionDTO = paymentResultModel.getPaymentsMetadata()
                .getPaymentsLinks().getPaymentsPatientBalances();
        getWorkflowServiceHelper().execute(transitionDTO, patientBalancesCallback, queryMap);
    }

    @NonNull
    private Map<String, String> createQueryMap() {
        Map<String, String> queryMap = new HashMap<>();

        if (!paymentResultModel.getPaymentPayload().getPatientBalances().isEmpty() &&
                !paymentResultModel.getPaymentPayload().getPatientBalances().get(0).getBalances().isEmpty()) {
            PendingBalanceDTO patientBalanceDTO = paymentResultModel.getPaymentPayload()
                    .getPatientBalances().get(0).getBalances().get(0);

            queryMap.put("patient_id", patientBalanceDTO.getMetadata().getPatientId());
            queryMap.put("practice_mgmt", patientBalanceDTO.getMetadata().getPracticeMgmt());
            queryMap.put("practice_id", patientBalanceDTO.getMetadata().getPracticeId());
        } else if (!paymentResultModel.getPaymentPayload().getPatientPaymentPlans().isEmpty()) {
            PaymentPlanDTO paymentPlanDTO = paymentResultModel.getPaymentPayload().getPatientPaymentPlans().get(0);
            queryMap.put("patient_id", paymentPlanDTO.getMetadata().getPatientId());
            queryMap.put("practice_mgmt", paymentPlanDTO.getMetadata().getPracticeMgmt());
            queryMap.put("practice_id", paymentPlanDTO.getMetadata().getPracticeId());
        }
        return queryMap;
    }

    private WorkflowServiceCallback patientBalancesCallback = new WorkflowServiceCallback() {

        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            paymentResultModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO.toString());
            if (hasNoPayments()) {
                showNoPaymentsImage();
            } else {
                showPayments(paymentResultModel);
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
        }
    };

    @Override
    protected void processExternalPayment(PaymentExecution execution, Intent data) {
        switch (execution) {
            case clover: {
                String jsonPayload = data.getStringExtra(CarePayConstants.CLOVER_PAYMENT_SUCCESS_INTENT_DATA);
                if (jsonPayload != null) {
                    Gson gson = new Gson();
                    WorkflowDTO workflowDTO = gson.fromJson(jsonPayload, WorkflowDTO.class);
                    showPaymentConfirmation(workflowDTO);
                }
                break;
            }
            default:
                //nothing
        }
    }

    @Override
    protected void processExternalPaymentFailure(PaymentExecution paymentExecution, int resultCode) {
        if (resultCode == CarePayConstants.PAYMENT_RETRY_PENDING_RESULT_CODE) {
            //Display a success notification and do some cleanup
            PaymentQueuedDialogFragment dialogFragment = new PaymentQueuedDialogFragment();
            DialogInterface.OnDismissListener dismissListener = dialog -> {
                Intent intent = getIntent();
                setResult(CarePayConstants.HOME_PRESSED, intent);
                finish();
            };
            dialogFragment.setOnDismissListener(dismissListener);
            displayDialogFragment(dialogFragment, false);
        }
    }

    @Override
    public void onCashSelected(PaymentsModel paymentsModel) {
        //Not Implemented
    }

    @Override
    public void onStartPaymentPlan(PaymentsModel paymentsModel,
                                   PaymentPlanPostModel paymentPlanPostModel) {
        //TODO: Delete this when refactor. This code is not used anymore
//        PracticePaymentPlanPaymentMethodFragment fragment = PracticePaymentPlanPaymentMethodFragment
//                .newInstance(paymentsModel, paymentPlanPostModel);
//        displayDialogFragment(fragment, false);
    }

    @Override
    public void onSubmitPaymentPlan(WorkflowDTO workflowDTO) {
        PracticePaymentPlanConfirmationFragment fragment = PracticePaymentPlanConfirmationFragment
                .newInstance(workflowDTO, getApplicationMode().getUserPracticeDTO(),
                        PaymentPlanConfirmationFragment.MODE_CREATE);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void showScheduledPaymentConfirmation(WorkflowDTO workflowDTO) {
        popBackStackImmediate(PatientModePaymentPlanDetailsDialogFragment.class.getName());
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        ScheduledPaymentModel scheduledPayment = paymentsModel.getPaymentPayload()
                .getScheduledPaymentModel();

        if (scheduledPayment.getMetadata().getOneTimePaymentId() == null &&
                !paymentsModel.getPaymentPayload().getScheduledOneTimePayments().isEmpty()) {
            scheduledPayment = paymentsModel.getPaymentPayload().getScheduledOneTimePayments().get(0);
        }

        List<ScheduledPaymentModel> scheduledPaymentModels = this.paymentResultModel
                .getPaymentPayload().getScheduledOneTimePayments();
        for (ScheduledPaymentModel scheduledPaymentModel : scheduledPaymentModels) {
            if (scheduledPaymentModel.getMetadata().getOneTimePaymentId()
                    .equals(scheduledPayment.getMetadata().getOneTimePaymentId())) {
                scheduledPaymentModels.remove(scheduledPayment);
                break;
            }
        }
        scheduledPaymentModels.add(scheduledPayment);
        completePaymentProcess(workflowDTO);

        DateUtil.getInstance().setDateRaw(scheduledPayment.getPayload().getPaymentDate());
        String message = String.format(Label.getLabel("payment.oneTimePayment.schedule.success"),
                StringUtil.getFormattedBalanceAmount(scheduledPayment.getPayload().getAmount()),
                DateUtil.getInstance().toStringWithFormatMmSlashDdSlashYyyy());
        showSuccessToast(message);

        MixPanelUtil.incrementPeopleProperty(getString(R.string.count_payments_scheduled), 1);
    }

    @Override
    public void showDeleteScheduledPaymentConfirmation(WorkflowDTO workflowDTO,
                                                       ScheduledPaymentPayload scheduledPaymentPayload) {
        popBackStackImmediate(PatientModePaymentPlanDetailsDialogFragment.class.getName());
        showSuccessToast(String.format(
                Label.getLabel("payment.oneTimePayment.scheduled.delete.success"),
                StringUtil.getFormattedBalanceAmount(scheduledPaymentPayload.getAmount()),
                DateUtil.getInstance()
                        .setDateRaw(scheduledPaymentPayload.getPaymentDate())
                        .toStringWithFormatMmSlashDdSlashYyyy()));
        completePaymentProcess(workflowDTO);

        MixPanelUtil.incrementPeopleProperty(getString(R.string.count_scheduled_payments_deleted), 1);
    }

    @Override
    public void showPaymentConfirmation(WorkflowDTO workflowDTO, boolean isOneTimePayment) {
        if (workflowDTO != null) {
            PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
            IntegratedPatientPaymentPayload payload = paymentsModel.getPaymentPayload().getPatientPayments().getPayload();
            if (!payload.getProcessingErrors().isEmpty() && payload.getTotalPaid() == 0D) {
                StringBuilder builder = new StringBuilder();
                for (IntegratedPatientPaymentPayload.ProcessingError processingError : payload.getProcessingErrors()) {
                    builder.append(processingError.getError());
                    builder.append("\n");
                }
                int last = builder.lastIndexOf("\n");
                builder.replace(last, builder.length(), "");
                showErrorNotification(builder.toString());
            } else {
                PaymentConfirmationFragment confirmationFragment = PaymentConfirmationFragment
                        .newInstance(workflowDTO, isOneTimePayment);
                displayDialogFragment(confirmationFragment, false);

                if (isOneTimePayment) {
                    MixPanelUtil.incrementPeopleProperty(getString(R.string.count_one_time_payments_completed), 1);
                }
            }
        }
    }

    @Override
    public void onPaymentPlanEdited(WorkflowDTO workflowDTO) {
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        if (paymentsModel.getPaymentPayload().getPatientPaymentPlans().isEmpty()) {
            //no changes to plan
            return;
        }
        PracticePaymentPlanConfirmationFragment fragment = PracticePaymentPlanConfirmationFragment
                .newInstance(workflowDTO, getApplicationMode().getUserPracticeDTO(),
                        PaymentPlanConfirmationFragment.MODE_EDIT);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onPaymentPlanAddedExisting(WorkflowDTO workflowDTO) {
        PracticePaymentPlanConfirmationFragment fragment = PracticePaymentPlanConfirmationFragment
                .newInstance(workflowDTO, getApplicationMode().getUserPracticeDTO(),
                        PaymentPlanConfirmationFragment.MODE_ADD);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onPaymentPlanCanceled(WorkflowDTO workflowDTO, boolean isDeleted) {
        String message = Label.getLabel("payment.cancelPaymentPlan.success.banner.text");
        if (isDeleted) {
            message = Label.getLabel("payment.deletePaymentPlan.success.banner.text");
        }
        showSuccessToast(message);
        popBackStackImmediate(PatientModePaymentPlanDetailsDialogFragment.class.getName());
        refreshBalance();
    }

    @Override
    public void showCancelPaymentPlanConfirmDialog(ConfirmationCallback confirmationCallback, boolean isDeleted) {
        //TODO: Delete this when refactor. This code is not used anymore
//        String title = Label.getLabel("payment.cancelPaymentPlan.confirmation.popup.title");
//        String message = Label.getLabel("payment.cancelPaymentPlan.confirmation.popup.message");
//        if (isDeleted) {
//            title = Label.getLabel("payment.deletePaymentPlan.confirmation.popup.title");
//            message = Label.getLabel("payment.deletePaymentPlan.confirmation.popup.message");
//        }
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance(title, message);
//        confirmDialogFragment.setCallback(confirmationCallback);
//        String tag = confirmDialogFragment.getClass().getName();
//        confirmDialogFragment.show(ft, tag);
    }

    @Override
    public DTO getDto() {
        return paymentResultModel;
    }

    @Override
    public void completePaymentPlanProcess(WorkflowDTO workflowDTO) {
        popBackStackImmediate(PracticePaymentPlanAmountFragment.class.getName());
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        List<PaymentPlanDTO> paymentPlanList = this.paymentResultModel.getPaymentPayload()
                .getPatientPaymentPlans();
        this.paymentResultModel.getPaymentPayload().setPatientBalances(paymentsModel
                .getPaymentPayload().getPatientBalances());


        if (!paymentPlanList.isEmpty()) {
            PaymentPlanDTO modifiedPaymentPlan = paymentsModel.getPaymentPayload()
                    .getPatientPaymentPlans().get(0);
            for (PaymentPlanDTO paymentPlan : paymentPlanList) {
                if (modifiedPaymentPlan.getMetadata().getPaymentPlanId()
                        .equals(paymentPlan.getMetadata().getPaymentPlanId())) {
                    paymentPlanList.remove(paymentPlan);
                    paymentPlanList.add(modifiedPaymentPlan);
                    completePaymentProcess(workflowDTO);
                    return;
                }
            }
        }

        //payment plan not found for modification
        paymentPlanList.addAll(paymentsModel.getPaymentPayload().getPatientPaymentPlans());
        completePaymentProcess(workflowDTO);
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        //NA
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        //NA
    }

    @Override
    public boolean manageSession() {
        return true;
    }

    @Override
    public TransitionDTO getLogoutTransition() {
        return paymentResultModel.getPaymentsMetadata().getPaymentsTransitions().getLogout();
    }
}
