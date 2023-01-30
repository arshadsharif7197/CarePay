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


import androidx.multidex.BuildConfig;

import com.carecloud.shamrocksdk.payment.DevicePayment;
import com.carecloud.shamrocksdk.payment.adapters.ConnectorServiceAdapter;
import com.carecloud.shamrocksdk.payment.interfaces.AsyncPaymentConnectorInterface;
import com.carecloud.shamrocksdk.payment.interfaces.PaymentActionCallback;
import com.carecloud.shamrocksdk.payment.models.CardData;
import com.carecloud.shamrocksdk.payment.models.PaymentLineItem;
import com.carecloud.shamrocksdk.payment.models.PaymentMethod;
import com.carecloud.shamrocksdk.payment.models.PaymentRequest;
import com.carecloud.shamrocksdk.payment.models.RequestError;
import com.carecloud.shamrocksdk.payment.models.clover.CloverCardTransactionInfo;
import com.carecloud.shamrocksdk.payment.models.clover.CloverPaymentDTO;
import com.carecloud.shamrocksdk.payment.models.clover.CloverPaymentQueryResponse;
import com.carecloud.shamrocksdk.payment.models.defs.ExecDef;
import com.carecloud.shamrocksdk.payment.models.defs.StateDef;
import com.carecloud.shamrocksdk.services.ServiceGenerator;
import com.carecloud.shamrocksdk.services.ServiceHelper;
import com.carecloud.shamrocksdk.services.ServiceInterface;
import com.clover.connector.sdk.v3.PaymentConnector;
import com.clover.connector.sdk.v3.PaymentV3Connector;
import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.util.CloverAuth;
import com.clover.sdk.v1.BindingException;
import com.clover.sdk.v1.ClientException;
import com.clover.sdk.v1.Intents;
import com.clover.sdk.v1.ServiceConnector;
import com.clover.sdk.v1.ServiceException;
import com.clover.sdk.v1.printer.Category;
import com.clover.sdk.v1.printer.Printer;
import com.clover.sdk.v1.printer.PrinterConnector;
import com.clover.sdk.v1.printer.job.PrintJob;
import com.clover.sdk.v1.printer.job.PrintJobsConnector;
import com.clover.sdk.v1.printer.job.StaticPaymentPrintJob;
import com.clover.sdk.v3.base.Reference;
import com.clover.sdk.v3.employees.EmployeeConnector;
import com.clover.sdk.v3.order.LineItem;
import com.clover.sdk.v3.order.Order;
import com.clover.sdk.v3.order.OrderConnector;
import com.clover.sdk.v3.payments.Payment;
import com.clover.sdk.v3.remotepay.SaleRequest;
import com.clover.sdk.v3.remotepay.SaleResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import io.deepstream.Record;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for handling Clover Refunds and posting back results to Deep Stream
 */

public class CloverPaymentConnectorActivity extends Activity implements AsyncPaymentConnectorInterface {
    private static final String TAG = CloverPaymentConnectorActivity.class.getName();
    private static final String CLOVER_MERCHANT_SETTINGS_URL = "%s/v3/merchants/%s/properties?access_token=%s";
    private static final int MAX_DEEPSTREAM_RETRIES = 3;
    private static final String EXTRA_PAYMENT_REQUEST = "payment_request";
    private static final String PAYMENT_PREFIX = "payment_request/";
    private static final int MAX_CLOVER_QUERY_RETRY = 3;
    private static final int CLOVER_QUERY_BACKOFF_FACTOR_MS = 500;
    private static final String CLOVER_PAYMENTS_QUERY_URL = "%s/v3/merchants/%s/orders/%s/payments?expand=cardTransaction&access_token=%s";

    private static Record processingPaymentRecord;
    private static PaymentActionCallback currentPaymentCallback;

    /**
     * Create new instance of the Activity
     *
     * @param context  context
     * @param record   DeepStream Payment Record
     * @param callback action callback
     */
    public static void newInstance(Context context, Record record, PaymentActionCallback callback) {
        processingPaymentRecord = record;
        currentPaymentCallback = callback;

        Gson gson = new Gson();
        Intent intent = new Intent(context, CloverPaymentConnectorActivity.class);
        intent.putExtra(EXTRA_PAYMENT_REQUEST, gson.toJson(record.get()));
        context.startActivity(intent);

    }

