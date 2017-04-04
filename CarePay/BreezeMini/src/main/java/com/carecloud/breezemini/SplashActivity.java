package com.carecloud.breezemini;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.carecloud.breezemini.payments.postmodel.BreezePaymentDTO;
import com.carecloud.breezemini.payments.postmodel.PaymentLineItem;
import com.clover.sdk.util.CloverAuth;
import com.clover.sdk.v1.BindingException;
import com.clover.sdk.v1.ClientException;
import com.clover.sdk.v1.Intents;
import com.clover.sdk.v1.ServiceException;
import com.clover.sdk.v3.order.LineItem;
import com.clover.sdk.v3.order.Order;
import com.clover.sdk.v3.order.OrderConnector;
import com.clover.sdk.v3.payments.Result;

import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import io.deepstream.DeepstreamClient;
import io.deepstream.DeepstreamFactory;
import io.deepstream.DeepstreamRuntimeErrorHandler;
import io.deepstream.Event;
import io.deepstream.Topic;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity {
    public static final int creditCardIntentID = 555;
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = false;
    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private static final String TAG = SplashActivity.class.getName();
    private final Handler mHideHandler = new Handler();
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    // private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            //  mControlsView.setVisibility(View.VISIBLE);
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    private Account account;
    private CloverAuth.AuthResult authResult = null;
    private PaymentLineItem[] paymentLineItems;
    private BreezePaymentDTO breezePayment;
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    private OrderConnector orderConnector;
    private Order order;
    private Long amountLong = 0L;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        mVisible = true;

        LoginTask loginTask = new LoginTask();
        loginTask.execute();

//        try {
//            DeepstreamClient ds = new DeepstreamClient("192.168.125.255:6020" );
//            //DeepstreamClient ds = new DeepstreamClient( "10.21.4.45:6020" );
//            ds.login();
//            ds.event.emit( "Clover Account", CloverAccount.getAccount(this).toString() );
//            ds.event.subscribe("make_payment_654654654654", new EventListener() {
//                // receive any serializable data
//                public void onEvent(String eventName, Object payment) {
//                    Gson gson = new Gson();
//                    BreezePaymentDTO breezePayment = gson.fromJson((JsonElement) payment, BreezePaymentDTO.class);
//
//                    // BreezePaymentDTO = (BreezePaymentDTO) payment;
//                    paymentLineItems = breezePayment.getPaymentLineItems();
//                    amountLong = breezePayment.getAmountLong();
//
//                    if (account == null)
//                    {
//                        account = CloverAccount.getAccount(SplashActivity.this);
//                        authenticateCloverAccount();
//                    }
//                    else
//                    {
//                        connect();
//                        new OrderAsyncTask().execute();
//                    }
//
//
//                    // If an account can't be acquired, exit the app
//                    if (account == null) {
//                        Toast.makeText(SplashActivity.this, "No Account", Toast.LENGTH_SHORT).show();
//                        finish();
//                    }
//                }
//
//            });
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
    }

    public class LoginTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            DeepstreamFactory factory = DeepstreamFactory.getInstance();
            DeepstreamClient client = null;
            try {
                client = factory.getClient("192.16:6020");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            client.setRuntimeErrorHandler(new DeepstreamRuntimeErrorHandler() {
                @Override
                public void onException(Topic topic, Event event, String s) {
                    Log.w("dsh", "Error:" + event.toString() + ":" + s);
                }
            });

            return null;
        }
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
                    authResult = CloverAuth.authenticate(SplashActivity.this, account);
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
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
//        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
//        // Show the system bar
//        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
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

    private void startSecurePaymentIntent() {

        new AsyncTask<Void, Void, Result>() {
            @Override
            protected Result doInBackground(Void... params) {


                final Intent intent = new Intent(Intents.ACTION_SECURE_PAY);
                try {
                    if (amountLong != null) {
                        intent.putExtra(Intents.EXTRA_AMOUNT, amountLong);
                    } else {
                        Toast.makeText(SplashActivity.this, "yada yada", Toast.LENGTH_LONG).show();
                    }


                    String orderId = order.getId();

                    if (orderId != null) {
                        intent.putExtra(Intents.EXTRA_ORDER_ID, orderId);
                        //If no order id were passed to EXTRA_ORDER_ID a new empty order would be generated for the payment
                    }

                    intent.putExtra(Intents.EXTRA_CARD_ENTRY_METHODS, Intents.CARD_ENTRY_METHOD_ALL);
                    intent.putExtra(Intents.EXTRA_CARD_DATA_MESSAGE, "Please swipe your card");

                    dumpIntent(intent);
                    startActivityForResult(intent, creditCardIntentID);

                } catch (Exception e) {

                    e.printStackTrace();
                    //  Toast.makeText(SplashActivity.this, "Error", Toast.LENGTH_LONG).show();
                }

                return null;
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

                if(amountLong != null )
                {
                    if(paymentLineItems!=null && paymentLineItems.length > 0){
                        List<LineItem> lineItems = getLineItems();
                        for (LineItem lineItem : lineItems) {
                            orderConnector.addCustomLineItem(order.getId(), lineItem, false);
                        }
                    }else{
                        setResult(RESULT_CANCELED);
                        Toast.makeText(SplashActivity.this, "cancelled", Toast.LENGTH_SHORT).show();
                        finish();
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
                SplashActivity.this.order = order;
                startSecurePaymentIntent();

            }
        }
    }

}
