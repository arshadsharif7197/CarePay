package com.carecloud.carepay.practice.clover.notification;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.clover.sdk.v1.app.AppNotification;
import com.clover.sdk.v1.app.AppNotificationReceiver;

/*
 * Created by Jahirul Bhuiyan on 10/17/2016.
 * This is the Push notification receiver from server
 * This class handle checking-in and alert counter
 * after receiving this class broadcast a local broadcast for any component that implemented
 * */

public class NotificationReceiver extends AppNotificationReceiver {

    private  final static String NOTIFICATION_ACTION = "checkin";
    private Context context;

    public NotificationReceiver() {

    }

    @Override
    public void onReceive(Context context, AppNotification notification) {
        this.context = context;
        // Check Our App Notification
        if (notification.appEvent.contains(NOTIFICATION_ACTION)) {
            Log.d("Push Notification",notification.payload);
            Intent intent=new Intent("NEW_CHECKEDIN_NOTIFICATION");
            intent.putExtra("checkedin_notification", notification.payload);
            context.sendBroadcast(intent);
        }
    }
}