    private PaymentRequest paymentRequest;
    private Account account;
    private OrderConnector orderConnector;
    private PaymentConnector paymentConnector;
    private PrinterConnector printerConnector;
    private EmployeeConnector employeeConnector;
    private CloverAuth.AuthResult authResult;

    private Handler handler;

    private Long amountLong = 0L;
    private boolean shouldPrint = false;

    private Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();

        Gson gson = new Gson();
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_PAYMENT_REQUEST)) {
            paymentRequest = gson.fromJson(intent.getStringExtra(EXTRA_PAYMENT_REQUEST), PaymentRequest.class);
            amountLong = Math.round(paymentRequest.getAmount() * 100D);
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
        paymentRequest.setState(StateDef.STATE_CANCELED);
        setPaymentRecord(gson.toJsonTree(paymentRequest));
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
    }

    @Override
    public CloverPaymentConnectorActivity getConnectorActivity() {
        return this;
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
        if (order != null) {
            order = null;
        }
    }

    private List<LineItem> getLineItems() {
        List<LineItem> lineItems = new LinkedList<>();

        for (PaymentLineItem paymentLineItem : paymentRequest.getPaymentLineItems()) {
            LineItem item = new LineItem();
            item.setName(paymentLineItem.getDescription());
            item.setPrice(Math.round(paymentLineItem.getAmount() * 100D));
            lineItems.add(item);
        }
        return lineItems;
    }


    private PaymentV3Connector.PaymentServiceListener paymentServiceListener = new ConnectorServiceAdapter() {
        @Override
        public void onSaleResponse(SaleResponse response) {
            if(response == null){
                queryForCompletedPayment(0);
                return;
            }
            if (response.getSuccess()) {
                if (response.getPayment() != null) {
                    processPayment(response.getPayment(), 0);
                } else {
                    logRequestError("Payment not available in clover response");
                    queryForCompletedPayment(0);
                }
            } else {
                Gson gson = new Gson();
                paymentRequest.setState(StateDef.STATE_CANCELED);
                setPaymentRecord(gson.toJsonTree(paymentRequest));
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
                    if (orderConnector != null) {
                        List<LineItem> lineItems = getLineItems();
                        new OrderAsyncTask(getConnectorActivity()).execute(lineItems.toArray(new LineItem[lineItems.size()]));
                    }

                }

                @Override
                public void onServiceDisconnected(ServiceConnector<? extends IInterface> connector) {
                    Log.d(TAG, "onServiceDisconnected " + connector);
                    //todo maybe reconnect if payment not done??
                }
            });
            paymentConnector.connect();

        } else if (!paymentConnector.isConnected()) {
            paymentConnector.connect();
        }
    }

    private void startPayment(Order order) {
        paymentRequest.setState(StateDef.STATE_WAITING);
        Gson gson = new Gson();
        setPaymentRecord(gson.toJsonTree(paymentRequest));
        currentPaymentCallback.onPaymentStarted(processingPaymentRecord.name());

        SaleRequest saleRequest = new SaleRequest();
        saleRequest.setAmount(amountLong);
        saleRequest.setRequestId(Double.toString(Math.random() * System.currentTimeMillis()));
        saleRequest.setExternalId(paymentRequest.getDeepstreamId().replace(PAYMENT_PREFIX, ""));
        saleRequest.setOrderId(order.getId());

        //looks like there's a clover bug with the callback for this. setting this to ensure
        // its not called if they ever fix the bug because we have not implemented it
        saleRequest.setAutoAcceptSignature(true);

        try {
            paymentConnector.getService().sale(saleRequest);
            this.order = order;
        } catch (RemoteException re) {
            re.printStackTrace();
        }
    }

    private void processPayment(final Payment payment, final int attempt) {
        Gson gson = new Gson();
        String jsonString = payment.getJSONObject().toString();
        CloverPaymentDTO cloverPayment = gson.fromJson(jsonString, CloverPaymentDTO.class);
        CloverCardTransactionInfo transactionInfo = cloverPayment.getCloverCardTransactionInfo();
        CardData cardData = getCreditCardData(transactionInfo);

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setType(PaymentMethod.PAYMENT_METHOD_NEW_CARD);
        paymentMethod.setCardData(cardData);

        paymentRequest.setTransactionResponse(gson.fromJson(jsonString, JsonObject.class));
        paymentRequest.setExecutionType(ExecDef.EXECUTION_CLOVER);
        paymentRequest.setPaymentMethod(paymentMethod);
        setPaymentRequestState(payment, paymentRequest);

        try {
            setPaymentRecord(gson.toJsonTree(paymentRequest));
            PaymentRequest ackRequest = DevicePayment.getPaymentAck(processingPaymentRecord.name());
            if (ackRequest != null && ackRequest.getState() != null &&
                    (ackRequest.getState().equals(StateDef.STATE_CAPTURED)
                            || ackRequest.getState().equals(StateDef.STATE_CAPTURED_WITH_ADJUSTMENT)) &&
                    ackRequest.getTransactionResponse() != null &&
                    ackRequest.getPaymentMethod() != null &&
                    ackRequest.getPaymentMethod().getCardData() != null) {
                if (currentPaymentCallback != null) {
                    currentPaymentCallback.onPaymentComplete(processingPaymentRecord.name(), gson.toJsonTree(paymentRequest));
                }
                printReceipt(payment);
            } else if (attempt < MAX_DEEPSTREAM_RETRIES) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        processPayment(payment, attempt + 1);
                    }
                }, 1000);
            } else {
                if (currentPaymentCallback != null) {
                    currentPaymentCallback.onPaymentCompleteWithError(processingPaymentRecord.name(), gson.toJsonTree(paymentRequest), "Unable to update Payment Request Record in DeepStream");
                }
                printReceipt(payment);
            }
        } catch (WebsocketNotConnectedException wse) {
            wse.printStackTrace();
            if (currentPaymentCallback != null) {
                currentPaymentCallback.onPaymentCompleteWithError(processingPaymentRecord.name(), gson.toJsonTree(paymentRequest), "Unable to update Payment Request Record in DeepStream");
            }
            printReceipt(payment);
        }

    }

    private void logRequestError(String message){
        Gson gson = new Gson();
        if (paymentRequest.getRequestErrors() == null) {
            paymentRequest.setRequestErrors(new ArrayList<RequestError>());
        }
        RequestError requestError = new RequestError();
        requestError.setId("Clover Error " + System.currentTimeMillis());
        requestError.setError(message);
        paymentRequest.getRequestErrors().add(requestError);
        setPaymentRecord(gson.toJsonTree(paymentRequest));
    }

    private void queryForCompletedPayment(final int attempt) {
        if (attempt < MAX_CLOVER_QUERY_RETRY && order.getId() != null) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ServiceInterface service = ServiceGenerator.getInstance().createService(ServiceInterface.class);
                    Call<JsonElement> call = service.executeGet(String.format(CLOVER_PAYMENTS_QUERY_URL, authResult.baseUrl, authResult.merchantId, order.getId(), authResult.authToken));
                    call.enqueue(new Callback<JsonElement>() {
                        @Override
                        public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                            Log.d(TAG, response.toString());
                            if (response.isSuccessful()) {
                                Gson gson = new Gson();
                                CloverPaymentQueryResponse queryResponse = gson.fromJson(response.body(), CloverPaymentQueryResponse.class);
                                if (queryResponse.getPayments().isEmpty()) {
                                    queryForCompletedPayment(attempt + 1);
                                } else {
                                    try {
                                        Payment payment = Payment.JSON_CREATOR.create(new JSONObject(queryResponse.getPayments().get(queryResponse.getPayments().size() - 1).toString()));
                                        processPayment(payment, 0);
                                    } catch (JSONException jsx) {
                                        logRequestError(jsx.getMessage());
                                        queryForCompletedPayment(attempt + 1);
                                    }
                                }
                            } else {
                                queryForCompletedPayment(attempt + 1);
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonElement> call, Throwable throwable) {
                            Log.e(TAG, throwable.getMessage());
                            queryForCompletedPayment(attempt + 1);
                        }
                    });
                }
            }, CLOVER_QUERY_BACKOFF_FACTOR_MS * (attempt + 1));
        } else {
            paymentRequest.setState(StateDef.STATE_ERRORED);
            String message = "Unable to retrieve successfully processed clover payment";
            logRequestError(message);
            currentPaymentCallback.onPaymentFailed(processingPaymentRecord.name(), message);
            finish();
        }
    }

    /**
     * This method is to determine whether the paymentRequest's state has to be set as CAPTURED or as CAPTURED_WITH_ADJUSTMENT.
     * <p>
     * When using cards of type gift card, or a HSA card that has insufficient balance, CloverPayment will still try to process
     * the entire requested amount and returns a successful response. However the CloverPayment would have processed only the available balance in the card.
     * The actual amount processed can be seen in the payment.amount.
     * <p>
     * It is recommended to warn the user at such cases, that only a portion of the requested amount was processed.
     * To support this warning, a `CAPTURED_WITH_ADJUSTMENT' is set instead of CAPTURED.
     * <p>
     * To replicate this scenario in debug build, use a comment in the paymentRequest, by the wordings "test_partial_processed-##",
     * replacing ## with the amount that you like to test.
     * <p>
     * Note: This test is supported only when the Clover processed amount is greater than $2.
     */
    private void setPaymentRequestState(final Payment payment, final PaymentRequest paymentRequest) {

        if (BuildConfig.DEBUG && paymentRequest.getComments() != null && payment.getAmount() > 2 && paymentRequest.getComments().contains("test_partial_processed")) {
            try {
                String[] commentAmountArray = paymentRequest.getComments().split("-");
                Double testAmount = Double.parseDouble(commentAmountArray[1]);
                if (testAmount > payment.getAmount() / 100D) {
                    paymentRequest.setAmount(payment.getAmount() / 100D - 1);
                } else {
                    paymentRequest.setAmount(testAmount);
                }
            } catch (Exception e) {
                paymentRequest.setAmount(payment.getAmount() - 1);
                Log.e(TAG, e.getMessage());
            }
            paymentRequest.setState(StateDef.STATE_CAPTURED_WITH_ADJUSTMENT);
        } else if (payment.getAmount() / 100D < paymentRequest.getAmount()) { // When Clover transaction amount is less than the requested amount, set the state to CAPTURED_WITH_ADJUSTMENT.
            paymentRequest.setAmount(payment.getAmount() / 100D);
            paymentRequest.setState(StateDef.STATE_CAPTURED_WITH_ADJUSTMENT);
        } else {
            paymentRequest.setState(StateDef.STATE_CAPTURED);
        }
    }

    private CardData getCreditCardData(CloverCardTransactionInfo transactionInfo) {
        CardData cardData = new CardData();
        cardData.setCardType(transactionInfo.getCardType());
        cardData.setCardNumber(transactionInfo.getLast4());
        cardData.setToken(transactionInfo.getToken());
        cardData.setTokenizationService(CardData.TOKENIZATION_CLOVER);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMyy", Locale.US);
        if (transactionInfo.getVaultedCard() != null) {
            cardData.setExpiration(transactionInfo.getVaultedCard().getExpirationDate(dateFormat.format(calendar.getTime())));
            cardData.setCardholderName(transactionInfo.getVaultedCard().getCardholderName(""));
        } else {
            cardData.setExpiration(dateFormat.format(calendar.getTime()));
            cardData.setCardholderName("");
        }

        return cardData;
    }


    private void printReceipt(Payment payment) {
//        prevent auto print since we are going to always display the print option in the payment complete screen
//        new PrintAsyncTask(this, payment).execute();
        finish();
    }

    private void promptPrinting(Payment payment) {
        if (payment.getOrder() == null) {
            return;
        }

        Intent intent = new Intent(Intents.ACTION_START_PRINT_RECEIPTS);
        intent.putExtra(Intents.EXTRA_ORDER_ID, payment.getOrder().getId());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            String message = "No Activity found to respond to intent: " + Intents.ACTION_START_PRINT_RECEIPTS;
            Log.d(TAG, message);
        }
    }


    public void setAuthResult(CloverAuth.AuthResult authResult) {
        this.authResult = authResult;
    }

    private void setPaymentRecord(JsonElement paymentRecord) {
        try {
            processingPaymentRecord.set(paymentRecord);
        } catch (Exception ex) {
            ex.printStackTrace();
            if (currentPaymentCallback != null) {
                currentPaymentCallback.onConnectionFailed(ex.getMessage());
            }
        }
    }

    private static class AuthAsyncTask extends AsyncTask<Void, String, CloverAuth.AuthResult> {
        private AsyncPaymentConnectorInterface activityInterface;

        private AuthAsyncTask(AsyncPaymentConnectorInterface activityInterface) {
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
                activityInterface.getConnectorActivity().paymentRequest.setState(StateDef.STATE_CANCELED);
                activityInterface.getConnectorActivity().setPaymentRecord(gson.toJsonTree(activityInterface.getConnectorActivity().paymentRequest));
                currentPaymentCallback.onPaymentFailed(processingPaymentRecord.name(), "Clover account not authorized");
                activityInterface.getConnectorActivity().finish();
            }
        }

    }

    private static class OrderAsyncTask extends AsyncTask<LineItem, Void, Order> {
        private AsyncPaymentConnectorInterface activityInterface;

        OrderAsyncTask(AsyncPaymentConnectorInterface activityInterface) {
            this.activityInterface = activityInterface;
        }

        @Override
        protected final Order doInBackground(LineItem... lineItems) {
            Order order = null;

            try {
                if (activityInterface.getConnectorActivity().amountLong != null) {
                    // Create a new order
                    order = activityInterface.getConnectorActivity().orderConnector.createOrder(new Order());
                    if (lineItems != null && lineItems.length > 0) {
                        for (LineItem lineItem : lineItems) {
                            activityInterface.getConnectorActivity().orderConnector.addCustomLineItem(order.getId(), lineItem, false);
                        }
                    } else {
                        return null;
                    }
                    // Update local representation of the order
                    order = activityInterface.getConnectorActivity().orderConnector.getOrder(order.getId());

                    if (!order.hasEmployee() || order.getEmployee() == null || !order.getEmployee().isNotNullId()) {
                        Reference employee = new Reference();
                        EmployeeConnector employeeConnector = activityInterface.getConnectorActivity().employeeConnector;
                        if (employeeConnector != null) {
                            employee.setId(employeeConnector.getEmployee().getId());
                            order.setEmployee(employee);
                        }
                    }
                }
                CloverAuth.AuthResult authResult = activityInterface.getConnectorActivity().authResult;
                ServiceInterface service = ServiceGenerator.getInstance().createService(ServiceInterface.class);
                Call<JsonElement> call = service.executeGet(String.format(CLOVER_MERCHANT_SETTINGS_URL, authResult.baseUrl, authResult.merchantId, authResult.authToken));
                Response<JsonElement> response = call.execute();
                if (response.isSuccessful()) {
                    JsonElement jsonResponse = response.body();
                    String autoPrint = ServiceHelper.findErrorElement(jsonResponse, "autoPrint");
                    activityInterface.getConnectorActivity().shouldPrint = Boolean.parseBoolean(autoPrint);
                }
            } catch (RemoteException | ClientException | ServiceException | BindingException | IOException e) {
                e.printStackTrace();
            }


            return order;
        }

        @Override
        protected final void onPostExecute(Order order) {
            if (order == null) {
                Gson gson = new Gson();
                activityInterface.getConnectorActivity().paymentRequest.setState(StateDef.STATE_CANCELED);
                activityInterface.getConnectorActivity().setPaymentRecord(gson.toJsonTree(activityInterface.getConnectorActivity().paymentRequest));
                currentPaymentCallback.onPaymentCanceled(processingPaymentRecord.name(), "Error creating Clover Order");
                activityInterface.getConnectorActivity().finish();
                return;
            }

            activityInterface.getConnectorActivity().startPayment(order);

        }
    }

    private static class PrintAsyncTask extends AsyncTask<Void, Void, String> {
        private AsyncPaymentConnectorInterface activityInterface;
        private Payment payment;

        private PrintAsyncTask(AsyncPaymentConnectorInterface activityInterface, Payment payment) {
            this.activityInterface = activityInterface;
            this.payment = payment;
        }

        @Override
        protected String doInBackground(Void... params) {
            PrintJobsConnector printJobsConnector = new PrintJobsConnector(activityInterface.getConnectorActivity());
            Printer printer;
            try {
                if (activityInterface.getConnectorActivity().shouldPrint) {
                    printer = activityInterface.getConnectorActivity().printerConnector.getPrinters(Category.RECEIPT).get(0);
                    Order order = activityInterface.getConnectorActivity().orderConnector.getOrder(payment.getOrder().getId());
                    if (order.getPayments() == null || order.getPayments().isEmpty()) {
                        List<Payment> payments = new ArrayList<>();
                        order.setPayments(payments);
                    }
                    Log.d(TAG, order.getJSONObject().toString());
                    PrintJob printJob = new StaticPaymentPrintJob.Builder().order(order).build();
                    printJobsConnector.print(printer, printJob);
                }
            } catch (Exception e) {
                e.printStackTrace();

                //Prompt User to print if auto print fails
                activityInterface.getConnectorActivity().promptPrinting(payment);
            }
            activityInterface.getConnectorActivity().finish();
            return null;
        }

    }
}
