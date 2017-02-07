package com.carecloud.carepay.patient.payment.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.MenuPatientActivity;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.patient.payment.ResponsibilityFragment;
import com.carecloud.carepay.patient.payment.androidpay.ConfirmationActivity;
import com.carecloud.carepay.patient.payment.fragments.PaymentBalanceHistoryFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.gson.Gson;

/**
 * Created by jorge on 29/12/16.
 */

public class ViewPaymentBalanceHistoryActivity extends MenuPatientActivity {

    private static boolean isPaymentDone;
    private PaymentsModel paymentsDTO;
    public Bundle bundle;

    public static boolean isPaymentDone() {
        return isPaymentDone;
    }

    public static void setIsPaymentDone(boolean isPaymentDone) {
        ViewPaymentBalanceHistoryActivity.isPaymentDone = isPaymentDone;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_balance_history);

        drawer = (DrawerLayout) findViewById(com.carecloud.carepaylibrary.R.id.drawer_layout_hist);
        navigationView = (NavigationView) findViewById(com.carecloud.carepaylibrary.R.id.nav_view_hist);
        appointmentsDrawerUserIdTextView = (TextView) navigationView.getHeaderView(0)
                .findViewById(com.carecloud.carepaylibrary.R.id.appointmentsDrawerIdTextView);
        paymentsDTO = getConvertedDTO(PaymentsModel.class);

        practiceId = paymentsDTO.getPaymentPayload().getPatientPaymentPlans().getMetadata().getPracticeId();
        practiceMgmt = paymentsDTO.getPaymentPayload().getPatientPaymentPlans().getMetadata().getPracticeMgmt();
        patientId = paymentsDTO.getPaymentPayload().getPatientPaymentPlans().getMetadata().getPatientId();
        displayToolbar(true);
        inflateDrawer();
        FragmentManager fm = getSupportFragmentManager();
        PaymentBalanceHistoryFragment paymentBalanceHistoryFragment = (PaymentBalanceHistoryFragment)
                fm.findFragmentByTag(PaymentBalanceHistoryFragment.class.getSimpleName());
        if (paymentBalanceHistoryFragment == null) {
            paymentBalanceHistoryFragment = new PaymentBalanceHistoryFragment();
        }
        //params
        Gson gson = new Gson();
        String paymentsDTOString = gson.toJson(paymentsDTO);
        bundle = new Bundle();
        bundle.putString(CarePayConstants.INTAKE_BUNDLE, paymentsDTOString);
        if (paymentBalanceHistoryFragment.getArguments() != null) {
            paymentBalanceHistoryFragment.getArguments().putAll(bundle);
        } else {
            paymentBalanceHistoryFragment.setArguments(bundle);
        }
        //include fragment
        fm.beginTransaction().replace(R.id.add_balance_history_frag_holder, paymentBalanceHistoryFragment,
                PaymentBalanceHistoryFragment.class.getSimpleName()).commit();
        navigationView.getMenu().getItem(CarePayConstants.NAVIGATION_ITEM_INDEX_PAYMENTS).setChecked(true);

    }

    /**
     * Display toolbar.
     *
     * @param visibility the visibility
     */
    public void displayToolbar(boolean visibility){
        toolbar = (Toolbar) findViewById(com.carecloud.carepaylibrary.R.id.balance_history_toolbar);
        TextView toolbarText = (TextView) findViewById(R.id.balance_history_toolbar_title);
        String toolBarTitle = paymentsDTO.getPaymentsMetadata().getPaymentsLabel().getPaymentPatientBalanceToolbar();
        toolbarText.setText(StringUtil.isNullOrEmpty(toolBarTitle) ? CarePayConstants.NOT_DEFINED : toolBarTitle);
        if(visibility){
            setSupportActionBar(toolbar);
            getSupportActionBar().show();
        } else {
            getSupportActionBar().hide();
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
        transaction.replace(R.id.add_balance_history_frag_holder, fragment, fragment.getClass().getSimpleName());
        if (addToBackStack) {
            transaction.addToBackStack(fragment.getClass().getName());
        }
        transaction.commitAllowingStateLoss();
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
}
