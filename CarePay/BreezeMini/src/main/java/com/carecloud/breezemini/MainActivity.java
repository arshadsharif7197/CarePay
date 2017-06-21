package com.carecloud.breezemini;

import android.accounts.Account;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.os.OperationCanceledException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.carecloud.breezemini.payments.postmodel.cloverpayment.CloverPaymentDTO;
import com.carecloud.breezemini.payments.postmodel.credittransaction.CreditTransactionDTO;
import com.carecloud.breezemini.payments.postmodel.processpayment.PaymentLineItem;
import com.carecloud.breezemini.services.ServiceCallback;
import com.carecloud.breezemini.services.ServiceHelper;
import com.carecloud.breezemini.services.ServiceRequestDTO;
import com.carecloud.breezemini.services.ServiceResponseDTO;
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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.deepstream.DeepstreamClient;
import io.deepstream.DeepstreamFactory;
import io.deepstream.DeepstreamRuntimeErrorHandler;
import io.deepstream.Event;
import io.deepstream.EventListener;
import io.deepstream.LoginResult;
import io.deepstream.Topic;

/**
 * Created by Kavin Kannan on
 * An full-screen activity that processes clover payments through
 * ACTION_SECURE_PAY intent.
 */
public class MainActivity extends AppCompatActivity {
    /**
     * The constant creditCardIntentID.
     */
    public static final int creditCardIntentID = 555;
    private OrderConnector orderConnector;
    private Order order;
    private Long amountLong = 0L;
    private Account account;
    private CloverAuth.AuthResult authResult = null;
    private DeepstreamClient client = null;
    private PaymentLineItem[] paymentLineItems;
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        setSystemUiVisibility();

