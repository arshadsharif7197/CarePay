package com.carecloud.carepay.practice.clover.payments;

import android.accounts.Account;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IInterface;
import android.os.RemoteException;
import android.util.Log;

import com.carecloud.carepay.practice.clover.R;
import com.carecloud.carepay.practice.clover.models.CloverPaymentDTO;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.RestCallService;
import com.carecloud.carepay.service.library.RestCallServiceHelper;
import com.carecloud.carepay.service.library.ServiceGenerator;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.customcomponents.CustomMessageToast;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryItem;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryItemPayload;
import com.carecloud.carepaylibray.payments.models.refund.RefundLineItem;
import com.carecloud.carepaylibray.payments.models.refund.RefundPostModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.clover.connector.sdk.v3.PaymentConnector;
import com.clover.connector.sdk.v3.PaymentV3Connector;
import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.util.CloverAuth;
import com.clover.sdk.v1.Intents;
import com.clover.sdk.v1.ServiceConnector;
import com.clover.sdk.v1.printer.Category;
import com.clover.sdk.v1.printer.Printer;
import com.clover.sdk.v1.printer.PrinterConnector;
import com.clover.sdk.v1.printer.job.PrintJob;
import com.clover.sdk.v1.printer.job.PrintJobsConnector;
import com.clover.sdk.v1.printer.job.StaticReceiptPrintJob;
import com.clover.sdk.v3.employees.EmployeeConnector;
import com.clover.sdk.v3.order.LineItem;
import com.clover.sdk.v3.order.Order;
import com.clover.sdk.v3.order.OrderConnector;
import com.clover.sdk.v3.payments.Credit;
import com.clover.sdk.v3.payments.Refund;
import com.clover.sdk.v3.remotepay.RefundPaymentRequest;
import com.clover.sdk.v3.remotepay.RefundPaymentResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;


/**
 * The type Clover payment activity.
 */
public class CloverRefundActivity extends BaseActivity {
    /**
     * The constant refundIntentID.
     */
    public static final int refundIntentID = 444;
    private static final String TAG = CloverRefundActivity.class.getName();
    private static final String CLOVER_MERCHANT_SETTINGS_URL = "%s/v3/merchants/%s/properties?access_token=%s";


    private Account account;
    private OrderConnector orderConnector;
    private PaymentConnector paymentConnector;
    private PrinterConnector printerConnector;
    private EmployeeConnector employeeConnector;
    private CloverAuth.AuthResult authResult;

    private Long amountLong = 0L;
    private List<RefundLineItem> refundLineItems = new ArrayList<>();
    private PaymentHistoryItem historyItem;
    private TransitionDTO refundTransition;
    private String paymentId;
    private String orderId;


    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        setContentView(R.layout.dialog_progress);

        Intent intent = getIntent();
        double amountDouble = intent.getDoubleExtra(CarePayConstants.CLOVER_PAYMENT_AMOUNT, 0D);
        amountLong = (long) (amountDouble * 100);

        Gson gson = new Gson();
        String lineItemString = intent.getStringExtra(CarePayConstants.CLOVER_PAYMENT_LINE_ITEMS);
        refundLineItems = gson.fromJson(lineItemString, new TypeToken<List<RefundLineItem>>(){}.getType());

        String transactionResponse = intent.getStringExtra(CarePayConstants.CLOVER_PAYMENT_TRANSACTION_RESPONSE);
        if(transactionResponse != null){
            CloverPaymentDTO cloverPayment = gson.fromJson(transactionResponse, CloverPaymentDTO.class);
            paymentId = cloverPayment.getId();
            orderId = cloverPayment.getOrder().getId();
        }

        String historyItemString = intent.getStringExtra(CarePayConstants.CLOVER_PAYMENT_HISTORY_ITEM);
        if(historyItemString != null){
            historyItem = gson.fromJson(historyItemString, PaymentHistoryItem.class);
        }

