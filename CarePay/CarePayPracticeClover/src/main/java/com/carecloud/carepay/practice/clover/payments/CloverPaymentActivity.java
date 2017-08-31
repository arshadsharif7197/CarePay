package com.carecloud.carepay.practice.clover.payments;

import android.accounts.Account;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

import com.carecloud.carepay.practice.clover.CloverQueueUploadService;
import com.carecloud.carepay.practice.clover.R;
import com.carecloud.carepay.practice.clover.models.CloverCardTransactionInfo;
import com.carecloud.carepay.practice.clover.models.CloverPaymentDTO;
import com.carecloud.carepay.practice.clover.models.CloverQueuePaymentRecord;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentCardData;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentMetadata;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentLineItem;
import com.carecloud.carepaylibray.utils.EncryptionUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.util.CloverAuth;
import com.clover.sdk.v1.BindingException;
import com.clover.sdk.v1.ClientException;
import com.clover.sdk.v1.Intents;
import com.clover.sdk.v1.ServiceException;
import com.clover.sdk.v3.order.LineItem;
import com.clover.sdk.v3.order.Order;
import com.clover.sdk.v3.order.OrderConnector;
import com.clover.sdk.v3.payments.Payment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.newrelic.agent.android.NewRelic;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * The type Clover payment activity.
 */
public class CloverPaymentActivity extends BaseActivity {
    /**
     * The constant creditCardIntentID.
     */
    public static final int creditCardIntentID = 555;

    private static final String TAG = CloverPaymentActivity.class.getName();
    private Account account;
    private OrderConnector orderConnector;
    private Long amountLong = 0L;
    private double amountDouble;
    private String paymentTransitionString;
    private String queueTransitionString;
    private IntegratedPaymentPostModel postModel;
    private PatientBalanceDTO patientBalance;
    private String appointmentId;

    private PaymentLineItem[] paymentLineItems;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        setContentView(R.layout.dialog_progress);

        Intent intent = getIntent();
        if (intent.hasExtra(CarePayConstants.CLOVER_PAYMENT_AMOUNT)) {
            amountDouble = intent.getDoubleExtra(CarePayConstants.CLOVER_PAYMENT_AMOUNT, 0.00);
            amountLong = (long) (amountDouble * 100);
        }

        if (intent.hasExtra(CarePayConstants.CLOVER_PAYMENT_TRANSITION)) {
            paymentTransitionString = intent.getStringExtra(CarePayConstants.CLOVER_PAYMENT_TRANSITION);

        }

        if (intent.hasExtra(CarePayConstants.CLOVER_QUEUE_PAYMENT_TRANSITION)) {
            queueTransitionString = intent.getStringExtra(CarePayConstants.CLOVER_QUEUE_PAYMENT_TRANSITION);

        }

        if (intent.hasExtra(CarePayConstants.CLOVER_PAYMENT_METADATA)) {
            Gson gson = new Gson();
            String patientPaymentMetaDataString = intent.getStringExtra(CarePayConstants.CLOVER_PAYMENT_METADATA);
            patientBalance = gson.fromJson(patientPaymentMetaDataString, PatientBalanceDTO.class);

        }

        if (intent.hasExtra(CarePayConstants.APPOINTMENT_ID)){
            appointmentId = intent.getStringExtra(CarePayConstants.APPOINTMENT_ID);
        }

        Gson gson = new Gson();
        if (intent.hasExtra(CarePayConstants.CLOVER_PAYMENT_LINE_ITEMS)) {
            String lineItemString = intent.getStringExtra(CarePayConstants.CLOVER_PAYMENT_LINE_ITEMS);
            paymentLineItems = gson.fromJson(lineItemString, PaymentLineItem[].class);

        }
        if (intent.hasExtra(CarePayConstants.CLOVER_PAYMENT_POST_MODEL)) {
            String paymentPostModelString = intent.getStringExtra(CarePayConstants.CLOVER_PAYMENT_POST_MODEL);
            postModel = gson.fromJson(paymentPostModelString, IntegratedPaymentPostModel.class);
        }

