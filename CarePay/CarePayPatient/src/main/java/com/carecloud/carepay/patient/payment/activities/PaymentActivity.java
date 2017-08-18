package com.carecloud.carepay.patient.payment.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.payment.PatientPaymentPresenter;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.patient.payment.androidpay.ConfirmationActivity;
import com.carecloud.carepay.patient.payment.fragments.ResponsibilityFragment;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.presenter.PaymentPresenter;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.WalletConstants;

public class PaymentActivity extends BasePatientActivity implements PaymentViewHandler {
    PaymentsModel paymentsDTO;
    PatientPaymentPresenter presenter;

    private boolean isFirstFragment = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        paymentsDTO = getConvertedDTO(PaymentsModel.class);

        initPresenter();

        presenter.startPaymentProcess(paymentsDTO);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            SystemUtil.hideSoftKeyboard(this);
            onBackPressed();
        }
        return true;
    }

    private void initPresenter(){
        String defaultPatientId = paymentsDTO.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getMetadata().getPatientId();
        this.presenter = new PatientPaymentPresenter(this, paymentsDTO, defaultPatientId);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * Invoked after the user taps the Buy With Android Pay button and the selected
     * credit card and shipping address are confirmed. If the request succeeded,
     * a {@link MaskedWallet} object is attached to the Intent. The Confirmation Activity is
     * then launched, providing it with the {@link MaskedWallet} object.
     *
     * @param //requestCode The code that was set in the Masked Wallet Request
     * @param //resultCode  The result of the request execution
     * @param //data        Intent carrying the results
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
                        Fragment fragment = getSupportFragmentManager().findFragmentByTag(ResponsibilityFragment.class.getSimpleName());
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
        Bundle bundle = new Bundle();
        DtoHelper.bundleDto(bundle, paymentsDTO);
        Intent intent = ConfirmationActivity.newIntent(this, maskedWallet, paymentsDTO
                        .getPaymentPayload().getPatientBalances().get(0).getPendingRepsonsibility(), "CERT", bundle,
                Label.getLabel("payment_patient_balance_toolbar"));
        startActivity(intent);
    }


    protected void handleError(int errorCode) {
        switch (errorCode) {
            case WalletConstants.ERROR_CODE_SPENDING_LIMIT_EXCEEDED:
                showErrorNotification(PaymentConstants.ANDROID_PAY_SPENDING_LIMIT_EXCEEDED);
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
                String errorMessage = "<b>Android Pay is unavailable</b>" + "\n" +
                        "Error code: " + errorCode;
                showErrorNotification(errorMessage);
                break;
        }
    }

    @Override
    public PaymentPresenter getPaymentPresenter() {
        return presenter;
    }

    /**
     * Helper method to replace fragments
     *
     * @param fragment       The fragment
     * @param addToBackStack Whether to add the transaction to back-stack
     */
    public void navigateToFragment(final Fragment fragment, final boolean addToBackStack) {
//        FragmentManager fm = getSupportFragmentManager();
//        FragmentTransaction transaction = fm.beginTransaction();
//        transaction.replace(R.id.payment_frag_holder, fragment, fragment.getClass().getSimpleName());
//        if (addToBackStack) {
//            transaction.addToBackStack(fragment.getClass().getName());
//        }
//        transaction.commitAllowingStateLoss();
//
        replaceFragment(R.id.payment_frag_holder, fragment, addToBackStack && !isFirstFragment);
        isFirstFragment = false;
    }

    @Override
    public void exitPaymentProcess(boolean cancelled) {
        if(getCallingActivity()!=null){
            setResult(cancelled ? RESULT_CANCELED : RESULT_OK);
        }
        finish();
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

}