        String refundTransitionString = intent.getStringExtra(CarePayConstants.CLOVER_PAYMENT_TRANSITION);
        if(refundTransitionString != null){
            refundTransition = gson.fromJson(refundTransitionString, TransitionDTO.class);
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
                SystemUtil.showErrorToast(CloverRefundActivity.this, getString(R.string.no_account));
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
            printerConnector = new PrinterConnector(this, account, null);
            printerConnector.connect();
            employeeConnector = new EmployeeConnector(this, account, null);
            employeeConnector.connect();
            connectPaymentService();
        }
    }

    // Disconnects from the connectors
    private void disconnect() {
        if (orderConnector != null) {
            orderConnector.disconnect();
            orderConnector = null;
        }
        if(printerConnector != null){
            printerConnector.disconnect();
            printerConnector = null;
        }
        if(employeeConnector != null){
            employeeConnector.disconnect();
            employeeConnector = null;
        }
        if (paymentConnector != null) {
            paymentConnector.disconnect();
            paymentConnector = null;
        }
    }

    private PaymentV3Connector.PaymentServiceListener paymentServiceListener = new RefundServiceAdapter() {
        @Override
        public void onRefundPaymentResponse(RefundPaymentResponse response) {
            if(response.getSuccess()){
                processRefund(response);
            }else{
                new CustomMessageToast(CloverRefundActivity.this, response.getMessage(), CustomMessageToast.NOTIFICATION_TYPE_ERROR).show();
                setResult(RESULT_CANCELED);
                finish();
            }
        }

    };


    private void connectPaymentService(){
        if(paymentConnector == null){
            paymentConnector = new PaymentConnector(this, account, new ServiceConnector.OnServiceConnectedListener() {

                @Override
                public void onServiceConnected(ServiceConnector<? extends IInterface> connector) {
                    Log.d(TAG, "onServiceConnected " + connector);
                    paymentConnector.addPaymentServiceListener(paymentServiceListener);
                    startRefund();
                }

                @Override
                public void onServiceDisconnected(ServiceConnector<? extends IInterface> connector) {
                    Log.d(TAG, "onServiceDisconnected " + connector);

                }
            });
            paymentConnector.connect();

        }else if(!paymentConnector.isConnected()){
            paymentConnector.connect();
        }
    }

    @Override
    protected void onDestroy() {
        disconnect();
        super.onDestroy();

    }

    private void onCloverAuthenticated(){
        connect();
    }

    public void setAuthResult(CloverAuth.AuthResult authResult) {
        this.authResult = authResult;
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
                    authResult = CloverAuth.authenticate(CloverRefundActivity.this, account);
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
                    setAuthResult(authResult);
                }else {
                    SystemUtil.showErrorToast(getContext(), Label.getLabel("clover_account_not_authorized"));
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

    private void startRefund(){
        RefundPaymentRequest refundPaymentRequest = new RefundPaymentRequest();
        refundPaymentRequest.setPaymentId(paymentId);
        refundPaymentRequest.setOrderId(orderId);
        refundPaymentRequest.setAmount(amountLong);
        refundPaymentRequest.setRequestId(Double.toString(Math.random() * 100000));

        try {
            paymentConnector.getService().refundPayment(refundPaymentRequest);
        } catch (RemoteException re) {
            re.printStackTrace();
        }
    }


    private void processRefund(RefundPaymentResponse response){
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("patient_id", historyItem.getPayload().getMetadata().getPatientId());

        RefundPostModel refundPostModel = new RefundPostModel();
        refundPostModel.setPaymentRequestId(historyItem.getPayload().getDeepstreamRecordId());
        refundPostModel.setRefundLineItems(refundLineItems);

        Gson gson = new Gson();
        String jsonString = response.getJSONObject().toString();
        refundPostModel.setTransactionResponse(gson.fromJson(jsonString, JsonObject.class));

        getWorkflowServiceHelper().execute(refundTransition, getRefundCallback(response), gson.toJson(refundPostModel), queryMap);

    }


    private WorkflowServiceCallback getRefundCallback(final RefundPaymentResponse response) {
        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                PaymentsModel refundPaymentModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
                PaymentHistoryItemPayload refundPayload = refundPaymentModel.getPaymentPayload().getPatientRefund();
                historyItem.setPayload(refundPayload);

                new CustomMessageToast(getContext(), Label.getLabel("payment_refund_success"), CustomMessageToast.NOTIFICATION_TYPE_SUCCESS).show();//todo need to figure out if refund was actualy successful

                Intent resultIntent = new Intent();
                resultIntent.putExtra(CarePayConstants.CLOVER_PAYMENT_SUCCESS_INTENT_DATA, DtoHelper.getStringDTO(historyItem));
                resultIntent.putExtra(CarePayConstants.CLOVER_REFUND_INTENT_FLAG, true);
                setResult(RESULT_OK, resultIntent);

                printReceipt(response);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);

                setResult(RESULT_CANCELED);

                printReceipt(response);

                //TODO retry logic
            }
        };
    }


