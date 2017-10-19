package com.carecloud.carepay.patient.payment.androidpay;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.RestCallServiceCallback;
import com.carecloud.carepay.service.library.RestCallServiceHelper;
import com.carecloud.carepay.service.library.RestDef;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.customcomponents.CustomMessageToast;
import com.carecloud.carepaylibray.payments.models.MerchantServicesDTO;
import com.carecloud.carepaylibray.payments.models.PapiAccountsDTO;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wallet.Cart;
import com.google.android.gms.wallet.FullWallet;
import com.google.android.gms.wallet.FullWalletRequest;
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
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.RequestBody;

/**
 * Created by lmenendez on 10/17/17
 */

public class AndroidPayAdapter implements GoogleApiClient.OnConnectionFailedListener {

    public interface AndroidPayReadyCallback {
        void onAndroidPayReady();

    }

    public interface AndroidPayProcessingCallback{
        void onAndroidPayFailed(String message);

        void onAndroidPaySuccess(JsonElement jsonElement);

        ISession getSession();
    }

    private static final String TAG = AndroidPayAdapter.class.getName();

    private FragmentActivity activity;
    private FragmentManager fragMan;
    private List<MerchantServicesDTO> merchantServicesList = new ArrayList<>();

    private static GoogleApiClient googleApiClient;

    public AndroidPayAdapter(FragmentActivity activity, @NonNull List<MerchantServicesDTO> merchantServicesList, FragmentManager fragmentManager){
        this.activity = activity;
        this.fragMan = fragmentManager;
        this.merchantServicesList = merchantServicesList;
    }


    public AndroidPayAdapter(FragmentActivity activity, @NonNull List<MerchantServicesDTO> merchantServicesList){
        this.activity = activity;
        this.fragMan = activity.getSupportFragmentManager();
        this.merchantServicesList = merchantServicesList;
    }


    public void disconnectClient(){
        disconnectGoogleAPI();
    }

