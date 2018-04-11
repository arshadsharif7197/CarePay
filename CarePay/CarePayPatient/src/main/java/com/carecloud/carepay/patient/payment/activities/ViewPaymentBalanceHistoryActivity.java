package com.carecloud.carepay.patient.payment.activities;

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
import com.carecloud.carepay.patient.payment.fragments.NoPaymentsFragment;
import com.carecloud.carepay.patient.payment.fragments.PatientPaymentMethodFragment;
import com.carecloud.carepay.patient.payment.fragments.PaymentBalanceHistoryFragment;
import com.carecloud.carepay.patient.payment.fragments.PaymentDisabledAlertDialogFragment;
import com.carecloud.carepay.patient.payment.fragments.PaymentPlanPaymentMethodFragment;
import com.carecloud.carepay.patient.payment.interfaces.PaymentFragmentActivityInterface;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.carecloud.carepaylibray.customcomponents.CustomMessageToast;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.fragments.ActivePlansFragment;
import com.carecloud.carepaylibray.payments.fragments.AddExistingPaymentPlanFragment;
import com.carecloud.carepaylibray.payments.fragments.AddNewCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.ChooseCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.OneTimePaymentDialog;
import com.carecloud.carepaylibray.payments.fragments.PartialPaymentDialog;
import com.carecloud.carepaylibray.payments.fragments.PaymentConfirmationFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanAddCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanChooseCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanDetailsDialogFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanEditFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanTermsFragment;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanInterface;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDetailsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsBalancesItem;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPayloadSettingsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsSettingsPaymentPlansDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryItem;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.android.gms.wallet.MaskedWallet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jorge on 29/12/16
 */
