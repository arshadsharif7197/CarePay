package com.carecloud.carepay.patient.payment.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.db.BreezeDataBase;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.patient.payment.androidpay.AndroidPayQueueUploadService;
import com.carecloud.carepay.patient.payment.androidpay.models.AndroidPayQueuePaymentRecord;
import com.carecloud.carepay.patient.payment.androidpay.models.PayeezyAndroidPayResponse;
import com.carecloud.carepay.patient.payment.interfaces.PatientPaymentMethodInterface;
import com.carecloud.carepay.patient.utils.payments.GooglePaymentsUtil;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.RestCallServiceCallback;
import com.carecloud.carepay.service.library.RestCallServiceHelper;
import com.carecloud.carepay.service.library.RestDef;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.payeeze.PayeezyCall;
import com.carecloud.carepaylibray.payments.fragments.PaymentMethodFragment;
import com.carecloud.carepaylibray.payments.models.MerchantServicesDTO;
import com.carecloud.carepaylibray.payments.models.PapiAccountsDTO;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceMetadataDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentCardData;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentMetadata;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PapiPaymentMethod;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.EncryptionUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.WalletConstants;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.newrelic.agent.android.NewRelic;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.RequestBody;

import static com.carecloud.carepay.patient.R.id.paymentAmount;

/**
 * A simple {@link Fragment} subclass.
 */
public class PatientPaymentMethodFragment extends PaymentMethodFragment {

    //Patient Specific Stuff
    private PatientPaymentMethodInterface callback;

    private View mGooglePayButton;
    private PaymentsClient mPaymentsClient;
    private PapiAccountsDTO papiAccount;

    protected boolean shouldInitAndroidPay = true;
    public boolean isOnBackPressCalled = false;

