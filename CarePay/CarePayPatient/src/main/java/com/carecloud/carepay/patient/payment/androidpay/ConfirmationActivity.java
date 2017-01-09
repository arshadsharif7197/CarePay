package com.carecloud.carepay.patient.payment.androidpay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.android.gms.wallet.fragment.SupportWalletFragment;
import com.google.android.gms.wallet.fragment.WalletFragmentInitParams;
import com.google.android.gms.wallet.fragment.WalletFragmentMode;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions;
import com.google.android.gms.wallet.fragment.WalletFragmentStyle;

import static android.R.attr.fragment;

/**
 * Activity that displays the user's Google Wallet checkout confirmation page. It displays
 * the MaskedWallet fragment as well as a confirmation button.
 *
 * @see FullWalletConfirmationButtonFragment
 */
public class ConfirmationActivity extends FragmentActivity {

    private SupportWalletFragment walletFragment;
    private MaskedWallet maskedWallet;
    public Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        maskedWallet = getIntent().getParcelableExtra(PaymentConstants.EXTRA_MASKED_WALLET);

        bundle = getIntent().getExtras();
        setContentView(R.layout.activity_confirmation);

        createAndAddWalletFragment();

    }

    /**
     * Create the MaskedWallet fragment and add it to the UI.
     */
    private void createAndAddWalletFragment() {
        WalletFragmentStyle walletFragmentStyle = new WalletFragmentStyle()
                .setMaskedWalletDetailsTextAppearance(
                        R.style.WalletFragmentDetailsTextAppearance)
                .setMaskedWalletDetailsHeaderTextAppearance(
                        R.style.WalletFragmentDetailsHeaderTextAppearance)
                .setMaskedWalletDetailsBackgroundColor(
                        R.color.blue_cerulian)
                .setMaskedWalletDetailsButtonTextAppearance(R.drawable.cell_checkbox_on)
                .setMaskedWalletDetailsButtonBackgroundResource(R.drawable.button_blue)
                .setMaskedWalletDetailsBackgroundResource(R.color.blue_cerulian);

        WalletFragmentOptions walletFragmentOptions = WalletFragmentOptions.newBuilder()
                .setEnvironment(PaymentConstants.WALLET_ENVIRONMENT)
                .setFragmentStyle(walletFragmentStyle)
                .setTheme(WalletConstants.THEME_LIGHT)
                .setMode(WalletFragmentMode.SELECTION_DETAILS)
                .build();
        walletFragment = SupportWalletFragment.newInstance(walletFragmentOptions);

        // Now initialize the Wallet Fragment
        String accountName = getString(R.string.account_name);
        WalletFragmentInitParams.Builder startParamsBuilder = WalletFragmentInitParams.newBuilder()
                .setMaskedWallet(maskedWallet)
                .setMaskedWalletRequestCode(PaymentConstants.REQUEST_CODE_CHANGE_MASKED_WALLET)
                .setAccountName(accountName);
        walletFragment.initialize(startParamsBuilder.build());

        // add Wallet fragment to the UI
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.dynamic_wallet_masked_wallet_fragment, walletFragment)
                .commit();
    }

    /**
     * Invoked as a result of a user action on the confirmation page. The user can change the
     * credit card or the shipping address, in which case a new {@link MaskedWallet} is provided
     * in the Intent. If the user confirms the transaction, the call is delegated to the
     * {@link FullWalletConfirmationButtonFragment} which completes the transaction.
     *
     * @param requestCode The request code
     * @param resultCode  The result of the request execution
     * @param data        Intent carrying the results
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            //  The user made changes to the credit card or shipping address
            case PaymentConstants.REQUEST_CODE_CHANGE_MASKED_WALLET:
                if (resultCode == Activity.RESULT_OK &&
                        data.hasExtra(WalletConstants.EXTRA_MASKED_WALLET)) {
                    maskedWallet = data.getParcelableExtra(WalletConstants.EXTRA_MASKED_WALLET);
                    FullWalletConfirmationButtonFragment resultTargetFragment =
                            (FullWalletConfirmationButtonFragment) getResultTargetFragment();
                    resultTargetFragment.setArguments(bundle);
                    resultTargetFragment.updateMaskedWallet(maskedWallet);

                }
                // you may also want to use the new masked wallet data here, say to recalculate
                // shipping or taxes if shipping address changed
                break;
            case WalletConstants.RESULT_ERROR:
                int errorCode = data.getIntExtra(WalletConstants.EXTRA_ERROR_CODE, 0);
                handleError(errorCode);
                break;
            //  The user confirmed the transaction
            case FullWalletConfirmationButtonFragment.REQUEST_CODE_RESOLVE_LOAD_FULL_WALLET:
            case FullWalletConfirmationButtonFragment.REQUEST_CODE_RESOLVE_ERR:
                Fragment fragment = getResultTargetFragment();
                if (fragment != null) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
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

    public Fragment getResultTargetFragment() {
        return getSupportFragmentManager().findFragmentById(
                R.id.full_wallet_confirmation_button_fragment);
    }

    public static Intent newIntent(Context context, MaskedWallet maskedWallet, String amount, String env, Bundle bundle) {
        Intent intent = new Intent(context, ConfirmationActivity.class);
        intent.putExtra(PaymentConstants.EXTRA_MASKED_WALLET, maskedWallet);
        intent.putExtra(PaymentConstants.EXTRA_AMOUNT, amount);
        intent.putExtra(PaymentConstants.EXTRA_ENV, env);
        intent.putExtra(PaymentConstants.EXTRA_ENV, env);
        intent.putExtra(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, bundle);
        return intent;
    }
}