public class ViewPaymentBalanceHistoryActivity extends MenuPatientActivity implements PaymentFragmentActivityInterface,
        PaymentPlanInterface, PaymentDisabledAlertDialogFragment.DisabledPaymentAlertCallback {

    private PaymentsModel paymentsDTO;
    private UserPracticeDTO selectedUserPractice;
    private PendingBalanceDTO selectedBalancesItem;

    public Bundle bundle;
    private String toolBarTitle;

    private Fragment androidPayTargetFragment;

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
            replaceFragment(new PaymentBalanceHistoryFragment(), false);
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
                if (paymentPlanDTO.getPayload().getPaymentPlanDetails().getPaymentPlanStatus().equals(PaymentPlanDetailsDTO.STATUS_PROCESSING)) {
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
        replaceFragment(PatientPaymentMethodFragment.newInstance(paymentsModel, amount, false), true);
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
        PaymentsModel updatePaymentModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        updateBalances(updatePaymentModel.getPaymentPayload().getPatientBalances());
        if (updatePaymentModel.getPaymentPayload().getPaymentPlanUpdate() != null) {
            updatePaymentPlan(updatePaymentModel.getPaymentPayload().getPaymentPlanUpdate());
        }
        initFragments();
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
    public void onPaymentPlanAction(PaymentsModel paymentsModel) {
        PendingBalanceDTO reducedBalancesItem = reduceBalanceItems(selectedBalancesItem, paymentsModel.getPaymentPayload()
                .getActivePlans(selectedUserPractice.getPracticeId()));
        if(mustAddToExisting(paymentsModel)){
            onAddBalanceToExitingPlan(paymentsModel, reducedBalancesItem);
        } else {
            PaymentPlanFragment fragment = PaymentPlanFragment.newInstance(paymentsModel, reducedBalancesItem);
            replaceFragment(fragment, true);
        }
        displayToolbar(false, null);
    }

    @Override
    public void showPaymentConfirmation(WorkflowDTO workflowDTO) {
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
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            Bundle args = new Bundle();
            args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, workflowDTO.toString());
            PaymentConfirmationFragment confirmationFragment = new PaymentConfirmationFragment();
            confirmationFragment.setArguments(args);
            displayDialogFragment(confirmationFragment, false);
        }

    }

    @Override
    public void showPaymentPendingConfirmation(PaymentsModel paymentsModel) {
        new CustomMessageToast(this, Label.getLabel("payments_external_pending"), CustomMessageToast.NOTIFICATION_TYPE_SUCCESS).show();
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
        PaymentPlanDetailsDialogFragment fragment = PaymentPlanDetailsDialogFragment.newInstance(paymentsModel, paymentPlanDTO);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void displayPaymentHistoryDetails(PaymentHistoryItem paymentHistoryItem) {
        selectedUserPractice = getUserPracticeById(paymentHistoryItem.getMetadata().getPracticeId());
        PaymentHistoryDetailDialogFragment fragment = PaymentHistoryDetailDialogFragment.newInstance(paymentHistoryItem, selectedUserPractice);
        displayDialogFragment(fragment, false);
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

    private void updateBalances(List<PatientBalanceDTO> updatedBalances) {
        for (PatientBalanceDTO updatedManagementBalance : updatedBalances) {//should contain just 1 element
            for (PendingBalanceDTO updatedBalance : updatedManagementBalance.getBalances()) {//should contain just 1 element - the updated balance that was just paid
                for (PatientBalanceDTO existingManagementBalance : paymentsDTO.getPaymentPayload().getPatientBalances()) {//should contain only 1 element until another PM is supported
                    for (PendingBalanceDTO existingBalance : existingManagementBalance.getBalances()) {//can contain multiple balances in multi practice mode, otherwise just 1
                        if (existingBalance.getMetadata().getPracticeId().equals(updatedBalance.getMetadata().getPracticeId())) {
                            existingBalance.setPayload(updatedBalance.getPayload());
                        }
                    }
                }
            }
        }
    }

    private void updatePaymentPlan(PaymentPlanDTO paymentPlanUpdate) {
        for (PaymentPlanDTO paymentPlanDTO : paymentsDTO.getPaymentPayload().getPatientPaymentPlans()) {
            if (paymentPlanDTO.getMetadata().getPaymentPlanId().equals(paymentPlanUpdate.getMetadata().getPaymentPlanId())) {
                paymentPlanDTO.setMetadata(paymentPlanUpdate.getMetadata());
                paymentPlanDTO.setPayload(paymentPlanUpdate.getPayload());
                return;
            }
        }
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
        replaceFragment(AndroidPayDialogFragment.newInstance(maskedWallet, paymentsDTO, amount), true);
    }

    @Override
    public void forwardAndroidPayResult(int requestCode, int resultCode, Intent data) {
        Fragment targetFragment = getAndroidPayTargetFragment();
        if (targetFragment != null) {
            targetFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onStartPaymentPlan(PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel) {
        PaymentPlanPaymentMethodFragment fragment = PaymentPlanPaymentMethodFragment.newInstance(paymentsModel, paymentPlanPostModel);
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
    public void onAddPaymentPlanCard(PaymentsModel paymentsModel,
                                     PaymentPlanPostModel paymentPlanPostModel,
                                     boolean onlySelectMode) {
        PaymentPlanAddCreditCardFragment fragment = PaymentPlanAddCreditCardFragment
                .newInstance(paymentsModel, paymentPlanPostModel, onlySelectMode);
        replaceFragment(fragment, true);
    }

    @Override
    public void onDisplayPaymentPlanTerms(PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel) {
        PaymentPlanTermsFragment fragment = PaymentPlanTermsFragment.newInstance(paymentsModel, paymentPlanPostModel);
        replaceFragment(fragment, true);
    }

    @Override
    public void onSubmitPaymentPlan(WorkflowDTO workflowDTO) {
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        List<PaymentPlanDTO> paymentPlanList = this.paymentsDTO.getPaymentPayload().getPatientPaymentPlans();
        paymentPlanList.addAll(paymentsModel.getPaymentPayload().getPatientPaymentPlans());

        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
            getSupportFragmentManager().popBackStack();
        }

        completePaymentProcess(workflowDTO);
    }

    @Override
    public void onMakeOneTimePayment(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO) {
        new OneTimePaymentDialog(getContext(), paymentsModel, paymentPlanDTO, this).show();
    }

    @Override
    public void onStartOneTimePayment(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO) {
        PaymentPlanPaymentMethodFragment fragment = PaymentPlanPaymentMethodFragment
                .newInstance(paymentsModel, paymentPlanDTO, false);
        replaceFragment(fragment, true);
        displayToolbar(false, toolBarTitle);
    }

    @Override
    public void onSelectPaymentPlanMethod(PaymentsMethodsDTO selectedPaymentMethod,
                                          PaymentsModel paymentsModel,
                                          PaymentPlanDTO paymentPlanDTO,
                                          boolean onlySelectMode) {
        if ((paymentsModel.getPaymentPayload().getPatientCreditCards() != null)
                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            PaymentPlanChooseCreditCardFragment fragment = PaymentPlanChooseCreditCardFragment
                    .newInstance(paymentsModel, selectedPaymentMethod.getLabel(), paymentPlanDTO, onlySelectMode);
            replaceFragment(fragment, true);
        } else {
            onAddPaymentPlanCard(paymentsModel, paymentPlanDTO, onlySelectMode);
        }
    }

    @Override
    public void onAddPaymentPlanCard(PaymentsModel paymentsModel,
                                     PaymentPlanDTO paymentPlanDTO,
                                     boolean onlySelectMode) {
        PaymentPlanAddCreditCardFragment fragment = PaymentPlanAddCreditCardFragment
                .newInstance(paymentsModel, paymentPlanDTO, onlySelectMode);
        replaceFragment(fragment, true);
    }

    @Override
    public void displayBalanceDetails(PaymentsModel paymentsModel, PendingBalancePayloadDTO paymentLineItem, PendingBalanceDTO selectedBalance) {
        PaymentDetailsFragmentDialog dialog = PaymentDetailsFragmentDialog
                .newInstance(paymentsModel, paymentLineItem, selectedBalance, false);
        displayDialogFragment(dialog, false);
    }

    @Override
    public void onEditPaymentPlan(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO) {
        PaymentPlanEditFragment fragment = PaymentPlanEditFragment.newInstance(paymentsModel, paymentPlanDTO);
        replaceFragment(fragment, true);
        displayToolbar(false, null);
    }

    @Override
    public void onPaymentPlanEdited(WorkflowDTO workflowDTO) {
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        if (!paymentsModel.getPaymentPayload().getPatientPaymentPlans().isEmpty()) {
            PaymentPlanDTO modifiedPaymentPlan = paymentsModel.getPaymentPayload().getPatientPaymentPlans().get(0);
            for (PaymentPlanDTO paymentPlan : paymentsDTO.getPaymentPayload().getPatientPaymentPlans()) {
                if (modifiedPaymentPlan.getMetadata().getPaymentPlanId()
                        .equals(paymentPlan.getMetadata().getPaymentPlanId())) {
                    paymentsDTO.getPaymentPayload().getPatientPaymentPlans().remove(paymentPlan);
                    paymentsDTO.getPaymentPayload().getPatientPaymentPlans().add(modifiedPaymentPlan);
                    break;
                }
            }
        }
        initFragments();
    }

    @Override
    public void onDismissEditPaymentPlan(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO) {

    }

    @Override
    public void onAddBalanceToExitingPlan(PaymentsModel paymentsModel, PendingBalanceDTO selectedBalance) {
        String practiceId = selectedBalance.getMetadata().getPracticeId();
        if(paymentsModel.getPaymentPayload().getActivePlans(practiceId).size() == 1){
            onSelectedPlanToAdd(paymentsModel,
                    selectedBalance,
                    paymentsModel.getPaymentPayload().getActivePlans(practiceId).get(0));
        }else{
            ActivePlansFragment fragment = ActivePlansFragment.newInstance(paymentsModel, selectedBalance);
            replaceFragment(fragment, true);
        }
    }

    @Override
    public void onSelectedPlanToAdd(PaymentsModel paymentsModel, PendingBalanceDTO selectedBalance, PaymentPlanDTO selectedPlan) {
        AddExistingPaymentPlanFragment fragment = AddExistingPaymentPlanFragment.newInstance(paymentsModel, selectedBalance, selectedPlan);
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
    public void onEditPaymentPlanPaymentMethod(PaymentsModel paymentsModel) {
        addFragment(PaymentPlanPaymentMethodFragment
                .newInstance(paymentsModel, new PaymentPlanDTO(), true), true);
    }

    private boolean mustAddToExisting(PaymentsModel paymentsModel) {
        if (paymentsModel.getPaymentPayload().getFilteredPlans(selectedBalancesItem.getMetadata().getPracticeId()).isEmpty()) {
            return false;
        }
        String practiceId = selectedBalancesItem.getMetadata().getPracticeId();
        for (PaymentsPayloadSettingsDTO settingsDTO : paymentsModel.getPaymentPayload().getPaymentSettings()) {
            if (practiceId != null && practiceId.equals(settingsDTO.getMetadata().getPracticeId())) {
                PaymentsSettingsPaymentPlansDTO paymentPlanSettings = settingsDTO.getPayload().getPaymentPlans();
                return !paymentPlanSettings.isCanHaveMultiple() && paymentPlanSettings.isAddBalanceToExisting();
            }
        }
        return false;
    }

    private PendingBalanceDTO reduceBalanceItems(PendingBalanceDTO selectedBalance, List<PaymentPlanDTO> currentPaymentPlans){
        Map<String, Double> paymentPlanItems = new HashMap<>();
        for(PaymentPlanDTO paymentPlanDTO : currentPaymentPlans){
            for(PaymentPlanLineItem lineItem : paymentPlanDTO.getPayload().getLineItems()){
                Double amount = lineItem.getAmount();
                if(paymentPlanItems.containsKey(lineItem.getTypeId())){//we may have the line item split on more than one plan potentially
                    amount = SystemUtil.safeAdd(paymentPlanItems.get(lineItem.getTypeId()), lineItem.getAmount());//sum both items
                }
                paymentPlanItems.put(lineItem.getTypeId(), amount);
            }
        }

        String balanceHolder = DtoHelper.getStringDTO(selectedBalance);
        PendingBalanceDTO copyPendingBalance = DtoHelper.getConvertedDTO(PendingBalanceDTO.class, balanceHolder);

        List<BalanceItemDTO> reducedBalances;
        for(PendingBalancePayloadDTO pendingBalancePayloadDTO : copyPendingBalance.getPayload()){
            reducedBalances = new ArrayList<>();
            for(BalanceItemDTO balanceItemDTO : pendingBalancePayloadDTO.getDetails()){
                if(paymentPlanItems.containsKey(balanceItemDTO.getId().toString())){
                    Double paymentPlanLineItemAmount = paymentPlanItems.get(balanceItemDTO.getId().toString());
                    if(paymentPlanLineItemAmount < balanceItemDTO.getBalance()){//reduce the balance item by the payment plan item amount
                        double originalBalanceItemBalance = balanceItemDTO.getBalance();
                        balanceItemDTO.setBalance(SystemUtil.safeSubtract(originalBalanceItemBalance, paymentPlanLineItemAmount));
                        reducedBalances.add(balanceItemDTO);
                    }//else the entire balance item will be dropped
                }else{
                    reducedBalances.add(balanceItemDTO); //since this item was not already on PP we need to keep it
                }
            }
            pendingBalancePayloadDTO.setDetails(reducedBalances);
        }
        return copyPendingBalance;
    }



}