    /**
     * @param paymentsModel  the payments DTO
     * @param amount         the amount
     * @param onlySelectMode indicates only selection mode
     * @return an instance of PatientPaymentMethodFragment
     */
    public static PatientPaymentMethodFragment newInstance(PaymentsModel paymentsModel,
                                                           double amount,
                                                           boolean onlySelectMode) {
        PatientPaymentMethodFragment fragment = new PatientPaymentMethodFragment();
        Bundle args = new Bundle();
//        DtoHelper.bundleDto(args, paymentsModel);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        args.putBoolean(CarePayConstants.ONLY_SELECT_MODE, onlySelectMode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void attachCallback(Context context) {
        super.attachCallback(context);
        try {
            if (context instanceof PaymentViewHandler) {
                callback = (PatientPaymentMethodInterface) ((PaymentViewHandler) context).getPaymentPresenter();
            } else if (context instanceof AppointmentViewHandler) {
                callback = (PatientPaymentMethodInterface) ((AppointmentViewHandler) context).getAppointmentPresenter();
            } else {
                callback = (PatientPaymentMethodInterface) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached Context must implement PaymentMethodInterface");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_method, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        // Initialize a Google Pay API client for an environment suitable for testing.
        // It's recommended to create the PaymentsClient object inside of the onCreate method.
        mPaymentsClient = GooglePaymentsUtil.createPaymentsClient(getActivity());
        mGooglePayButton = findViewById(R.id.google_pay_button);
        possiblyShowGooglePayButton();
    }

    /**
     * Determine the viewer's ability to pay with a payment method supported by your app and display a
     * Google Pay payment button.
     *
     * @see <a href=
     * "https://developers.google.com/android/reference/com/google/android/gms/wallet/PaymentsClient.html#isReadyToPay
     * (com.google.android.gms.wallet.IsReadyToPayRequest)">PaymentsClient#IsReadyToPay</a>
     */
    private void possiblyShowGooglePayButton() {
        final JSONObject isReadyToPayJson = GooglePaymentsUtil.getIsReadyToPayRequest();
        if (isReadyToPayJson == null) {
            return;
        }
        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString());
        if (request == null) {
            return;
        }

        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        Task<Boolean> task = mPaymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(getActivity(),
                task1 -> {
                    if (task1.isSuccessful()) {
                        callback.setAndroidPayTargetFragment(this);
                        //TODO: Uncomment this when google pay review is ready
//                        mGooglePayButton.setVisibility(View.VISIBLE);
                        mGooglePayButton.setClickable(true);
                        mGooglePayButton.setOnClickListener(
                                view1 -> {
                                    if (papiAccount == null) {
                                        getPapiAccount();
                                    } else {
                                        requestPayment(papiAccount, amountToMakePayment);
                                    }
                                });
                    } else {
                        Log.w("isReadyToPay failed", task1.getException());
                    }
                });
    }

    // This method is called when the Pay with Google button is clicked.
    public void requestPayment(PapiAccountsDTO papiAccount, double amountToMakePayment) {
        // Disables the button to prevent multiple clicks.
//        mGooglePayButton.setClickable(false);

        // The price provided to the API should include taxes and shipping.
        // This price is not displayed to the user.
        String price = GooglePaymentsUtil.microsToString(amountToMakePayment);

//         TransactionInfo transaction = GooglePaymentsUtil.createTransaction(price);
        JSONObject paymentDataRequestJson = GooglePaymentsUtil.getPaymentDataRequest(papiAccount, price);
        if (paymentDataRequestJson == null) {
            return;
        }
        PaymentDataRequest request =
                PaymentDataRequest.fromJson(paymentDataRequestJson.toString());

        // Since loadPaymentData may show the UI asking the user to select a payment method, we use
        // AutoResolveHelper to wait for the user interacting with it. Once completed,
        // onActivityResult will be called with the result.
        if (request != null) {
            Task<PaymentData> task = mPaymentsClient.loadPaymentData(request);
            task.addOnCanceledListener(() -> {
                Log.e("Pablo", "yeah");
            });
            task.addOnFailureListener(e -> {
                Log.e("error", e.getLocalizedMessage());
            });
            AutoResolveHelper.resolveTask(task, getActivity(), PaymentConstants.REQUEST_CODE_GOOGLE_PAYMENT);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // retrieve the error code, if available
        int errorCode = -1;
        if (data != null) {
            errorCode = data.getIntExtra(WalletConstants.EXTRA_ERROR_CODE, -1);
        }
        switch (requestCode) {
            case WalletConstants.RESULT_ERROR:
                handleError(errorCode);
                break;
            case PaymentConstants.REQUEST_CODE_GOOGLE_PAYMENT:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        handleGooglePaymentData(data, papiAccount, amountToMakePayment);
                        break;
                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        if (status != null) {
                            Log.e(PatientPaymentMethodFragment.TAG, "" + status.getStatusMessage());
                        }
                        showDialog();
                        break;
                    default:
                        showDialog();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void handleGooglePaymentData(Intent data, PapiAccountsDTO papiAccountsDTO,
                                         Double paymentAmount) {
        PaymentData paymentData = PaymentData.getFromIntent(data);
        String paymentInformation = paymentData.toJson();
        JSONObject paymentMethodData;
        try {
            paymentMethodData = new JSONObject(paymentInformation).getJSONObject("paymentMethodData");
            String token = paymentMethodData.getJSONObject("tokenizationData").getString("token");
            processPaymentWithPayeezy(token, papiAccountsDTO, paymentAmount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void processPaymentWithPayeezy(String tokenJSON,
                                           PapiAccountsDTO papiAccountsDTO,
                                           Double paymentAmount) {
        try {
            JSONObject jsonTokenData = new JSONObject(tokenJSON);

            //  Create a First Data Json request
            MerchantServicesDTO payeezyMerchantService = paymentsModel.getPaymentPayload()
                    .getMerchantService(PaymentConstants.ANDROID_PAY_MERCHANT_SERVICE);
            if (payeezyMerchantService == null) {
                onAndroidPayFailed("No Merchant Service Available");
                return;
            }
            JSONObject requestPayload = GooglePaymentsUtil.getGooglePaymentsPayload(jsonTokenData, paymentAmount);
            final String payloadString = requestPayload.toString();

            if (papiAccountsDTO == null) {
                onAndroidPayFailed("No Account Available");
                return;
            }
            final Map<String, String> HMACMap = computeHMAC(payloadString, payeezyMerchantService, papiAccountsDTO);
            if (HMACMap.isEmpty()) {
                onAndroidPayFailed("An unknown error has occurred");
                return;
            }

            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), payloadString);
            RestCallServiceHelper restCallServiceHelper = new RestCallServiceHelper(getAppAuthorizationHelper(),
                    getApplicationMode());
            restCallServiceHelper.executeRequest(RestDef.POST,
                    payeezyMerchantService.getMetadata().getBaseUrl(),
                    getPayeezyServiceCallback(),
                    false,
                    false,
                    null,
                    null,
                    HMACMap,
                    body,
                    "v1/transactions");
        } catch (JSONException jsx) {
            jsx.printStackTrace();
            onAndroidPayFailed("An unknown error has occurred");
        }

    }

    private static Map<String, String> computeHMAC(String payload,
                                                   MerchantServicesDTO payeezyMerchantService,
                                                   PapiAccountsDTO papiAccountsDTO) {
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
                authorizeString = Base64.encodeToString(PayeezyCall.toHex(macHash), Base64.NO_WRAP);

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

    protected void setupTitleViews(View view) {
        super.setupTitleViews(view);
        Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
        if (toolbar != null) {
            toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back));
            toolbar.setNavigationOnClickListener(view1 -> onBackPressed());
        }
    }

    private RestCallServiceCallback getPayeezyServiceCallback() {
        return new RestCallServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(JsonElement jsonElement) {
                hideProgressDialog();
                onAndroidPaySuccess(jsonElement);
            }

            @Override
            public void onFailure(String errorMessage) {
                hideProgressDialog();
                onAndroidPayFailed(errorMessage);
            }

        };
    }

    private void handleError(int errorCode) {
        switch (errorCode) {
            case WalletConstants.ERROR_CODE_SPENDING_LIMIT_EXCEEDED:
                Toast.makeText(getContext(), "Way too much!!", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void getPapiAccount() {
        UserPracticeDTO userPractice = callback.getPracticeInfo(paymentsModel);

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_id", userPractice.getPracticeId());
        queryMap.put("practice_mgmt", userPractice.getPracticeMgmt());

        TransitionDTO transition = paymentsModel.getPaymentsMetadata().getPaymentsLinks().getPapiAccounts();
        getWorkflowServiceHelper().execute(transition, papiAccountsCallback, queryMap);

    }

    private WorkflowServiceCallback papiAccountsCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
            papiAccount = paymentsModel.getPaymentPayload().getPapiAccountByType(PaymentConstants.ANDROID_PAY_PAPI_ACCOUNT_TYPE);
            papiAccount.setDefaultBankAccountMid(papiAccount.getBankAccount().getMetadata().getMid());
            requestPayment(papiAccount, amountToMakePayment);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            Log.d(TAG, exceptionMessage);
        }
    };

    private void onAndroidPayFailed(String message) {
        showErrorNotification(message);
        //TODO: Uncomment this when google pay review is ready
        //mGooglePayButton.setVisibility(View.VISIBLE);
    }

    private void onAndroidPaySuccess(JsonElement jsonElement) {
        showProgressDialog();
        if (paymentsModel.getPaymentPayload().getPaymentPostModel() == null) {
            processPayment(jsonElement);
        } else {
            processPayment(jsonElement, paymentsModel.getPaymentPayload().getPaymentPostModel());
        }
    }

    private void processPayment(JsonElement rawResponse) {
        try {
            Gson gson = new Gson();
            PayeezyAndroidPayResponse androidPayResponse = gson.fromJson(rawResponse, PayeezyAndroidPayResponse.class);

            PapiPaymentMethod papiPaymentMethod = new PapiPaymentMethod();
            papiPaymentMethod.setPaymentMethodType(PapiPaymentMethod.PAYMENT_METHOD_NEW_CARD);
            papiPaymentMethod.setCardData(getCreditCardModel(androidPayResponse));

            IntegratedPaymentLineItem paymentLineItem = new IntegratedPaymentLineItem();
            paymentLineItem.setAmount(paymentAmount);
            paymentLineItem.setItemType(IntegratedPaymentLineItem.TYPE_UNAPPLIED);
            paymentLineItem.setDescription("Unapplied Amount");

            IntegratedPaymentPostModel postModel = new IntegratedPaymentPostModel();
            postModel.setExecution(IntegratedPaymentPostModel.EXECUTION_ANDROID);
            postModel.setPapiPaymentMethod(papiPaymentMethod);
            postModel.setAmount(paymentAmount);
            postModel.addLineItem(paymentLineItem);

            IntegratedPaymentMetadata postModelMetadata = postModel.getMetadata();
            postModelMetadata.setAppointmentId(callback.getAppointmentId());

            postModel.setTransactionResponse(rawResponse.getAsJsonObject());

            paymentsModel.getPaymentPayload().setPaymentPostModel(postModel);
            postPayment(gson.toJson(postModel), rawResponse);

        } catch (JsonSyntaxException jsx) {
            logPaymentFail("Unable to parse Payment", true, rawResponse, jsx.getMessage());
        }
    }

    protected void processPayment(JsonElement rawResponse, IntegratedPaymentPostModel postModel) {
        try {
            Gson gson = new Gson();
            PayeezyAndroidPayResponse androidPayResponse = gson.fromJson(rawResponse, PayeezyAndroidPayResponse.class);

            PapiPaymentMethod papiPaymentMethod = new PapiPaymentMethod();
            papiPaymentMethod.setPaymentMethodType(PapiPaymentMethod.PAYMENT_METHOD_NEW_CARD);
            papiPaymentMethod.setCardData(getCreditCardModel(androidPayResponse));

            postModel.setTransactionResponse(rawResponse.getAsJsonObject());
            postModel.setExecution(IntegratedPaymentPostModel.EXECUTION_ANDROID);
            postModel.setPapiPaymentMethod(papiPaymentMethod);

            IntegratedPaymentMetadata postModelMetadata = postModel.getMetadata();
            if (StringUtil.isNullOrEmpty(postModel.getMetadata().getAppointmentId()) &&
                    postModel.getMetadata().getAppointmentRequestDTO() == null) {
                postModelMetadata.setAppointmentId(callback.getAppointmentId());
            }
            postPayment(gson.toJson(postModel), rawResponse);
        } catch (JsonSyntaxException jsx) {
            logPaymentFail("Unable to parse Payment", true, rawResponse, jsx.getMessage());
        }
    }

    private void postPayment(String paymentModelJson, JsonElement rawResponse) {
        Map<String, String> queries = new HashMap<>();
        UserPracticeDTO userPracticeDTO = callback.getPracticeInfo(paymentsModel);
        AppointmentDTO appointment = callback.getAppointment();
        if (appointment != null) {
            queries.put("practice_mgmt", appointment.getMetadata().getPracticeMgmt());
            queries.put("practice_id", appointment.getMetadata().getPracticeId());
            queries.put("patient_id", appointment.getMetadata().getPatientId());
        } else if (userPracticeDTO != null) {
            queries.put("practice_mgmt", userPracticeDTO.getPracticeMgmt());
            queries.put("practice_id", userPracticeDTO.getPracticeId());
            if (userPracticeDTO.getPatientId() != null) {
                queries.put("patient_id", userPracticeDTO.getPatientId());
            } else {
                queries.put("patient_id", findPatientId(userPracticeDTO.getPracticeId()));
            }
        } else {
            PendingBalanceMetadataDTO metadata = paymentsModel.getPaymentPayload().getPatientBalances()
                    .get(0).getBalances().get(0).getMetadata();
            queries.put("practice_mgmt", metadata.getPracticeMgmt());
            queries.put("practice_id", metadata.getPracticeId());
            queries.put("patient_id", metadata.getPatientId());
        }
        if (callback.getAppointmentId() != null) {
            queries.put("appointment_id", callback.getAppointmentId());
        }
        Map<String, String> header = new HashMap<>();
        header.put("transition", "true");

        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getMakePayment();
        getWorkflowServiceHelper().execute(transitionDTO, getMakePaymentCallback(rawResponse),
                paymentModelJson, queries, header);
    }

    protected WorkflowServiceCallback getMakePaymentCallback(final JsonElement rawResponse) {
        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                callback.showPaymentConfirmation(workflowDTO);
                if (getDialog() != null) {
                    dismiss();
                }
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                System.out.print(exceptionMessage);
                logPaymentFail("Failed to reach make payment endpoint",
                        true, rawResponse, exceptionMessage);
            }
        };
    }

    protected IntegratedPaymentCardData getCreditCardModel(PayeezyAndroidPayResponse androidPayResponse) {
        IntegratedPaymentCardData creditCardModel = new IntegratedPaymentCardData();
        creditCardModel.setCardType(androidPayResponse.getCard().getType());
        creditCardModel.setCardNumber(androidPayResponse.getCard().getCardNumber());
        creditCardModel.setExpiryDate(androidPayResponse.getCard().getExpDate());
        creditCardModel.setNameOnCard(androidPayResponse.getCard().getCardholderName());
        creditCardModel.setToken(androidPayResponse.getToken().getTokenData().getValue());
        creditCardModel.setTokenizationService(IntegratedPaymentCardData.TOKENIZATION_ANDROID);

        return creditCardModel;
    }

    protected void logPaymentFail(String message, boolean paymentSuccess, Object paymentJson, String error) {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("Successful Payment", paymentSuccess);
        eventMap.put("Fail Reason", message);
        eventMap.put("Amount", paymentAmount);
        UserPracticeDTO userPracticeDTO = callback.getPracticeInfo(paymentsModel);
        String patientId;
        String practiceId;
        String practiceMgmt;
        if (userPracticeDTO != null) {
            patientId = userPracticeDTO.getPracticeMgmt();
            practiceId = userPracticeDTO.getPracticeId();
            practiceMgmt = findPatientId(userPracticeDTO.getPracticeId());
        } else {
            PendingBalanceMetadataDTO metadata = paymentsModel.getPaymentPayload().getPatientBalances()
                    .get(0).getBalances().get(0).getMetadata();
            patientId = metadata.getPracticeMgmt();
            practiceId = metadata.getPracticeId();
            practiceMgmt = metadata.getPatientId();
        }
        eventMap.put("Patient Id", patientId);
        eventMap.put("Practice Id", practiceId);
        eventMap.put("Practice Mgmt", practiceMgmt);

        Gson gson = new Gson();
        eventMap.put("Post Model", gson.toJson(paymentsModel.getPaymentPayload().getPaymentPostModel()));

        if (paymentJson == null) {
            paymentJson = "";
        } else {
            eventMap.put("Payment Object", paymentJson.toString());
        }

        if (error == null) {
            error = "";
        } else {
            eventMap.put("Error Message", error);
        }

        NewRelic.recordCustomEvent("AndroidPaymentFail", eventMap);

        if (paymentSuccess) {
            //sent to Pay Queue API endpoint
            queuePayment(
                    amountToMakePayment,
                    paymentsModel.getPaymentPayload().getPaymentPostModel(),
                    patientId,
                    practiceId,
                    practiceMgmt,
                    paymentJson.toString(),
                    error);

            callback.showPaymentPendingConfirmation(paymentsModel, practiceId);
        }
    }

    private void queuePayment(double amount,
                              IntegratedPaymentPostModel postModel,
                              String patientID, String practiceId,
                              String practiceMgmt,
                              String paymentJson,
                              String errorMessage) {
        if (postModel != null) {

            if (postModel.getExecution() == null) {
                postModel.setExecution(IntegratedPaymentPostModel.EXECUTION_CLOVER);
            }

            if (postModel.getTransactionResponse() == null) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("Payment Response", paymentJson);
                jsonObject.addProperty("Error Message", errorMessage);
                postModel.setTransactionResponse(jsonObject);
            } else {
                if (!postModel.getTransactionResponse().has("Payment Response")) {
                    postModel.getTransactionResponse().addProperty("Payment Response", paymentJson);
                }
                if (!postModel.getTransactionResponse().has("Error Message")) {
                    postModel.getTransactionResponse().addProperty("Error Message", errorMessage);
                }
            }

            if (postModel.getAmount() == 0) {
                postModel.setAmount(amount);
            }
        }


        Gson gson = new Gson();
        String paymentModelJson = paymentJson;
        try {
            paymentModelJson = gson.toJson(postModel);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        //store in local DB
        final AndroidPayQueuePaymentRecord paymentRecord = new AndroidPayQueuePaymentRecord();
        paymentRecord.setPatientID(patientID);
        paymentRecord.setPracticeID(practiceId);
        paymentRecord.setPracticeMgmt(practiceMgmt);
        paymentRecord.setQueueTransition(gson.toJson(paymentsModel.getPaymentsMetadata()
                .getPaymentsTransitions().getQueuePayment()));
        paymentRecord.setUsername(getApplicationPreferences().getUserId());

        String paymentModelJsonEnc = EncryptionUtil.encrypt(getContext(), paymentModelJson, practiceId);
        paymentRecord.setPaymentModelJsonEnc(paymentModelJsonEnc);

        if (StringUtil.isNullOrEmpty(paymentModelJsonEnc)) {
            paymentRecord.setPaymentModelJson(paymentModelJson);
        }
        Executors.newSingleThreadExecutor().execute(() -> {
            BreezeDataBase database = BreezeDataBase.getDatabase(getContext());
            database.getAndroidPayDao().insert(paymentRecord);
        });

        AndroidPayQueueUploadService.enqueueWork(getContext(), new Intent());
    }

    private String findPatientId(String practiceId) {
        for (PatientBalanceDTO balanceDTO : paymentsModel.getPaymentPayload().getPatientBalances()) {
            for (PendingBalanceDTO pendingBalanceDTO : balanceDTO.getBalances()) {
                if (pendingBalanceDTO.getMetadata().getPracticeId().equals(practiceId)) {
                    return pendingBalanceDTO.getMetadata().getPatientId();
                }
            }
        }
        return null;
    }


    @Override
    public void onBackPressed() {
        isOnBackPressCalled = true;
        super.onBackPressed();
    }
}
