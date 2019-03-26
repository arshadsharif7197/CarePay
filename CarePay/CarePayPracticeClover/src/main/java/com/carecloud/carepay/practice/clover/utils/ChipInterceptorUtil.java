package com.carecloud.carepay.practice.clover.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.carecloud.carepaylibray.utils.StringUtil;

public class ChipInterceptorUtil {
    public static final String CLOVER_CARD_INTERCEPT_ACTION = "com.clover.intent.action.broadcast.CARD_INSERTED";

    private static final int RECEIVER_PRIORITY = 200;
    private static InterceptReceiver interceptReceiver;

    private static ChipInterceptorUtil instance;

    public static ChipInterceptorUtil getInstance() {
        if (instance == null) {
            instance = new ChipInterceptorUtil();
        }
        return instance;
    }

    private ChipInterceptorUtil() {
    }

    public void startIntercept(Context context, String... actions) {
        IntentFilter intentFilter = new IntentFilter();
        for (String action : actions) {
            intentFilter.addAction(action);
        }
        intentFilter.setPriority(RECEIVER_PRIORITY);
        if (interceptReceiver == null) {
            interceptReceiver = new InterceptReceiver(actions);
        }

        context.registerReceiver(interceptReceiver, intentFilter);
        Log.d(this.getClass().getName(), "Interceptor Receiver registered");
    }

    public void stopIntercept(Context context) {
        if (interceptReceiver != null) {
            context.unregisterReceiver(interceptReceiver);
            Log.d(this.getClass().getName(), "Interceptor Receiver unregistered");
        }
    }


    private class InterceptReceiver extends BroadcastReceiver {
        private String[] actions;

        public InterceptReceiver(String[] actions) {
            this.actions = actions;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent != null ? intent.getAction() : null;
            for (String action : actions) {
                if (!StringUtil.isNullOrEmpty(action) &&
                        action.equals(intentAction) &&
                        isOrderedBroadcast()) {
                    abortBroadcast();
                    return;
                }
            }
        }
    }
}
