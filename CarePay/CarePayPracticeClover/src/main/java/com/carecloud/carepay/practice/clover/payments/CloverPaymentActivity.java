package com.carecloud.carepay.practice.clover.payments;

import android.accounts.Account;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.carecloud.carepay.practice.clover.BuildConfig;
import com.carecloud.carepay.practice.clover.R;
import com.carecloud.carepay.practice.clover.models.CloverCardTransactionInfo;
import com.carecloud.carepay.practice.clover.models.CloverPaymentDTO;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsCreditCardBillingInformationDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.CreditCardModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentExecution;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentObject;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentType;
import com.carecloud.carepaylibray.payments.models.postmodel.TokenizationService;
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
import com.clover.sdk.v3.payments.Result;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
    private Order order;
    private Long amountLong = 0L;
    private double amountDouble;
    private String paymentTransitionString ;
    private CloverAuth.AuthResult authResult = null;
    private PaymentPostModel postModel;
    private PatientBalanceDTO patientBalance;

    private PaymentLineItem[] paymentLineItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress);
        try
        {

            Intent intent = getIntent();
            if (intent.hasExtra(CarePayConstants.CLOVER_PAYMENT_AMOUNT)) {
                amountDouble = intent.getDoubleExtra(CarePayConstants.CLOVER_PAYMENT_AMOUNT, 0.00);
                amountLong = (long) (amountDouble * 100);
            }

            if (intent.hasExtra(CarePayConstants.CLOVER_PAYMENT_TRANSITION)) {
                paymentTransitionString = intent.getStringExtra(CarePayConstants.CLOVER_PAYMENT_TRANSITION);

            }
            if (intent.hasExtra(CarePayConstants.CLOVER_PAYMENT_METADATA)) {
                Gson gson = new Gson();
                String patientPaymentMetaDataString = intent.getStringExtra(CarePayConstants.CLOVER_PAYMENT_METADATA);
                patientBalance = gson.fromJson(patientPaymentMetaDataString, PatientBalanceDTO.class);

            }
            Gson gson = new Gson();
            if (intent.hasExtra(CarePayConstants.CLOVER_PAYMENT_LINE_ITEMS)) {
                String lineItemString = intent.getStringExtra(CarePayConstants.CLOVER_PAYMENT_LINE_ITEMS);
                paymentLineItems = gson.fromJson(lineItemString, PaymentLineItem[].class);

            }
            if(intent.hasExtra(CarePayConstants.CLOVER_PAYMENT_POST_MODEL)){
                String paymentPostModelString = intent.getStringExtra(CarePayConstants.CLOVER_PAYMENT_POST_MODEL);
                postModel = gson.fromJson(paymentPostModelString, PaymentPostModel.class);
            }

            if (account == null) {
                {
                    account = CloverAccount.getAccount(this);
                    authenticateCloverAccount();
                }


                // If an account can't be acquired, exit the app
                if (account == null) {
                    SystemUtil.showErrorToast(CloverPaymentActivity.this, getString(R.string.no_account));
                    finish();
                }
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (account == null) {
            {
                account = CloverAccount.getAccount(this);
                authenticateCloverAccount();
            }


            // If an account can't be acquired, exit the app
            if (account == null) {
                SystemUtil.showErrorToast(CloverPaymentActivity.this, getString(R.string.no_account));
                finish();
                return;
            }
        }

    }

    @Override
    public void navigateToWorkflow(WorkflowDTO workflowDTO) {

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
    protected void onPause() {
        disconnect();
        super.onPause();

    }

    private void authenticateCloverAccount() {
        new AsyncTask<Void, String, Void>() {

            @Override
            protected void onProgressUpdate(String... values) {
                String logString = values[0];
                Log.d(TAG, logString);
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    publishProgress("Requesting auth token");
                    authResult = CloverAuth.authenticate(CloverPaymentActivity.this, account);
                    publishProgress("Successfully authenticated as " + account + ".  authToken=" + authResult.authToken + ", authData=" + authResult.authData);
                } catch (Exception e) {
                    publishProgress("Error retrieving merchant info from server" + e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void param) {
                if (authResult != null && authResult.authToken != null && authResult.baseUrl != null) {
                    connect();
                    new OrderAsyncTask().execute();
                }
            }
        }.execute();
    }

    private class OrderAsyncTask extends AsyncTask<Void, Void, Order> {

        @Override
        protected final Order doInBackground(Void... params) {
            Order order = null;

            try {

                if(amountLong != null )
                {
                    // Create a new order
                    order = orderConnector.createOrder(new Order());
                    if(paymentLineItems!=null && paymentLineItems.length > 0){
                        List<LineItem> lineItems = getLineItems();
                        for (LineItem lineItem : lineItems) {
                            orderConnector.addCustomLineItem(order.getId(), lineItem, false);
                        }
                    }else{
                        return null;
                    }
                    // Update local representation of the order
                    order = orderConnector.getOrder(order.getId());
                }


                return order;
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (ClientException e) {
                e.printStackTrace();
            } catch (ServiceException e) {
                e.printStackTrace();
            } catch (BindingException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected final void onPostExecute(Order order) {
            if(order==null){
                setResult(RESULT_CANCELED);
                SystemUtil.showErrorToast(CloverPaymentActivity.this, getString(R.string.payment_cancelled));
                finish();
            }

            // Enables the pay buttons if the order is valid
            if (!isFinishing()) {
                CloverPaymentActivity.this.order = order;
                startSecurePaymentIntent();

            }
        }
    }

    private void startSecurePaymentIntent() {

        new AsyncTask<Void, Void, Result>() {
            @Override
            protected Result doInBackground(Void... params) {


                final Intent intent = new Intent(Intents.ACTION_SECURE_PAY);
                try {
                    if (amountLong != null) {
                        intent.putExtra(Intents.EXTRA_AMOUNT, amountLong);
                    } else {
                        SystemUtil.showErrorToast(getApplicationContext(), getString(R.string.amount_required));
                        throw new IllegalArgumentException("amount must not be null");
                    }


                    String orderId = order.getId();

                    if (orderId != null) {
                        intent.putExtra(Intents.EXTRA_ORDER_ID, orderId);
                        //If no order id were passed to EXTRA_ORDER_ID a new empty order would be generated for the payment
                    }

                    intent.putExtra(Intents.EXTRA_CARD_ENTRY_METHODS, Intents.CARD_ENTRY_METHOD_ALL);
                    intent.putExtra(Intents.EXTRA_CARD_DATA_MESSAGE, "Please swipe your card to complete check in");

                    dumpIntent(intent);

                    if(intent.resolveActivity(getPackageManager()) != null){
                        startActivityForResult(intent, creditCardIntentID);
                    }else{
                        SystemUtil.showErrorToast(CloverPaymentActivity.this, "Unable to find StationPay Application on this Clover device");
                        throw new IllegalArgumentException("No Activity found to respont to intent: "+Intents.ACTION_SECURE_PAY);
                    }

                }catch (IllegalArgumentException iae){
                    iae.printStackTrace();
                }
                catch (Exception e) {

                    e.printStackTrace();
                    SystemUtil.showErrorToast(CloverPaymentActivity.this, "An unknown error has occured while launching the Payment Intent");
                }

                return null;
            }
        }.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == creditCardIntentID) {
            if (resultCode == RESULT_OK) {

                Payment payment = data.getParcelableExtra(Intents.EXTRA_PAYMENT);
                if (payment != null) {
                    if(postModel!=null){
                        processPayment(payment, postModel);
                    }else {
                        processPayment(payment);
                    }
                }
            } else if(resultCode == RESULT_CANCELED) {
                String manufacturer = Build.MANUFACTURER;
                if(BuildConfig.DEBUG && manufacturer.equals("unknown")) {
                    fakePaymentResponse();
                }else {
                    SystemUtil.showErrorToast(CloverPaymentActivity.this, getString(R.string.payment_cancelled));
                    setResult(resultCode);
                    finish();
                }
            } else {
                SystemUtil.showErrorToast(CloverPaymentActivity.this, getString(R.string.payment_failed));
                setResult(resultCode);
                finish();
            }
        }
    }

    private void processPayment(Payment payment){
        Gson gson = new Gson();
        String jsonString = payment.getJSONObject().toString();
        CloverPaymentDTO cloverPayment = gson.fromJson(jsonString, CloverPaymentDTO.class);
        CloverCardTransactionInfo transactionInfo = cloverPayment.getCloverCardTransactionInfo();

        PaymentObject paymentObject = new PaymentObject();
        paymentObject.setType(PaymentType.credit_card);
        paymentObject.setExecution(PaymentExecution.clover);
        paymentObject.setAmount(amountDouble);
        paymentObject.setCreditCard(getCreditCardModel(transactionInfo));

//        paymentObject.setBankAccountToken(paymentTransition.getMetadata());//TODO

        PaymentPostModel paymentPostModel = new PaymentPostModel();
        paymentPostModel.setAmount(amountDouble);
        paymentPostModel.addPaymentMethod(paymentObject);

//        TransactionResponse transactionResponse = new TransactionResponse();
//        transactionResponse.setTransactionID(cloverPayment.getId());
//        transactionResponse.setResponse(payment.getJSONObject());
        postModel.setTransactionResponse(gson.fromJson(jsonString, JsonObject.class));

        if(paymentPostModel.isPaymentModelValid()){
            postPayment(gson.toJson(paymentPostModel));
        }else{
            SystemUtil.showErrorToast(CloverPaymentActivity.this, getString(R.string.payment_failed));
            finish();
        }
    }

    private void processPayment(Payment payment, PaymentPostModel postModel){
        Gson gson = new Gson();
        String jsonString = payment.getJSONObject().toString();
        CloverPaymentDTO cloverPayment = gson.fromJson(jsonString, CloverPaymentDTO.class);
        CloverCardTransactionInfo transactionInfo = cloverPayment.getCloverCardTransactionInfo();
        CreditCardModel creditCardModel = getCreditCardModel(transactionInfo);

//        TransactionResponse transactionResponse = new TransactionResponse();
//        transactionResponse.setTransactionID(cloverPayment.getId());
//        transactionResponse.setResponse(payment.getJSONObject());

        postModel.setTransactionResponse(gson.fromJson(jsonString, JsonObject.class));

        for(PaymentObject paymentObject : postModel.getPaymentObjects()){
            paymentObject.setType(PaymentType.credit_card);
            paymentObject.setExecution(PaymentExecution.clover);
            paymentObject.setCreditCard(creditCardModel);
        }


        if(postModel.isPaymentModelValid()){
            postPayment(gson.toJson(postModel));
        }else{
            SystemUtil.showErrorToast(CloverPaymentActivity.this, getString(R.string.payment_failed));
            finish();
        }

    }

    private void postPayment(String paymentModelJson){
        Gson gson = new Gson();
        Map<String, String> queries = new HashMap<>();
        queries.put("patient_id", patientBalance.getBalances().get(0).getMetadata().getPatientId());

        Map<String, String> header = new HashMap<>();
        header.put("transition", "true");

        TransitionDTO transitionDTO = gson.fromJson(paymentTransitionString, TransitionDTO.class);
        getWorkflowServiceHelper().execute(transitionDTO, makePaymentCallback, paymentModelJson, queries, header);

    }

    private CreditCardModel getCreditCardModel(CloverCardTransactionInfo transactionInfo){
        CreditCardModel creditCardModel = new CreditCardModel();
        creditCardModel.setCardType(transactionInfo.getCardType());
        creditCardModel.setCardNumber(transactionInfo.getLast4());
        creditCardModel.setToken(transactionInfo.getToken());
        creditCardModel.setTokenizationService(TokenizationService.clover);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMyy");
        creditCardModel.setExpiryDate(transactionInfo.getVaultedCard().getExpirationDate(dateFormat.format(calendar.getTime())));
        creditCardModel.setNameOnCard(transactionInfo.getVaultedCard().getCardholderName(patientBalance.getDemographics().getPayload().getPersonalDetails().getFullName()));

        PaymentsCreditCardBillingInformationDTO billingInformation = new PaymentsCreditCardBillingInformationDTO();
        billingInformation.setSameAsPatient(true);
        creditCardModel.setBillingInformation(billingInformation);

        return creditCardModel;
    }

    private void fakePaymentResponse(){
        CreditCardModel creditCardModel = new CreditCardModel();
        creditCardModel.setCardType("MASTERCARD");
        creditCardModel.setCardNumber("1212");
        creditCardModel.setExpiryDate("1020");
        creditCardModel.setNameOnCard("Leo Menendez");
        creditCardModel.setToken("FDLKN4LN44KBJL4"+ System.currentTimeMillis()%5);

        PaymentsCreditCardBillingInformationDTO billingInformation = new PaymentsCreditCardBillingInformationDTO();
        billingInformation.setSameAsPatient(true);
        creditCardModel.setBillingInformation(billingInformation);

        PaymentObject paymentObject = new PaymentObject();
        paymentObject.setType(PaymentType.credit_card);
        paymentObject.setExecution(PaymentExecution.clover);
        paymentObject.setAmount(5.00);
        paymentObject.setCreditCard(creditCardModel);


        PaymentPostModel paymentPostModel = new PaymentPostModel();
        paymentPostModel.setAmount(5.00);
        paymentPostModel.addPaymentMethod(paymentObject);

//        TransactionResponse transactionResponse = new TransactionResponse();
//        transactionResponse.setTransactionID(String.valueOf(System.currentTimeMillis()));
//        transactionResponse.setResponse(new JSONObject());
//        paymentPostModel.setTransactionResponse(new JSONObject());

        Gson gson = new Gson();
        if(paymentPostModel.isPaymentModelValid()){
            postPayment(gson.toJson(paymentPostModel));
        }else{
            SystemUtil.showErrorToast(CloverPaymentActivity.this, getString(R.string.payment_failed));
            finish();
        }


    }


    /**
     * The Make payment callback.
     */
    WorkflowServiceCallback makePaymentCallback = new WorkflowServiceCallback() {
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
//            PracticeNavigationHelper.navigateToWorkflow(CloverPaymentActivity.this, workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            setResult(RESULT_CANCELED);
            System.out.print(exceptionMessage);
            SystemUtil.showErrorToast(CloverPaymentActivity.this, getString(R.string.payment_failed));
            finish();
        }
    };

    private List<LineItem> getLineItems() {
        List<LineItem> lineItems = new LinkedList<>();
        for (PaymentLineItem paymentLineItem : paymentLineItems) {
            LineItem item = new LineItem();
            item.setName(paymentLineItem.getDescription());
            item.setPrice((long) (paymentLineItem.getAmount() * 100));
            item.setNote(paymentLineItem.getMetadata().toString());
            lineItems.add(item);
        }
        return lineItems;
    }

    /**
     * Dump intent.
     *
     * @param intent the intent
     */
    public static void dumpIntent(Intent intent) {

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

}