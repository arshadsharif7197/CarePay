package com.carecloud.carepay.patient.payment.androidpay;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.customcomponents.CustomMessageToast;
import com.carecloud.carepaylibray.demographicsettings.models.MerchantServicesDTO;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wallet.Cart;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.LineItem;
import com.google.android.gms.wallet.MaskedWallet;
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
 * Created by lmenendez on 10/17/17
 */

public class AndroidPayAdapter implements GoogleApiClient.OnConnectionFailedListener {

    public interface AndroidPayCallback{
        List<LineItem> getLineItems();

        void onAndroidPayReady();
    }

    private static final String TAG = AndroidPayAdapter.class.getName();

    private FragmentActivity activity;
    private FragmentManager fragMan;
    private List<MerchantServicesDTO> merchantServicesList = new ArrayList<>();
    private AndroidPayCallback callback;

    private static GoogleApiClient googleApiClient;

    public AndroidPayAdapter(FragmentActivity activity, @NonNull List<MerchantServicesDTO> merchantServicesList, FragmentManager fragmentManager){
        this.activity = activity;
        this.fragMan = fragmentManager;
        this.merchantServicesList = merchantServicesList;
        setGoogleApiClient();
    }


    public AndroidPayAdapter(FragmentActivity activity, @NonNull List<MerchantServicesDTO> merchantServicesList, AndroidPayCallback callback){
        this.activity = activity;
        this.fragMan = activity.getSupportFragmentManager();
        this.merchantServicesList = merchantServicesList;
        this.callback = callback;
        setGoogleApiClient();
    }


    public void disconnectClient(){
        disconnectGoogleAPI();
    }

    public void initAndroidPay(){
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
                        if (booleanResult.getStatus().isSuccess() && booleanResult.getValue()) {
                            if(callback != null) {
                                callback.onAndroidPayReady();
                            }
                        }
                    }
                });
    }

    //region GoogleClient
    private void setGoogleApiClient() {
        if(googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(activity)
                    .addApi(Wallet.API, new Wallet.WalletOptions.Builder()
                            .setEnvironment(PaymentConstants.WALLET_ENVIRONMENT)
                            .build())
                    .enableAutoManage(activity, this)
                    .build();
        }
    }

    private void disconnectGoogleAPI() {
        googleApiClient.stopAutoManage(activity);
        googleApiClient.disconnect();
        googleApiClient = null;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed:" + connectionResult.getErrorMessage());
        new CustomMessageToast(activity, connectionResult.getErrorMessage(), CustomMessageToast.NOTIFICATION_TYPE_ERROR).show();
    }
    //endregion

    //region WalletButton
    public void createWalletButton(Double amount, ViewGroup container) {
        if(callback == null){
            return;
        }
        SupportWalletFragment walletFragment = SupportWalletFragment.newInstance(walletButtonOptions);

        // Now initialize the Wallet Fragment
        MaskedWalletRequest maskedWalletRequest = createMaskedWalletRequest(callback.getLineItems(), amount);

//        String accountName = activity.getString(R.string.account_name);

        WalletFragmentInitParams.Builder startParamsBuilder = WalletFragmentInitParams.newBuilder()
                .setMaskedWalletRequest(maskedWalletRequest)
                .setMaskedWalletRequestCode(PaymentConstants.REQUEST_CODE_MASKED_WALLET);
//                .setAccountName(accountName);
        walletFragment.initialize(startParamsBuilder.build());

        // add Wallet fragment to the UI
        container.setVisibility(View.VISIBLE);
        fragMan.beginTransaction()
                .replace(container.getId(), walletFragment)
                .commit();
    }



    public void createWalletDetails(MaskedWallet maskedWallet, ViewGroup container){
        SupportWalletFragment walletFragment = SupportWalletFragment.newInstance(walletDetailOptions);

        // Now initialize the Wallet Fragment
//        String accountName = getString(R.string.account_name);
        WalletFragmentInitParams.Builder startParamsBuilder = WalletFragmentInitParams.newBuilder()
                .setMaskedWallet(maskedWallet)
                .setMaskedWalletRequestCode(PaymentConstants.REQUEST_CODE_CHANGE_MASKED_WALLET);
//                .setAccountName(accountName);
        walletFragment.initialize(startParamsBuilder.build());

        fragMan.beginTransaction()
                .replace(container.getId(), walletFragment)
                .commit();
    }




    private MaskedWalletRequest createMaskedWalletRequest(List<LineItem> lineItems, Double amount) {
        MaskedWalletRequest.Builder builder = MaskedWalletRequest.newBuilder()
                .setMerchantName(PaymentConstants.MERCHANT_NAME)
                .setPhoneNumberRequired(true)
                .setShippingAddressRequired(true)
                .setCurrencyCode(PaymentConstants.CURRENCY_CODE_USD)
                .setEstimatedTotalPrice(amount.toString())
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
        for (MerchantServicesDTO merchantServices : merchantServicesList) {
            if (merchantServices.getCode().equals(PaymentConstants.ANDROID_PAY_MERCHANT_SERVICE)) {
                return merchantServices.getMetadata().getAndroidPublicKey();
            }
        }
        return "";
    }


    //endregion


    private static WalletFragmentStyle walletButtonStyle = new WalletFragmentStyle()
            .setBuyButtonHeight(WalletFragmentStyle.Dimension.UNIT_DIP, CarePayConstants.ANDROID_PAY_BUTTON_HEIGHT)
            .setBuyButtonWidth(WalletFragmentStyle.Dimension.MATCH_PARENT)
            .setBuyButtonText(WalletFragmentStyle.BuyButtonText.LOGO_ONLY)
            .setBuyButtonAppearance(WalletFragmentStyle.BuyButtonAppearance.ANDROID_PAY_LIGHT_WITH_BORDER)
            .setMaskedWalletDetailsBackgroundColor(R.color.android_pay_background_color);

    private static WalletFragmentOptions walletButtonOptions = WalletFragmentOptions.newBuilder()
            .setEnvironment(PaymentConstants.WALLET_ENVIRONMENT)
            .setFragmentStyle(walletButtonStyle)
            .setTheme(WalletConstants.THEME_DARK)
            .setMode(WalletFragmentMode.BUY_BUTTON)
            .build();

    private static WalletFragmentStyle walletDetailStyle = new WalletFragmentStyle()
            .setMaskedWalletDetailsTextAppearance(R.style.WalletFragmentDetailsTextAppearance)
            .setMaskedWalletDetailsHeaderTextAppearance(R.style.WalletFragmentDetailsHeaderTextAppearance)
            .setMaskedWalletDetailsBackgroundColor(R.color.colorPrimary)
            .setMaskedWalletDetailsButtonTextAppearance(R.drawable.cell_checkbox_on)
            .setMaskedWalletDetailsButtonBackgroundResource(R.drawable.button_blue)
            .setMaskedWalletDetailsBackgroundResource(R.color.colorPrimary);


    private static WalletFragmentOptions walletDetailOptions = WalletFragmentOptions.newBuilder()
            .setEnvironment(PaymentConstants.WALLET_ENVIRONMENT)
            .setFragmentStyle(walletDetailStyle)
            .setTheme(WalletConstants.THEME_LIGHT)
            .setMode(WalletFragmentMode.SELECTION_DETAILS)
            .build();


}
