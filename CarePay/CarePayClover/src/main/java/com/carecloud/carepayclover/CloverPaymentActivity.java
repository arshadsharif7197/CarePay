package com.carecloud.carepayclover;

import android.accounts.Account;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.carecloud.carepaylibray.base.BaseServiceGenerator;
import com.carecloud.carepaylibray.payment.services.PaymentsService;
import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.util.CloverAuth;
import com.clover.sdk.v1.BindingException;
import com.clover.sdk.v1.ClientException;
import com.clover.sdk.v1.Intents;
import com.clover.sdk.v1.ServiceException;
import com.clover.sdk.v3.inventory.InventoryConnector;
import com.clover.sdk.v3.inventory.Item;
import com.clover.sdk.v3.inventory.PriceType;
import com.clover.sdk.v3.order.Order;
import com.clover.sdk.v3.order.OrderConnector;
import com.clover.sdk.v3.payments.Payment;
import com.clover.sdk.v3.payments.Result;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CloverPaymentActivity extends AppCompatActivity {
    public static final int creditCardIntentID = 555;
    private static final String TAG = CloverPaymentActivity.class.getName();
    private Account account;
    private OrderConnector orderConnector;
    private InventoryConnector inventoryConnector;
    private Order order;
    public Long amount=new Long(2000);;
    private CloverAuth.AuthResult authResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clover_payment);
        Intent intent = getIntent();
        if (intent.hasExtra("total_pay")) {
            double totalAmount = intent.getDoubleExtra("total_pay_balance", 20.00);
            amount = new Long((long) (totalAmount*100));
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

        // For testing Purpose only
//        RelativeLayout rlayout = (RelativeLayout) findViewById(R.id.activity_clover_payment);
//        rlayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startSecurePaymentIntent();
//            }
//        });

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

    // Establishes a connection with the connectors
    private void connect() {
        disconnect();
        if (account != null) {
            orderConnector = new OrderConnector(this, account, null);
            orderConnector.connect();
            inventoryConnector = new InventoryConnector(this, account, null);
            inventoryConnector.connect();
        }
    }

    // Disconnects from the connectors
    private void disconnect() {
        if (orderConnector != null) {
            orderConnector.disconnect();
            orderConnector = null;
        }
        if (inventoryConnector != null) {
            inventoryConnector.disconnect();
            inventoryConnector = null;
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
            protected void onPostExecute(Void aVoid) {
                if (authResult.authToken != null && authResult.baseUrl != null) {
                    connect();
                    new OrderAsyncTask().execute();
                }
            }
        }.execute();
    }



    // Creates a new order w/ the first inventory item
    private class OrderAsyncTask extends AsyncTask<Void, Void, Order> {

        @Override
        protected final Order doInBackground(Void... params) {
            Order mOrder;
            List<Item> merchantItems;
            Item mItem;
            try {
                // Create a new order
                mOrder = orderConnector.createOrder(new Order());
                // Grab the items from the merchant's inventory
                merchantItems = inventoryConnector.getItems();
                // If there are no item's in the merchant's inventory, then call a toast and return null
                if (merchantItems.isEmpty()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.empty_inventory), Toast.LENGTH_SHORT).show();
                    finish();
                    return null;
                }
                // Taking the first item from the inventory
                mItem = merchantItems.get(0);
                // Add this item to the order, must add using its PriceType
                if (mItem.getPriceType() == PriceType.FIXED) {
                    orderConnector.addFixedPriceLineItem(mOrder.getId(), mItem.getId(), null, null);
                } else if (mItem.getPriceType() == PriceType.PER_UNIT) {
                    orderConnector.addPerUnitLineItem(mOrder.getId(), mItem.getId(), 1, null, null);
                } else { // The item must be of a VARIABLE PriceType
                    orderConnector.addVariablePriceLineItem(mOrder.getId(), mItem.getId(), 5, null, null);
                }
                // Update local representation of the order
                mOrder = orderConnector.getOrder(mOrder.getId());

                return mOrder;
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
                    if (amount != null) {
                        intent.putExtra(Intents.EXTRA_AMOUNT, amount);
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.amount_required), Toast.LENGTH_LONG).show();
                    }


                    String orderId = order.getId();

                    if (orderId != null) {
                        intent.putExtra(Intents.EXTRA_ORDER_ID, orderId);
                        //If no order id were passed to EXTRA_ORDER_ID a new empty order would be generated for the payment
                    }


                    intent.putExtra(Intents.EXTRA_CARD_ENTRY_METHODS, Intents.CARD_ENTRY_METHOD_ALL);
                     // intent.putExtra(Intents.EXTRA_TRANSACTION_TYPE, Intents.TRANSACTION_TYPE_CARD_DATA); // For future testing
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
                    postCloverPayment(payment);
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.payment_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void postCloverPayment(Payment payment) {
        //To DO
        PaymentsService paymentsService = (new BaseServiceGenerator(this)).createServicePractice(PaymentsService.class);
        Call<Object> call = paymentsService.updateCarePayPayment(payment.getJSONObject());
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                Intent rotateInIntent = new Intent(CloverPaymentActivity.this, CloverMainActivity.class);
                rotateInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(rotateInIntent);
                CloverPaymentActivity.this.finish();
                Toast.makeText(getApplicationContext(), "CareCloud Payment Success", Toast.LENGTH_SHORT).show();
                Log.d(TAG, response.toString());
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }


    /**
     * Debugs the intent we created
     *
     * @param i - intent to debug contents of
     */
    public static void dumpIntent(Intent i) {

        Bundle bundle = i.getExtras();
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
