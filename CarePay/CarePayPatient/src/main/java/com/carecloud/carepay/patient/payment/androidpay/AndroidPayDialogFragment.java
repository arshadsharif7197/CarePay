package com.carecloud.carepay.patient.payment.androidpay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.patient.payment.androidpay.models.AndroidPayQueuePaymentRecord;
import com.carecloud.carepay.patient.payment.androidpay.models.PayeezyAndroidPayResponse;
import com.carecloud.carepay.patient.payment.interfaces.PatientPaymentMethodInterface;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.payments.models.PapiAccountsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
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
import com.google.android.gms.identity.intents.model.UserAddress;
import com.google.android.gms.wallet.FullWallet;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.newrelic.agent.android.NewRelic;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


import static android.app.Activity.RESULT_OK;

/**
 * Created by lmenendez on 10/17/17
 */

public class AndroidPayDialogFragment extends BaseDialogFragment implements AndroidPayAdapter.AndroidPayProcessingCallback {
    private static final String KEY_MASKED_WALLET = "masked_wallet";

    private ViewGroup detailsContainer;
    private View buttonConfirm;

    private AndroidPayAdapter androidPayAdapter;

    private MaskedWallet maskedWallet;
    private PaymentsModel paymentsModel;
    private Double paymentAmount;
    private PapiAccountsDTO papiAccount;

    private PatientPaymentMethodInterface callback;

