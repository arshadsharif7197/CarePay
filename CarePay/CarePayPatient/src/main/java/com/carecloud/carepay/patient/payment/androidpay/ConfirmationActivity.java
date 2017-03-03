package com.carecloud.carepay.patient.payment.androidpay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.android.gms.wallet.fragment.SupportWalletFragment;
import com.google.android.gms.wallet.fragment.WalletFragmentInitParams;
import com.google.android.gms.wallet.fragment.WalletFragmentMode;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions;
import com.google.android.gms.wallet.fragment.WalletFragmentStyle;
import com.google.gson.Gson;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;

/**
 * Activity that displays the user's Google Wallet checkout confirmation page. It displays
 * the MaskedWallet fragment as well as a confirmation button.
 *
 * @see FullWalletConfirmationButtonFragment
 */
public class ConfirmationActivity extends AppCompatActivity {

    private SupportWalletFragment walletFragment;
    private MaskedWallet maskedWallet;
    /**
     * The Bundle.
     */
    public Bundle bundle;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        maskedWallet = getIntent().getParcelableExtra(PaymentConstants.EXTRA_MASKED_WALLET);

        bundle = getIntent().getExtras();
        String paymentsDTOString = bundle.getString(PaymentConstants.EXTRA_RESULT_MESSAGE);

        setContentView(R.layout.activity_confirmation);
        Toolbar toolbar = (Toolbar) findViewById(com.carecloud.carepaylibrary.R.id.toolbar_layout);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, com.carecloud.carepaylibrary.R.drawable.icn_nav_back));
        TextView title = (TextView) toolbar.findViewById(com.carecloud.carepaylibrary.R.id.respons_toolbar_title);

        if(paymentsDTOString != null){
            Gson gson = new Gson();
            PaymentsLabelDTO paymentLabelsDTO = gson.fromJson(paymentsDTOString, PaymentsLabelDTO.class);
            title.setText(paymentLabelsDTO.getPaymentPatientBalanceToolbar());
        }
        setGothamRoundedMediumTypeface(this, title);
        setSupportActionBar(toolbar);
        createAndAddWalletFragment();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
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
                        R.color.colorPrimary)
                .setMaskedWalletDetailsButtonTextAppearance(R.drawable.cell_checkbox_on)
                .setMaskedWalletDetailsButtonBackgroundResource(R.drawable.button_blue)
                .setMaskedWalletDetailsBackgroundResource(R.color.colorPrimary);

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

    /**
     * Handle error.
     *
     * @param errorCode the error code
     */
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
     * Gets result target fragment.
     *
     * @return the result target fragment
     */
    public Fragment getResultTargetFragment() {
        return getSupportFragmentManager().findFragmentById(
                R.id.full_wallet_confirmation_button_fragment);
    }

    /**
     * New intent intent.
     *
     * @param context      the context
     * @param maskedWallet the masked wallet
     * @param amount       the amount
     * @param env          the env
     * @param bundle       the bundle
     * @return the intent
     */
    public static Intent newIntent(Context context, MaskedWallet maskedWallet, String amount, String env, Bundle bundle, String labels) {
        Intent intent = new Intent(context, ConfirmationActivity.class);
        intent.putExtra(PaymentConstants.EXTRA_MASKED_WALLET, maskedWallet);
        intent.putExtra(PaymentConstants.EXTRA_AMOUNT, amount);
        intent.putExtra(PaymentConstants.EXTRA_ENV, env);
        intent.putExtra(PaymentConstants.EXTRA_RESULT_MESSAGE, labels);
        intent.putExtra(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, bundle);
        return intent;
    }
}