        geAuthToken();
        if (account == null) {
            account = CloverAccount.getAccount(MainActivity.this);
            authenticateCloverAccount();
        }
    }

    private void geAuthToken() {
        Map<String, String> queryMap = new HashMap<>();
        Map<String, String> header = new HashMap<>();
        header.put("transition", "true");
        header.put("x-api-key", HttpConstants.getApiStartKey());
        ServiceHelper serviceHelper =  new ServiceHelper();
        ServiceRequestDTO serviceRequestDTO = new ServiceRequestDTO();
        serviceRequestDTO.setUrl(BuildConfig.API_START_URL);
        serviceRequestDTO.setMethod("GET");
        serviceHelper.execute(serviceRequestDTO, signInCallback, queryMap, header);
    }

    private ServiceCallback signInCallback = new ServiceCallback() {

        @Override
        public void onPreExecute() {
        }


        @Override
        public void onPostExecute(ServiceResponseDTO serviceResponseDTO) {

        }


        @Override
        public void onFailure(String exceptionMessage) {

        }

    };

    @Override
    protected void onRestart() {
        super.onRestart();
        setSystemUiVisibility();
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
                Toast.makeText(this, R.string.no_account_string, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }

    }

    @Override
    protected void onPause() {
        disconnect();
        super.onPause();

    }

    private void  setSystemUiVisibility(){
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    /**
     * The type Login task.
     */
    public class LoginTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            DeepstreamFactory factory = DeepstreamFactory.getInstance();
            try {
                client = factory.getClient(BuildConfig.DEEPSTREAM_URL);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            client.setRuntimeErrorHandler(new DeepstreamRuntimeErrorHandler() {
                @Override
                public void onException(Topic topic, Event event, String errorString) {
                    Log.w("dsh", "Error:" + event.toString() + ":" + errorString);
                }
            });

            JsonObject authData = new JsonObject();
            authData.addProperty("device_type", "CloverMini");
            authData.addProperty("device_id", Build.SERIAL);

            LoginResult result = client.login(authData);
            client.event.subscribe(BuildConfig.START_SECUREPAYMENT_TOPIC, new EventListener() {
                @Override
                public void onEvent(String topic, final Object paymentRequestInfo) {
                    setTransactionInfo(topic, paymentRequestInfo);

                    if (authResult != null && authResult.authToken != null && authResult.baseUrl != null) {
                        connect();
                        new OrderAsyncTask().execute();
                    }
                }

            });

            return result.loggedIn();
        }
    }

    private void setTransactionInfo(String topic, final Object paymentRequestInfo) {
        Log.i("startSwipe event", topic);
        Log.i("Payment Info", toPrettyFormat(paymentRequestInfo.toString()));

        try {
            Gson gson = new Gson();
            CreditTransactionDTO creditTransaction = gson.fromJson((JsonElement) paymentRequestInfo, CreditTransactionDTO.class);
            if (creditTransaction.getAmount().equalsIgnoreCase("0")) {
                Toast.makeText(MainActivity.this, R.string.amount_greater_than_0_string, Toast.LENGTH_LONG);
//                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.pacman_die);
//                mp.start();
            } else {
                amountLong = (long) (Double.parseDouble(creditTransaction.getAmount()) * 100) ;
//                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.coins);
//                mp.start();
            }
            paymentLineItems = new PaymentLineItem[]{};
        } catch (Exception e) {
            Log.e("Payment Info", e.getMessage());
        }

    }

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

    private void startSecurePaymentIntent() {

        new AsyncTask<Void, Void, Result>() {
            @Override
            protected Result doInBackground(Void... params) {


                final Intent intent = new Intent(Intents.ACTION_SECURE_PAY);
                try {
                    if (amountLong != null) {
                        intent.putExtra(Intents.EXTRA_AMOUNT, amountLong);
                    }else {
                        Toast.makeText(MainActivity.this, R.string.valid_amount_required_string, Toast.LENGTH_LONG);
                        throw new IllegalArgumentException(getString(R.string.amount_must_not_be_null_string));
                    }

                    String orderId = order.getId();

                    if (orderId != null) {
                        intent.putExtra(Intents.EXTRA_ORDER_ID, orderId);
                        //If no order id were passed to EXTRA_ORDER_ID a new empty order would be generated for the payment
                    }

                    intent.putExtra(Intents.EXTRA_CARD_ENTRY_METHODS, Intents.CARD_ENTRY_METHOD_ALL);
                    dumpIntent(intent);
                    if(intent.resolveActivity(getPackageManager()) != null){
                        startActivityForResult(intent, creditCardIntentID);
                    }else{
                        Toast.makeText(MainActivity.this, R.string.stationpay_application_missing_string, Toast.LENGTH_LONG);
                        throw new IllegalArgumentException(getString(R.string.no_activity_found_string)+Intents.ACTION_SECURE_PAY);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
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
                try {
                    Payment payment = data.getParcelableExtra(Intents.EXTRA_PAYMENT);


                    Gson gson = new Gson();
                    String jsonString = payment.getJSONObject().toString();
                    CloverPaymentDTO cloverPayment = gson.fromJson(jsonString, CloverPaymentDTO.class);

                    client.event.emit(BuildConfig.PAYMENT_CONFIRMATION_TOPIC, cloverPayment);
                } catch (Exception e) {
                    Log.e("CloverPaymentResponse", e.getMessage());
                }


            }
        } else if (resultCode == RESULT_CANCELED) {
            String manufacturer = Build.MANUFACTURER;
            if (BuildConfig.DEBUG && manufacturer.equals("unknown")) {
                Toast.makeText(getApplicationContext(), R.string.uknown_manufacturer_string, Toast.LENGTH_SHORT).show();
                setResult(resultCode);
            } else {
                Toast.makeText(getApplicationContext(), R.string.payment_cancelled_string, Toast.LENGTH_SHORT).show();
                setResult(resultCode);
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.payment_failed_string, Toast.LENGTH_SHORT).show();
            setResult(resultCode);
        }

        reset();

    }

    private class OrderAsyncTask extends AsyncTask<Void, Void, Order> {

        @Override
        protected final Order doInBackground(Void... params) {
            Order order;

            try {
                // Create a new order
                order = orderConnector.createOrder(new Order());

                if (amountLong != null) {
                    if (paymentLineItems != null && paymentLineItems.length > 0) {
                        List<LineItem> lineItems = getLineItems();
                        for (LineItem lineItem : lineItems) {
                            orderConnector.addCustomLineItem(order.getId(), lineItem, false);
                        }
                    } else {
                        setResult(RESULT_CANCELED);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(MainActivity.this, R.string.cancelled_string, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
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
                MainActivity.this.order = order;
                disconnect();
                if (amountLong != null && amountLong  > 0) {
                    startSecurePaymentIntent();
                }

            }
        }
    }

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


    private void authenticateCloverAccount() {
        new AsyncTask<Void, String, CloverAuth.AuthResult>() {

            @Override
            protected void onProgressUpdate(String... values) {
                String logString = values[0];
                Log.d(TAG, logString);
            }

            @Override
            protected CloverAuth.AuthResult doInBackground(Void... params) {
                try {
                    publishProgress("Requesting auth token");
                    authResult = CloverAuth.authenticate(MainActivity.this, account);
                    if (authResult == null) {
                        Log.e(TAG, "Null AuthResult");
                    } else if (authResult.authToken == null) {
                        Log.e(TAG, "No auth token in AuthResult");
                    }

                    return authResult;
                } catch (OperationCanceledException e) {
                    Log.e(TAG, "Authentication cancelled", e);
                } catch (Exception e) {
                    publishProgress("Error retrieving merchant info from server" + e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(CloverAuth.AuthResult authResult) {
                super.onPostExecute(authResult);
                if (authResult != null && authResult.authToken != null && authResult.baseUrl != null) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            new LoginTask().execute();
                        }
                    });
                }
            }
        }.execute();
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

    /**
     * To pretty format string.
     *
     * @param jsonString the json string
     * @return the string
     */
    private String toPrettyFormat(String jsonString) {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(jsonString).getAsJsonObject();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(json);

        return prettyJson;
    }

    private void reset(){
        amountLong = null;
    }

}
