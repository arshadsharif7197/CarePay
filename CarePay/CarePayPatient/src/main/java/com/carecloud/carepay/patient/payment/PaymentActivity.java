package com.carecloud.carepay.patient.payment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.Toast;

import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.payment.androidpay.ConfirmationActivity;
import com.carecloud.carepay.patient.payment.fragments.PatientPaymentMethodFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.payments.fragments.ResponsibilityBaseFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.gson.Gson;

public class PaymentActivity extends BasePatientActivity implements ResponsibilityBaseFragment.ResponsibilityActionCallback {
    PaymentsModel paymentsDTO;
    private String paymentsDTOString;
    public Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        //Intent intent = getIntent();
        paymentsDTO = getConvertedDTO(PaymentsModel.class);


        FragmentManager fm = getSupportFragmentManager();
        ResponsibilityFragment fragment = (ResponsibilityFragment)
                fm.findFragmentByTag(ResponsibilityFragment.class.getSimpleName());
        if (fragment == null) {
            fragment = new ResponsibilityFragment();
        }
        bundle = new Bundle();

        Gson gson = new Gson();
        paymentsDTOString = gson.toJson(paymentsDTO);
        bundle.putString(CarePayConstants.INTAKE_BUNDLE, paymentsDTOString);
        //fix for random crashes
        if (fragment.getArguments() != null) {
            fragment.getArguments().putAll(bundle);
        } else {
            fragment.setArguments(bundle);
        }

        fm.beginTransaction().replace(R.id.payment_frag_holder, fragment,
                ResponsibilityFragment.class.getSimpleName()).commit();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            SystemUtil.hideSoftKeyboard(this);
            onBackPressed();
        }
        return true;
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
        //setTitle("");
        Intent intent = ConfirmationActivity.newIntent(this, maskedWallet, paymentsDTO.getPaymentPayload().getPatientBalances().get(0).getPendingRepsonsibility(), "CERT", bundle, new Gson().toJson(paymentsDTO.getPaymentsMetadata().getPaymentsLabel()));// .getPayload().get(0).getTotal(), "CERT");
        // intent.putExtra(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, paymentsDTOString);
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

    /**
     * Helper method to replace fragments
     *
     * @param fragment       The fragment
     * @param addToBackStack Whether to add the transaction to back-stack
     */
    public void navigateToFragment(final Fragment fragment, final boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(com.carecloud.carepay.patient.R.id.payment_frag_holder, fragment, fragment.getClass().getSimpleName());
        if (addToBackStack) {
            transaction.addToBackStack(fragment.getClass().getName());
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void makePayment(double amount) {
        PatientPaymentMethodFragment fragment = new PatientPaymentMethodFragment();


        Bundle bundle = new Bundle();
        bundle.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        bundle.putString(CarePayConstants.INTAKE_BUNDLE, paymentsDTOString);
        fragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.payment_frag_holder, fragment);
        fragmentTransaction.addToBackStack(PatientPaymentMethodFragment.class.getSimpleName());
        fragmentTransaction.commit();

    }

    @Override
    public void startPartialPayment() {

    }
}
