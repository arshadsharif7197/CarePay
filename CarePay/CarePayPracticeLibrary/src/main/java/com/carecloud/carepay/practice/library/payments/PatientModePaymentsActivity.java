package com.carecloud.carepay.practice.library.payments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.models.ResponsibilityHeaderModel;
import com.carecloud.carepay.practice.library.payments.adapter.PaymentBalancesAdapter;
import com.carecloud.carepay.practice.library.payments.dialogs.PatientModePaymentPlanDetailsDialogFragment;
import com.carecloud.carepay.practice.library.payments.dialogs.PaymentDetailsFragmentDialog;
import com.carecloud.carepay.practice.library.payments.dialogs.PaymentQueuedDialogFragment;
import com.carecloud.carepay.practice.library.payments.dialogs.ResponsibilityFragmentDialog;
import com.carecloud.carepay.practice.library.payments.fragments.PatientModeAddExistingPaymentPlanFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PatientModePaymentPlanEditFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PatientModePaymentPlanFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeAddNewCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeChooseCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeOneTimePaymentFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePartialPaymentDialogFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentMethodDialogFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentPlanAddCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentPlanAmountFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentPlanChooseCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentPlanConfirmationFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentPlanPaymentMethodFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentPlanTermsFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeValidPlansFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.common.ConfirmationCallback;
import com.carecloud.carepaylibray.customdialogs.LargeAlertDialog;
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
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentExecution;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
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
    private PaymentBalancesAdapter paymentBalancesAdapter;
    private PaymentPlanDTO paymentPlan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_payment);
        paymentResultModel = getConvertedDTO(PaymentsModel.class);

        setUpUI();
    }

    private void setUpUI() {
        if (hasNoPayments()) {
            showNoPaymentsImage();
        } else {
            showPayments(paymentResultModel);
        }
        findViewById(R.id.btnHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHome(paymentResultModel.getPaymentsMetadata().getPaymentsTransitions().getLogout());
            }
        });
        TextView logoutTextview = (TextView) findViewById(R.id.logoutTextview);
        logoutTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHome(paymentResultModel.getPaymentsMetadata().getPaymentsTransitions().getLogout());
            }
        });
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
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.appointmentsRecyclerView);
        recyclerView.setVisibility(View.VISIBLE);
        findViewById(R.id.emptyPaymentsImageView).setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        paymentBalancesAdapter = new PaymentBalancesAdapter(this, getBalances(paymentsModel),
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
    public void onPaymentPlanButonClicked(PaymentPlanDTO paymentPlanDTO) {
        PatientModePaymentPlanDetailsDialogFragment fragment = PatientModePaymentPlanDetailsDialogFragment
                .newInstance(paymentResultModel, paymentPlanDTO, true);
        displayDialogFragment(fragment, false);
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
        PracticePartialPaymentDialogFragment dialog = PracticePartialPaymentDialogFragment
                .newInstance(paymentResultModel, owedAmount);
        displayDialogFragment(dialog, false);

        MixPanelUtil.logEvent(getString(R.string.event_payment_make_partial_payment));
    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        paymentPlan = null;
        PracticePaymentMethodDialogFragment fragment = PracticePaymentMethodDialogFragment
                .newInstance(paymentsModel, amount);
        displayDialogFragment(fragment, false);

        MixPanelUtil.logEvent(getString(R.string.event_payment_make_full_payment));
    }

    @Override
    public void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod,
                                      double amount,
                                      PaymentsModel paymentsModel) {
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null
                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            PracticeChooseCreditCardFragment fragment = PracticeChooseCreditCardFragment
                    .newInstance(paymentsModel, selectedPaymentMethod.getLabel(), amount);
            displayDialogFragment(fragment, false);
        } else {
            showAddCard(amount, paymentsModel);
        }
    }

    @Override
    public void onPaymentPlanAction(final PaymentsModel paymentsModel) {
        PendingBalanceDTO selectedPendingBalance = selectedBalance.getBalances().get(0);
        selectedPendingBalance = paymentsModel.getPaymentPayload()
                .reduceBalanceItems(selectedPendingBalance, false);
        PracticePaymentPlanAmountFragment fragment = PracticePaymentPlanAmountFragment
                .newInstance(paymentsModel, selectedPendingBalance);
        fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                startPaymentProcess(paymentsModel);
            }
        });
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onPaymentPlanAmount(PaymentsModel paymentsModel,
                                    PendingBalanceDTO selectedBalance,
                                    double amount) {
        boolean addExisting = false;
        if (paymentsModel.getPaymentPayload().mustAddToExisting(amount, selectedBalance)) {
            onAddBalanceToExistingPlan(paymentsModel, selectedBalance, amount);
            addExisting = true;
        } else {
            PatientModePaymentPlanFragment fragment = PatientModePaymentPlanFragment
                    .newInstance(paymentsModel, selectedBalance, amount);
            displayDialogFragment(fragment, false);
        }

        String[] params = {getString(R.string.param_practice_id),
                getString(R.string.param_balance_amount),
                getString(R.string.param_is_add_existing)};
        Object[] values = {selectedBalance.getMetadata().getPracticeId(),
                selectedBalance.getPayload().get(0).getAmount(),
                addExisting};

        MixPanelUtil.logEvent(getString(R.string.event_paymentplan_started), params, values);

    }

    @Override
    public void showPaymentConfirmation(WorkflowDTO workflowDTO) {
        showPaymentConfirmation(workflowDTO, false);
    }

    @Override
    public void showAddCard(double amount, PaymentsModel paymentsModel) {
        PracticeAddNewCreditCardFragment fragment = PracticeAddNewCreditCardFragment.newInstance(paymentsModel, amount);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onCreditCardSelected(PaymentCreditCardsPayloadDTO creditCard) {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag(PatientModePaymentPlanEditFragment.class.getName());
        if (fragment != null && fragment instanceof PatientModePaymentPlanEditFragment) {
            ((PaymentPlanEditFragment) fragment).replacePaymentMethod(creditCard);
        }
    }

    @Override
    public void completePaymentProcess(WorkflowDTO workflowDTO) {
        refreshBalance();
    }

    @Override
    public void onPayLaterClicked(PendingBalanceDTO pendingBalanceDTO) {

    }

    @Override
    public UserPracticeDTO getPracticeInfo(PaymentsModel paymentsModel) {
        if (paymentsModel != null && !paymentsModel.getPaymentPayload().getUserPractices().isEmpty()) {
            return paymentsModel.getPaymentPayload().getUserPractices().get(0);
        }
        return null;
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
        onPartialPaymentClicked(amount, null);
    }

    @Override
    public void onDetailItemClick(PaymentsModel paymentsModel, PendingBalancePayloadDTO paymentLineItem) {
        PaymentDetailsFragmentDialog dialog = PaymentDetailsFragmentDialog
                .newInstance(paymentsModel, paymentLineItem, false);
        displayDialogFragment(dialog, true);
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
            DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    Intent intent = getIntent();
                    setResult(CarePayConstants.HOME_PRESSED, intent);
                    finish();
                }
            };
            dialogFragment.setOnDismissListener(dismissListener);
            displayDialogFragment(dialogFragment, false);
        }
    }

    @Override
    public void onDetailCancelClicked(PaymentsModel paymentsModel) {
        startPaymentProcess(paymentsModel);
    }

    @Override
    public void onDismissPaymentMethodDialog(PaymentsModel paymentsModel) {
        startPaymentProcess(paymentsModel);
    }

    @Override
    public void onCashSelected(PaymentsModel paymentsModel) {
        //Not Implemented
    }

    @Override
    public void onStartPaymentPlan(PaymentsModel paymentsModel,
                                   PaymentPlanPostModel paymentPlanPostModel) {
        PracticePaymentPlanPaymentMethodFragment fragment = PracticePaymentPlanPaymentMethodFragment
                .newInstance(paymentsModel, paymentPlanPostModel);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onDismissPaymentPlan(PaymentsModel paymentsModel) {
        startPaymentProcess(paymentsModel);
    }

    @Override
    public void onSelectPaymentPlanMethod(PaymentsMethodsDTO selectedPaymentMethod,
                                          PaymentsModel paymentsModel,
                                          PaymentPlanPostModel paymentPlanPostModel,
                                          boolean onlySelectMode) {
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null
                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            PracticePaymentPlanChooseCreditCardFragment fragment = PracticePaymentPlanChooseCreditCardFragment
                    .newInstance(paymentsModel, selectedPaymentMethod.getLabel(), paymentPlanPostModel);
            displayDialogFragment(fragment, false);
        } else {
            onAddPaymentPlanCard(paymentsModel, paymentPlanPostModel, onlySelectMode);
        }
    }

    @Override
    public void onAddPaymentPlanCard(final PaymentsModel paymentsModel,
                                     final PaymentPlanPostModel paymentPlanPostModel,
                                     boolean onlySelectMode) {
        PracticePaymentPlanAddCreditCardFragment fragment = PracticePaymentPlanAddCreditCardFragment
                .newInstance(paymentsModel, paymentPlanPostModel);
        fragment.setChangePaymentMethodListener(new LargeAlertDialog.LargeAlertInterface() {
            @Override
            public void onActionButton() {
                PracticePaymentPlanPaymentMethodFragment fragment = PracticePaymentPlanPaymentMethodFragment
                        .newInstance(paymentsModel, paymentPlanPostModel);
                fragment.setOnCancelListener(new Dialog.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogg) {
                        ResponsibilityHeaderModel headerModel = ResponsibilityHeaderModel.newClinicHeader(paymentsModel);
                        ResponsibilityFragmentDialog dialog = ResponsibilityFragmentDialog
                                .newInstance(paymentsModel, headerModel, selectedBalance);
                        displayDialogFragment(dialog, false);
                    }
                });
                displayDialogFragment(fragment, false);
            }
        });
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onDisplayPaymentPlanTerms(PaymentsModel paymentsModel,
                                          PaymentPlanPostModel paymentPlanPostModel) {
        PracticePaymentPlanTermsFragment fragment = PracticePaymentPlanTermsFragment
                .newInstance(paymentsModel, paymentPlanPostModel);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onSubmitPaymentPlan(WorkflowDTO workflowDTO) {
        PracticePaymentPlanConfirmationFragment fragment = PracticePaymentPlanConfirmationFragment
                .newInstance(workflowDTO, getApplicationMode().getUserPracticeDTO(),
                        PaymentPlanConfirmationFragment.MODE_CREATE);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onMakeOneTimePayment(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO) {
        paymentPlan = paymentPlanDTO;
        PracticeOneTimePaymentFragment fragment = PracticeOneTimePaymentFragment.newInstance(paymentsModel,
                0, paymentPlanDTO);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onStartOneTimePayment(final PaymentsModel paymentsModel,
                                      final PaymentPlanDTO paymentPlanDTO) {
        PracticePaymentPlanPaymentMethodFragment fragment = PracticePaymentPlanPaymentMethodFragment
                .newInstance(paymentsModel, paymentPlanDTO, false);

        fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                onDismissEditPaymentPlan(paymentsModel, paymentPlanDTO);
            }
        });
        displayDialogFragment(fragment, false);

        String[] params = {getString(R.string.param_practice_id),
                getString(R.string.param_payment_plan_id),
                getString(R.string.param_payment_plan_amount)};
        Object[] values = {
                paymentPlanDTO.getMetadata().getPracticeId(),
                paymentPlanDTO.getMetadata().getPaymentPlanId(),
                paymentPlanDTO.getPayload().getAmount()};
        MixPanelUtil.logEvent(getString(R.string.event_paymentplan_onetime_payment), params, values);
    }

    @Override
    public void onSelectPaymentPlanMethod(PaymentsMethodsDTO selectedPaymentMethod,
                                          PaymentsModel paymentsModel,
                                          PaymentPlanDTO paymentPlanDTO,
                                          boolean onlySelectMode,
                                          Date paymentDate) {
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null
                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            PracticePaymentPlanChooseCreditCardFragment fragment = PracticePaymentPlanChooseCreditCardFragment
                    .newInstance(paymentsModel, selectedPaymentMethod.getLabel(), paymentPlanDTO,
                            onlySelectMode, paymentDate);
            displayDialogFragment(fragment, false);
        } else {
            onAddPaymentPlanCard(paymentsModel, paymentPlanDTO, onlySelectMode, paymentDate);
        }
    }

    @Override
    public void onAddPaymentPlanCard(final PaymentsModel paymentsModel,
                                     final PaymentPlanDTO paymentPlanDTO,
                                     boolean onlySelectMode,
                                     final Date paymentDate) {
        PracticePaymentPlanAddCreditCardFragment fragment = PracticePaymentPlanAddCreditCardFragment
                .newInstance(paymentsModel, paymentPlanDTO, onlySelectMode, paymentDate);
        fragment.setChangePaymentMethodListener(new LargeAlertDialog.LargeAlertInterface() {
            @Override
            public void onActionButton() {
                PracticePaymentPlanPaymentMethodFragment fragment = PracticePaymentPlanPaymentMethodFragment
                        .newInstance(paymentsModel, paymentPlan, false, paymentDate);
                fragment.setOnCancelListener(new Dialog.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        onMakeOneTimePayment(paymentsModel, paymentPlanDTO);
                    }
                });
                displayDialogFragment(fragment, true);
            }
        });
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onScheduleOneTimePayment(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO, Date paymentDate) {
        PracticePaymentPlanPaymentMethodFragment fragment = PracticePaymentPlanPaymentMethodFragment
                .newInstance(paymentsModel, paymentPlanDTO, false, paymentDate);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void showScheduledPaymentConfirmation(WorkflowDTO workflowDTO) {
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        ScheduledPaymentModel scheduledPayment = paymentsModel.getPaymentPayload()
                .getScheduledPaymentModel();
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
        completePaymentPlanProcess(workflowDTO);

        DateUtil.getInstance().setDateRaw(scheduledPayment.getPayload().getPaymentDate());
        String message = String.format(Label.getLabel("payment.oneTimePayment.schedule.success"),
                StringUtil.getFormattedBalanceAmount(scheduledPayment.getPayload().getAmount()),
                DateUtil.getInstance().getDateAsDayShortMonthDayOrdinal());
        showSuccessToast(message);

    }

    @Override
    public void showPaymentConfirmation(WorkflowDTO workflowDTO, boolean isOneTimePayment) {
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
            PaymentConfirmationFragment confirmationFragment = PaymentConfirmationFragment.newInstance(workflowDTO, isOneTimePayment);
            displayDialogFragment(confirmationFragment, false);
        }
    }

    @Override
    public void displayBalanceDetails(PaymentsModel paymentsModel, PendingBalancePayloadDTO paymentLineItem,
                                      PendingBalanceDTO selectedBalance) {
        PaymentDetailsFragmentDialog dialog = PaymentDetailsFragmentDialog
                .newInstance(paymentsModel, selectedBalance.getPayload().get(0), false);
        displayDialogFragment(dialog, false);

    }

    @Override
    public void onEditPaymentPlan(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO) {
        paymentPlan = paymentPlanDTO;
        PatientModePaymentPlanEditFragment fragment = PatientModePaymentPlanEditFragment
                .newInstance(paymentsModel, paymentPlanDTO);
        displayDialogFragment(fragment, true);
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
    public void onDismissEditPaymentPlan(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO) {
        PatientModePaymentPlanDetailsDialogFragment fragment = PatientModePaymentPlanDetailsDialogFragment
                .newInstance(paymentsModel, paymentPlanDTO, true);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onAddBalanceToExistingPlan(PaymentsModel paymentsModel,
                                           PendingBalanceDTO selectedBalance,
                                           double amount) {
        PracticeValidPlansFragment fragment = PracticeValidPlansFragment
                .newInstance(paymentsModel, selectedBalance, amount);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onSelectedPlanToAdd(PaymentsModel paymentsModel,
                                    PendingBalanceDTO selectedBalance,
                                    PaymentPlanDTO selectedPlan,
                                    double amount) {
        PatientModeAddExistingPaymentPlanFragment fragment = PatientModeAddExistingPaymentPlanFragment
                .newInstance(paymentsModel, selectedBalance, selectedPlan, amount);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onEditPaymentPlanPaymentMethod(PaymentsModel paymentsModel) {
        displayDialogFragment(PracticePaymentPlanPaymentMethodFragment
                .newInstance(paymentsModel, new PaymentPlanDTO(), true), false);
    }

    @Override
    public void onPaymentPlanCanceled(WorkflowDTO workflowDTO) {

    }

    @Override
    public void showCancelPaymentPlanConfirmDialog(ConfirmationCallback confirmationCallback) {

    }

    @Override
    public DTO getDto() {
        return paymentResultModel;
    }

    @Override
    public void completePaymentPlanProcess(WorkflowDTO workflowDTO) {
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
}
