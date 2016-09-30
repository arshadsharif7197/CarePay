package com.carecloud.carepayclover.notification;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.clover.sdk.v1.app.AppNotification;
import com.clover.sdk.v1.app.AppNotificationReceiver;
import com.clover.sdk.v3.order.OrderConnector;

public class NotificationReceiver extends AppNotificationReceiver {
    public final static String NOTIFICATION_ACTION = "checkin";
    private Context context;

    public NotificationReceiver() {

    }

    @Override
    public void onReceive(Context context, AppNotification notification) {
        this.context = context;
        // Check Our App Notification
        if (notification.appEvent.contains(NOTIFICATION_ACTION)) {
            Log.d("Push Notification",notification.payload);
            Intent intent=new Intent("NEW_ORDER_FOUND");
            intent.putExtra("checkedin_notification", notification.payload);
            context.sendBroadcast(intent);
        }
    }
}
