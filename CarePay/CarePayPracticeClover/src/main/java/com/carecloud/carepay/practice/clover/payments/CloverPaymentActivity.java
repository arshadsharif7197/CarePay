package com.carecloud.carepay.practice.clover.payments;

import android.accounts.Account;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.carecloud.carepay.practice.clover.R;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPayloadMetaDataDTO;
import com.carecloud.carepaylibray.utils.StringUtil;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * The type Clover payment activity.
 */
public class CloverPaymentActivity extends AppCompatActivity {
    /**
     * The constant creditCardIntentID.
     */
    public static final int creditCardIntentID = 555;
    private static final String TAG = CloverPaymentActivity.class.getName();
    private Account account;
    private OrderConnector orderConnector;
    private Order order;
    private Long amountLong=new Long(0);
    private double amountDouble;
    private String itemName ;
    private String paymentTransitionString ;
    private String patientPaymentMetaDataString;
    private CloverAuth.AuthResult authResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  setContentView(R.layout.activity_clover_payment);
        Intent intent = getIntent();
        if (intent.hasExtra("PAYMENT_AMOUNT")) {
            amountDouble = intent.getDoubleExtra("PAYMENT_AMOUNT", 0.00);
            amountLong = new Long((long) (amountDouble*100));
        }
        if (intent.hasExtra("ITEM_NAME")) {
            itemName = intent.getStringExtra("ITEM_NAME");

        }
        if (intent.hasExtra("PAYMENT_TRANSITION")) {
            paymentTransitionString = intent.getStringExtra("PAYMENT_TRANSITION");

        }
        if (intent.hasExtra("PAYMENT_METADATA")) {
            patientPaymentMetaDataString = intent.getStringExtra("PAYMENT_METADATA");

        }

        if (account == null) {
            {
                account = CloverAccount.getAccount(this);
                authenticateCloverAccount();
            }


            // If an account can't be acquired, exit the app
            if (account == null) {
                Toast.makeText(this, getString(R.string.no_account), Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
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
                Toast.makeText(this, getString(R.string.no_account), Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        disconnect();
        finish();
    }
    private boolean isPaymentComplete = false;
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if(!isPaymentComplete) {
//            disconnect();
//            finish();
//        }
//    }

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
                if (authResult.authToken != null && authResult.baseUrl != null) {
                    connect();
                    new OrderAsyncTask().execute();
                }
            }
        }.execute();
    }



    private class OrderAsyncTask extends AsyncTask<Void, Void, Order> {

        @Override
        protected final Order doInBackground(Void... params) {
            Order order;

            try {
                // Create a new order
                order = orderConnector.createOrder(new Order());

                if(amountLong != null && StringUtil.isNullOrEmpty(itemName))
                {
                    orderConnector.addCustomLineItem(order.getId(), getLineItem(), false);
                }

                // Update local representation of the order
                order = orderConnector.getOrder(order.getId());

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
            // Enables the pay buttons if the order is valid
            if (!isFinishing()) {
                CloverPaymentActivity.this.order = order;
                isPaymentComplete = true;
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
                        Toast.makeText(getApplicationContext(), getString(R.string.amount_required), Toast.LENGTH_LONG).show();
                    }


                    String orderId = order.getId();

                    if (orderId != null) {
                        intent.putExtra(Intents.EXTRA_ORDER_ID, orderId);
                        //If no order id were passed to EXTRA_ORDER_ID a new empty order would be generated for the payment
                    }

                    intent.putExtra(Intents.EXTRA_CARD_ENTRY_METHODS, Intents.CARD_ENTRY_METHOD_ALL);
                    intent.putExtra(Intents.EXTRA_CARD_DATA_MESSAGE, "Please swipe your card to complete check in");

                    dumpIntent(intent);
                    startActivityForResult(intent, creditCardIntentID);

                } catch (Exception e) {

                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), getString(R.string.payment_successful, payment.getOrder().getId()), Toast.LENGTH_SHORT).show();

                if (payment != null) {
                    isPaymentComplete = true;
                    postPaymentConfirmation(payment.getJSONObject().toString());
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.payment_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void postPaymentConfirmation(String jsonInString)
    {
        JSONObject payload = new JSONObject();
        try {

            JSONObject paymentMethod = new JSONObject();
            JSONObject creditCard = new JSONObject(jsonInString);

            paymentMethod.put("amount", amountLong);
            payload.put("amount", amountLong);

            JSONObject billingInformation = new JSONObject();
            billingInformation.put("same_as_patient", true);
            creditCard.put("billing_information", billingInformation);

            paymentMethod.put("credit_card", creditCard);
            paymentMethod.put("type", "credit_card");
            paymentMethod.put("execution", "clover");

            JSONArray paymentMethods = new JSONArray();
            paymentMethods.put(paymentMethod);
            payload.put("payment_methods", paymentMethods);

            Gson gson = new Gson();

            PaymentPayloadMetaDataDTO metadataDTO = gson.fromJson(patientPaymentMetaDataString, PaymentPayloadMetaDataDTO.class);
            Map<String, String> queries = new HashMap<>();
            queries.put("practice_mgmt", metadataDTO.getPracticeMgmt());
            queries.put("practice_id", metadataDTO.getPracticeId());
            queries.put("patient_id", metadataDTO.getPatientId());

            Map<String, String> header = new HashMap<>();
            header.put("transition", "true");

            TransitionDTO transitionDTO = gson.fromJson(paymentTransitionString, TransitionDTO.class);
            WorkflowServiceHelper.getInstance().execute(transitionDTO, makePaymentCallback, payload.toString(), queries, header);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * The Make payment callback.
     */
    WorkflowServiceCallback makePaymentCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            //TODO Go back to start
            finish();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            System.out.print(exceptionMessage);
            finish();
        }
    };

    private LineItem getLineItem()
    {
        LineItem item;
        item = new LineItem();
        item.setName(itemName);
        item.setPrice(amountLong);

        return item;
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