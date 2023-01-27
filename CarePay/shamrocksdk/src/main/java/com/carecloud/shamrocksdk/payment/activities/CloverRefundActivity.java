package com.carecloud.shamrocksdk.payment.activities;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IInterface;
import android.os.RemoteException;
import android.util.Log;

import com.carecloud.shamrocksdk.payment.DeviceRefund;
import com.carecloud.shamrocksdk.payment.adapters.ConnectorServiceAdapter;
import com.carecloud.shamrocksdk.payment.interfaces.AsyncRefundConnectorInterface;
import com.carecloud.shamrocksdk.payment.interfaces.PaymentActionCallback;
import com.carecloud.shamrocksdk.payment.models.PaymentRequest;
import com.carecloud.shamrocksdk.payment.models.RefundRequest;
import com.carecloud.shamrocksdk.payment.models.RequestError;
import com.carecloud.shamrocksdk.payment.models.clover.CloverPaymentDTO;
import com.carecloud.shamrocksdk.payment.models.defs.StateDef;
import com.carecloud.shamrocksdk.services.ServiceGenerator;
import com.carecloud.shamrocksdk.services.ServiceHelper;
import com.carecloud.shamrocksdk.services.ServiceInterface;
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
import com.clover.sdk.v3.payments.Refund;
import com.clover.sdk.v3.remotepay.RefundPaymentRequest;
import com.clover.sdk.v3.remotepay.RefundPaymentResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import io.deepstream.Record;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for handling Clover Refunds and posting back results to Deep Stream
 */

public class CloverRefundActivity extends Activity implements AsyncRefundConnectorInterface {
    private static final String TAG = CloverRefundActivity.class.getName();
    private static final String CLOVER_MERCHANT_SETTINGS_URL = "%s/v3/merchants/%s/properties?access_token=%s";
    private static final int MAX_DEEPSTREAM_RETRIES = 3;
    private static final String EXTRA_REFUND_REQUEST = "refund_request";
    private static final String EXTRA_TRANSACTION_RESPONSE = "payment_transaction_response";
    private static final int MAX_CLOVER_QUERY_RETRY = 3;
    private static final int CLOVER_QUERY_BACKOFF_FACTOR_MS = 500;
    private static final String CLOVER_REFUNDS_QUERY_URL = "%s/v3/merchants/%s/orders/%s?expand=refunds&access_token=%s";
    private static final int CLOVER_REFUND_TIME_OFFSET_MS = 10 * 1000;//millis

    private static Record processingPaymentRecord;
    private static PaymentActionCallback currentPaymentCallback;

    /**
     * Create new instance of the Activity and start the intent
     *
     * @param context        context
     * @param record         DeepStream Payment Record
     * @param callback       action callback
     * @param paymentRequest original payment request
     */
    public static void newInstance(Context context, Record record, PaymentActionCallback callback, PaymentRequest paymentRequest) {
        processingPaymentRecord = record;
        currentPaymentCallback = callback;

        Gson gson = new Gson();
        Intent intent = new Intent(context, CloverRefundActivity.class);
        intent.putExtra(EXTRA_REFUND_REQUEST, gson.toJson(record.get()));
        intent.putExtra(EXTRA_TRANSACTION_RESPONSE, gson.toJson(paymentRequest.getTransactionResponse()));
        context.startActivity(intent);

    }

    private Account account;
    private OrderConnector orderConnector;
    private PaymentConnector paymentConnector;
    private PrinterConnector printerConnector;
    private EmployeeConnector employeeConnector;
    private CloverAuth.AuthResult authResult;

    private Handler handler;

    private Long amountLong = 0L;
    //    private List<RefundLineItem> refundLineItems = new ArrayList<>();
    private String paymentId;
    private String orderId;


    private RefundRequest refundRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler();

        Gson gson = new Gson();
        Intent intent = getIntent();
        String refundRequestString = intent.getStringExtra(EXTRA_REFUND_REQUEST);
        if (refundRequestString != null) {
            refundRequest = gson.fromJson(refundRequestString, RefundRequest.class);
            amountLong = Math.round(refundRequest.getRefundAmount() * 100D);
        }