        account = CloverAccount.getAccount(this);
        if(account != null) {
            authenticateCloverAccount();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (account == null) {
            account = CloverAccount.getAccount(this);
            if(account!=null) {
                authenticateCloverAccount();
            }else{
                SystemUtil.showErrorToast(CloverPaymentActivity.this, getString(R.string.no_account));
                logPaymentFail(getString(R.string.no_account), false);
                finish();
            }
        }

    }

    @Override
    public void navigateToWorkflow(WorkflowDTO workflowDTO) {
        // not implemented for Clover Payment App... no workflow here
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        disconnect();
        finish();
    }

    // Establishes a connection with the connectors
    private void connect() {
        disconnect();
        if (account != null) {
            orderConnector = new OrderConnector(this, account, null);
            orderConnector.connect();
        }
    }

    // Disconnects from the connectors
    private void disconnect() {
        if (orderConnector != null) {
            orderConnector.disconnect();
            orderConnector = null;
        }
    }

    @Override
    protected void onDestroy() {
        disconnect();
        super.onDestroy();

    }

    private void onCloverAuthenticated(){
        connect();
        if (orderConnector != null) {
            List<LineItem> lineItems = getLineItems();
            new OrderAsyncTask(orderConnector).execute(lineItems.toArray(new LineItem[lineItems.size()]));
        }
    }

