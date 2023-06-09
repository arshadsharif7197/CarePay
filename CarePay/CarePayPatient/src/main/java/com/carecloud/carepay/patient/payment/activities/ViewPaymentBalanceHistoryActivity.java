package com.carecloud.carepay.patient.payment.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.ShimmerFragment;
import com.carecloud.carepay.patient.menu.MenuPatientActivity;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.patient.payment.dialogs.PaymentDetailsFragmentDialog;
import com.carecloud.carepay.patient.payment.fragments.NoPaymentsFragment;
import com.carecloud.carepay.patient.payment.fragments.PatientPaymentMethodFragment;
import com.carecloud.carepay.patient.payment.fragments.PatientPaymentPlanDetailsDialogFragment;
import com.carecloud.carepay.patient.payment.fragments.PatientPaymentPlanEditFragment;
import com.carecloud.carepay.patient.payment.fragments.PaymentBalanceHistoryFragment;
import com.carecloud.carepay.patient.payment.fragments.PaymentDisabledAlertDialogFragment;
import com.carecloud.carepay.patient.payment.fragments.PaymentPlanPaymentMethodFragment;
import com.carecloud.carepay.patient.payment.interfaces.PaymentFragmentActivityInterface;
import com.carecloud.carepay.patient.rate.RateDialog;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.common.ConfirmationCallback;
import com.carecloud.carepaylibray.customcomponents.CustomMessageToast;
import com.carecloud.carepaylibray.demographics.fragments.ConfirmDialogFragment;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.fragments.AddExistingPaymentPlanFragment;
import com.carecloud.carepaylibray.payments.fragments.AddNewCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.ChooseCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.PartialPaymentDialog;
import com.carecloud.carepaylibray.payments.fragments.PaymentConfirmationFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanConfirmationFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanFragment;
import com.carecloud.carepaylibray.payments.fragments.ValidPlansFragment;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanEditInterface;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDetailsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsBalancesItem;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentModel;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentPayload;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.payments.viewModel.PatientResponsibilityViewModel;
import com.carecloud.carepaylibray.profile.Profile;
import com.carecloud.carepaylibray.profile.ProfileDto;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.carecloud.carepay.patient.payment.fragments.PaymentBalanceHistoryFragment.PAGE_BALANCES;

