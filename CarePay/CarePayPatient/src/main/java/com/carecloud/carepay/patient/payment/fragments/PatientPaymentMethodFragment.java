package com.carecloud.carepay.patient.payment.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.patient.payment.PaymentResponsibilityModel;
import com.carecloud.carepay.patient.payment.androidpay.EnvData;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.payments.adapter.PaymentMethodAdapter;
import com.carecloud.carepaylibray.payments.fragments.PaymentMethodFragment;
import com.carecloud.carepaylibray.payments.models.PaymentPatientBalancesPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
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
    private Boolean isProgressBarVisible = false;
    private SupportWalletFragment walletFragment;

    private ScrollView scrollviewChoices;

    private List lineItems;
    private boolean isAndroidPayReady;

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        paymentTypeMap.put(CarePayConstants.TYPE_ANDROID_PAY, R.drawable.payment_android_button_selector);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_method, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        super.onViewCreated(view, icicle);
        paymentMethodFragmentProgressBar = (ProgressBar) view.findViewById(R.id.paymentMethodFragmentProgressBar);
         scrollviewChoices = (ScrollView) view.findViewById(R.id.scrollview_choices);

        if (googleApiClient == null) {
            setGoogleApiClient();
        }
        isAndroidPayReadyToUse();

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
        showOrHideProgressDialog();
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
                        showOrHideProgressDialog();
                        if (booleanResult.getStatus().isSuccess()) {
                            if (booleanResult.getValue()) {
                                showOrHideProgressDialog();
                                isAndroidPayReady = true;
                                addAndroidPayPaymentMethod();
                            } else {

                                showOrHideProgressDialog();
                            }
                        } else {
                            // Error making isReadyToPay call
                            Log.e(TAG, "isReadyToPay:" + booleanResult.getStatus().getStatusMessage());
                            showOrHideProgressDialog();
                        }
                    }
                });

        addAndroidPayPaymentMethod();
    }


    private void addAndroidPayPaymentMethod() {
        if (isAndroidPayReady) {
            PaymentsMethodsDTO androidPayPaymentMethod = new PaymentsMethodsDTO();
            androidPayPaymentMethod.setLabel(PaymentConstants.ANDROID_PAY);
            androidPayPaymentMethod.setType(CarePayConstants.TYPE_ANDROID_PAY);
            paymentMethodsList.add(androidPayPaymentMethod);
//            addPaymentMethodOptionView(paymentMethodsList.size() - 1);

            if(getPaymentMethodList()!=null){//listview already init
                PaymentMethodAdapter adapter = (PaymentMethodAdapter) getPaymentMethodList().getAdapter();
                adapter.setPaymentMethodsList(paymentMethodsList);
                adapter.notifyDataSetChanged();

            }
        }

    }


    private void showOrHideProgressDialog() {

        if (isProgressBarVisible) {
            paymentMethodFragmentProgressBar.setVisibility(View.GONE);
            isProgressBarVisible = false;
        } else {
            paymentMethodFragmentProgressBar.setVisibility(View.VISIBLE);
            isProgressBarVisible = true;
        }

    }


    @Override
    protected void handlePaymentButton(PaymentsMethodsDTO paymentMethod, double amount){
        if(paymentMethod.getType() == CarePayConstants.TYPE_ANDROID_PAY){
            setLineItems(paymentList.get(0).getPayload());
//            createAndAddWalletFragment(paymentsModel.getPaymentPayload().getPatientBalances().get(0).getPendingRepsonsibility());// getPayload().get(0).getTotal());
            createAndAddWalletFragment(String.valueOf(amount));
        }else{
            super.handlePaymentButton(paymentMethod, amount);
        }
    }


    /**
     * Create the wallet fragment. This will create the "Buy with Android Pay" button.
     *
     * @param totalPrice Amount received from the user
     */
    private void createAndAddWalletFragment(String totalPrice) {
        WalletFragmentStyle walletFragmentStyle = new WalletFragmentStyle()
                .setBuyButtonHeight(WalletFragmentStyle.Dimension.UNIT_DIP, CarePayConstants.ANDROID_PAY_BUTTON_HEIGHT)
                .setBuyButtonWidth(WalletFragmentStyle.Dimension.MATCH_PARENT)
                .setBuyButtonText(WalletFragmentStyle.BuyButtonText.LOGO_ONLY)
                .setBuyButtonAppearance(WalletFragmentStyle.BuyButtonAppearance.ANDROID_PAY_LIGHT_WITH_BORDER)
                .setMaskedWalletDetailsBackgroundColor(R.color.android_pay_background_color);

        WalletFragmentOptions walletFragmentOptions = WalletFragmentOptions.newBuilder()
                .setEnvironment(PaymentConstants.WALLET_ENVIRONMENT)
                .setFragmentStyle(walletFragmentStyle)
                .setTheme(WalletConstants.THEME_DARK)
                .setMode(WalletFragmentMode.BUY_BUTTON)
                .build();
        walletFragment = SupportWalletFragment.newInstance(walletFragmentOptions);

        // Now initialize the Wallet Fragment
        MaskedWalletRequest maskedWalletRequest = createMaskedWalletRequest(totalPrice);

        String accountName = getString(com.carecloud.carepay.patient.R.string.account_name);

        WalletFragmentInitParams.Builder startParamsBuilder = WalletFragmentInitParams.newBuilder()
                .setMaskedWalletRequest(maskedWalletRequest)
                .setMaskedWalletRequestCode(PaymentConstants.REQUEST_CODE_MASKED_WALLET)
                .setAccountName(accountName);
        walletFragment.initialize(startParamsBuilder.build());

        // add Wallet fragment to the UI
        getFragmentManager().beginTransaction()
                .replace(com.carecloud.carepay.patient.R.id.dynamic_wallet_button_fragment, walletFragment)
                .commit();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollviewChoices.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 300);

    }


    /**
     * Create the Masked Wallet request. Note that the Tokenization Type is set to
     * {@code NETWORK_TOKEN} and the {@code publicKey} parameter is set to the public key
     * that was created by First Data.
     *
     * @param totalPrice The amount the user entered
     * @return A Masked Wallet request object
     */
    private MaskedWalletRequest createMaskedWalletRequest(String totalPrice) {
        MaskedWalletRequest.Builder builder = MaskedWalletRequest.newBuilder()
                .setMerchantName(PaymentConstants.MERCHANT_NAME)
                .setPhoneNumberRequired(true)
                .setShippingAddressRequired(true)
                .setCurrencyCode(PaymentConstants.CURRENCY_CODE_USD)
                .setEstimatedTotalPrice(totalPrice)
//                 Create a Cart with the current line items. Provide all the information
//                 available up to this point with estimates for shipping and tax included.
                .setCart(Cart.newBuilder()
                        .setCurrencyCode(PaymentConstants.CURRENCY_CODE_USD)
                        .setTotalPrice(totalPrice)
                        .setLineItems(getLineItems())
                        .build());

        //  Set tokenization type and First Data issued public key
        PaymentMethodTokenizationParameters tokenizationParameters = PaymentMethodTokenizationParameters.newBuilder()
                .setPaymentMethodTokenizationType(PaymentMethodTokenizationType.NETWORK_TOKEN)
                .addParameter("publicKey", EnvData.getProperties("CERT").getPublicKey())
                .build();
        builder.setPaymentMethodTokenizationParameters(tokenizationParameters);
        return builder.build();
    }

    /**
     * Create a fake line item list. Set the amount to the one received from the user.
     *
     * @return List of line items
     */
    public List getLineItems() {
        return lineItems;
    }

    private void setLineItems(List<PaymentPatientBalancesPayloadDTO> balances) {
        List<LineItem> list = new ArrayList<LineItem>();

        PaymentResponsibilityModel paymentModel = PaymentResponsibilityModel.getInstance();
        paymentModel.balancesList = new ArrayList();

        for (PaymentPatientBalancesPayloadDTO balance : balances) {
            list.add(LineItem.newBuilder()
                    .setCurrencyCode(PaymentConstants.CURRENCY_CODE_USD)
                    .setDescription(balance.getBalanceType())
                    .setQuantity("1")
                    .setTotalPrice(balance.getTotal())
                    .setUnitPrice(balance.getTotal())
                    .build());
            paymentModel.balance1 = balance.getTotal();
            paymentModel.balancesList.add(paymentModel.balance1);


        }

        this.lineItems = list;
    }

    private void removeWalletFragment() {
        getFragmentManager().beginTransaction()
                .remove(walletFragment)
                .commit();

        walletFragment = null;
    }
}
