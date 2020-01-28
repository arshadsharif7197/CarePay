package com.carecloud.carepay.patient.payment.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.patient.payment.androidpay.AndroidPayAdapter;
import com.carecloud.carepay.patient.payment.androidpay.AndroidPayQueueUploadService;
import com.carecloud.carepay.patient.db.BreezeDataBase;
import com.carecloud.carepay.patient.payment.androidpay.models.AndroidPayQueuePaymentRecord;
import com.carecloud.carepay.patient.payment.androidpay.models.PayeezyAndroidPayResponse;
import com.carecloud.carepay.patient.payment.interfaces.PatientPaymentMethodInterface;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.ServerErrorDTO;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.payments.fragments.PaymentMethodFragment;
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
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.newrelic.agent.android.NewRelic;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import static com.carecloud.carepay.patient.R.id.paymentAmount;

/**
 * A simple {@link Fragment} subclass.
 */
public class PatientPaymentMethodFragment extends PaymentMethodFragment
        implements AndroidPayAdapter.AndroidPayReadyCallback, AndroidPayAdapter.AndroidPayProcessingCallback {

    //Patient Specific Stuff
    private ProgressBar paymentMethodFragmentProgressBar;
    private PatientPaymentMethodInterface callback;

    private AndroidPayAdapter androidPayAdapter;
    private View androidPayButton;
    private PapiAccountsDTO papiAccount;

    protected boolean shouldInitAndroidPay = true;

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
        DtoHelper.bundleDto(args, paymentsModel);
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
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_method, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        paymentMethodFragmentProgressBar = view.findViewById(R.id.paymentMethodFragmentProgressBar);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (shouldInitAndroidPay) {
            initAndroidPay();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (androidPayAdapter != null) {
            androidPayAdapter.disconnectClient();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // retrieve the error code, if available
        int errorCode = -1;
        if (data != null) {
            errorCode = data.getIntExtra(WalletConstants.EXTRA_ERROR_CODE, -1);
        }
        switch (requestCode) {
            case PaymentConstants.REQUEST_CODE_MASKED_WALLET:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (data != null) {
                            MaskedWallet maskedWallet = data.getParcelableExtra(WalletConstants.EXTRA_MASKED_WALLET);
                            callback.createWalletFragment(maskedWallet, amountToMakePayment);
                        }
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
            case PaymentConstants.REQUEST_CODE_GOOGLE_PAYMENT:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (androidPayAdapter == null) {
                            androidPayAdapter = new AndroidPayAdapter(getActivity(),
                                    paymentsModel.getPaymentPayload().getMerchantServices());
                        }
                        androidPayAdapter.handleGooglePaymentData(data, papiAccount, amountToMakePayment, this);
                        break;
                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        if (status != null) {
                            Log.e(PatientPaymentMethodFragment.TAG, "" + status.getStatusMessage());
                        }
                        break;
                    default:

                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    protected void setupTitleViews(View view) {
        super.setupTitleViews(view);
        Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
        if (toolbar != null) {
            toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back));
            toolbar.setNavigationOnClickListener(view1 -> onBackPressed());
        }
    }

    private void showOrHideProgressDialog(boolean show) {
        if (show) {
            paymentMethodFragmentProgressBar.setVisibility(View.VISIBLE);
        } else {
            paymentMethodFragmentProgressBar.setVisibility(View.GONE);
        }

    }

    private void initAndroidPay() {
        androidPayAdapter = new AndroidPayAdapter(getActivity(), paymentsModel.getPaymentPayload().getMerchantServices());
        androidPayAdapter.initAndroidPay(this);
    }


    private void addAndroidPayPaymentMethod() {
        callback.setAndroidPayTargetFragment(this);
        androidPayButton = findViewById(R.id.google_pay_button);
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


    @Override
    public void onAndroidPayReady() {
        showOrHideProgressDialog(false);
        if (getActivity() != null) {
            addAndroidPayPaymentMethod();
            getPapiAccount();
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

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
            papiAccount = paymentsModel.getPaymentPayload().getPapiAccountByType(PaymentConstants.ANDROID_PAY_PAPI_ACCOUNT_TYPE);
            if (papiAccount.getDefaultBankAccountMid() != null) {
                androidPayButton.setVisibility(View.VISIBLE);
                androidPayButton.setOnClickListener(view -> {
                    androidPayAdapter.createAndroidPayRequest(amountToMakePayment, papiAccount);
                    view.setVisibility(View.INVISIBLE);
                });
            }
        }

        @Override
        public void onFailure(ServerErrorDTO serverErrorDto) {
            Log.d(TAG, serverErrorDto.getMessage().getBody().getError().getMessage());
        }
    };

    @Override
    public void onAndroidPayFailed(String message) {
        showErrorNotification(message);
        androidPayButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAndroidPaySuccess(JsonElement jsonElement) {
        showProgressDialog();
        if (paymentsModel.getPaymentPayload().getPaymentPostModel() == null) {
            processPayment(jsonElement);
        } else {
            processPayment(jsonElement, paymentsModel.getPaymentPayload().getPaymentPostModel());
        }
    }

    @Override
    public ISession getSession() {
        return this;
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
        getWorkflowServiceHelper().execute(transitionDTO, getMakePaymentCallback(rawResponse), paymentModelJson, queries, header);
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
            public void onFailure(ServerErrorDTO serverErrorDto) {
                hideProgressDialog();
                System.out.print(serverErrorDto.getMessage().getBody().getError().getMessage());
                logPaymentFail("Failed to reach make payment endpoint", true,
                        rawResponse, serverErrorDto.getMessage().getBody().getError().getMessage());
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
            PendingBalanceMetadataDTO metadata = paymentsModel.getPaymentPayload()
                    .getPatientBalances().get(0).getBalances().get(0).getMetadata();
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

            callback.showPaymentPendingConfirmation(paymentsModel);
        }
    }

    private void queuePayment(double amount,
                              IntegratedPaymentPostModel postModel,
                              String patientID,
                              String practiceId,
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
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                BreezeDataBase database = BreezeDataBase.getDatabase(getContext());
                database.getAndroidPayDao().insert(paymentRecord);
            }
        });

        Intent intent = new Intent(getContext(), AndroidPayQueueUploadService.class);
        getContext().startService(intent);
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
}