//import com.google.android.gms.wallet.MaskedWallet;

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
    private boolean paymentEnabled;
    private PatientPaymentPlanDetailsDialogFragment patientPaymentPlanDetailsDialogFragment;
    private PatientResponsibilityViewModel patientResponsibilityViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        patientResponsibilityViewModel = new ViewModelProvider(this).get(PatientResponsibilityViewModel.class);
        toolBarTitle = getScreenTitle(Label.getLabel("payment_patient_balance_toolbar"));
        displayToolbar(true, toolBarTitle);
        paymentsDTO = getConvertedDTO(PaymentsModel.class);
        inflateDrawer();
        if (paymentsDTO == null) {
            callPaymentsService();
        } else {
            patientResponsibilityViewModel.setPaymentsModel(paymentsDTO);
            initFragments();
        }
    }

    private void callPaymentsService() {
        TransitionDTO transitionDTO = getTransitionBalance();
        Map<String, String> queryMap = new HashMap<>();
        getWorkflowServiceHelper().execute(transitionDTO, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                ShimmerFragment fragment = ShimmerFragment.newInstance(R.layout.shimmer_default_item);
                fragment.setTabbed(true, Label.getLabel("payment_patient_balance_tab"),
                        Label.getLabel("payment_patient_history_tab"));
                replaceFragment(fragment, false);
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                paymentsDTO = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
                patientResponsibilityViewModel.setPaymentsModel(paymentsDTO);
                initFragments();
            }

            @Override
            public void onFailure(String exceptionMessage) {
                showErrorNotification(exceptionMessage);
            }
        }, queryMap);
    }

    private void initFragments() {
        if (!canViewBalanceAndHistoricalPayments(paymentsDTO.getPaymentPayload().getUserPractices())) {
            showNoPermissionsLayout();
        } else if (hasPayments() || hasPaymentPlans() || hasCharges()) {
            replaceFragment(PaymentBalanceHistoryFragment.newInstance(displayPage), false);
        } else {
            showNoPaymentsLayout();
        }
    }

    private void showNoPermissionsLayout() {
        replaceFragment(NoPaymentsFragment.newInstance(true), false);
    }

    private boolean canViewBalanceAndHistoricalPayments(List<UserPracticeDTO> userPractices) {
        boolean atLeastOneHasPermission = false;
        for (UserPracticeDTO userPracticeDTO : userPractices) {
            if (paymentsDTO.getPaymentPayload().canViewBalanceAndHistoricalPayments(userPracticeDTO.getPracticeId())) {
                atLeastOneHasPermission = true;
            }
        }
        return atLeastOneHasPermission
                || (userPractices.isEmpty() && paymentsDTO.getPaymentPayload().getDelegate() == null);
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
        selectMenuItem(R.id.paymentsMenuItem);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PaymentConstants.REQUEST_CODE_CHANGE_MASKED_WALLET:
            case PaymentConstants.REQUEST_CODE_FULL_WALLET:
            case PaymentConstants.REQUEST_CODE_GOOGLE_PAYMENT:
                forwardAndroidPayResult(requestCode, resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void startPaymentProcess(PaymentsModel paymentsModel) {
        PaymentDetailsFragmentDialog dialog = PaymentDetailsFragmentDialog
                .newInstance(paymentsModel, selectedBalancesItem.getPayload().get(0),
                        selectedBalancesItem, paymentEnabled, true);
        displayDialogFragment(dialog, true);
    }

    @Override
    public void onPartialPaymentClicked(double owedAmount, PendingBalanceDTO selectedBalance) {
        PartialPaymentDialog dialog = PartialPaymentDialog.newInstance(paymentsDTO, selectedBalance);
        displayDialogFragment(dialog, false);
        MixPanelUtil.logEvent(getString(R.string.event_payment_make_partial_payment),
                getString(R.string.param_practice_id),
                selectedBalance.getMetadata().getPracticeId());
    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        replaceFragment(PatientPaymentMethodFragment
                .newInstance(paymentsModel, amount, false), true);
        displayToolbar(false, toolBarTitle);
        String[] params = {getString(R.string.param_balance_amount),
                getString(R.string.param_practice_id)
        };

        Object[] values = {amount,
                selectedBalancesItem.getMetadata().getPracticeId()
        };
        MixPanelUtil.logEvent(getString(R.string.event_payment_make_full_payment), params, values);
    }

    @Override
    public void onBackPressed() {
        if (!isPaymentFragmentExist()) {
            if (!toolbarVisibility && getSupportFragmentManager().getBackStackEntryCount() < 2) {
                displayToolbar(true, toolBarTitle);
            }
            super.onBackPressed();
        }
    }

    public Fragment getTopFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            return null;
        }
        String fragmentTag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
        return getSupportFragmentManager().findFragmentByTag(fragmentTag);
    }

    private boolean isPaymentFragmentExist() {
        Fragment fragment = getTopFragment();
        if (fragment != null) {
            if (fragment instanceof PaymentPlanPaymentMethodFragment) {
                if (((PaymentPlanPaymentMethodFragment) fragment).isOnBackPressCalled) {
                    return false;
                }
                ((PaymentPlanPaymentMethodFragment) fragment).onBackPressed();
                return true;
            } else if (fragment instanceof PatientPaymentMethodFragment) {
                if (((PatientPaymentMethodFragment) fragment).isOnBackPressCalled) {
                    return false;
                }
                ((PatientPaymentMethodFragment) fragment).onBackPressed();
                return true;
            } else if (fragment instanceof PaymentPlanFragment) {
                if (((PaymentPlanFragment) fragment).isOnBackPressCalled) {
                    return false;
                }
                ((PaymentPlanFragment) fragment).onBackPressed();
                return true;
            } else if (fragment instanceof ValidPlansFragment) {
                if (((ValidPlansFragment) fragment).isOnBackPressCalled) {
                    return false;
                }
                ((ValidPlansFragment) fragment).onBackPressed();
                return true;
            } else if (fragment instanceof PaymentPlanFragment) {
                if (((PaymentPlanFragment) fragment).isOnBackPressCalled) {
                    return false;
                }
                ((PaymentPlanFragment) fragment).onBackPressed();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod, double amount,
                                      PaymentsModel paymentsModel) {
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null
                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            String methodLabel = Label.getLabel("payment_method_" + selectedPaymentMethod.getType());
            Fragment fragment = ChooseCreditCardFragment.newInstance(paymentsModel,
                    methodLabel, amount);
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
        String tag = PatientPaymentPlanEditFragment.class.getName();
        getSupportFragmentManager().popBackStackImmediate(tag, 0);
        PatientPaymentPlanEditFragment fragment = (PatientPaymentPlanEditFragment) getSupportFragmentManager()
                .findFragmentByTag(tag);
        fragment.replacePaymentMethod(creditCard);
    }

    @Override
    public void completePaymentProcess(WorkflowDTO workflowDTO) {
        displayPage = PAGE_BALANCES;
        initFragments();
        clearFragments();
        refreshBalance(true);
        showRateDialogFragment();
    }

    @Override
    public void onPayLaterClicked(PendingBalanceDTO pendingBalanceDTO) {
        //NA
    }

    @Override
    public UserPracticeDTO getPracticeInfo(PaymentsModel paymentsModel) {
        return selectedUserPractice;
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
    public void showPaymentConfirmation(WorkflowDTO workflowDTO) {
        showPaymentConfirmation(workflowDTO, false);
    }

    @Override
    public void showPaymentPendingConfirmation(PaymentsModel paymentsModel, String practiceId) {
        new CustomMessageToast(this, Label.getLabel("payment_queued_patient"),
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
        replaceFragment(NoPaymentsFragment.newInstance(false), false);
    }

    @Override
    public void loadPaymentAmountScreen(PaymentsBalancesItem selectedBalancesItem, PaymentsModel paymentDTO) {
        setPendingBalance(selectedBalancesItem);
        selectedUserPractice = DtoHelper.getConvertedDTO(UserPracticeDTO.class,
                DtoHelper.getStringDTO(selectedBalancesItem.getMetadata()));
        String practiceId = selectedBalancesItem.getMetadata().getPracticeId();
        if (paymentDTO.getPaymentPayload().canMakePayments(practiceId) && paymentDTO.getPaymentPayload().hasPaymentMethods(practiceId)) {
            paymentEnabled = true;
            startPaymentProcess(paymentDTO);
        } else {
            paymentEnabled = false;
            PaymentDisabledAlertDialogFragment fragment = PaymentDisabledAlertDialogFragment
                    .newInstance(Label.getLabel("payments.pendingPayments.patientFeedbackPopup.label.title"),
                            Label.getLabel("payments.pendingPayments.patientFeedbackPopup.label.description"),
                            selectedBalancesItem);
            displayDialogFragment(fragment, true);
        }
    }

    @Override
    public void loadPaymentPlanScreen(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO) {
        selectedUserPractice = paymentsModel.getPaymentPayload().getUserPractice(paymentPlanDTO.getMetadata().getPracticeId());
        patientPaymentPlanDetailsDialogFragment = PatientPaymentPlanDetailsDialogFragment
                .newInstance(paymentsModel, paymentPlanDTO);
        displayDialogFragment(patientPaymentPlanDetailsDialogFragment, false);
    }

    @Override
    public void onRequestRefresh(int requestedPage) {
        displayPage = requestedPage;
        refreshBalance(false);
    }

    private void setPendingBalance(PaymentsBalancesItem selectedBalancesItem) {
        PendingBalanceDTO pendingBalanceDTO = new PendingBalanceDTO();
        pendingBalanceDTO.setMetadata(selectedBalancesItem.getMetadata());
        pendingBalanceDTO.getPayload().add(selectedBalancesItem.getBalance());
        this.selectedBalancesItem = pendingBalanceDTO;
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
    public void onSubmitPaymentPlan(WorkflowDTO workflowDTO) {
        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
            getSupportFragmentManager().popBackStack();
        }

        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        String practiceId = paymentsModel.getPaymentPayload()
                .getPatientPaymentPlans().get(0).getMetadata().getPracticeId();
        PaymentPlanConfirmationFragment fragment = PaymentPlanConfirmationFragment
                .newInstance(workflowDTO, paymentsModel.getPaymentPayload().getUserPractice(practiceId),
                        PaymentPlanConfirmationFragment.MODE_CREATE);
        displayDialogFragment(fragment, false);
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
        List<ScheduledPaymentModel> scheduledPaymentModels = this.paymentsDTO.getPaymentPayload()
                .getScheduledOneTimePayments();
        for (ScheduledPaymentModel scheduledPaymentModel : scheduledPaymentModels) {
            if (scheduledPaymentModel.getMetadata().getOneTimePaymentId().equals(scheduledPayment
                    .getMetadata().getOneTimePaymentId())) {
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
        SystemUtil.hideSoftKeyboard(this);
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
        if (workflowDTO == null) {

        } else {
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
            initFragments();
            return;
        }
        String practiceId = paymentsModel.getPaymentPayload().getPatientPaymentPlans().get(0)
                .getMetadata().getPracticeId();

        PaymentPlanConfirmationFragment fragment = PaymentPlanConfirmationFragment
                .newInstance(workflowDTO, paymentsModel.getPaymentPayload().getUserPractice(practiceId),
                        PaymentPlanConfirmationFragment.MODE_EDIT);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onPaymentPlanAddedExisting(WorkflowDTO workflowDTO) {
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        String practiceId = paymentsModel.getPaymentPayload().getPatientPaymentPlans().get(0)
                .getMetadata().getPracticeId();

        PaymentPlanConfirmationFragment confirmationFragment = PaymentPlanConfirmationFragment
                .newInstance(workflowDTO, paymentsModel.getPaymentPayload().getUserPractice(practiceId),
                        PaymentPlanConfirmationFragment.MODE_ADD);
        displayDialogFragment(confirmationFragment, false);
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
    public void onPaymentPlanCanceled(WorkflowDTO workflowDTO, boolean isDeleted) {
        String message = Label.getLabel("payment.cancelPaymentPlan.success.banner.text");
        if (isDeleted) {
            message = Label.getLabel("payment.deletePaymentPlan.success.banner.text");
        }
        showSuccessToast(message);
        clearFragments();
        initFragments();
        refreshBalance(true);
    }

    @Override
    public void showCancelPaymentPlanConfirmDialog(ConfirmationCallback confirmationCallback, boolean isDeleted) {
        String title = Label.getLabel("payment.cancelPaymentPlan.confirmation.popup.title");
        String message = Label.getLabel("payment.cancelPaymentPlan.confirmation.popup.message");
        if (isDeleted) {
            title = Label.getLabel("payment.deletePaymentPlan.confirmation.popup.title");
            message = Label.getLabel("payment.deletePaymentPlan.confirmation.popup.message");
        }
        ConfirmDialogFragment fragment = ConfirmDialogFragment
                .newInstance(title, message, Label.getLabel("no"), Label.getLabel("yes"));
        fragment.setCallback(confirmationCallback);
        fragment.show(getSupportFragmentManager().beginTransaction(), fragment.getClass().getName());
    }

    @Override
    public void completePaymentPlanProcess(WorkflowDTO workflowDTO) {
        clearFragments();
        showRateDialogFragment();
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

        //payments plan not found for modification
        paymentPlanList.addAll(paymentsModel.getPaymentPayload().getPatientPaymentPlans());
        completePaymentProcess(workflowDTO);

    }

    private void showRateDialogFragment() {
        if (ApplicationPreferences.getInstance().shouldShowRateDialog()) {
            displayDialogFragment(RateDialog.newInstance(), true);
        }
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

    @Override
    protected void onProfileChanged(ProfileDto profile) {
        toolBarTitle = getScreenTitle(Label.getLabel("payment_patient_balance_toolbar"));
        displayToolbar(true, toolBarTitle);
        callPaymentsService();
    }

    @Override
    protected Profile getCurrentProfile() {
        return paymentsDTO.getPaymentPayload().getDelegate();
    }

    public void navigateToFragment(Fragment fragment, boolean addToBackStack) {
        //NA
    }
}