    /**
     * get new instance of AndroidPayDialogFragment
     * @param maskedWallet masked wallet
     * @param paymentsModel payments model
     * @param amount payments amount
     * @return new instance of AndroidPayDialogFragment
     */
    public static AndroidPayDialogFragment newInstance(MaskedWallet maskedWallet, PaymentsModel paymentsModel, Double amount){
        Bundle args = new Bundle();
        args.putParcelable(KEY_MASKED_WALLET, maskedWallet);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        DtoHelper.bundleDto(args, paymentsModel);

        AndroidPayDialogFragment fragment = new AndroidPayDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try {
            if (context instanceof PaymentViewHandler) {
                callback = (PatientPaymentMethodInterface) ((PaymentViewHandler) context).getPaymentPresenter();
            }else if (context instanceof AppointmentViewHandler){
                callback = (PatientPaymentMethodInterface) ((AppointmentViewHandler) context).getAppointmentPresenter();
            } else {
                callback = (PatientPaymentMethodInterface) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached Context must implement PaymentMethodInterface");
        }
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        Bundle args = getArguments();
        if(args != null){
            maskedWallet = args.getParcelable(KEY_MASKED_WALLET);
            paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, args);
            paymentAmount = args.getDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE);
        }
        androidPayAdapter = new AndroidPayAdapter(getActivity(), paymentsModel.getPaymentPayload().getMerchantServices(), getChildFragmentManager());
        callback.setAndroidPayTargetFragment(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // retrieve the error code, if available
        int errorCode = -1;
        if (data != null) {
            errorCode = data.getIntExtra(WalletConstants.EXTRA_ERROR_CODE, -1);
        }
        switch (requestCode) {
            case PaymentConstants.REQUEST_CODE_FULL_WALLET:
                switch (resultCode) {
                    case RESULT_OK:
                        if(data != null) {
                            FullWallet fullWallet = data.getParcelableExtra(WalletConstants.EXTRA_FULL_WALLET);
                            androidPayAdapter.sendRequestToPayeezy(fullWallet, papiAccount, paymentAmount, this);
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

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_androidpay_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        initToolbar(view);

        TextView account = (TextView) view.findViewById(R.id.pay_account);
        account.setText(maskedWallet.getEmail());

        TextView method = (TextView) view.findViewById(R.id.pay_method);
        method.setText(maskedWallet.getPaymentDescriptions()[0]);

        TextView address = (TextView) view.findViewById(R.id.pay_billing_address);
        address.setText(buildAddress());


        detailsContainer = (ViewGroup) view.findViewById(R.id.androidpay_invoice_container);
        buttonConfirm = view.findViewById(R.id.button_place_order);

        TextView textView = (TextView) view.findViewById(R.id.paymentAmount);
        textView.setText(NumberFormat.getCurrencyInstance(Locale.US).format(paymentAmount));

//        initChildFragments();
        getPapiAccount();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(androidPayAdapter != null) {
            androidPayAdapter.disconnectClient();
        }

    }

    private void initToolbar(View view){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);

        if (toolbar != null) {
            TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
            title.setText(Label.getLabel("payment_patient_balance_toolbar"));

            toolbar.setNavigationIcon(R.drawable.icn_patient_mode_nav_close);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().onBackPressed();
                }
            });
        }
    }

    private String buildAddress(){
        StringBuilder builder = new StringBuilder();
        UserAddress address = maskedWallet.getBuyerBillingAddress();
        builder.append(address.getName())
                .append("\n")
                .append(address.getAddress1())
                .append("\n");
        if(address.getAddress2() != null && address.getAddress2().length() > 0){
            builder.append(address.getAddress2());
            builder.append("\n");
        }
        builder.append(address.getLocality())
                .append(", ")
                .append(address.getAdministrativeArea())
                .append(", ")
                .append(address.getPostalCode());
        return builder.toString();
    }

    private void initChildFragments(){
        androidPayAdapter.createWalletDetails(maskedWallet, detailsContainer);
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

    private void getPapiAccount(){
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
            buttonConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    androidPayAdapter.createFullWallet(maskedWallet, paymentAmount);
                }
            });
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
            dismiss();
        }
    };

    @Override
    public void onAndroidPayFailed(String message) {

    }

    @Override
    public void onAndroidPaySuccess(JsonElement jsonElement) {
        if(paymentsModel.getPaymentPayload().getPaymentPostModel() == null){
            processPayment(jsonElement);
        }else{
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

    private void processPayment(JsonElement rawResponse, IntegratedPaymentPostModel postModel) {
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
            postModelMetadata.setAppointmentId(callback.getAppointmentId());

            postPayment(gson.toJson(postModel), rawResponse);
        } catch (JsonSyntaxException jsx) {
            logPaymentFail("Unable to parse Payment", true, rawResponse, jsx.getMessage());
        }
    }

    private void postPayment(String paymentModelJson, JsonElement rawResponse) {
        Map<String, String> queries = new HashMap<>();
        UserPracticeDTO userPracticeDTO = callback.getPracticeInfo(paymentsModel);
        if (userPracticeDTO != null) {
            queries.put("practice_mgmt", userPracticeDTO.getPracticeMgmt());
            queries.put("practice_id", userPracticeDTO.getPracticeId());
            queries.put("patient_id", userPracticeDTO.getPatientId());
        } else {
            PendingBalanceMetadataDTO metadata = paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getMetadata();
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

    private WorkflowServiceCallback getMakePaymentCallback(final JsonElement rawResponse) {
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
                logPaymentFail("Failed to reach make payments endpoint", true, rawResponse, exceptionMessage);

            }
        };
    }


    private IntegratedPaymentCardData getCreditCardModel(PayeezyAndroidPayResponse androidPayResponse) {
        IntegratedPaymentCardData creditCardModel = new IntegratedPaymentCardData();
        creditCardModel.setCardType(androidPayResponse.getCard().getType());
        creditCardModel.setCardNumber(androidPayResponse.getCard().getCardNumber());
        creditCardModel.setExpiryDate(androidPayResponse.getCard().getExpDate());
        creditCardModel.setNameOnCard(androidPayResponse.getCard().getCardholderName());
        creditCardModel.setToken(androidPayResponse.getToken().getTokenData().getValue());
        creditCardModel.setTokenizationService(IntegratedPaymentCardData.TOKENIZATION_ANDROID);

        return creditCardModel;
    }


    private void logPaymentFail(String message, boolean paymentSuccess, Object paymentJson, String error) {
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
            practiceMgmt = userPracticeDTO.getPatientId();
        } else {
            PendingBalanceMetadataDTO metadata = paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getMetadata();
            patientId = metadata.getPracticeMgmt();
            practiceId = metadata.getPracticeId();
            practiceMgmt = metadata.getPatientId();
        }
        eventMap.put("Patient Id", patientId);
        eventMap.put("Practice Id", practiceId);
        eventMap.put("Practice Mgmt", practiceMgmt);

        Gson gson = new Gson();
        eventMap.put("Post Model", gson.toJson(paymentsModel.getPaymentPayload().getPaymentPostModel()));

        if(paymentJson == null){
            paymentJson = "";
        }else{
            eventMap.put("Payment Object", paymentJson.toString());
        }

        if(error == null){
            error = "";
        }else{
            eventMap.put("Error Message", error);
        }

        NewRelic.recordCustomEvent("AndroidPaymentFail", eventMap);

        if(paymentSuccess){
            //sent to Pay Queue API endpoint
            queuePayment(
                    paymentAmount,
                    paymentsModel.getPaymentPayload().getPaymentPostModel(),
                    patientId,
                    practiceId,
                    practiceMgmt,
                    paymentJson.toString(),
                    error);

            callback.showPaymentPendingConfirmation(paymentsModel);
        }
    }

    private void queuePayment(double amount, IntegratedPaymentPostModel postModel, String patientID, String practiceId, String practiceMgmt, String paymentJson, String errorMessage){
        if(postModel!=null){

            if(postModel.getExecution() == null){
                postModel.setExecution(IntegratedPaymentPostModel.EXECUTION_CLOVER);
            }

            if(postModel.getTransactionResponse()==null){
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("Payment Response", paymentJson);
                jsonObject.addProperty("Error Message", errorMessage);
                postModel.setTransactionResponse(jsonObject);
            }else{
                if(!postModel.getTransactionResponse().has("Payment Response")) {
                    postModel.getTransactionResponse().addProperty("Payment Response", paymentJson);
                }
                if(!postModel.getTransactionResponse().has("Error Message")) {
                    postModel.getTransactionResponse().addProperty("Error Message", errorMessage);
                }
            }

            if(postModel.getAmount() == 0){
                postModel.setAmount(amount);
            }
        }


        Gson gson = new Gson();
        String paymentModelJson = paymentJson;
        try{
            paymentModelJson = gson.toJson(postModel);
        }catch (Exception ex){
            ex.printStackTrace();
        }



        //store in local DB
        AndroidPayQueuePaymentRecord paymentRecord = new AndroidPayQueuePaymentRecord();
        paymentRecord.setPatientID(patientID);
        paymentRecord.setPracticeID(practiceId);
        paymentRecord.setPracticeMgmt(practiceMgmt);
        paymentRecord.setQueueTransition(gson.toJson(paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getQueuePayment()));
        paymentRecord.setUsername(getApplicationPreferences().getUserId());

        String paymentModelJsonEnc = EncryptionUtil.encrypt(getContext(), paymentModelJson, practiceId);
        paymentRecord.setPaymentModelJsonEnc(paymentModelJsonEnc);

        if(StringUtil.isNullOrEmpty(paymentModelJsonEnc)){
            paymentRecord.setPaymentModelJson(paymentModelJson);
        }
        paymentRecord.save();

        Intent intent = new Intent(getContext(), AndroidPayQueueUploadService.class);
        getContext().startService(intent);
    }


}
