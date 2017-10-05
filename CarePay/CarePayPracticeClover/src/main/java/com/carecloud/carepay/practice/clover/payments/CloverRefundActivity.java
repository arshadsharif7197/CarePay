package com.carecloud.carepay.practice.clover.payments;

import android.accounts.Account;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.carecloud.carepay.practice.clover.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.util.CloverAuth;
import com.clover.sdk.v1.Intents;
import com.clover.sdk.v3.payments.Payment;

import java.util.Iterator;
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
    private Long amountLong = 0L;

    private String transactionId;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        setContentView(R.layout.dialog_progress);

        Intent intent = getIntent();
        double amountDouble = intent.getDoubleExtra(CarePayConstants.CLOVER_PAYMENT_AMOUNT, 0.00);
        amountLong = (long) (amountDouble * 100);

        transactionId = intent.getStringExtra("transaction_id");

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
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void onCloverAuthenticated(){
        startRefundIntent();
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


    private void startRefundIntent() {
        Intent intent = new Intent(Intents.ACTION_SECURE_PAY);
        intent.putExtra(Intents.EXTRA_TRANSACTION_TYPE, Intents.TRANSACTION_TYPE_CREDIT);
        try {
            if (amountLong != null) {
                intent.putExtra(Intents.EXTRA_AMOUNT, amountLong*-1);
            } else {
                SystemUtil.showErrorToast(getApplicationContext(), Label.getLabel("clover_payment_amount_error"));
                throw new IllegalArgumentException("amount must not be null");
            }

//            if (transactionId != null) {
//                intent.putExtra(Intents.EXTRA_PAYMENT_ID, transactionId);
//            }else{
//                throw new IllegalArgumentException("Must provide a valid transaction id");
//            }

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


}