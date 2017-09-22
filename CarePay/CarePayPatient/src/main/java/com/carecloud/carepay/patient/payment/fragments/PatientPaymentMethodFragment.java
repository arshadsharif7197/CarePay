package com.carecloud.carepay.patient.payment.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.demographicsettings.models.MerchantServicesDTO;
import com.carecloud.carepaylibray.payments.fragments.PaymentMethodFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wallet.Cart;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.LineItem;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.android.gms.wallet.PaymentMethodTokenizationType;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.android.gms.wallet.fragment.SupportWalletFragment;
import com.google.android.gms.wallet.fragment.WalletFragmentInitParams;
import com.google.android.gms.wallet.fragment.WalletFragmentMode;
import com.google.android.gms.wallet.fragment.WalletFragmentOptions;
import com.google.android.gms.wallet.fragment.WalletFragmentStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PatientPaymentMethodFragment extends PaymentMethodFragment implements GoogleApiClient.OnConnectionFailedListener {

    //Patient Specific Stuff
    private ProgressBar paymentMethodFragmentProgressBar;
    private GoogleApiClient googleApiClient;

    /**
     * @param paymentsModel the payments DTO
     * @param amount        the amount
     * @return an instance of PatientPaymentMethodFragment
     */
    public static PatientPaymentMethodFragment newInstance(PaymentsModel paymentsModel, double amount) {
        PatientPaymentMethodFragment fragment = new PatientPaymentMethodFragment();
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        paymentTypeMap.put(CarePayConstants.TYPE_ANDROID_PAY, R.drawable.payment_android_button_selector);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_method, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        paymentMethodFragmentProgressBar = (ProgressBar) view.findViewById(R.id.paymentMethodFragmentProgressBar);

        if (googleApiClient == null) {
            setGoogleApiClient();
        }
//        isAndroidPayReadyToUse(); TODO need to reenable this when ready
    }

    protected void setupTitleViews(View view) {
        super.setupTitleViews(view);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        if (toolbar != null) {
            toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().onBackPressed();
                }
            });
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }
    }

    private void setGoogleApiClient() {
        // [START basic_google_api_client]
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Wallet.API, new Wallet.WalletOptions.Builder()
                        .setEnvironment(PaymentConstants.WALLET_ENVIRONMENT)
                        .build())
                .enableAutoManage(getActivity(), this)
                .build();
        // [END basic_google_api_client]

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnectGoogleAPI();
    }

    @Override
    public void onStop() {
        super.onStop();
        disconnectGoogleAPI();
    }

    private void disconnectGoogleAPI() {
        googleApiClient.stopAutoManage(getActivity());
        googleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed:" + connectionResult.getErrorMessage());
        Toast.makeText(getActivity(), "Google Play Services error", Toast.LENGTH_SHORT).show();
    }


    private void isAndroidPayReadyToUse() {
        showOrHideProgressDialog(true);
        IsReadyToPayRequest req = IsReadyToPayRequest.newBuilder()
                .addAllowedCardNetwork(WalletConstants.CardNetwork.MASTERCARD)
                .addAllowedCardNetwork(WalletConstants.CardNetwork.VISA)
                .addAllowedCardNetwork(WalletConstants.CardNetwork.AMEX)
                .addAllowedCardNetwork(WalletConstants.CardNetwork.DISCOVER)
                .build();

        Wallet.Payments.isReadyToPay(googleApiClient, req).setResultCallback(
                new ResultCallback<BooleanResult>() {
                    @Override
                    public void onResult(@NonNull BooleanResult booleanResult) {
                        showOrHideProgressDialog(false);
                        if (booleanResult.getStatus().isSuccess() && booleanResult.getValue()) {
                            addAndroidPayPaymentMethod();
                        }
                    }
                });

    }


    private void addAndroidPayPaymentMethod() {
        List<LineItem> lineItems = getLineItems(amountToMakePayment);
        createWalletFragment(lineItems, amountToMakePayment);
    }


    private void showOrHideProgressDialog(boolean show) {
        if (show) {
            paymentMethodFragmentProgressBar.setVisibility(View.VISIBLE);
        } else {
            paymentMethodFragmentProgressBar.setVisibility(View.GONE);
        }

    }


    private void createWalletFragment(List<LineItem> lineItems, Double amount) {
        SupportWalletFragment walletFragment = SupportWalletFragment.newInstance(walletFragmentOptions);

        // Now initialize the Wallet Fragment
        MaskedWalletRequest maskedWalletRequest = createMaskedWalletRequest(lineItems, amount);

        String accountName = getString(R.string.account_name);

        WalletFragmentInitParams.Builder startParamsBuilder = WalletFragmentInitParams.newBuilder()
                .setMaskedWalletRequest(maskedWalletRequest)
                .setMaskedWalletRequestCode(PaymentConstants.REQUEST_CODE_MASKED_WALLET)
                .setAccountName(accountName);
        walletFragment.initialize(startParamsBuilder.build());

        // add Wallet fragment to the UI
        findViewById(R.id.dynamic_wallet_button_fragment).setVisibility(View.VISIBLE);
        getFragmentManager().beginTransaction()
                .replace(R.id.dynamic_wallet_button_fragment, walletFragment)
                .commit();
    }


    /**
     * Create the Masked Wallet request. Note that the Tokenization Type is set to
     * {@code NETWORK_TOKEN} and the {@code publicKey} parameter is set to the public key
     * that was created by First Data.
     *
     * @param amount The amount the user entered
     * @return A Masked Wallet request object
     */
    private MaskedWalletRequest createMaskedWalletRequest(List<LineItem> lineItems, Double amount) {
        MaskedWalletRequest.Builder builder = MaskedWalletRequest.newBuilder()
                .setMerchantName(PaymentConstants.MERCHANT_NAME)
                .setPhoneNumberRequired(true)
                .setShippingAddressRequired(true)
                .setCurrencyCode(PaymentConstants.CURRENCY_CODE_USD)
                .setEstimatedTotalPrice(amount.toString())
//                 Create a Cart with the current line items. Provide all the information
//                 available up to this point with estimates for shipping and tax included.
                .setCart(Cart.newBuilder()
                        .setCurrencyCode(PaymentConstants.CURRENCY_CODE_USD)
                        .setTotalPrice(amount.toString())
                        .setLineItems(lineItems)
                        .build());

        //  Set tokenization type and First Data issued public key
        PaymentMethodTokenizationParameters tokenizationParameters = PaymentMethodTokenizationParameters.newBuilder()
                .setPaymentMethodTokenizationType(PaymentMethodTokenizationType.NETWORK_TOKEN)
                .addParameter("publicKey", getAndroidPublicKey())
                .build();
        builder.setPaymentMethodTokenizationParameters(tokenizationParameters);
        return builder.build();
    }


    private String getAndroidPublicKey() {
        for (MerchantServicesDTO merchantServices : paymentsModel.getPaymentPayload().getMerchantServices()) {
            if (merchantServices.getCode().equals(PaymentConstants.ANDROID_PAY_MERCHANT_SERVICE)) {
                return merchantServices.getMetadata().getAndroidPublicKey();
            }
        }
        return "";
    }


    private List<LineItem> getLineItems(Double amount) {
        List<LineItem> list = new ArrayList<LineItem>();

        list.add(LineItem.newBuilder()
                .setCurrencyCode(PaymentConstants.CURRENCY_CODE_USD)
                .setDescription(Label.getLabel("payment_patient_payment_description"))
                .setQuantity("1")
                .setTotalPrice(amount.toString())
                .setUnitPrice(amount.toString())
                .build());


        return list;
    }

    private static WalletFragmentStyle walletFragmentStyle = new WalletFragmentStyle()
            .setBuyButtonHeight(WalletFragmentStyle.Dimension.UNIT_DIP, CarePayConstants.ANDROID_PAY_BUTTON_HEIGHT)
            .setBuyButtonWidth(WalletFragmentStyle.Dimension.MATCH_PARENT)
            .setBuyButtonText(WalletFragmentStyle.BuyButtonText.LOGO_ONLY)
            .setBuyButtonAppearance(WalletFragmentStyle.BuyButtonAppearance.ANDROID_PAY_LIGHT_WITH_BORDER)
            .setMaskedWalletDetailsBackgroundColor(R.color.android_pay_background_color);

    private static WalletFragmentOptions walletFragmentOptions = WalletFragmentOptions.newBuilder()
            .setEnvironment(PaymentConstants.WALLET_ENVIRONMENT)
            .setFragmentStyle(walletFragmentStyle)
            .setTheme(WalletConstants.THEME_DARK)
            .setMode(WalletFragmentMode.BUY_BUTTON)
            .build();


}
