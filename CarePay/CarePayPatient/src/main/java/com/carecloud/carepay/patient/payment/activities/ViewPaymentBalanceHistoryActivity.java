package com.carecloud.carepay.patient.payment.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.patient.payment.androidpay.AndroidPayDialogFragment;
import com.carecloud.carepay.patient.payment.dialogs.PaymentDetailsFragmentDialog;
import com.carecloud.carepay.patient.payment.dialogs.PaymentHistoryDetailDialogFragment;
import com.carecloud.carepay.patient.payment.dialogs.PaymentPlanHistoryDetailDialogFragment;
import com.carecloud.carepay.patient.payment.fragments.NoPaymentsFragment;
import com.carecloud.carepay.patient.payment.fragments.PatientPaymentMethodFragment;
import com.carecloud.carepay.patient.payment.fragments.PaymentBalanceHistoryFragment;
import com.carecloud.carepay.patient.payment.fragments.PaymentDisabledAlertDialogFragment;
import com.carecloud.carepay.patient.payment.fragments.PaymentPlanPaymentMethodFragment;
import com.carecloud.carepay.patient.payment.interfaces.PaymentFragmentActivityInterface;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.common.ConfirmationCallback;
import com.carecloud.carepaylibray.customcomponents.CustomMessageToast;
import com.carecloud.carepaylibray.customdialogs.LargeAlertDialog;
import com.carecloud.carepaylibray.demographics.fragments.ConfirmDialogFragment;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.fragments.AddExistingPaymentPlanFragment;
import com.carecloud.carepaylibray.payments.fragments.AddNewCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.ChooseCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.EditOneTimePaymentDialog;
import com.carecloud.carepaylibray.payments.fragments.OneTimePaymentDialog;
import com.carecloud.carepaylibray.payments.fragments.PartialPaymentDialog;
import com.carecloud.carepaylibray.payments.fragments.PaymentConfirmationFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanAddCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanAmountDialog;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanChooseCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanConfirmationFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanDetailsDialogFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanEditFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanTermsFragment;
import com.carecloud.carepaylibray.payments.fragments.ValidPlansFragment;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanEditInterface;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDetailsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsBalancesItem;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentModel;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentPayload;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryItem;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.android.gms.wallet.MaskedWallet;

import static com.carecloud.carepay.patient.payment.fragments.PaymentBalanceHistoryFragment.PAGE_BALANCES;

import java.util.Date;
import java.util.List;

/**
 * Created by jorge on 29/12/16
 */
