package com.carecloud.carepay.practice.clover.payments;

import android.accounts.Account;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

import com.carecloud.carepay.practice.clover.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentLineItem;
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
import com.clover.sdk.v3.payments.Credit;
import com.clover.sdk.v3.payments.Payment;
import com.clover.sdk.v3.payments.Refund;
import com.google.gson.Gson;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


/**
 * The type Clover payment activity.
 */
public class CloverRefundActivity extends BaseActivity {
    /**
     * The constant refundIntentID.
     */
    public static final int refundIntentID = 444;
    private static final String TAG = CloverRefundActivity.class.getName();

    private Account account;
    private OrderConnector orderConnector;

    private Long amountLong = 0L;
    private PaymentLineItem[] paymentLineItems;


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
        paymentLineItems = gson.fromJson(lineItemString, PaymentLineItem[].class);

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
            new OrderAsyncTask(orderConnector).execute();
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


    private List<LineItem> getLineItems() {
        List<LineItem> lineItems = new LinkedList<>();
        for (PaymentLineItem paymentLineItem : paymentLineItems) {
            LineItem item = new LineItem();
            item.setName(paymentLineItem.getDescription());
            item.setPrice(Math.round(paymentLineItem.getAmount() * -100D));
            lineItems.add(item);
        }
        return lineItems;
    }

    private List<Refund> getRefundItems(){
        List<Refund> refunds = new LinkedList<>();
        for(PaymentLineItem paymentLineItem : paymentLineItems){
            Refund refund = new Refund();
            refund.setAmount(Math.round(paymentLineItem.getAmount() * 100D));
            refunds.add(refund);
        }
        return refunds;
    }

    private List<Credit> getCreditItems(){
        List<Credit> credits = new LinkedList<>();
        for(PaymentLineItem paymentLineItem : paymentLineItems){
            Credit credit = new Credit();
            credit.setAmount(Math.round(paymentLineItem.getAmount() * 100D));
            credits.add(credit);
        }
        return credits;
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



    private class OrderAsyncTask extends AsyncTask<Void, Void, Order> {
        private OrderConnector orderConnector;

        OrderAsyncTask(OrderConnector orderConnector){
            this.orderConnector = orderConnector;
        }

        @Override
        protected final Order doInBackground(Void... params) {
            Order order = null;

            try {
                if (amountLong != null) {
                    // Create a new order
                    order = orderConnector.createOrder(new Order());
//                    for (Refund refund : getRefundItems()) {
//                        orderConnector.addRefund(order.getId(), refund);
//
//                    }

//                    for (Credit credit : getCreditItems()){
//                        orderConnector.addCredit(order.getId(), credit);
//                    }

                    List<LineItem> lineItems = getLineItems();
                    if (lineItems != null && !lineItems.isEmpty()) {
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
                SystemUtil.showErrorToast(CloverRefundActivity.this, Label.getLabel("clover_payment_canceled"));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 3000);
                return;
            }

            // Enables the pay buttons if the order is valid
            startRefundIntent(order);

        }
    }

}