//region naked refund
/*
    private void startRefundIntent(Order order) {
        Intent intent = new Intent(Intents.ACTION_SECURE_PAY);
        intent.putExtra(Intents.EXTRA_TRANSACTION_TYPE, Intents.TRANSACTION_TYPE_CREDIT);
        try {
            if (amountLong != null) {
                intent.putExtra(Intents.EXTRA_AMOUNT, amountLong*-1);
            } else {
                SystemUtil.showErrorToast(getApplicationContext(), Label.getLabel("clover_payment_amount_error"));
                throw new IllegalArgumentException("amount must not be null");
            }

            String orderId = order.getId();
            if (orderId != null) {
                intent.putExtra(Intents.EXTRA_ORDER_ID, orderId);
                //If no order id were passed to EXTRA_ORDER_ID a new empty order would be generated for the payment
            }

            dumpIntentLog(intent);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, refundIntentID);
            } else {
                SystemUtil.showErrorToast(CloverRefundActivity.this, Label.getLabel("clover_payment_app_missing_error"));
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
        if (requestCode == refundIntentID) {
            if (resultCode == RESULT_OK) {
                Payment payment = data.getParcelableExtra(Intents.EXTRA_PAYMENT);

            } else if (resultCode == RESULT_CANCELED) {
                SystemUtil.showErrorToast(getContext(), Label.getLabel("clover_payment_canceled"));
                setResult(resultCode);
                finish();

            } else {
                SystemUtil.showErrorToast(getContext(), Label.getLabel("clover_payment_failed"));
                setResult(resultCode);
                finish();
            }
        }
    }
*/

//endregion


    private List<Credit> getCreditItems(){
        List<Credit> credits = new LinkedList<>();
        for(RefundLineItem refundlineItem : refundLineItems){
            Credit credit = new Credit();
            credit.setAmount(Math.round(refundlineItem.getAmount() * 100D));
            credits.add(credit);
        }
        return credits;
    }


    private void printReceipt(final RefundPaymentResponse response){
        new AsyncTask<Void, Void, String>(){

            @Override
            protected String doInBackground(Void... params) {
                PrintJobsConnector printJobsConnector = new PrintJobsConnector(CloverRefundActivity.this);
                Printer printer;
                Order order = null;
                boolean shouldPrint = false;
                try {
                    if(authResult != null) {
                        RestCallService service = ServiceGenerator.getInstance().createService(RestCallService.class);
                        Call<JsonElement> call = service.executeGet(String.format(CLOVER_MERCHANT_SETTINGS_URL, authResult.baseUrl, authResult.merchantId, authResult.authToken));
                        Response<JsonElement> response = call.execute();
                        if (response.isSuccessful()) {
                            JsonElement jsonResponse = response.body();
                            String autoPrint = RestCallServiceHelper.findErrorElement(jsonResponse, "autoPrint");
                            shouldPrint = Boolean.parseBoolean(autoPrint);
                        }
                    }

                    order = orderConnector.getOrder(orderId);
                    if(order.getRefunds() == null){
                        order.setRefunds(new ArrayList<Refund>());
                    }
                    if(order.getRefunds().isEmpty() || !containsRefund(order, response.getRefund())){
                        order.getRefunds().add(response.getRefund());
                    }

//                        order.setCredits(getCreditItems());

                    for(LineItem lineItem : order.getLineItems()){
                        if(containsLineItem(lineItem)) {
                            lineItem.setRefunded(true);
                        }
                    }

                    Log.d(TAG, order.getJSONObject().toString());

                    if(shouldPrint=true) {
                        printer = printerConnector.getPrinters(Category.RECEIPT).get(0);

                        PrintJob printJob = new StaticReceiptPrintJob.Builder().order(order).build();
                        printJobsConnector.print(printer, printJob);
                    }else{
                        promptPrinting(order);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if(order != null){
                        promptPrinting(order);
                    }
                }finally {
                    finish();
                }
                return null;
            }
        }.execute();

    }

    private void promptPrinting(Order order){
        Intent intent = new Intent(Intents.ACTION_START_PRINT_RECEIPTS);

        intent.putExtra(Intents.EXTRA_ORDER_ID, order.getId());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            String message = "No Activity found to respond to intent: " + Intents.ACTION_START_PRINT_RECEIPTS;
            Log.d(TAG, message);
        }
    }


    boolean containsLineItem(LineItem lineItem){
        Iterator<RefundLineItem> iterator = refundLineItems.iterator();
        while (iterator.hasNext()){
            RefundLineItem refundLineItem = iterator.next();
            if(refundLineItem.getDescription()!=null &&
                    refundLineItem.getDescription().equals(lineItem.getName()) &&
                    lineItem.getPrice() >= refundLineItem.getAmount() * 100){
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    boolean containsRefund(Order order, Refund checkRefund){
        if(order == null || checkRefund == null){
            return false;
        }
        for(Refund refund : order.getRefunds()){
            if(refund.getId().equals(checkRefund.getId())){
                return true;
            }
        }
        return false;
    }

}