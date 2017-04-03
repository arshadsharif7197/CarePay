package com.carecloud.carepaylibray.utils;

import android.content.Context;

import com.payeezy.sdk.payeezytokenised.RequestTask;


/**
 * Created by lmenendez on 4/2/17.
 */

public class PayeezyRequestTask extends RequestTask {
    public void setCallback(AuthorizeCreditCardCallback callback) {
        this.callback = callback;
    }

    public interface AuthorizeCreditCardCallback{
        void onAuthorizeCreditCard(String response);
    }

    private AuthorizeCreditCardCallback callback;

    public PayeezyRequestTask(Context context) {
        super(context);
    }

    public PayeezyRequestTask(Context context, AuthorizeCreditCardCallback callback){
        super(context);
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if(callback!=null){
            callback.onAuthorizeCreditCard(result);
        }
    }
}