public class ViewPaymentBalanceHistoryActivity extends MenuPatientActivity implements PaymentFragmentActivityInterface,
        PaymentPlanEditInterface, PaymentDisabledAlertDialogFragment.DisabledPaymentAlertCallback {

    private PaymentsModel paymentsDTO;
    private UserPracticeDTO selectedUserPractice;
    private PendingBalanceDTO selectedBalancesItem;

    public Bundle bundle;
    private String toolBarTitle;

    private Fragment androidPayTargetFragment;
    private int displayPage = PAGE_BALANCES;
    private PaymentPlanDTO paymentPlan;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paymentsDTO = getConvertedDTO(PaymentsModel.class);

        toolBarTitle = Label.getLabel("payment_patient_balance_toolbar");
        displayToolbar(true, toolBarTitle);
        inflateDrawer();
        initFragments();
    }

    private void initFragments() {
        if (hasPayments() || hasPaymentPlans() || hasCharges()) {
            replaceFragment(PaymentBalanceHistoryFragment.newInstance(displayPage), false);
        } else {
            showNoPaymentsLayout();
        }
    }

    private boolean hasCharges() {
        return !paymentsDTO.getPaymentPayload().getTransactionHistory().getPaymentHistoryList().isEmpty();
    }

    private boolean hasPayments() {
        if (!paymentsDTO.getPaymentPayload().getPatientBalances().isEmpty()) {
            for (PatientBalanceDTO patientBalanceDTO : paymentsDTO.getPaymentPayload().getPatientBalances()) {
                if (!patientBalanceDTO.getBalances().isEmpty()) {
                    for (PendingBalanceDTO pendingBalanceDTO : patientBalanceDTO.getBalances()) {
                        if (!pendingBalanceDTO.getPayload().isEmpty()) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean hasPaymentPlans() {
        if (!paymentsDTO.getPaymentPayload().getActivePlans(null).isEmpty()) {
            for (PaymentPlanDTO paymentPlanDTO : paymentsDTO.getPaymentPayload().getActivePlans(null)) {
                if (paymentPlanDTO.getPayload().getPaymentPlanDetails().getPaymentPlanStatus()
                        .equals(PaymentPlanDetailsDTO.STATUS_PROCESSING)) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        navigationView.getMenu().findItem(R.id.nav_payments).setChecked(true);
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

    @Override
    public void startPaymentProcess(PaymentsModel paymentsModel) {
        String tag = PaymentDetailsFragmentDialog.class.getName();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        PaymentDetailsFragmentDialog dialog = PaymentDetailsFragmentDialog
                .newInstance(paymentsModel, selectedBalancesItem.getPayload().get(0),
                        selectedBalancesItem, true);
        dialog.show(ft, tag);
    }

    @Override
    public void onPartialPaymentClicked(double owedAmount, PendingBalanceDTO selectedBalance) {
        new PartialPaymentDialog(this, paymentsDTO, selectedBalance).show();
    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        paymentPlan = null;
        replaceFragment(PatientPaymentMethodFragment
                .newInstance(paymentsModel, amount, false), true);
        displayToolbar(false, toolBarTitle);
        MixPanelUtil.logEvent(getString(R.string.event_payment_make_full_payment));
    }

    @Override
    public void onBackPressed() {
        if (!toolbarVisibility && getSupportFragmentManager().getBackStackEntryCount() < 2) {
            displayToolbar(true, toolBarTitle);
        }
        super.onBackPressed();
    }

    @Override
    public void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod, double amount,
                                      PaymentsModel paymentsModel) {
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null
                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            Fragment fragment = ChooseCreditCardFragment.newInstance(paymentsModel,
                    selectedPaymentMethod.getLabel(), amount);
            replaceFragment(fragment, true);
        } else {
            showAddCard(amount, paymentsModel);
        }
    }

    @Override
    public void showAddCard(double amount, PaymentsModel paymentsModel) {
        Fragment fragment = AddNewCreditCardFragment.newInstance(paymentsDTO, amount);
        replaceFragment(fragment, true);
    }

    @Override
    public void onCreditCardSelected(PaymentCreditCardsPayloadDTO creditCard) {
        getSupportFragmentManager().popBackStackImmediate();
        getSupportFragmentManager().popBackStackImmediate();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container_main);
        if (fragment instanceof PaymentPlanEditFragment) {
            ((PaymentPlanEditFragment) fragment).replacePaymentMethod(creditCard);
        } else {
            getSupportFragmentManager().popBackStackImmediate();
            fragment = getSupportFragmentManager().findFragmentById(R.id.container_main);
            if (fragment instanceof PaymentPlanEditFragment) {
                ((PaymentPlanEditFragment) fragment).replacePaymentMethod(creditCard);
            }
        }
    }

    @Override
    public void completePaymentProcess(WorkflowDTO workflowDTO) {
        displayPage = PAGE_BALANCES;
        initFragments();
        refreshBalance(true);
    }

    @Override
    public void onPayLaterClicked(PendingBalanceDTO pendingBalanceDTO) {

    }

    @Override
    public UserPracticeDTO getPracticeInfo(PaymentsModel paymentsModel) {
        return selectedUserPractice;
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
    public void onPaymentPlanAction(final PaymentsModel paymentsModel) {
        PendingBalanceDTO reducedBalancesItem = paymentsModel.getPaymentPayload()
                .reduceBalanceItems(selectedBalancesItem, false);
        Dialog dialog = new PaymentPlanAmountDialog(getContext(), paymentsModel,
                reducedBalancesItem, this);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                startPaymentProcess(paymentsModel);
            }
        });
        dialog.show();
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
            PaymentPlanFragment fragment = PaymentPlanFragment.newInstance(paymentsModel,
                    selectedBalance, amount);
            replaceFragment(fragment, true);
        }
        displayToolbar(false, null);

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
    public void showPaymentPendingConfirmation(PaymentsModel paymentsModel) {
        new CustomMessageToast(this, Label.getLabel("payments_external_pending"),
                CustomMessageToast.NOTIFICATION_TYPE_SUCCESS).show();
        initFragments();
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
    public DTO getDto() {
        return paymentsDTO == null ? paymentsDTO = getConvertedDTO(PaymentsModel.class) : paymentsDTO;
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.container_main, fragment, addToBackStack);
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        addFragment(R.id.container_main, fragment, addToBackStack);
    }

    public void showNoPaymentsLayout() {
        replaceFragment(NoPaymentsFragment.newInstance(), false);
    }

    @Override
    public void loadPaymentAmountScreen(PaymentsBalancesItem selectedBalancesItem, PaymentsModel paymentDTO) {
        setPendingBalance(selectedBalancesItem);
        selectedUserPractice = DtoHelper.getConvertedDTO(UserPracticeDTO.class,
                DtoHelper.getStringDTO(selectedBalancesItem.getMetadata()));
        if (paymentDTO.getPaymentPayload().canMakePayments(selectedBalancesItem.getMetadata().getPracticeId())) {
            startPaymentProcess(paymentDTO);
        } else {
            PaymentDisabledAlertDialogFragment fragment = PaymentDisabledAlertDialogFragment
                    .newInstance(Label.getLabel("payments.pendingPayments.patientFeedbackPopup.label.title"),
                            Label.getLabel("payments.pendingPayments.patientFeedbackPopup.label.description"),
                            selectedBalancesItem);
            displayDialogFragment(fragment, true);
        }
    }

    @Override
    public void loadPaymentPlanScreen(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO) {
        selectedUserPractice = getUserPracticeById(paymentPlanDTO.getMetadata().getPracticeId());
        PaymentPlanDetailsDialogFragment fragment = PaymentPlanDetailsDialogFragment
                .newInstance(paymentsModel, paymentPlanDTO);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void displayPaymentHistoryDetails(PaymentHistoryItem paymentHistoryItem) {
        selectedUserPractice = getUserPracticeById(paymentHistoryItem.getMetadata().getPracticeId());
        PaymentHistoryDetailDialogFragment fragment = PaymentHistoryDetailDialogFragment
                .newInstance(paymentHistoryItem, selectedUserPractice);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onRequestRefresh(int requestedPage) {
        displayPage = requestedPage;
        refreshBalance(false);
    }

    @Override
    public void displayPaymentPlanHistoryDetails(final PaymentHistoryItem historyItem, PaymentPlanPayloadDTO paymentPlanPayloadDTO) {
        String taskId = paymentPlanPayloadDTO.getMetadata().getTaskId();
        PaymentPlanDTO selectedPaymentPlan = null;
        for (PaymentPlanDTO paymentPlanDTO : paymentsDTO.getPaymentPayload().getPatientPaymentPlans()) {
            if (taskId.equals(paymentPlanDTO.getPayload().getMetadata().getTaskId())) {
                selectedPaymentPlan = paymentPlanDTO;
                break;
            }
        }

        selectedUserPractice = getUserPracticeById(historyItem.getMetadata().getPracticeId());
        PaymentPlanHistoryDetailDialogFragment planHistoryFragment = PaymentPlanHistoryDetailDialogFragment.newInstance(selectedPaymentPlan, selectedUserPractice);
        planHistoryFragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                PaymentHistoryDetailDialogFragment historyFragment = PaymentHistoryDetailDialogFragment.newInstance(historyItem, selectedUserPractice);
                displayDialogFragment(historyFragment, false);
            }
        });
        displayDialogFragment(planHistoryFragment, false);
    }

    @Override
    public void onDetailCancelClicked(PaymentsModel paymentsModel) {
        loadPaymentAmountScreen(null, paymentsModel);
    }

    private void setPendingBalance(PaymentsBalancesItem selectedBalancesItem) {
        PendingBalanceDTO pendingBalanceDTO = new PendingBalanceDTO();
        pendingBalanceDTO.setMetadata(selectedBalancesItem.getMetadata());
        pendingBalanceDTO.getPayload().add(selectedBalancesItem.getBalance());
        this.selectedBalancesItem = pendingBalanceDTO;
    }

    private UserPracticeDTO getUserPracticeById(String practiceId) {
        for (UserPracticeDTO userPracticeDTO : paymentsDTO.getPaymentPayload().getUserPractices()) {
            if (userPracticeDTO.getPracticeId().equals(practiceId)) {
                return userPracticeDTO;
            }
        }
        return null;
    }

    @Override
    public void createWalletFragment(MaskedWallet maskedWallet, Double amount) {
        replaceFragment(AndroidPayDialogFragment
                .newInstance(maskedWallet, paymentsDTO, amount), true);
    }

    @Override
    public void forwardAndroidPayResult(int requestCode, int resultCode, Intent data) {
        Fragment targetFragment = getAndroidPayTargetFragment();
        if (targetFragment != null) {
            targetFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onStartPaymentPlan(PaymentsModel paymentsModel,
                                   PaymentPlanPostModel paymentPlanPostModel) {
        PaymentPlanPaymentMethodFragment fragment = PaymentPlanPaymentMethodFragment
                .newInstance(paymentsModel, paymentPlanPostModel);
        replaceFragment(fragment, true);
    }

    @Override
    public void onDismissPaymentPlan(PaymentsModel paymentsModel) {
        onBackPressed();
    }

    @Override
    public void onSelectPaymentPlanMethod(PaymentsMethodsDTO selectedPaymentMethod,
                                          PaymentsModel paymentsModel,
                                          PaymentPlanPostModel paymentPlanPostModel,
                                          boolean onlySelectMode) {
        if ((paymentsModel.getPaymentPayload().getPatientCreditCards() != null)
                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            PaymentPlanChooseCreditCardFragment fragment = PaymentPlanChooseCreditCardFragment
                    .newInstance(paymentsModel, selectedPaymentMethod.getLabel(), paymentPlanPostModel, onlySelectMode);
            replaceFragment(fragment, true);
        } else {
            onAddPaymentPlanCard(paymentsModel, paymentPlanPostModel, onlySelectMode);
        }
    }

    @Override
    public void onAddPaymentPlanCard(final PaymentsModel paymentsModel,
                                     final PaymentPlanPostModel paymentPlanPostModel,
                                     boolean onlySelectMode) {
        PaymentPlanAddCreditCardFragment fragment = PaymentPlanAddCreditCardFragment
                .newInstance(paymentsModel, paymentPlanPostModel, onlySelectMode);
        fragment.setChangePaymentMethodListener(new LargeAlertDialog.LargeAlertInterface() {
            @Override
            public void onActionButton() {
                PaymentPlanPaymentMethodFragment fragment = PaymentPlanPaymentMethodFragment
                        .newInstance(paymentsModel, paymentPlanPostModel);
                replaceFragment(fragment, true);
                displayToolbar(false, toolBarTitle);
            }
        });
        replaceFragment(fragment, true);
    }

    @Override
    public void onDisplayPaymentPlanTerms(PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel) {
        PaymentPlanTermsFragment fragment = PaymentPlanTermsFragment.newInstance(paymentsModel, paymentPlanPostModel);
        replaceFragment(fragment, true);
    }

    @Override
    public void onSubmitPaymentPlan(WorkflowDTO workflowDTO) {
        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
            getSupportFragmentManager().popBackStack();
        }

        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        String practiceId = paymentsModel.getPaymentPayload()
                .getPatientPaymentPlans().get(0).getMetadata().getPracticeId();
        PaymentPlanConfirmationFragment fragment = PaymentPlanConfirmationFragment
                .newInstance(workflowDTO, getUserPracticeById(practiceId), PaymentPlanConfirmationFragment.MODE_CREATE);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onMakeOneTimePayment(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO) {
        paymentPlan = paymentPlanDTO;
        new OneTimePaymentDialog(getContext(), paymentsModel, paymentPlanDTO, this).show();
    }

    @Override
    public void onStartOneTimePayment(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO) {
        PaymentPlanPaymentMethodFragment fragment = PaymentPlanPaymentMethodFragment
                .newInstance(paymentsModel, paymentPlanDTO, false);
        replaceFragment(fragment, true);
        displayToolbar(false, toolBarTitle);

        String[] params = {getString(com.carecloud.carepaylibrary.R.string.param_practice_id),
                getString(com.carecloud.carepaylibrary.R.string.param_payment_plan_id),
                getString(com.carecloud.carepaylibrary.R.string.param_payment_plan_amount)};
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
        if ((paymentsModel.getPaymentPayload().getPatientCreditCards() != null)
                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            PaymentPlanChooseCreditCardFragment fragment = PaymentPlanChooseCreditCardFragment
                    .newInstance(paymentsModel, selectedPaymentMethod.getLabel(), paymentPlanDTO,
                            onlySelectMode, paymentDate);
            replaceFragment(fragment, true);
        } else {
            onAddPaymentPlanCard(paymentsModel, paymentPlanDTO, onlySelectMode, paymentDate);
        }
    }

    @Override
    public void onAddPaymentPlanCard(final PaymentsModel paymentsModel,
                                     final PaymentPlanDTO paymentPlanDTO,
                                     boolean onlySelectMode,
                                     final Date paymentDate) {
        PaymentPlanAddCreditCardFragment fragment = PaymentPlanAddCreditCardFragment
                .newInstance(paymentsModel, paymentPlanDTO, onlySelectMode, paymentDate);
        fragment.setChangePaymentMethodListener(new LargeAlertDialog.LargeAlertInterface() {
            @Override
            public void onActionButton() {
                PaymentPlanPaymentMethodFragment fragment = PaymentPlanPaymentMethodFragment
                        .newInstance(paymentsModel, paymentPlan, false, paymentDate);
                fragment.setOnCancelListener(new Dialog.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        onMakeOneTimePayment(paymentsModel, paymentPlanDTO);
                    }
                });
                replaceFragment(fragment, true);
                displayToolbar(false, toolBarTitle);
            }
        });
        replaceFragment(fragment, true);
    }

    @Override
    public void onScheduleOneTimePayment(PaymentsModel paymentsModel,
                                         PaymentPlanDTO paymentPlanDTO,
                                         Date paymentDate) {
        PaymentPlanPaymentMethodFragment fragment = PaymentPlanPaymentMethodFragment
                .newInstance(paymentsModel, paymentPlanDTO, false, paymentDate);
        replaceFragment(fragment, true);
        displayToolbar(false, toolBarTitle);
    }

    @Override
    public void showScheduledPaymentConfirmation(WorkflowDTO workflowDTO) {
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        ScheduledPaymentModel scheduledPayment = paymentsModel.getPaymentPayload()
                .getScheduledPaymentModel();
        if (scheduledPayment.getMetadata().getOneTimePaymentId() == null &&
                !paymentsModel.getPaymentPayload().getScheduledOneTimePayments().isEmpty()) {
            scheduledPayment = paymentsModel.getPaymentPayload().getScheduledOneTimePayments().get(0);
        }
        List<ScheduledPaymentModel> scheduledPaymentModels = this.paymentsDTO.getPaymentPayload().getScheduledOneTimePayments();
        for (ScheduledPaymentModel scheduledPaymentModel : scheduledPaymentModels) {
            if (scheduledPaymentModel.getMetadata().getOneTimePaymentId().equals(scheduledPayment.getMetadata().getOneTimePaymentId())) {
                scheduledPaymentModels.remove(scheduledPayment);
                break;
            }
        }
        scheduledPaymentModels.add(scheduledPayment);
        completePaymentProcess(workflowDTO);

        DateUtil.getInstance().setDateRaw(scheduledPayment.getPayload().getPaymentDate());
        String message = String.format(Label.getLabel("payment.oneTimePayment.schedule.success"),
                StringUtil.getFormattedBalanceAmount(scheduledPayment.getPayload().getAmount()),
                DateUtil.getInstance().getDateAsDayShortMonthDayOrdinal());
        showSuccessToast(message);

    }

    @Override
    public void showDeleteScheduledPaymentConfirmation(WorkflowDTO workflowDTO, ScheduledPaymentPayload scheduledPaymentPayload) {
        SystemUtil.hideSoftKeyboard(this);
        showSuccessToast(String.format(
                Label.getLabel("payment.oneTimePayment.scheduled.delete.success"),
                StringUtil.getFormattedBalanceAmount(scheduledPaymentPayload.getAmount()),
                DateUtil.getInstance()
                        .setDateRaw(scheduledPaymentPayload.getPaymentDate())
                        .toStringWithFormatMmSlashDdSlashYyyy()));
        completePaymentProcess(workflowDTO);
    }

    @Override
    public void showPaymentConfirmation(WorkflowDTO workflowDTO, boolean isOneTimePayment) {
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        IntegratedPatientPaymentPayload payload = paymentsModel.getPaymentPayload()
                .getPatientPayments().getPayload();
        if (!payload.getProcessingErrors().isEmpty() && payload.getTotalPaid() == 0D) {
            StringBuilder builder = new StringBuilder();
            for (IntegratedPatientPaymentPayload.ProcessingError processingError : payload
                    .getProcessingErrors()) {
                builder.append(processingError.getError());
                builder.append("\n");
            }
            int last = builder.lastIndexOf("\n");
            builder.replace(last, builder.length(), "");
            showErrorNotification(builder.toString());
        } else {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            PaymentConfirmationFragment confirmationFragment = PaymentConfirmationFragment
                    .newInstance(workflowDTO, isOneTimePayment);
            displayDialogFragment(confirmationFragment, false);
        }
    }

    @Override
    public void displayBalanceDetails(PaymentsModel paymentsModel,
                                      PendingBalancePayloadDTO paymentLineItem,
                                      PendingBalanceDTO selectedBalance) {
        PaymentDetailsFragmentDialog dialog = PaymentDetailsFragmentDialog
                .newInstance(paymentsModel, paymentLineItem, selectedBalance, false);
        displayDialogFragment(dialog, false);
    }

    @Override
    public void onEditPaymentPlan(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO) {
        paymentPlan = paymentPlanDTO;
        PaymentPlanEditFragment fragment = PaymentPlanEditFragment.newInstance(paymentsModel, paymentPlanDTO);
        replaceFragment(fragment, true);
        displayToolbar(false, null);
    }

    @Override
    public void onPaymentPlanEdited(WorkflowDTO workflowDTO) {
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        if (paymentsModel.getPaymentPayload().getPatientPaymentPlans().isEmpty()) {
            //no changes to plan
            initFragments();
            return;
        }
        String practiceId = paymentsModel.getPaymentPayload().getPatientPaymentPlans().get(0)
                .getMetadata().getPracticeId();

        PaymentPlanConfirmationFragment fragment = PaymentPlanConfirmationFragment
                .newInstance(workflowDTO, getUserPracticeById(practiceId), PaymentPlanConfirmationFragment.MODE_EDIT);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onPaymentPlanAddedExisting(WorkflowDTO workflowDTO) {
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        String practiceId = paymentsModel.getPaymentPayload().getPatientPaymentPlans().get(0)
                .getMetadata().getPracticeId();

        PaymentPlanConfirmationFragment confirmationFragment = PaymentPlanConfirmationFragment
                .newInstance(workflowDTO, getUserPracticeById(practiceId),
                        PaymentPlanConfirmationFragment.MODE_ADD);
        displayDialogFragment(confirmationFragment, false);
    }

    @Override
    public void onDismissEditPaymentPlan(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO) {

    }

    @Override
    public void onAddBalanceToExistingPlan(PaymentsModel paymentsModel,
                                           PendingBalanceDTO selectedBalance,
                                           double amount) {
        ValidPlansFragment fragment = ValidPlansFragment.newInstance(paymentsModel, selectedBalance, amount);
        replaceFragment(fragment, true);
    }

    @Override
    public void onSelectedPlanToAdd(PaymentsModel paymentsModel,
                                    PendingBalanceDTO selectedBalance,
                                    PaymentPlanDTO selectedPlan,
                                    double amount) {
        AddExistingPaymentPlanFragment fragment = AddExistingPaymentPlanFragment
                .newInstance(paymentsModel, selectedBalance, selectedPlan, amount);
        replaceFragment(fragment, true);
    }

    @Override
    public void onContinueClicked() {
        startPaymentProcess(paymentsDTO);
    }

    @Override
    public void onAlertClicked() {
        //TODO: Not yet
    }

    @Override
    public void onEditPaymentPlanPaymentMethod(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO) {
        addFragment(PaymentPlanPaymentMethodFragment
                .newInstance(paymentsModel, paymentPlanDTO, true), true);
    }

    @Override
    public void onStartEditScheduledPayment(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO, ScheduledPaymentModel scheduledPaymentModel) {
        new EditOneTimePaymentDialog(this, paymentsDTO, paymentPlanDTO,
                scheduledPaymentModel, this).show();
    }

    @Override
    public void onPaymentPlanCanceled(WorkflowDTO workflowDTO) {
        showSuccessToast(Label.getLabel("payment.cancelPaymentPlan.success.banner.text"));
        initFragments();
        refreshBalance(true);
    }

    @Override
    public void showCancelPaymentPlanConfirmDialog(ConfirmationCallback confirmationCallback) {
        ConfirmDialogFragment fragment = ConfirmDialogFragment
                .newInstance(Label.getLabel("payment.cancelPaymentPlan.confirmDialog.title.cancelPaymentPlanTitle"),
                        Label.getLabel("payment.cancelPaymentPlan.confirmDialog.message.cancelPaymentPlanMessage"),
                        Label.getLabel("no"),
                        Label.getLabel("yes"));
        fragment.setCallback(confirmationCallback);
        fragment.show(getSupportFragmentManager().beginTransaction(), fragment.getClass().getName());
    }

    @Override
    public void completePaymentPlanProcess(WorkflowDTO workflowDTO) {
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        List<PaymentPlanDTO> paymentPlanList = this.paymentsDTO.getPaymentPayload().getPatientPaymentPlans();

        if (!paymentPlanList.isEmpty()) {
            PaymentPlanDTO modifiedPaymentPlan = paymentsModel.getPaymentPayload().getPatientPaymentPlans().get(0);
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

    private void refreshBalance(boolean showProgress) {
        TransitionDTO transitionDTO = paymentsDTO.getPaymentsMetadata().getPaymentsLinks()
                .getPaymentsPatientBalances();
        getWorkflowServiceHelper().execute(transitionDTO, getPatientBalancesCallback(showProgress));
    }


    private WorkflowServiceCallback getPatientBalancesCallback(final boolean showProgress) {
        return new WorkflowServiceCallback() {

            @Override
            public void onPreExecute() {
                if (showProgress) {
                    showProgressDialog();
                }
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                if (showProgress) {
                    hideProgressDialog();
                }
                paymentsDTO = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO.toString());
                initFragments();
            }

            @Override
            public void onFailure(String exceptionMessage) {
                if (showProgress) {
                    hideProgressDialog();
                }
                showErrorNotification(exceptionMessage);
            }
        };
    }

}
