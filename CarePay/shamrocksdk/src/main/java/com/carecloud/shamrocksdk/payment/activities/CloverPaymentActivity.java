package com.carecloud.shamrocksdk.payment.activities;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;


import androidx.multidex.BuildConfig;

import com.carecloud.shamrocksdk.payment.DevicePayment;
import com.carecloud.shamrocksdk.payment.interfaces.PaymentActionCallback;
import com.carecloud.shamrocksdk.payment.models.CardData;
import com.carecloud.shamrocksdk.payment.models.PaymentLineItem;
import com.carecloud.shamrocksdk.payment.models.PaymentMethod;
import com.carecloud.shamrocksdk.payment.models.PaymentRequest;
import com.carecloud.shamrocksdk.payment.models.clover.CloverCardTransactionInfo;
import com.carecloud.shamrocksdk.payment.models.clover.CloverPaymentDTO;
import com.carecloud.shamrocksdk.payment.models.defs.ExecDef;
import com.carecloud.shamrocksdk.payment.models.defs.StateDef;
import com.carecloud.shamrocksdk.services.ServiceGenerator;
import com.carecloud.shamrocksdk.services.ServiceHelper;
import com.carecloud.shamrocksdk.services.ServiceInterface;
import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.util.CloverAuth;
import com.clover.sdk.util.CustomerMode;
import com.clover.sdk.v1.BindingException;
import com.clover.sdk.v1.ClientException;
import com.clover.sdk.v1.Intents;
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
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.java_websocket.exceptions.WebsocketNotConnectedException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import io.deepstream.Record;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Activity for handling Clover Payments and posting back results to Deep Stream
 */

public class CloverPaymentActivity extends Activity {
    private static final String TAG =CloverPaymentActivity.class.getName();
    private static final int creditCardIntentID = 555;
    private static final int MAX_DEEPSTREAM_RETRIES = 3;
    private static final String EXTRA_PAYMENT_REQUEST = "payment_request";

    private static Record processingPaymentRecord;
    private static PaymentActionCallback currentPaymentCallback;

    /**
     * Create new instance of the Activity and start the intent
     * @param context context
     * @param record DeepStream Payment Record
     * @param callback action callback
     */
    public static void newInstance(Context context, Record record, PaymentActionCallback callback){
        processingPaymentRecord = record;
        currentPaymentCallback = callback;

        Gson gson = new Gson();
        Intent intent = new Intent(context, CloverPaymentActivity.class);
        intent.putExtra(EXTRA_PAYMENT_REQUEST, gson.toJson(record.get()));
        context.startActivity(intent);

    }

    private PaymentRequest paymentRequest;
    private Account account;
    private OrderConnector orderConnector;
    private PrinterConnector printerConnector;
    private EmployeeConnector employeeConnector;
    private Long amountLong;

    private Handler handler;

