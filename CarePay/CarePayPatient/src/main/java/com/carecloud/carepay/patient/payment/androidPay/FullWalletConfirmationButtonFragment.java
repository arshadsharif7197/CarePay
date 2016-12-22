package com.carecloud.carepay.patient.payment.androidPay;

/**
 * Created by kkannan on 12/17/16.
 */


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.patient.payment.PaymentResponsibilityModel;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.wallet.Cart;
import com.google.android.gms.wallet.FullWallet;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.LineItem;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.PaymentMethodToken;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This is a fragment that handles the creating and sending of a {@link FullWalletRequest} using
 * {@link Wallet#loadFullWallet(GoogleApiClient, FullWalletRequest, int)}. This fragment renders
 * a button which hides the complexity of managing Google Play Services connection states,
 * creation and sending of requests and handling responses. Applications may use this fragment as
 * a drop in replacement of a confirmation button in case the user has chosen to use Google Wallet.
 */
public class FullWalletConfirmationButtonFragment extends Fragment
        implements ConnectionCallbacks, OnConnectionFailedListener {

    /**
     * Request code used when attempting to resolve issues with connecting to Google Play Services.
     * Only use this request code when calling {@link ConnectionResult#startResolutionForResult(
     *Activity, int)}.
     */
    public static final int REQUEST_CODE_RESOLVE_ERR = 1003;
    /**
     * Request code used when loading a full wallet. Only use this request code when calling
     * {@link Wallet#loadFullWallet(GoogleApiClient, FullWalletRequest, int)}.
     */
    public static final int REQUEST_CODE_RESOLVE_LOAD_FULL_WALLET = 1004;
    private static final String TAG = "FullWallet";
    // Maximum number of times to try to connect to GoogleApiClient if the connection is failing
    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_RETRY_DELAY_MILLISECONDS = 3000;
    private static final int MESSAGE_RETRY_CONNECTION = 1010;
    private static final String KEY_RETRY_COUNTER = "KEY_RETRY_COUNTER";
    private static final String KEY_HANDLE_FULL_WALLET_WHEN_READY = "KEY_HANDLE_FULL_WALLET_WHEN_READY";

    // No. of times to retry loadFullWallet on receiving a ConnectionResult.INTERNAL_ERROR
    private static final int MAX_FULL_WALLET_RETRIES = 1;
    private static final String KEY_RETRY_FULL_WALLET_COUNTER = "KEY_RETRY_FULL_WALLET_COUNTER";
    protected GoogleApiClient mGoogleApiClient;
    protected ProgressDialog progressDialog;
    // whether the user tried to do an action that requires a full wallet (i.e.: loadFullWallet)
    // before a full wallet was acquired (i.e.: still waiting for mGoogleApiClient to connect)
    protected boolean handleFullWalletWhenReady = false;
    protected int itemId;
    // Cached connection result for resolving connection failures on user action.
    protected ConnectionResult connectionResult;
    private int mRetryCounter = 0;
    // handler for processing retry attempts
    private RetryHandler retryHandler;
    //    private ItemInfo mItemInfo;
    private Button mConfirmButton;
    private MaskedWallet maskedWallet;
    private int retryLoadFullWalletCount = 0;
    private Intent activityLaunchIntent;

    private String payeezyEnvironment;
    private String totalAmount;

    /**
     * Select the appropriate First Data server for the environment.
     *
     * @param env Environment
     * @return URL
     */
    private static String getUrl(String env) {
        return EnvData.getProperties(env).getUrl();
    }

    private static String bytesToHex(byte[] byteArray) {
        StringBuilder sb = new StringBuilder(byteArray.length * 2);
        for (byte b : byteArray) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mRetryCounter = savedInstanceState.getInt(KEY_RETRY_COUNTER);
            retryLoadFullWalletCount = savedInstanceState.getInt(KEY_RETRY_FULL_WALLET_COUNTER);
            handleFullWalletWhenReady =
                    savedInstanceState.getBoolean(KEY_HANDLE_FULL_WALLET_WHEN_READY);
        }
        activityLaunchIntent = getActivity().getIntent();
        itemId = activityLaunchIntent.getIntExtra(PaymentConstants.EXTRA_ITEM_ID, 0);
        maskedWallet = activityLaunchIntent.getParcelableExtra(PaymentConstants.EXTRA_MASKED_WALLET);

        totalAmount = activityLaunchIntent.getStringExtra(PaymentConstants.EXTRA_AMOUNT);
        payeezyEnvironment = activityLaunchIntent.getStringExtra(PaymentConstants.EXTRA_ENV);

        String accountName = getString(R.string.account_name);

        // Set up an API client;
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .setAccountName(accountName)
                .addApi(Wallet.API, new Wallet.WalletOptions.Builder()
                        .setEnvironment(PaymentConstants.WALLET_ENVIRONMENT)
                        .setTheme(WalletConstants.THEME_HOLO_LIGHT)
                        .build())
                .build();

        retryHandler = new RetryHandler(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Connect to Google Play Services
        mGoogleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnectGoogleApiClient();
    }

    private void disconnectGoogleApiClient() {
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_RETRY_COUNTER, mRetryCounter);
        outState.putBoolean(KEY_HANDLE_FULL_WALLET_WHEN_READY, handleFullWalletWhenReady);
        outState.putInt(KEY_RETRY_FULL_WALLET_COUNTER, retryLoadFullWalletCount);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        initializeProgressDialog();
        View view = inflater.inflate(R.layout.fragment_full_wallet_confirmation_button, container, false);
        //mItemInfo = PaymentConstants.ITEMS_FOR_SALE[itemId];

        mConfirmButton = (Button) view.findViewById(R.id.button_place_order);
        mConfirmButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View targetView) {
                confirmPurchase();
            }
        });

        TextView responsePreviousBalance = (TextView) view.findViewById(com.carecloud.carepaylibrary.R.id.respons_prev_balance);

        PaymentResponsibilityModel paymentModel = PaymentResponsibilityModel.getInstance();

        responsePreviousBalance.setText(CarePayConstants.DOLLAR.concat(paymentModel.getBalancesList().get(0).toString()));
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();

        // Disconnect from Google Play Services

        disconnectGoogleApiClient();

        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        retryHandler.removeMessages(MESSAGE_RETRY_CONNECTION);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // don't need to do anything here
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // don't need to do anything here
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        connectionResult = result;

        // Handle the user's tap by dismissing the progress dialog and attempting to resolve the
        // connection result.
        if (handleFullWalletWhenReady) {
            progressDialog.dismiss();
            resolveUnsuccessfulConnectionResult();
        }
    }

    /**
     * Helper to try to resolve a user recoverable error (i.e. the user has an out of date version
     * of Google Play Services installed), via an error dialog provided by
     * {@link GooglePlayServicesUtil#getErrorDialog(int, Activity, int, OnCancelListener)}. If an,
     * error is not user recoverable then the error will be handled in {@link #handleError(int)}.
     */
    protected void resolveUnsuccessfulConnectionResult() {
        // Additional user input is needed
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(getActivity(), REQUEST_CODE_RESOLVE_ERR);
            } catch (IntentSender.SendIntentException e) {
                reconnect();
            }
        } else {
            int errorCode = connectionResult.getErrorCode();
            if (GooglePlayServicesUtil.isUserRecoverableError(errorCode)) {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(errorCode, getActivity(),
                        REQUEST_CODE_RESOLVE_ERR, new OnCancelListener() {

                            @Override
                            public void onCancel(DialogInterface dialog) {
                                // get a new connection result
                                mGoogleApiClient.connect();
                            }
                        });

                // the dialog will either be dismissed, which will invoke the OnCancelListener, or
                // the dialog will be addressed, which will result in a callback to
                // OnActivityResult()
                dialog.show();
            } else {
                switch (errorCode) {
                    case ConnectionResult.INTERNAL_ERROR:
                    case ConnectionResult.NETWORK_ERROR:
                        reconnect();
                        break;
                    default:
                        handleError(errorCode);
                }
            }
        }

        connectionResult = null;
    }

    /**
     * Invoked when the user confirms the transaction.
     *
     * @param requestCode Request Code
     * @param resultCode  Result of request execution
     * @param data        Intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        progressDialog.hide();

        // retrieve the error code, if available
        int errorCode = -1;
        if (data != null) {
            errorCode = data.getIntExtra(WalletConstants.EXTRA_ERROR_CODE, -1);
        }

        switch (requestCode) {
            case REQUEST_CODE_RESOLVE_ERR:
                if (resultCode == Activity.RESULT_OK) {
                    mGoogleApiClient.connect();
                } else {
                    handleUnrecoverableGoogleWalletError(errorCode);
                }
                break;
            case REQUEST_CODE_RESOLVE_LOAD_FULL_WALLET:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        //  Transaction confirmed - Full Wallet is finally here
                        if (data.hasExtra(WalletConstants.EXTRA_FULL_WALLET)) {
                            FullWallet fullWallet =
                                    data.getParcelableExtra(WalletConstants.EXTRA_FULL_WALLET);
                            // the full wallet can now be used to process the customer's payment
                            // send the wallet info up to server to process, and to get the result
                            // for sending a transaction status
                            fetchTransactionStatus(fullWallet);
                        } else if (data.hasExtra(WalletConstants.EXTRA_MASKED_WALLET)) {
                            // re-launch the activity with new masked wallet information
                            maskedWallet =
                                    data.getParcelableExtra(WalletConstants.EXTRA_MASKED_WALLET);
                            activityLaunchIntent.putExtra(PaymentConstants.EXTRA_MASKED_WALLET,
                                    maskedWallet);
                            startActivity(activityLaunchIntent);
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        // nothing to do here
                        break;
                    default:
                        handleError(errorCode);
                        break;
                }
                break;
            default:
                handleError(errorCode);
                break;
        }
    }

    /*package*/ void updateMaskedWallet(MaskedWallet maskedWallet) {
        maskedWallet = maskedWallet;
    }

    private void reconnect() {
        if (mRetryCounter < MAX_RETRIES) {
            progressDialog.show();
            Message message = retryHandler.obtainMessage(MESSAGE_RETRY_CONNECTION);
            // back off exponentially
            long delay = (long) (INITIAL_RETRY_DELAY_MILLISECONDS * Math.pow(2, mRetryCounter));
            retryHandler.sendMessageDelayed(message, delay);
            mRetryCounter++;
        } else {
            handleError(WalletConstants.ERROR_CODE_SERVICE_UNAVAILABLE);
        }
    }

    /**
     * For unrecoverable Google Wallet errors, send the user back to the checkout page to handle the
     * problem.
     *
     * @param errorCode Error code
     */
    protected void handleUnrecoverableGoogleWalletError(int errorCode) {

        Toast.makeText(getActivity(),
                String.format("Google Wallet Error %d", errorCode), Toast.LENGTH_LONG).show();
    }

    private void handleError(int errorCode) {
        if (checkAndRetryFullWallet(errorCode)) {
            // handled by retrying
            return;
        }
        switch (errorCode) {
            case WalletConstants.ERROR_CODE_SPENDING_LIMIT_EXCEEDED:
                // may be recoverable if the user tries to lower their charge
                // take the user back to the checkout page to try to handle
            case WalletConstants.ERROR_CODE_INVALID_PARAMETERS:
            case WalletConstants.ERROR_CODE_AUTHENTICATION_FAILURE:
            case WalletConstants.ERROR_CODE_BUYER_ACCOUNT_ERROR:
            case WalletConstants.ERROR_CODE_MERCHANT_ACCOUNT_ERROR:
            case WalletConstants.ERROR_CODE_SERVICE_UNAVAILABLE:
            case WalletConstants.ERROR_CODE_UNSUPPORTED_API_VERSION:
            case WalletConstants.ERROR_CODE_UNKNOWN:
            default:
                // unrecoverable error
                // take the user back to the checkout page to handle these errors
                handleUnrecoverableGoogleWalletError(errorCode);
        }
    }

    /**
     * Invoked when the user clicks the button. Issues a request to get the full wallet and
     * starts the progress dialog.
     */
    private void confirmPurchase() {
        if (connectionResult != null) {
            // The user needs to resolve an issue before GoogleApiClient can connect
            resolveUnsuccessfulConnectionResult();
        } else {
            getFullWallet();
            progressDialog.setCancelable(false);
            progressDialog.show();
            handleFullWalletWhenReady = true;
        }
    }

    private List<LineItem> buildLineItems(String amount) {
        List<LineItem> list = new ArrayList<>();
        list.add(LineItem.newBuilder()
                .setCurrencyCode(PaymentConstants.CURRENCY_CODE_USD)
                .setDescription("Tesla model S")
                .setQuantity("1")
                .setUnitPrice(amount)
                .setTotalPrice(amount)
                .build());

        return list;
    }

    /**
     * Create a Full Wallet request. We need to provide the line items and amount.
     *
     * @param googleTransactionId The transaction Id from the {@link MaskedWallet}
     * @return Full Wallet request object
     */
    public FullWalletRequest createFullWalletRequest(String googleTransactionId) {

        List<LineItem> lineItems = buildLineItems(totalAmount);

        return FullWalletRequest.newBuilder()
                .setGoogleTransactionId(googleTransactionId)
                .setCart(Cart.newBuilder()
                        .setCurrencyCode(PaymentConstants.CURRENCY_CODE_USD)
                        .setTotalPrice(totalAmount)
                        .setLineItems(lineItems)
                        .build())
                .build();
    }

    /**
     * Issue a request to load the full wallet.
     */
    private void getFullWallet() {
        FullWalletRequest fullWalletRequest = createFullWalletRequest(
                maskedWallet.getGoogleTransactionId());

        Wallet.Payments.loadFullWallet(mGoogleApiClient,
                fullWalletRequest,
                REQUEST_CODE_RESOLVE_LOAD_FULL_WALLET);
    }