    private void authenticateCloverAccount() {
        new AsyncTask<Void, String, CloverAuth.AuthResult>() {

            @Override
            protected void onProgressUpdate(String... values) {
                String logString = values[0];
                Log.d(TAG, logString);
            }

            @Override
            protected CloverAuth.AuthResult doInBackground(Void... params) {
                CloverAuth.AuthResult authResult = null;
                try {
                    publishProgress("Requesting auth token");
                    authResult = CloverAuth.authenticate(CloverPaymentActivity.this, account);
                    publishProgress("Successfully authenticated as " + account + ".  authToken=" + authResult.authToken + ", authData=" + authResult.authData);
                } catch (Exception e) {
                    publishProgress("Error retrieving merchant info from server" + e);
                }
                return authResult;
            }

            @Override
            protected void onPostExecute(CloverAuth.AuthResult authResult) {
                if (authResult != null && authResult.authToken != null && authResult.baseUrl != null) {
                    onCloverAuthenticated();
                }else {
                    SystemUtil.showErrorToast(getContext(), Label.getLabel("clover_account_not_authorized"));
                    logPaymentFail("account not authorized", false);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 3000);
                }
            }
        }.execute();
    }


    private class OrderAsyncTask extends AsyncTask<LineItem, Void, Order> {
        private OrderConnector orderConnector;

        OrderAsyncTask(OrderConnector orderConnector){
            this.orderConnector = orderConnector;
        }

        @Override
        protected final Order doInBackground(LineItem... lineItems) {
            Order order = null;

            try {
                if (amountLong != null) {
                    // Create a new order
                    order = orderConnector.createOrder(new Order());
                    if (lineItems != null && lineItems.length > 0) {
                        for (LineItem lineItem : lineItems) {
                            orderConnector.addCustomLineItem(order.getId(), lineItem, false);
                        }
                    } else {
                        return null;
                    }
                    // Update local representation of the order
                    order = orderConnector.getOrder(order.getId());
                }
            } catch (RemoteException | ClientException | ServiceException | BindingException e) {
                e.printStackTrace();
            }

            return order;
        }

        @Override
        protected final void onPostExecute(Order order) {
            if (order == null) {
                setResult(RESULT_CANCELED);
                SystemUtil.showErrorToast(CloverPaymentActivity.this, getString(R.string.payment_cancelled));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 3000);
                return;
            }

            // Enables the pay buttons if the order is valid
            startSecurePaymentIntent(order);

        }
    }

    private void startSecurePaymentIntent(Order order) {
        Intent intent = new Intent(Intents.ACTION_SECURE_PAY);
        try {
            if (amountLong != null) {
                intent.putExtra(Intents.EXTRA_AMOUNT, amountLong);
            } else {
                SystemUtil.showErrorToast(getApplicationContext(), Label.getLabel("clover_payment_amount_error"));
                throw new IllegalArgumentException("amount must not be null");
            }

            String orderId = order.getId();
            if (orderId != null) {
                intent.putExtra(Intents.EXTRA_ORDER_ID, orderId);
                //If no order id were passed to EXTRA_ORDER_ID a new empty order would be generated for the payment
            }

            intent.putExtra(Intents.EXTRA_CARD_ENTRY_METHODS, Intents.CARD_ENTRY_METHOD_ALL);
            intent.putExtra(Intents.EXTRA_CARD_DATA_MESSAGE, Label.getLabel("clover_payment_screen_message"));

            dumpIntentLog(intent);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, creditCardIntentID);
            } else {
                SystemUtil.showErrorToast(CloverPaymentActivity.this, Label.getLabel("clover_payment_app_missing_error"));
                throw new IllegalArgumentException("No Activity found to respond to intent: " + Intents.ACTION_SECURE_PAY);
            }

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            SystemUtil.showErrorToast(getContext(), Label.getLabel("clover_payment_unknown_error"));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == creditCardIntentID) {
            if (resultCode == RESULT_OK) {
                Payment payment = data.getParcelableExtra(Intents.EXTRA_PAYMENT);
                if (payment != null) {
                    if (postModel != null) {
                        processPayment(payment, postModel);
                    } else {
                        processPayment(payment);
                    }
                } else {
                    logPaymentFail("Payment object is null, unable to obtain payment results but received a Success Result for payment intent", true);
                }
            } else if (resultCode == RESULT_CANCELED) {
                SystemUtil.showErrorToast(getContext(), Label.getLabel("clover_payment_canceled"));
                setResult(resultCode);
                finish();

            } else {
                SystemUtil.showErrorToast(getContext(), Label.getLabel("clover_payment_failed"));
                logPaymentFail("Clover payment was not successfully processed", false);
                setResult(resultCode);
                finish();
            }
        }
    }

    private void processPayment(Payment payment) {
        try {
            Gson gson = new Gson();
            String jsonString = payment.getJSONObject().toString();
            CloverPaymentDTO cloverPayment = gson.fromJson(jsonString, CloverPaymentDTO.class);
            CloverCardTransactionInfo transactionInfo = cloverPayment.getCloverCardTransactionInfo();

//            PaymentObject paymentObject = new PaymentObject();
//            paymentObject.setType(PaymentType.credit_card);
//            paymentObject.setExecution(PaymentExecution.clover);
//            paymentObject.setAmount(amountDouble);
//            paymentObject.setCreditCard(getCreditCardModel(transactionInfo));
//
//            postModel = new PaymentPostModel();
//            postModel.setAmount(amountDouble);
//            postModel.addPaymentMethod(paymentObject);

            IntegratedPaymentLineItem paymentLineItem = new IntegratedPaymentLineItem();
            paymentLineItem.setAmount(amountDouble);
            paymentLineItem.setItemType(IntegratedPaymentLineItem.TYPE_UNAPPLIED);
            paymentLineItem.setDescription("Unapplied Amount");

            IntegratedPaymentPostModel postModel = new IntegratedPaymentPostModel();
            postModel.setExecution(IntegratedPaymentPostModel.EXECUTION_CLOVER);
            postModel.setCardData(getCreditCardModel(transactionInfo));
            postModel.setAmount(amountDouble);
            postModel.addLineItem(paymentLineItem);

            IntegratedPaymentMetadata postModelMetadata = postModel.getMetadata();
            postModelMetadata.setAppointmentId(appointmentId);

            postModel.setTransactionResponse(gson.fromJson(jsonString, JsonObject.class));

            postPayment(gson.toJson(postModel), payment);
        } catch (JsonSyntaxException jsx) {
            logPaymentFail("Unable to parse Payment", true, payment.getJSONObject(), jsx.getMessage());
        }
    }

    private void processPayment(Payment payment, IntegratedPaymentPostModel postModel) {
        try {
            Gson gson = new Gson();
            String jsonString = payment.getJSONObject().toString();
            CloverPaymentDTO cloverPayment = gson.fromJson(jsonString, CloverPaymentDTO.class);
            CloverCardTransactionInfo transactionInfo = cloverPayment.getCloverCardTransactionInfo();
            IntegratedPaymentCardData creditCardModel = getCreditCardModel(transactionInfo);

            postModel.setTransactionResponse(gson.fromJson(jsonString, JsonObject.class));
            postModel.setExecution(IntegratedPaymentPostModel.EXECUTION_CLOVER);

//            for (PaymentObject paymentObject : postModel.getPaymentObjects()) {
//                paymentObject.setType(PaymentType.credit_card);
//                paymentObject.setExecution(PaymentExecution.clover);
//                paymentObject.setCreditCard(creditCardModel);
//            }

            postModel.setCardData(creditCardModel);
            IntegratedPaymentMetadata postModelMetadata = postModel.getMetadata();
            postModelMetadata.setAppointmentId(appointmentId);

            postPayment(gson.toJson(postModel), payment);
        } catch (JsonSyntaxException jsx) {
            logPaymentFail("Unable to parse Payment", true, payment.getJSONObject(), jsx.getMessage());
        }
    }

    private void postPayment(String paymentModelJson, Payment payment) {
        Map<String, String> queries = new HashMap<>();
        queries.put("patient_id", patientBalance.getBalances().get(0).getMetadata().getPatientId());//todo this wont work for patient mode prepayments.. needs work
        if(appointmentId != null){
            queries.put("appointment_id", appointmentId);
        }

        Map<String, String> header = new HashMap<>();
        header.put("transition", "true");

        Gson gson = new Gson();
        TransitionDTO transitionDTO = gson.fromJson(paymentTransitionString, TransitionDTO.class);
        getWorkflowServiceHelper().execute(transitionDTO, getMakePaymentCallback(payment), paymentModelJson, queries, header);

    }

    private IntegratedPaymentCardData getCreditCardModel(CloverCardTransactionInfo transactionInfo) {
        IntegratedPaymentCardData creditCardModel = new IntegratedPaymentCardData();
        creditCardModel.setCardType(transactionInfo.getCardType());
        creditCardModel.setCardNumber(transactionInfo.getLast4());
        creditCardModel.setToken(transactionInfo.getToken());
        creditCardModel.setTokenizationService(IntegratedPaymentCardData.TOKENIZATION_CLOVER);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMyy", Locale.US);
        if (transactionInfo.getVaultedCard() != null) {
            creditCardModel.setExpiryDate(transactionInfo.getVaultedCard().getExpirationDate(dateFormat.format(calendar.getTime())));
            creditCardModel.setNameOnCard(transactionInfo.getVaultedCard().getCardholderName(patientBalance.getDemographics().getPayload().getPersonalDetails().getFullName()));
        } else {
            creditCardModel.setExpiryDate(dateFormat.format(calendar.getTime()));
            creditCardModel.setNameOnCard(patientBalance.getDemographics().getPayload().getPersonalDetails().getFullName());
        }

//        PaymentsCreditCardBillingInformationDTO billingInformation = new PaymentsCreditCardBillingInformationDTO();
//        billingInformation.setSameAsPatient(true);
//        creditCardModel.setBillingInformation(billingInformation);

        return creditCardModel;
    }


    private WorkflowServiceCallback getMakePaymentCallback(final Payment payment) {
        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                Intent intent = getIntent();
                intent.putExtra(CarePayConstants.CLOVER_PAYMENT_SUCCESS_INTENT_DATA, workflowDTO.toString());
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                System.out.print(exceptionMessage);
                logPaymentFail("Failed to reach make payment endpoint", true, payment.getJSONObject(), exceptionMessage);

            }
        };
    }


    private List<LineItem> getLineItems() {
        List<LineItem> lineItems = new LinkedList<>();
        for (PaymentLineItem paymentLineItem : paymentLineItems) {
            LineItem item = new LineItem();
            item.setName(paymentLineItem.getDescription());
            item.setPrice(Math.round(paymentLineItem.getAmount() * 100D));
            lineItems.add(item);
        }
        return lineItems;
    }

    /**
     * Dump intent.
     *
     * @param intent the intent
     */
    public static void dumpIntentLog(Intent intent) {

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();
            Log.d(TAG, "Dumping Intent start");
            while (it.hasNext()) {
                String key = it.next();
                Log.e(TAG, "[" + key + "=" + bundle.get(key) + "]");
            }
            Log.e(TAG, "Dumping Intent end");
        }
    }

    private void logPaymentFail(String message, boolean paymentSuccess){
        logPaymentFail(message, paymentSuccess, null, null);
    }

    private void logPaymentFail(String message, boolean paymentSuccess, Object paymentJson, String error) {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("Successful Payment", paymentSuccess);
        eventMap.put("Fail Reason", message);
        eventMap.put("Amount", amountDouble);
        eventMap.put("Patient Id", patientBalance.getBalances().get(0).getMetadata().getPatientId());
        eventMap.put("Practice Id", patientBalance.getBalances().get(0).getMetadata().getPracticeId());
        eventMap.put("Practice Mgmt", patientBalance.getBalances().get(0).getMetadata().getPracticeMgmt());

        Gson gson = new Gson();
        eventMap.put("Post Model", gson.toJson(postModel));

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

        NewRelic.recordCustomEvent("CloverPaymentFail", eventMap);

        if(paymentSuccess){
            //sent to Pay Queue API endpoint
            queuePayment(
                    amountDouble,
                    postModel,
                    patientBalance.getBalances().get(0).getMetadata().getPatientId(),
                    patientBalance.getBalances().get(0).getMetadata().getPracticeId(),
                    patientBalance.getBalances().get(0).getMetadata().getPracticeMgmt(),
                    paymentJson.toString(),
                    error);

            setResult(CarePayConstants.PAYMENT_RETRY_PENDING_RESULT_CODE);
            finish();

        }
    }

    private void queuePayment(double amount, IntegratedPaymentPostModel postModel, String patientID, String practiceId, String practiceMgmt, String paymentJson, String errorMessage){
        if(postModel!=null){
//            for (PaymentObject paymentObject : postModel.getPaymentObjects()) {
//                if(paymentObject.getType() == null) {
//                    paymentObject.setType(PaymentType.credit_card);
//                }
//                if(paymentObject.getExecution() == null) {
//                    paymentObject.setExecution(PaymentExecution.clover);
//                }
//                if(paymentObject.getCreditCard() == null){
//                    paymentObject.setCreditCard(new CreditCardModel());
//                }
//            }

            if(postModel.getExecution() == null){
                postModel.setExecution(IntegratedPaymentPostModel.EXECUTION_CLOVER);
            }
            if(postModel.getCardData() == null){
                postModel.setCardData(new IntegratedPaymentCardData());
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
        CloverQueuePaymentRecord paymentRecord = new CloverQueuePaymentRecord();
        paymentRecord.setPatientID(patientID);
        paymentRecord.setPracticeID(practiceId);
        paymentRecord.setPracticeMgmt(practiceMgmt);
        paymentRecord.setQueueTransition(queueTransitionString);
        paymentRecord.setUsername(getApplicationMode().getUserPracticeDTO().getUserName());

        String paymentModelJsonEnc = EncryptionUtil.encrypt(getContext(), paymentModelJson, practiceId);
        paymentRecord.setPaymentModelJsonEnc(paymentModelJsonEnc);

        if(StringUtil.isNullOrEmpty(paymentModelJsonEnc)){
            paymentRecord.setPaymentModelJson(paymentModelJson);
        }
        paymentRecord.save();

        Intent intent = new Intent(getContext(), CloverQueueUploadService.class);
        startService(intent);


    }


}