    private CloverAuth.AuthResult authResult;
    private static final String CLOVER_MERCHANT_SETTINGS_URL = "%s/v3/merchants/%s/properties?access_token=%s";

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
        if(account != null) {
            authenticateCloverAccount();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == creditCardIntentID) {
            Gson gson = new Gson();
            if (resultCode == RESULT_OK) {
                Payment payment = data.getParcelableExtra(Intents.EXTRA_PAYMENT);
                if (payment != null) {
                    processPayment(payment, paymentRequest, 0);
                } else {
                    if(currentPaymentCallback != null) {
                        currentPaymentCallback.onPaymentFailed(processingPaymentRecord.name(), "Payment object is null, unable to obtain payment results but received a Success Result for payment intent");
                    }
//                    logPaymentFail("Payment object is null, unable to obtain payment results but received a Success Result for payment intent", true);
                }
            } else if (resultCode == RESULT_CANCELED) {
                paymentRequest.setState(StateDef.STATE_CANCELED);
                setPaymentRecord(gson.toJsonTree(paymentRequest));
                if(currentPaymentCallback != null) {
                    currentPaymentCallback.onPaymentCanceled(processingPaymentRecord.name(), "Payment canceled by user");
                }
                setResult(resultCode);
                finish();

            } else {
                paymentRequest.setState(StateDef.STATE_CANCELED);
                setPaymentRecord(gson.toJsonTree(paymentRequest));
                if(currentPaymentCallback != null) {
                    currentPaymentCallback.onPaymentFailed(processingPaymentRecord.name(), "An unknown error has occurred");
                }
//                logPaymentFail("Clover payment was not successfully processed", false);
                setResult(resultCode);
                finish();
            }
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
                    setAuthResult(authResult);
                }else {
                    Gson gson = new Gson();
                    paymentRequest.setState(StateDef.STATE_CANCELED);
                    setPaymentRecord(gson.toJsonTree(paymentRequest));
                    currentPaymentCallback.onPaymentFailed(processingPaymentRecord.name(), "Clover account not authorized");
//                    logPaymentFail("account not authorized", false);
                    finish();
                }
            }
        }.execute();
    }

    private void onCloverAuthenticated(){
        connect();
        if (orderConnector != null) {
            List<LineItem> lineItems = getLineItems();
            new OrderAsyncTask(orderConnector).execute(lineItems.toArray(new LineItem[lineItems.size()]));
        }
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

        }
    }

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

    private void processPayment(final Payment payment, final PaymentRequest paymentRequest, final int attempt) {
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
        setPaymentRequestState(payment,paymentRequest) ;

        try {
            setPaymentRecord(gson.toJsonTree(paymentRequest));
            PaymentRequest ackRequest = DevicePayment.getPaymentAck(processingPaymentRecord.name());
            if (ackRequest != null && ackRequest.getState() != null &&
                    (ackRequest.getState().equals(StateDef.STATE_CAPTURED)
                            || ackRequest.getState().equals(StateDef.STATE_CAPTURED_WITH_ADJUSTMENT))&&
                    ackRequest.getTransactionResponse() != null &&
                    ackRequest.getPaymentMethod() != null &&
                    ackRequest.getPaymentMethod().getCardData() != null) {
                if(currentPaymentCallback != null) {
                    currentPaymentCallback.onPaymentComplete(processingPaymentRecord.name(), gson.toJsonTree(paymentRequest));
                }
                printReceipt(payment);
            } else if (attempt < MAX_DEEPSTREAM_RETRIES) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        processPayment(payment, paymentRequest, attempt + 1);
                    }
                }, 1000);
            } else {
                if(currentPaymentCallback != null) {
                    currentPaymentCallback.onPaymentCompleteWithError(processingPaymentRecord.name(), gson.toJsonTree(paymentRequest), "Unable to update Payment Request Record in DeepStream");
                }
                printReceipt(payment);
            }
        }catch (WebsocketNotConnectedException wse){
            wse.printStackTrace();
            if(currentPaymentCallback != null) {
                currentPaymentCallback.onPaymentCompleteWithError(processingPaymentRecord.name(), gson.toJsonTree(paymentRequest), "Unable to update Payment Request Record in DeepStream");
            }
            printReceipt(payment);
        }
    }

    /**
     * This method is to determine whether the paymentRequest's state has to be set as CAPTURED or as CAPTURED_WITH_ADJUSTMENT.
     *
     * When using cards of type gift card, or a HSA card that has insufficient balance, CloverPayment will still try to process
     * the entire requested amount and returns a successful response. However the CloverPayment would have processed only the available balance in the card.
     * The actual amount processed can be seen in the payment.amount.
     *
     * It is recommended to warn the user at such cases, that only a portion of the requested amount was processed.
     * To support this warning, a `CAPTURED_WITH_ADJUSTMENT' is set instead of CAPTURED.
     *
     * To replicate this scenario in debug build, use a comment in the paymentRequest, by the wordings "test_partial_processed-##",
     * replacing ## with the amount that you like to test.
     *
     * Note: This test is supported only when the Clover processed amount is greater than $2.
     *
     */
    private void setPaymentRequestState(final Payment payment, final PaymentRequest paymentRequest){

        if (BuildConfig.DEBUG && paymentRequest.getComments() != null && payment.getAmount() > 2 && paymentRequest.getComments().contains("test_partial_processed")) {
            try {
                String[] commentAmountArray = paymentRequest.getComments().split("-") ;
                Double testAmount = Double.parseDouble(commentAmountArray[1]) ;
                if(testAmount > payment.getAmount()/100D) {
                    paymentRequest.setAmount(payment.getAmount()/100D - 1);
                } else{
                    paymentRequest.setAmount(testAmount);
                }
            } catch (Exception e) {
                paymentRequest.setAmount(payment.getAmount() - 1);
                   Log.e(TAG, e.getMessage());
            }
            paymentRequest.setState(StateDef.STATE_CAPTURED_WITH_ADJUSTMENT);
        } else if (payment.getAmount()/100D < paymentRequest.getAmount()) { // When Clover transaction amount is less than the requested amount, set the state to CAPTURED_WITH_ADJUSTMENT.
            paymentRequest.setAmount(payment.getAmount()/100D);
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


    private void startSecurePaymentIntent(Order order) {
        Intent intent = new Intent(Intents.ACTION_SECURE_PAY);
        try {
            if (amountLong != null) {
                intent.putExtra(Intents.EXTRA_AMOUNT, amountLong);
            } else {
                if(currentPaymentCallback != null) {
                    currentPaymentCallback.onPaymentFailed(processingPaymentRecord.name(), "Payment amount must not be null");
                }
                throw new IllegalArgumentException("amount must not be null");
            }

            String orderId = order.getId();
            if (orderId != null) {
                intent.putExtra(Intents.EXTRA_ORDER_ID, orderId);
                //If no order id were passed to EXTRA_ORDER_ID a new empty order would be generated for the payment
            }

            intent.putExtra(Intents.EXTRA_CARD_ENTRY_METHODS, Intents.CARD_ENTRY_METHOD_ALL);

            if (intent.resolveActivity(getPackageManager()) != null) {
                CustomerMode.disable(this);
                startActivityForResult(intent, creditCardIntentID);
                paymentRequest.setState(StateDef.STATE_WAITING);
                Gson gson = new Gson();
                setPaymentRecord(gson.toJsonTree(paymentRequest));
                if(currentPaymentCallback != null) {
                    currentPaymentCallback.onPaymentStarted(processingPaymentRecord.name());
                }
            } else {
                String message = "No Activity found to respond to intent: " + Intents.ACTION_SECURE_PAY;
                if(currentPaymentCallback != null) {
                    currentPaymentCallback.onPaymentFailed(processingPaymentRecord.name(), message);
                }
                throw new IllegalArgumentException(message);
            }

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            if(currentPaymentCallback != null) {
                currentPaymentCallback.onPaymentFailed(processingPaymentRecord.name(), "An unknown error has occurred");
            }
        }
    }

    private void printReceipt(final Payment payment){
        new AsyncTask<Void, Void, String>(){

            @Override
            protected String doInBackground(Void... params) {
                PrintJobsConnector printJobsConnector = new PrintJobsConnector(CloverPaymentActivity.this);
                Printer printer;
                boolean shouldPrint = false;
                try {
                    if(authResult != null) {
                        ServiceInterface service = ServiceGenerator.getInstance().createService(ServiceInterface.class);
                        Call<JsonElement> call = service.executeGet(String.format(CLOVER_MERCHANT_SETTINGS_URL, authResult.baseUrl, authResult.merchantId, authResult.authToken));
                        Response<JsonElement> response = call.execute();
                        if (response.isSuccessful()) {
                            JsonElement jsonResponse = response.body();
                            String autoPrint = ServiceHelper.findErrorElement(jsonResponse, "autoPrint");
                            shouldPrint = Boolean.parseBoolean(autoPrint);
                        }
                    }

                    if(shouldPrint) {
                        printer = printerConnector.getPrinters(Category.RECEIPT).get(0);
                        Order order = orderConnector.getOrder(payment.getOrder().getId());
                        if(order.getPayments() == null || order.getPayments().isEmpty()){
                            List<Payment> payments = new ArrayList<>();
                            order.setPayments(payments);
                        }
                        Log.d(TAG, order.getJSONObject().toString());
                        PrintJob printJob = new StaticPaymentPrintJob.Builder().order(order).build();
                        printJobsConnector.print(printer, printJob);
                    }else{
                        promptPrinting(payment);
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    //Prompt User to print if auto print fails
                    promptPrinting(payment);
                }
                finish();
                return null;
            }
        }.execute();

    }

    private void promptPrinting(Payment payment){
        if(payment.getOrder() == null){
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

                    if(!order.hasEmployee() || order.getEmployee()==null || !order.getEmployee().isNotNullId()){
                        Reference employee = new Reference();
                        if(employeeConnector != null) {
                            employee.setId(employeeConnector.getEmployee().getId());
                            order.setEmployee(employee);
                        }
                    }
                }
            } catch (RemoteException | ClientException | ServiceException | BindingException e) {
                e.printStackTrace();
            }

            return order;
        }

        @Override
        protected final void onPostExecute(Order order) {
            if (order == null) {
                Gson gson = new Gson();
                paymentRequest.setState(StateDef.STATE_CANCELED);
                setPaymentRecord(gson.toJsonTree(paymentRequest));
                currentPaymentCallback.onPaymentCanceled(processingPaymentRecord.name(), "Error creating Clover Order");
                finish();
                return;
            }

            // Enables the pay buttons if the order is valid
            startSecurePaymentIntent(order);

        }
    }


    public void testSetStatic(Record record, PaymentActionCallback callback){
        processingPaymentRecord = record;
        currentPaymentCallback = callback;
    }

    public void testDeepStreamSet(final Payment payment, final PaymentRequest paymentRequest){
        processPayment(payment, paymentRequest, MAX_DEEPSTREAM_RETRIES);
    }

    private void setPaymentRecord(JsonElement paymentRecord){
        try {
            processingPaymentRecord.set(paymentRecord);
        }catch (Exception ex) {
            ex.printStackTrace();
            if(currentPaymentCallback != null) {
                currentPaymentCallback.onConnectionFailed(ex.getMessage());
            }
        }
    }
}