    public void initAndroidPay(final AndroidPayReadyCallback callback){
        IsReadyToPayRequest req = IsReadyToPayRequest.newBuilder()
                .addAllowedCardNetwork(WalletConstants.CardNetwork.MASTERCARD)
                .addAllowedCardNetwork(WalletConstants.CardNetwork.VISA)
                .addAllowedCardNetwork(WalletConstants.CardNetwork.AMEX)
                .addAllowedCardNetwork(WalletConstants.CardNetwork.DISCOVER)
                .build();
        setGoogleApiClient();
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
                            .setTheme(WalletConstants.THEME_LIGHT)
                            .build())
                    .enableAutoManage(activity, this)
                    .build();
        }
    }

    private void disconnectGoogleAPI() {
        if(googleApiClient != null) {
            googleApiClient.stopAutoManage(activity);
            googleApiClient.disconnect();
            googleApiClient = null;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed:" + connectionResult.getErrorMessage());
        new CustomMessageToast(activity, connectionResult.getErrorMessage(), CustomMessageToast.NOTIFICATION_TYPE_ERROR).show();
    }
    //endregion

    //region WalletViews
    public void createWalletButton(Double amount, ViewGroup container) {
        SupportWalletFragment walletFragment = SupportWalletFragment.newInstance(walletButtonOptions);

        // Now initialize the Wallet Fragment
        MaskedWalletRequest maskedWalletRequest = createMaskedWalletRequest(getLineItems(amount), amount);

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

    public void createFullWallet(MaskedWallet maskedWallet, Double amount){
        setGoogleApiClient();
        Wallet.Payments.loadFullWallet(googleApiClient,
                createFullWalletRequest(maskedWallet, amount),
                PaymentConstants.REQUEST_CODE_FULL_WALLET);

    }

    private FullWalletRequest createFullWalletRequest(MaskedWallet maskedWallet, Double amount){
        FullWalletRequest.Builder builder = FullWalletRequest.newBuilder()
                .setGoogleTransactionId(maskedWallet.getGoogleTransactionId())
                .setCart(Cart.newBuilder()
                        .setCurrencyCode(PaymentConstants.CURRENCY_CODE_USD)
                        .setTotalPrice(amount.toString())
                        .setLineItems(getLineItems(amount))
                        .build());

        return builder.build();
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
        MerchantServicesDTO androidPayMerchantService = getAndroidPayMerchantService();
        if(androidPayMerchantService != null){
            return androidPayMerchantService.getMetadata().getAndroidPublicKey();
        }
        return "";
    }

    private MerchantServicesDTO getAndroidPayMerchantService(){
        for (MerchantServicesDTO merchantServices : merchantServicesList) {
            if (merchantServices.getCode().equals(PaymentConstants.ANDROID_PAY_MERCHANT_SERVICE)) {
                return merchantServices;
            }
        }
        return null;
    }

    private List<LineItem> getLineItems(Double amount) {
        List<LineItem> list = new ArrayList<>();

        list.add(LineItem.newBuilder()
                .setCurrencyCode(PaymentConstants.CURRENCY_CODE_USD)
                .setDescription(Label.getLabel("payment_patient_payment_description"))
                .setQuantity("1")
                .setTotalPrice(amount.toString())
                .setUnitPrice(amount.toString())
                .build());


        return list;
    }


    //endregion

    //region ViewHelpers
    private static WalletFragmentStyle walletButtonStyle = new WalletFragmentStyle()
            .setBuyButtonHeight(WalletFragmentStyle.Dimension.UNIT_DIP, CarePayConstants.ANDROID_PAY_BUTTON_HEIGHT)
            .setBuyButtonWidth(WalletFragmentStyle.Dimension.MATCH_PARENT)
            .setBuyButtonText(WalletFragmentStyle.BuyButtonText.LOGO_ONLY)
            .setBuyButtonAppearance(WalletFragmentStyle.BuyButtonAppearance.ANDROID_PAY_LIGHT_WITH_BORDER)
            .setMaskedWalletDetailsBackgroundColor(R.color.android_pay_background_color);

    private static WalletFragmentOptions walletButtonOptions = WalletFragmentOptions.newBuilder()
            .setEnvironment(PaymentConstants.WALLET_ENVIRONMENT)
            .setFragmentStyle(walletButtonStyle)
            .setTheme(WalletConstants.THEME_LIGHT)
            .setMode(WalletFragmentMode.BUY_BUTTON)
            .build();

    private static WalletFragmentStyle walletDetailStyle = new WalletFragmentStyle()
            .setMaskedWalletDetailsTextAppearance(R.style.WalletFragmentDetailsTextAppearance)
            .setMaskedWalletDetailsHeaderTextAppearance(R.style.WalletFragmentDetailsHeaderTextAppearance)
            .setMaskedWalletDetailsButtonTextAppearance(R.style.WalletFragmentDetailsButtonTextAppearance)
            .setMaskedWalletDetailsButtonBackgroundResource(R.drawable.round_border_button_bg)
            .setMaskedWalletDetailsBackgroundResource(R.color.white);

    private static WalletFragmentOptions walletDetailOptions = WalletFragmentOptions.newBuilder()
            .setEnvironment(PaymentConstants.WALLET_ENVIRONMENT)
            .setFragmentStyle(walletDetailStyle)
            .setTheme(WalletConstants.THEME_LIGHT)
            .setMode(WalletFragmentMode.SELECTION_DETAILS)
            .build();
    //endregion

    //region Processing
    public void sendRequestToPayeezy(FullWallet fullWallet, PapiAccountsDTO papiAccountsDTO, Double paymentAmount, @NonNull AndroidPayProcessingCallback callback) {
        try {
            //  Parse the Json token retrieved from the Full Wallet.
            String tokenJSON = fullWallet.getPaymentMethodToken().getToken();
            final JSONObject jsonObject = new JSONObject(tokenJSON);

            String encryptedMessage = jsonObject.getString("encryptedMessage");
            String publicKey = jsonObject.getString("ephemeralPublicKey");
            String signature = jsonObject.getString("tag");

            //  Create a First Data Json request
            MerchantServicesDTO payeezyMerchantService = getAndroidPayMerchantService();
            if(payeezyMerchantService == null){
                callback.onAndroidPayFailed("No Merchant Service Available");
                return;
            }
            JSONObject requestPayload = getRequestPayload(encryptedMessage, signature, publicKey, payeezyMerchantService, paymentAmount);
            final String payloadString = requestPayload.toString();

            if(papiAccountsDTO == null){
                callback.onAndroidPayFailed("No Account Available");
                return;
            }
            final Map<String, String> HMACMap = computeHMAC(payloadString, payeezyMerchantService, papiAccountsDTO);
            if(HMACMap.isEmpty()){
                callback.onAndroidPayFailed("An unknown error has occurred");
                return;
            }

            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),payloadString);
            RestCallServiceHelper restCallServiceHelper = new RestCallServiceHelper(callback.getSession().getAppAuthorizationHelper(), callback.getSession().getApplicationMode());
            restCallServiceHelper.executeRequest(RestDef.POST,
                    payeezyMerchantService.getMetadata().getBaseUrl(),
                    getPayeezyServiceCallback(callback),
                    false,
                    false,
                    null,
                    null,
                    HMACMap,
                    body,
                    "v1/transactions");

        } catch (JSONException e) {
            Toast.makeText(activity, "Error parsing JSON payload", Toast.LENGTH_LONG).show();
        }
    }

    private JSONObject getRequestPayload(String data, String signature, String ephemeralPublicKey, MerchantServicesDTO payeezyMerchantService, Double paymentAmount) {
        Map<String, Object> pm = new HashMap<>();
        pm.put("merchant_ref", "orderid");
        pm.put("transaction_type", "purchase");
        pm.put("method", "3DS");
        pm.put("amount", String.valueOf(Math.round(paymentAmount * 100)));
        pm.put("currency_code", "USD");

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("ephemeralPublicKey", ephemeralPublicKey);
        //  First data issued Public Key Hash identifies the public key used to encrypt the data
        headerMap.put("publicKeyHash", payeezyMerchantService.getMetadata().getAndroidPublicKeyHash());

        Map<String, Object> ccmap = new HashMap<>();
        ccmap.put("type", "G");             //  Identify the request as Android Pay request
        ccmap.put("data", data);
        ccmap.put("signature", signature);
        ccmap.put("header", headerMap);

        pm.put("3DS", ccmap);
        return new JSONObject(pm);
    }


    private static Map<String, String> computeHMAC(String payload, MerchantServicesDTO payeezyMerchantService, PapiAccountsDTO papiAccountsDTO) {
        String apiSecret = payeezyMerchantService.getMetadata().getApiSecret();
        String apiKey = payeezyMerchantService.getMetadata().getApiKey();
        String token = papiAccountsDTO.getBankAccount().getToken();

        Map<String, String> headerMap = new HashMap<>();
        if (apiSecret != null) {
            try {
                String authorizeString;
                String nonce = Long.toString(Math.abs(SecureRandom.getInstance("SHA1PRNG").nextLong()));
                String timestamp = Long.toString(System.currentTimeMillis());

                Mac mac = Mac.getInstance("HmacSHA256");
                SecretKeySpec secretKey = new SecretKeySpec(apiSecret.getBytes(), "HmacSHA256");
                mac.init(secretKey);

                StringBuilder buffer = new StringBuilder()
                        .append(apiKey)
                        .append(nonce)
                        .append(timestamp)
                        .append(token)
                        .append(payload);

                byte[] macHash = mac.doFinal(buffer.toString().getBytes("UTF-8"));
                authorizeString = Base64.encodeToString(bytesToHex(macHash).getBytes(), Base64.NO_WRAP);

                headerMap.put("nonce", nonce);
                headerMap.put("timestamp", timestamp);
                headerMap.put("Authorization", authorizeString);
                headerMap.put("token", token);
                headerMap.put("apikey", apiKey);
            } catch (Exception e) {
                //  Nothing to do
            }
        }
        return headerMap;
    }


    private static String bytesToHex(byte[] byteArray) {
        StringBuilder sb = new StringBuilder(byteArray.length * 2);
        for (byte b : byteArray) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }


    private RestCallServiceCallback getPayeezyServiceCallback(final AndroidPayProcessingCallback callback) {
        return new RestCallServiceCallback() {
            @Override
            public void onPreExecute() {
                callback.getSession().showProgressDialog();
            }

            @Override
            public void onPostExecute(JsonElement jsonElement) {
                callback.getSession().hideProgressDialog();
                callback.onAndroidPaySuccess(jsonElement);
            }

            @Override
            public void onFailure(String errorMessage) {
                callback.getSession().hideProgressDialog();
                callback.onAndroidPayFailed(errorMessage);
            }

        };
    }

    //endregion

}