        String transactionResponse = intent.getStringExtra(EXTRA_TRANSACTION_RESPONSE);
        if (transactionResponse != null) {
            CloverPaymentDTO cloverPayment = gson.fromJson(transactionResponse, CloverPaymentDTO.class);
            if (cloverPayment != null) {
                paymentId = cloverPayment.getId();
                orderId = cloverPayment.getOrder().getId();
            } else {
                currentPaymentCallback.onPaymentFailed(processingPaymentRecord.name(), "No Clover Payment found");
                return;
            }
        }

        account = CloverAccount.getAccount(this);
        if (account != null) {
            authenticateCloverAccount();
        }

    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        Gson gson = new Gson();
        refundRequest.setState(StateDef.STATE_CANCELED);
        setPaymentRecord(gson.toJsonTree(refundRequest));
        currentPaymentCallback.onPaymentCanceled(processingPaymentRecord.name(), "User canceled payment");
        disconnect();
        finish();
    }

    @Override
    protected void onDestroy() {
        disconnect();
        processingPaymentRecord = null;
        currentPaymentCallback = null;
        super.onDestroy();
    }

    private void authenticateCloverAccount() {
        new AuthAsyncTask(this).execute();
/*
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
                } else {
                    Gson gson = new Gson();
                    refundRequest.setState(StateDef.STATE_CANCELED);
                    setPaymentRecord(gson.toJsonTree(refundRequest));
                    currentPaymentCallback.onPaymentFailed(processingPaymentRecord.name(), "Clover account not authorized");
                    finish();
                }
            }
        }.execute();
*/
    }

    private void onCloverAuthenticated() {
        connect();
    }

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

    private void disconnect() {
        if (orderConnector != null) {
            orderConnector.disconnect();
            orderConnector = null;
        }
        if (printerConnector != null) {
            printerConnector.disconnect();
            printerConnector = null;
        }
        if (employeeConnector != null) {
            employeeConnector.disconnect();
            employeeConnector = null;
        }
        if (paymentConnector != null) {
            paymentConnector.disconnect();
            paymentConnector = null;
        }
    }

    private PaymentV3Connector.PaymentServiceListener paymentServiceListener = new ConnectorServiceAdapter() {
        @Override
        public void onRefundPaymentResponse(RefundPaymentResponse response) {
            if(response == null){
                queryForCompletedRefund(0, System.currentTimeMillis());
                return;
            }
            if (response.getSuccess()) {
                if (response.getRefund() != null) {
                    processRefund(response.getRefund(), 0);
                } else {
                    logRequestError("Refund not available in clover response");
                    queryForCompletedRefund(0, System.currentTimeMillis());
                }
            } else {
                Gson gson = new Gson();
                refundRequest.setState(StateDef.STATE_CANCELED);
                setPaymentRecord(gson.toJsonTree(refundRequest));
                String message = response.getReason();
                switch (response.getResult()) {
                    case CANCEL:
                        if (currentPaymentCallback != null) {
                            currentPaymentCallback.onPaymentCanceled(processingPaymentRecord.name(), message);
                        }
                        break;
                    case FAIL:
                    case UNSUPPORTED:
                    case ERROR:
                    case DUPLICATE:
                    default:
                        if (currentPaymentCallback != null) {
                            message = message + " " + (response.getMessage() != null ?
                                    response.getMessage() : "No response message available");
                            logRequestError(message);
                            currentPaymentCallback.onPaymentFailed(processingPaymentRecord.name(), message);
                        }
                        break;
                }
                Log.e(TAG, message);
                finish();
            }
        }

    };

    private void connectPaymentService() {
        if (paymentConnector == null) {
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

        } else if (!paymentConnector.isConnected()) {
            paymentConnector.connect();
        }
    }

    private void startRefund() {
        refundRequest.setState(StateDef.STATE_WAITING);
        Gson gson = new Gson();
        setPaymentRecord(gson.toJsonTree(refundRequest));
        currentPaymentCallback.onPaymentStarted(processingPaymentRecord.name());

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


    private void processRefund(final Refund refund, final int attempt) {
        Gson gson = new Gson();
        String jsonString = refund.getJSONObject().toString();

        refundRequest.setTransactionResponse(gson.fromJson(jsonString, JsonObject.class));
        refundRequest.setState(StateDef.STATE_CAPTURED);

        try {
            setPaymentRecord(gson.toJsonTree(refundRequest));
            RefundRequest ackRequest = DeviceRefund.getRefundAck(processingPaymentRecord.name());
            if (ackRequest != null &&
                    ackRequest.getState().equals(StateDef.STATE_CAPTURED) &&
                    ackRequest.getTransactionResponse() != null) {
                currentPaymentCallback.onPaymentComplete(processingPaymentRecord.name(), gson.toJsonTree(refundRequest));
                printReceipt(refund);
            } else if (attempt < MAX_DEEPSTREAM_RETRIES) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        processRefund(refund, attempt + 1);
                    }
                }, 1000);
            } else {
                currentPaymentCallback.onPaymentCompleteWithError(processingPaymentRecord.name(), gson.toJsonTree(refundRequest), "Unable to update Payment Request Record in DeepStream");
                printReceipt(refund);
            }
        } catch (WebsocketNotConnectedException wse) {
            wse.printStackTrace();
            currentPaymentCallback.onPaymentCompleteWithError(processingPaymentRecord.name(), gson.toJsonTree(refundRequest), "Unable to update Payment Request Record in DeepStream");
            printReceipt(refund);
        }
    }

    private void logRequestError(String message) {
        Gson gson = new Gson();
        if (refundRequest.getRequestErrors() == null) {
            refundRequest.setRequestErrors(new ArrayList<RequestError>());
        }
        RequestError requestError = new RequestError();
        requestError.setId("Clover Error " + System.currentTimeMillis());
        requestError.setError(message);
        refundRequest.getRequestErrors().add(requestError);
        setPaymentRecord(gson.toJsonTree(refundRequest));
    }

    private void queryForCompletedRefund(final int attempt, final long targetTime) {
        if (attempt < MAX_CLOVER_QUERY_RETRY) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ServiceInterface service = ServiceGenerator.getInstance().createService(ServiceInterface.class);
                    Call<JsonElement> call = service.executeGet(String.format(CLOVER_REFUNDS_QUERY_URL, authResult.baseUrl, authResult.merchantId, orderId, authResult.authToken));
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                            Log.d(TAG, response.toString());
                            if (response.isSuccessful()) {
                                try {
                                    Order order = Order.JSON_CREATOR.create(new JSONObject(response.body().toString()));
                                    if (order.getRefunds().isEmpty()) {
                                        logRequestError("Refund not yet available on Order");
                                    } else {
                                        Refund refund = order.getRefunds().get(order.getRefunds().size() - 1);
                                        long offset = CLOVER_REFUND_TIME_OFFSET_MS + CLOVER_QUERY_BACKOFF_FACTOR_MS * (attempt + 1);
                                        if (timeMatches(refund.getCreatedTime(), targetTime, offset)) {
                                            processRefund(refund, 0);
                                        } else {
                                            logRequestError(String.format(Locale.getDefault(), "Last available refund for order not within %d seconds", offset / 1000));
                                        }
                                    }
                                } catch (JSONException jsx) {
                                    logRequestError(jsx.getMessage());
                                    queryForCompletedRefund(attempt + 1, targetTime);
                                }
                            } else {
                                queryForCompletedRefund(attempt + 1, targetTime);
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonElement> call, Throwable throwable) {
                            Log.e(TAG, throwable.getMessage());
                            queryForCompletedRefund(attempt + 1, targetTime);
                        }
                    });
                }
            }, CLOVER_QUERY_BACKOFF_FACTOR_MS * (attempt + 1));
        } else {
            refundRequest.setState(StateDef.STATE_ERRORED);
            String message = "Unable to retrieve successfully processed clover refund";
            logRequestError(message);
            currentPaymentCallback.onPaymentFailed(processingPaymentRecord.name(), message);
            finish();
        }
    }

    private void printReceipt(final Refund refund) {
        new PrintAsyncTask(this, refund).execute();
/*
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                PrintJobsConnector printJobsConnector = new PrintJobsConnector(CloverRefundActivity.this);
                Printer printer;
                Order order = null;
                boolean shouldPrint = false;
                try {
                    if (authResult != null) {
                        ServiceInterface service = ServiceGenerator.getInstance().createService(ServiceInterface.class);
                        Call<JsonElement> call = service.executeGet(String.format(CLOVER_MERCHANT_SETTINGS_URL, authResult.baseUrl, authResult.merchantId, authResult.authToken));
                        Response<JsonElement> response = call.execute();
                        if (response.isSuccessful()) {
                            JsonElement jsonResponse = response.body();
                            String autoPrint = ServiceHelper.findErrorElement(jsonResponse, "autoPrint");
                            shouldPrint = Boolean.parseBoolean(autoPrint);
                        }
                    }

                    order = orderConnector.getOrder(orderId);
                    if (order.getRefunds() == null) {
                        order.setRefunds(new ArrayList<Refund>());
                    }
                    if (order.getRefunds().isEmpty() || !containsRefund(order, refund)) {
                        order.getRefunds().add(refund);
                    }

                    for (LineItem lineItem : order.getLineItems()) {
                        if (containsLineItem(lineItem)) {
                            lineItem.setRefunded(true);
                        }
                    }

                    Log.d(TAG, order.getJSONObject().toString());
                    orderConnector.updateOrder(order);
                    orderConnector.updateLineItems(orderId, order.getLineItems());

                    if (shouldPrint) {
                        printer = printerConnector.getPrinters(Category.RECEIPT).get(0);

                        PrintJob printJob = new StaticReceiptPrintJob.Builder().order(order).build();
                        printJobsConnector.print(printer, printJob);
                    } else {
                        promptPrinting(order);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (order != null) {
                        promptPrinting(order);
                    }
                } finally {
                    finish();
                }
                return null;
            }
        }.execute();
*/

    }


    private void promptPrinting(Order order) {
        Intent intent = new Intent(Intents.ACTION_START_PRINT_RECEIPTS);

        intent.putExtra(Intents.EXTRA_ORDER_ID, order.getId());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            String message = "No Activity found to respond to intent: " + Intents.ACTION_START_PRINT_RECEIPTS;
            Log.d(TAG, message);
        }
    }

    static boolean containsLineItem(LineItem lineItem) {
//        Iterator<RefundLineItem> iterator = refundLineItems.iterator();
//        while (iterator.hasNext()){
//            RefundLineItem refundLineItem = iterator.next();
//            if(refundLineItem.getDescription()!=null &&
//                    refundLineItem.getDescription().equals(lineItem.getName()) &&
//                    lineItem.getPrice() >= refundLineItem.getAmount() * 100){
//                iterator.remove();
//                return true;
//            }
//        }
        return false;
    }

    static boolean containsRefund(Order order, Refund checkRefund) {
        if (order == null || checkRefund == null) {
            return false;
        }
        for (Refund refund : order.getRefunds()) {
            if (refund.getId().equals(checkRefund.getId())) {
                return true;
            }
        }
        return false;
    }


    public void setAuthResult(CloverAuth.AuthResult authResult) {
        this.authResult = authResult;
    }

    private void setPaymentRecord(JsonElement paymentRecord) {
        try {
            processingPaymentRecord.set(paymentRecord);
        } catch (Exception ex) {
            ex.printStackTrace();
            currentPaymentCallback.onConnectionFailed(ex.getMessage());
        }
    }

    private boolean timeMatches(long test, long target, long diff) {
        return Math.abs(test - target) <= diff;
    }

    @Override
    public CloverRefundActivity getConnectorActivity() {
        return this;
    }

    private static class AuthAsyncTask extends AsyncTask<Void, String, CloverAuth.AuthResult> {
        private AsyncRefundConnectorInterface activityInterface;

        private AuthAsyncTask(AsyncRefundConnectorInterface activityInterface) {
            this.activityInterface = activityInterface;
        }


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
                Account account = activityInterface.getConnectorActivity().account;
                authResult = CloverAuth.authenticate(activityInterface.getConnectorActivity(), account);
                publishProgress("Successfully authenticated as " + account + ".  authToken=" + authResult.authToken + ", authData=" + authResult.authData);
            } catch (Exception e) {
                publishProgress("Error retrieving merchant info from server" + e);
            }
            return authResult;
        }

        @Override
        protected void onPostExecute(CloverAuth.AuthResult authResult) {
            if (authResult != null && authResult.authToken != null && authResult.baseUrl != null) {
                activityInterface.getConnectorActivity().onCloverAuthenticated();
                activityInterface.getConnectorActivity().setAuthResult(authResult);
            } else {
                Gson gson = new Gson();
                activityInterface.getConnectorActivity().refundRequest.setState(StateDef.STATE_CANCELED);
                activityInterface.getConnectorActivity().setPaymentRecord(gson.toJsonTree(activityInterface.getConnectorActivity().refundRequest));
                currentPaymentCallback.onPaymentFailed(processingPaymentRecord.name(), "Clover account not authorized");
                activityInterface.getConnectorActivity().finish();
            }
        }

    }

    private static class PrintAsyncTask extends AsyncTask<Void, Void, String> {
        private AsyncRefundConnectorInterface activityInterface;
        private Refund refund;

        private PrintAsyncTask(AsyncRefundConnectorInterface activityInterface, Refund refund) {
            this.activityInterface = activityInterface;
            this.refund = refund;
        }

        @Override
        protected String doInBackground(Void... params) {
            PrintJobsConnector printJobsConnector = new PrintJobsConnector(activityInterface.getConnectorActivity());
            Printer printer;
            Order order = null;
            boolean shouldPrint = false;
            try {
                CloverAuth.AuthResult authResult = activityInterface.getConnectorActivity().authResult;
                if (authResult != null) {
                    ServiceInterface service = ServiceGenerator.getInstance().createService(ServiceInterface.class);
                    Call<JsonElement> call = service.executeGet(String.format(CLOVER_MERCHANT_SETTINGS_URL, authResult.baseUrl, authResult.merchantId, authResult.authToken));
                    Response<JsonElement> response = call.execute();
                    if (response.isSuccessful()) {
                        JsonElement jsonResponse = response.body();
                        String autoPrint = ServiceHelper.findErrorElement(jsonResponse, "autoPrint");
                        shouldPrint = Boolean.parseBoolean(autoPrint);
                    }
                }

                OrderConnector orderConnector = activityInterface.getConnectorActivity().orderConnector;
                order = orderConnector.getOrder(activityInterface.getConnectorActivity().orderId);
                if (order.getRefunds() == null) {
                    order.setRefunds(new ArrayList<Refund>());
                }
                if (order.getRefunds().isEmpty() || !containsRefund(order, refund)) {
                    order.getRefunds().add(refund);
                }

                for (LineItem lineItem : order.getLineItems()) {
                    if (containsLineItem(lineItem)) {
                        lineItem.setRefunded(true);
                    }
                }

                Log.d(TAG, order.getJSONObject().toString());
                orderConnector.updateOrder(order);
                orderConnector.updateLineItems(order.getId(), order.getLineItems());

                if (shouldPrint) {
                    printer = activityInterface.getConnectorActivity().printerConnector.getPrinters(Category.RECEIPT).get(0);

                    PrintJob printJob = new StaticReceiptPrintJob.Builder().order(order).build();
                    printJobsConnector.print(printer, printJob);
                } else {
                    activityInterface.getConnectorActivity().promptPrinting(order);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (order != null) {
                    activityInterface.getConnectorActivity().promptPrinting(order);
                }
            } finally {
                activityInterface.getConnectorActivity().finish();
            }
            return null;

        }

    }

}