//   private Map<String, String> HMACMap ;
//
//    private void sendFirstDataRequestWithRetroFit(final FullWallet fullWallet, String env)
//    {
//        try {
//
//            String tokenJSON = fullWallet.getPaymentMethodToken().getToken();
//            final JSONObject jsonObject = new JSONObject(tokenJSON);
//
//            String encryptedMessage = jsonObject.getString("encryptedMessage");
//            String publicKey = jsonObject.getString("ephemeralPublicKey");
//            String signature = jsonObject.getString("tag");
//
//            //  Create a First Data Json request
//            JSONObject requestPayload = getRequestPayload(encryptedMessage, signature, publicKey);
//            final String payloadString = requestPayload.toString();
//
//            getUTFEncodePayload(payloadString);
//            HMACMap = computeHMAC(payloadString) ;
//
//
//            FirstDataService paymentService = FirstDataServiceGenerator.createService(FirstDataService.class, getHeaders() ); //, String token, String searchString
//            Call<String> call = paymentService.postPayment(payloadString);
//            call.enqueue(new Callback<String>() {
//                @Override
//                public void onResponse(Call<String> call, retrofit2.Response<String> response) {
//
//                    response.body();
//
//                }
//
//                @Override
//                public void onFailure(Call<String> call, Throwable t) {
//                    Log.e("Payeezy Android Pay", t.getMessage());
//
//                }
//
//            });
//
//
//        }
//        catch (JSONException e) {
//            Toast.makeText(getActivity(), "Error parsing JSON payload", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    private byte[] getUTFEncodePayload(String payloadString)
//    {
//        try {
//                        return payloadString.getBytes("UTF-8");
//                    } catch (UnsupportedEncodingException e) {
//                        return null;
//                    }
//    }
//
//    public Map<String, String> getHeaders() {
//        Map<String, String> headerMap = new HashMap<>(HMACMap);
//        //  First data issued APIKey identifies the developer
//        headerMap.put("apikey", EnvData.getProperties(payeezyEnvironment).getApiKey());
//        //  First data issued token identifies the merchant
//        headerMap.put("token", EnvData.getProperties(payeezyEnvironment).getToken());
//
//        return headerMap;
//    }

    /**
     * Here the client should connect to First Data, process the credit card/instrument
     * and get back a status indicating whether charging the card was successful or not
     */
    private void fetchTransactionStatus(FullWallet fullWallet) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        // Log payment method token, if it exists
        PaymentMethodToken token = fullWallet.getPaymentMethodToken();
        if (token != null) {
            // getToken returns a JSON object as a String.  Replace newlines to make LogCat output
            // nicer.  The 'id' field of the object contains the Stripe token we are interested in.
            Log.d(TAG, "PaymentMethodToken:" + token.getToken().replace('\n', ' '));
        }

        sendRequestToFirstData(fullWallet, payeezyEnvironment);
        //sendFirstDataRequestWithRetroFit(fullWallet, payeezyEnvironment);
    }

    /**
     * Send a request to the First Data server to process the payment. The REST request
     * includes HTTP headers that identify the developer and the merchant issuing the request:
     * <ul>
     * <li>{@code apikey} - identifies the developer</li>
     * <li>{@code token} - identifies the merchant</li>
     * </ul>
     * The values for the two headers are provided by First Data.
     * <p>
     * The token created by Android Pay is extracted from the FullWallet object. The token
     * is in JSON format and consists of the following fields:
     * <ul>
     * <li>{@code encryptedMessage} - the encrypted details of the transaction</li>
     * <li>{@code ephemeralPublicKey} - the key used, together with the key pair issued
     * by First Data, to decrypt of the transaction detail</li>
     * <li>{@code tag} - a signature field</li>
     * </ul>
     * These items, along with a {@code PublicKeyHash} that is used to identify the
     * key pair provided by First data, are used
     * to create the transaction payload. The payload is sent to the First Data servers
     * for execution.
     * </p>
     *
     * @param fullWallet Full wallet object
     * @param env        First Data environment to be used
     */
    public void sendRequestToFirstData(final FullWallet fullWallet, String env) {
        try {
            //  Parse the Json token retrieved from the Full Wallet.
            String tokenJSON = fullWallet.getPaymentMethodToken().getToken();
            final JSONObject jsonObject = new JSONObject(tokenJSON);

            String encryptedMessage = jsonObject.getString("encryptedMessage");
            String publicKey = jsonObject.getString("ephemeralPublicKey");
            String signature = jsonObject.getString("tag");

            //  Create a First Data Json request
            JSONObject requestPayload = getRequestPayload(encryptedMessage, signature, publicKey);
            final String payloadString = requestPayload.toString();
            final Map<String, String> HMACMap = computeHMAC(payloadString);


            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    getUrl(env),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //   request completed - launch the response activity
                            try {
                                JSONObject obj = new JSONObject(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            // startResponseActivity("SUCCESS", response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //      startResponseActivity("ERROR", formatErrorResponse(error));
                        }
                    }) {

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public byte[] getBody() {
                    try {
                        return payloadString.getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        return null;
                    }
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headerMap = new HashMap<>(HMACMap);
                    //  First data issued APIKey identifies the developer
                    headerMap.put("apikey", EnvData.getProperties(payeezyEnvironment).getApiKey());
                    //  First data issued token identifies the merchant
                    headerMap.put("token", EnvData.getProperties(payeezyEnvironment).getToken());

                    return headerMap;
                }
            };

            request.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            queue.add(request);
        } catch (JSONException e) {
            Toast.makeText(getActivity(), "Error parsing JSON payload", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Format the amount to decimal without the decimal point as required by First Data servers.
     * For example, "25.30" is converted into "2530"
     *
     * @param amount Amount with decimal point
     * @return Amount without the decimal point
     */
    private String formatAmount(String amount) {
        BigDecimal bigDecimalAmount = new BigDecimal(amount);
        BigDecimal scaled = bigDecimalAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        return scaled.toString().replace(".", "");
    }

    /**
     * Create First Data request payload
     *
     * @param data               Encrypted transaction detail created by Android Pay
     * @param signature          The data signature create by Android Pay
     * @param ephemeralPublicKey Ephemeral public key created by Android Pay
     * @return JSON Object containg the request payload
     */
    private JSONObject getRequestPayload(String data, String signature, String ephemeralPublicKey) {
        Map<String, Object> pm = new HashMap<>();
        pm.put("merchant_ref", "orderid");
        pm.put("transaction_type", "purchase");
        pm.put("method", "3DS");
        pm.put("amount", formatAmount(totalAmount));
        pm.put("currency_code", "USD");

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("ephemeralPublicKey", ephemeralPublicKey);
        //  First data issued Public Key Hash identifies the public key used to encrypt the data
        headerMap.put("publicKeyHash", EnvData.getProperties(payeezyEnvironment).getPublicKeyHash());

        Map<String, Object> ccmap = new HashMap<>();
        ccmap.put("type", "G");             //  Identify the request as Android Pay request
        ccmap.put("data", data);
        ccmap.put("signature", signature);
        ccmap.put("header", headerMap);

        pm.put("3DS", ccmap);
        return new JSONObject(pm);
    }

    /**
     * Compute HMAC signature for the payload. The signature is based on the APIKey and the
     * APISecret provided by First Data. If the APISecret is not specified, the HMAC is
     * not computed.
     *
     * @param payload The payload as a String
     * @return Map of HTTP headers to be added to the request
     */
    private Map<String, String> computeHMAC(String payload) {

        FirstDatEnvironmentProperties ep = EnvData.getProperties(payeezyEnvironment);
        String apiSecret = ep.getApiSecret();
        String apiKey = ep.getApiKey();
        String token = ep.getToken();

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
            } catch (Exception e) {
                //  Nothing to do
            }
        }
        return headerMap;
    }

    /**
     * Retries {@link Wallet#loadFullWallet(GoogleApiClient, FullWalletRequest, int)} if
     * {@link #MAX_FULL_WALLET_RETRIES} has not been reached.
     *
     * @return {@code true} if {@link FullWalletConfirmationButtonFragment#getFullWallet()} is retried,
     * {@code false} otherwise.
     */
    private boolean checkAndRetryFullWallet(int errorCode) {
        if ((errorCode == WalletConstants.ERROR_CODE_SERVICE_UNAVAILABLE ||
                errorCode == WalletConstants.ERROR_CODE_UNKNOWN) &&
                retryLoadFullWalletCount < MAX_FULL_WALLET_RETRIES) {
            retryLoadFullWalletCount++;
            getFullWallet();
            return true;
        }
        return false;
    }

    protected void initializeProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading");
        progressDialog.setIndeterminate(true);
        progressDialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                handleFullWalletWhenReady = false;
            }
        });
    }

    private static class RetryHandler extends Handler {

        private WeakReference<FullWalletConfirmationButtonFragment> mWeakReference;

        protected RetryHandler(FullWalletConfirmationButtonFragment fragment) {
            mWeakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_RETRY_CONNECTION:
                    FullWalletConfirmationButtonFragment fragment = mWeakReference.get();
                    if (fragment != null) {
                        fragment.mGoogleApiClient.connect();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
