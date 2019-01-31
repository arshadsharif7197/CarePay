package com.carecloud.carepay.patient.payment.androidpay;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.CardRequirements;
import com.google.android.gms.wallet.Cart;
import com.google.android.gms.wallet.FullWallet;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.LineItem;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.android.gms.wallet.PaymentMethodTokenizationType;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.TransactionInfo;
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

    private static PaymentsClient paymentsClient;

    /**
     * Constructor
     * @param activity activity
     * @param merchantServicesList merchant services list
     * @param fragmentManager frament manager
     */
    public AndroidPayAdapter(FragmentActivity activity, @NonNull List<MerchantServicesDTO> merchantServicesList, FragmentManager fragmentManager){
        this.activity = activity;
        this.fragMan = fragmentManager;
        this.merchantServicesList = merchantServicesList;
    }


    /**
     * Constructor
     * @param activity activity
     * @param merchantServicesList merchant services list
     * @param merchantServicesList
     */
    public AndroidPayAdapter(FragmentActivity activity, @NonNull List<MerchantServicesDTO> merchantServicesList){
        this.activity = activity;
        this.fragMan = activity.getSupportFragmentManager();
        this.merchantServicesList = merchantServicesList;
    }


    /**
     * disconnect google Api client
     */
    public void disconnectClient(){
        disconnectGoogleAPI();
    }

    /**
     * Check if android pay is avaiable and init android pay
     * @param callback callback
     */
    public void initAndroidPay(final AndroidPayReadyCallback callback){
        IsReadyToPayRequest req = IsReadyToPayRequest.newBuilder()
                .addAllowedCardNetwork(WalletConstants.CardNetwork.MASTERCARD)
                .addAllowedCardNetwork(WalletConstants.CardNetwork.VISA)
                .addAllowedCardNetwork(WalletConstants.CardNetwork.AMEX)
                .addAllowedCardNetwork(WalletConstants.CardNetwork.DISCOVER)
                .build();
        setGoogleApiClient();

        Task<Boolean> task = paymentsClient.isReadyToPay(req);
        task.addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                try {
                    boolean result = task.getResult(ApiException.class);
                    if(result){
                        callback.onAndroidPayReady();
                    }
                }catch (ApiException apx){
                    apx.printStackTrace();
                }
            }
        });
    }

    //region GoogleClient
    private void setGoogleApiClient() {
        if(paymentsClient == null){
            paymentsClient = Wallet.getPaymentsClient(activity,
                    new Wallet.WalletOptions.Builder()
                            .setEnvironment(PaymentConstants.WALLET_ENVIRONMENT)
                            .build());
        }
    }

    private void disconnectGoogleAPI() {
        if(paymentsClient != null){
            paymentsClient = null;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed:" + connectionResult.getErrorMessage());
        new CustomMessageToast(activity, connectionResult.getErrorMessage(), CustomMessageToast.NOTIFICATION_TYPE_ERROR).show();
    }
    //endregion

    //region WalletViews

    /**
     * Creates a Eallet Button fragment
     * @param amount amount to make payment
     * @param container container to insert fragment
     */
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


    /**
     * Creates a Wallet Details Fragment
     * @param maskedWallet  maskedWallet
     * @param container  container to insert fragment
     */
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

    /**
     * Send a full wallet request for payment
     * @param maskedWallet masked wallet
     * @param amount amount of payment
     */
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

    //region Payment Request
    private TransactionInfo getTransactionInfo(double amount){
        return TransactionInfo.newBuilder()
                .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                .setTotalPrice(String.valueOf(amount))
                .setCurrencyCode(PaymentConstants.CURRENCY_CODE_USD)
                .build();
    }

    private CardRequirements getCardRequirements(){
        return CardRequirements.newBuilder()
                .addAllowedCardNetwork(WalletConstants.CARD_NETWORK_AMEX)
                .addAllowedCardNetwork(WalletConstants.CARD_NETWORK_DISCOVER)
                .addAllowedCardNetwork(WalletConstants.CARD_NETWORK_MASTERCARD)
                .addAllowedCardNetwork(WalletConstants.CARD_NETWORK_VISA)
                .build();
    }

    @Deprecated
    private PaymentMethodTokenizationParameters getTokenizationParameters(){
        return PaymentMethodTokenizationParameters.newBuilder()
                .setPaymentMethodTokenizationType(WalletConstants.PAYMENT_METHOD_TOKENIZATION_TYPE_DIRECT)
                .addParameter("publicKey", getAndroidPublicKey())
                .build();
    }

    private PaymentMethodTokenizationParameters getTokenizationParameters(PapiAccountsDTO papiAccount){
        return PaymentMethodTokenizationParameters.newBuilder()
                .setPaymentMethodTokenizationType(WalletConstants.PAYMENT_METHOD_TOKENIZATION_TYPE_PAYMENT_GATEWAY)
                .addParameter("gateway", PaymentConstants.MERCHANT_GATEWAY)
                .addParameter("gatewayMerchantId", papiAccount.getDefaultBankAccountMid())
                .build();
    }

    @Deprecated
    private PaymentDataRequest createPaymentDataRequest(double amount){
        return PaymentDataRequest.newBuilder()
                .setTransactionInfo(getTransactionInfo(amount))
                .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD)
                .setCardRequirements(getCardRequirements())
                .setPaymentMethodTokenizationParameters(getTokenizationParameters())
                .build();
    }

    private PaymentDataRequest createPaymentDataRequest(double amount, PapiAccountsDTO papiAccount){
        return PaymentDataRequest.newBuilder()
                .setTransactionInfo(getTransactionInfo(amount))
                .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_TOKENIZATION_TYPE_PAYMENT_GATEWAY)
                .setCardRequirements(getCardRequirements())
                .setPaymentMethodTokenizationParameters(getTokenizationParameters(papiAccount))
                .build();
    }

    /**
     * Initialize Google Payment Request
     * @param amount amount to pay
     */
    @Deprecated
    public void createAndroidPayRequest(double amount){
        setGoogleApiClient();
        PaymentDataRequest paymentDataRequest = createPaymentDataRequest(amount);
        if(paymentDataRequest != null){
            AutoResolveHelper.resolveTask(paymentsClient.loadPaymentData(paymentDataRequest), activity, PaymentConstants.REQUEST_CODE_GOOGLE_PAYMENT);
        }
    }

    /**
     * Initialize Google Payment Request
     * @param amount amount to pay
     */
    public void createAndroidPayRequest(double amount, PapiAccountsDTO papiAccount){
        setGoogleApiClient();
        PaymentDataRequest paymentDataRequest = createPaymentDataRequest(amount, papiAccount);
        if(paymentDataRequest != null){
            AutoResolveHelper.resolveTask(paymentsClient.loadPaymentData(paymentDataRequest), activity, PaymentConstants.REQUEST_CODE_GOOGLE_PAYMENT);
        }
    }


    /**
     * Handle Google Payment result
     * @param data intent data for successful result
     * @param papiAccountsDTO papiAccountsDTO
     * @param paymentAmount payment Amount
     * @param callback callback for processing results
     */
    public void handleGooglePaymentData(Intent data, PapiAccountsDTO papiAccountsDTO, Double paymentAmount, @NonNull AndroidPayProcessingCallback callback){
        PaymentData paymentData = PaymentData.getFromIntent(data);

        if(paymentData != null) {
            String token = paymentData.getPaymentMethodToken().getToken();
            processPaymentWithPayeezy(token, papiAccountsDTO, paymentAmount, callback);
        }
    }

    //endregion

    //region Processing

    /**
     * Complete android pay request and process payment
     * @param fullWallet full wallet
     * @param papiAccountsDTO papi account for payment
     * @param paymentAmount amount of payment
     * @param callback callback
     */
    @Deprecated
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
            callback.onAndroidPayFailed("An unknown error has occurred");
        }
    }

    private void processPaymentWithPayeezy(String tokenJSON, PapiAccountsDTO papiAccountsDTO, Double paymentAmount, @NonNull AndroidPayProcessingCallback callback){
        try {
            JSONObject jsonTokenData = new JSONObject(tokenJSON);

            //  Create a First Data Json request
            MerchantServicesDTO payeezyMerchantService = getAndroidPayMerchantService();
            if (payeezyMerchantService == null) {
                callback.onAndroidPayFailed("No Merchant Service Available");
                return;
            }
            JSONObject requestPayload = getGooglePaymentsPayload(jsonTokenData, paymentAmount);
            final String payloadString = requestPayload.toString();

            if (papiAccountsDTO == null) {
                callback.onAndroidPayFailed("No Account Available");
                return;
            }
            final Map<String, String> HMACMap = computeHMAC(payloadString, payeezyMerchantService, papiAccountsDTO);
            if (HMACMap.isEmpty()) {
                callback.onAndroidPayFailed("An unknown error has occurred");
                return;
            }

            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), payloadString);
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
        }catch (JSONException jsx){
            jsx.printStackTrace();
            callback.onAndroidPayFailed("An unknown error has occurred");
        }

    }

    @Deprecated
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

    private JSONObject getGooglePaymentsPayload(JSONObject jsonToken, Double paymentAmount) throws JSONException{
        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("currency_code", PaymentConstants.CURRENCY_CODE_USD);
        payloadMap.put("amount", String.valueOf(Math.round(paymentAmount * 100)));
        payloadMap.put("merchant_ref", "orderid");//maybe we need actual order ID or a unique value??
        payloadMap.put("transaction_type", "purchase");
        payloadMap.put("method", "3DS");

        Map<String, Object> map3DS = new HashMap<>();
        map3DS.put("signature", jsonToken.getString("signature"));
        map3DS.put("version", jsonToken.getString("protocolVersion"));
        map3DS.put("data", jsonToken.getString("signedMessage"));
        map3DS.put("type", "G");

        payloadMap.put("3DS", map3DS);

        return new JSONObject(payloadMap);
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
                headerMap.put("content-type", "Application/json");
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
