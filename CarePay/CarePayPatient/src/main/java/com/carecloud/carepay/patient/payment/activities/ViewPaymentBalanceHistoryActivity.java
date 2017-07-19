package com.carecloud.carepay.patient.payment.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.patient.payment.androidpay.ConfirmationActivity;
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
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.fragments.AddNewCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.ChooseCreditCardFragment;
import com.carecloud.carepaylibray.payments.fragments.PartialPaymentDialog;
import com.carecloud.carepaylibray.payments.fragments.PaymentConfirmationFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsBalancesItem;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.gson.Gson;

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

    public static boolean isPaymentDone() {
        return isPaymentDone;
    }

    public static void setIsPaymentDone(boolean isPaymentDone) {
        ViewPaymentBalanceHistoryActivity.isPaymentDone = isPaymentDone;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paymentsDTO = getConvertedDTO(PaymentsModel.class);

        toolBarTitle = Label.getLabel("payment_patient_balance_toolbar");
        displayToolbar(true, toolBarTitle);
        inflateDrawer();

        initFragments();
    }

    private void initFragments(){
        if (hasPayments() || hasCharges()) {
            replaceFragment(new PaymentBalanceHistoryFragment(), false);
        } else {
            showNoPaymentsLayout();
        }
    }

    private boolean hasCharges() {
        return paymentsDTO.getPaymentPayload().getPatientHistory().getPaymentsPatientCharges()
                .getCharges().size() > 0;
    }

    private boolean hasPayments() {
        return paymentsDTO.getPaymentPayload().getPatientBalances().size() > 0
                && paymentsDTO.getPaymentPayload().getPatientBalances().get(0).getBalances().size() > 0
                && paymentsDTO.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0)
                .getPayload().size() > 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.getMenu().findItem(R.id.nav_payments).setChecked(true);
    }

    /**
     * Invoked after the user taps the Buy With Android Pay button and the selected
     * credit card and shipping address are confirmed. If the request succeeded,
     * a {@link MaskedWallet} object is attached to the Intent. The Confirmation Activity is
     * then launched, providing it with the {@link MaskedWallet} object.
     *
     * @param requestCode The code that was set in the Masked Wallet Request
     * @param resultCode  The result of the request execution
     * @param data        Intent carrying the results
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // retrieve the error code, if available
        int errorCode = -1;
        if (data != null) {
            errorCode = data.getIntExtra(WalletConstants.EXTRA_ERROR_CODE, -1);
        }
        switch (requestCode) {
            case PaymentConstants.REQUEST_CODE_MASKED_WALLET:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        MaskedWallet maskedWallet =
                                data.getParcelableExtra(WalletConstants.EXTRA_MASKED_WALLET);
                        Fragment fragment = getSupportFragmentManager().findFragmentByTag(ResponsibilityFragment.class.getName());
                        if (fragment != null) {
                            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                        }
                        //setTitle("Confirmation");

                        launchConfirmationPage(maskedWallet);
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                    default:
                        handleError(errorCode);
                        break;
                }
                break;

            case WalletConstants.RESULT_ERROR:
                handleError(errorCode);
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void launchConfirmationPage(MaskedWallet maskedWallet) {
        Intent intent = ConfirmationActivity.newIntent(this, maskedWallet, paymentsDTO
                        .getPaymentPayload().getPatientBalances().get(0).getPendingRepsonsibility(), "CERT", bundle,
                Label.getLabel("payment_patient_balance_toolbar"));
        startActivity(intent);
    }


    protected void handleError(int errorCode) {
        switch (errorCode) {
            case WalletConstants.ERROR_CODE_SPENDING_LIMIT_EXCEEDED:
                Toast.makeText(this, "Way too much!!", Toast.LENGTH_LONG).show();
                break;
            case WalletConstants.ERROR_CODE_INVALID_PARAMETERS:
            case WalletConstants.ERROR_CODE_AUTHENTICATION_FAILURE:
            case WalletConstants.ERROR_CODE_BUYER_ACCOUNT_ERROR:
            case WalletConstants.ERROR_CODE_MERCHANT_ACCOUNT_ERROR:
            case WalletConstants.ERROR_CODE_SERVICE_UNAVAILABLE:
            case WalletConstants.ERROR_CODE_UNSUPPORTED_API_VERSION:
            case WalletConstants.ERROR_CODE_UNKNOWN:
            default:
                // unrecoverable error
                String errorMessage = "Android Pay is unavailable" + "\n" +
                        "Error code: " + errorCode;
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void startPaymentProcess(PaymentsModel paymentsModel) {
        ResponsibilityFragment responsibilityFragment = ResponsibilityFragment.newInstance(paymentsModel, selectedBalancesItem, false);
        replaceFragment(responsibilityFragment, true);
    }

    @Override
    public void onPartialPaymentClicked(double owedAmount) {
        new PartialPaymentDialog(this, paymentsDTO).show();
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
        if (paymentsDTO.getPaymentPayload().getPatientCreditCards() != null && !paymentsDTO.getPaymentPayload().getPatientCreditCards().isEmpty()) {
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
        paymentsDTO.getPaymentPayload().setPatientBalances(updatePaymentModel.getPaymentPayload().getPatientBalances());
        initFragments();
    }

    @Override
    public void onPayLaterClicked(PendingBalanceDTO pendingBalanceDTO) {

    }

    @Override
    public UserPracticeDTO getPracticeInfo(PaymentsModel paymentsModel) {
        return selectedUserPractice;
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
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        Bundle args = new Bundle();
        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, workflowDTO.toString());

        PaymentConfirmationFragment confirmationFragment = new PaymentConfirmationFragment();
        confirmationFragment.setArguments(args);
        confirmationFragment.show(getSupportFragmentManager(), confirmationFragment.getClass().getSimpleName());

    }

    @Override
    public DTO getDto() {
        return paymentsDTO;
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
    public void onDetailCancelClicked(PaymentsModel paymentsModel) {
        loadPaymentAmountScreen(null, paymentsModel);
    }

    private void setPendingBalance(PaymentsBalancesItem selectedBalancesItem){
        PendingBalanceDTO pendingBalanceDTO = new PendingBalanceDTO();
        pendingBalanceDTO.setMetadata(selectedBalancesItem.getMetadata());
        pendingBalanceDTO.getPayload().add(selectedBalancesItem.getBalance());
        this.selectedBalancesItem = pendingBalanceDTO;
    }
}
