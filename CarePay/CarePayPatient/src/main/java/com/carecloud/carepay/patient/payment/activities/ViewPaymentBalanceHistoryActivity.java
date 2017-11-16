package com.carecloud.carepay.patient.payment.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.patient.payment.androidpay.AndroidPayDialogFragment;
import com.carecloud.carepay.patient.payment.dialogs.PaymentHistoryDetailDialogFragment;
import com.carecloud.carepay.patient.payment.fragments.NoPaymentsFragment;
import com.carecloud.carepay.patient.payment.fragments.PatientPaymentMethodFragment;
import com.carecloud.carepay.patient.payment.fragments.PaymentBalanceHistoryFragment;
import com.carecloud.carepay.patient.payment.fragments.PaymentPlanFragment;
import com.carecloud.carepay.patient.payment.fragments.ResponsibilityFragment;
import com.carecloud.carepay.patient.payment.interfaces.PaymentFragmentActivityInterface;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.customcomponents.CustomMessageToast;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.fragments.AddNewCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.ChooseCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.PartialPaymentDialog;
import com.carecloud.carepaylibray.payments.fragments.PaymentConfirmationFragment;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsBalancesItem;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryItem;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by jorge on 29/12/16
 */
public class ViewPaymentBalanceHistoryActivity extends MenuPatientActivity implements PaymentFragmentActivityInterface {

    private static boolean isPaymentDone;
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
        if (hasPayments() || hasCharges()) {
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
        ResponsibilityFragment responsibilityFragment = ResponsibilityFragment.newInstance(paymentsModel, selectedBalancesItem, false);
        replaceFragment(responsibilityFragment, true);
    }

    @Override
    public void onPartialPaymentClicked(double owedAmount, PendingBalanceDTO selectedBalance) {
        new PartialPaymentDialog(this, paymentsDTO, selectedBalance).show();
    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        replaceFragment(PatientPaymentMethodFragment.newInstance(paymentsModel, amount), true);
        displayToolbar(false, toolBarTitle);
    }

    @Override
    public void onBackPressed() {
        if (!toolbarVisibility && getSupportFragmentManager().getBackStackEntryCount() < 2) {
            displayToolbar(true, toolBarTitle);
        }
        super.onBackPressed();
    }

    @Override
    public void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod, double amount, PaymentsModel paymentsModel) {
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            Fragment fragment = ChooseCreditCardFragment.newInstance(paymentsModel, selectedPaymentMethod.getLabel(), amount);
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
        String paymentsDTOString = gson.toJson(paymentsDTO);
        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, paymentsDTOString);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        fragment = new AddNewCreditCardFragment();


        fragment.setArguments(args);
        replaceFragment(fragment, true);
    }

    @Override
    public void completePaymentProcess(WorkflowDTO workflowDTO) {
        PaymentsModel updatePaymentModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        updateBalances(updatePaymentModel.getPaymentPayload().getPatientBalances());
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
        PaymentPlanFragment fragment = new PaymentPlanFragment();

        Bundle args = new Bundle();
        Gson gson = new Gson();
        String paymentsDTOString = gson.toJson(paymentsDTO);
        args.putString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO, paymentsDTOString);
        fragment.setArguments(args);

        replaceFragment(fragment, true);
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
        selectedUserPractice = DtoHelper.getConvertedDTO(UserPracticeDTO.class, DtoHelper.getStringDTO(selectedBalancesItem.getMetadata()));
        startPaymentProcess(paymentDTO);
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
}
