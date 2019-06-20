package com.carecloud.carepaylibray.utils;

import com.payeezy.sdk.payeezytokenised.RequestTask;


/**
 * Created by lmenendez on 4/2/17.
 */

public class PayeezyRequestTask extends RequestTask {
    private AuthorizeCreditCardCallback callback;

    public PayeezyRequestTask(AuthorizeCreditCardCallback callback) {
        super();
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (callback != null) {
            callback.onAuthorizeCreditCard(result);
        }
    }

    public void setCallback(AuthorizeCreditCardCallback callback) {
        this.callback = callback;
    }

    public interface AuthorizeCreditCardCallback {
        void onAuthorizeCreditCard(String response);

    }
}
