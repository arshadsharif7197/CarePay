package com.carecloud.carepay.patient.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.notifications.activities.NotificationProxyActivity;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.platform.AndroidPlatform;
import com.carecloud.carepay.service.library.platform.Platform;
import com.carecloud.carepaylibray.fcm.NotificationModel;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by pjohnson on 4/04/17
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "Breeze";
    private static final String NOTIFICATION_CHANNEL_ID = "breeze_notifications";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(notificationManager != null && Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            NotificationChannel channel = notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID);
            if(channel == null){
                channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                        getString(R.string.notification_channel_name),
                        NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            NotificationModel notificationModel = getRemoteMessageInfo(remoteMessage.getData());
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.notification_icon)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText(notificationModel.getAlert())
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(notificationModel.getAlert()));
            Intent resultIntent = new Intent(this, NotificationProxyActivity.class);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            builder.setContentIntent(resultPendingIntent);
            builder.setChannelId(NOTIFICATION_CHANNEL_ID);

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            }
            // Sets an ID for the notification
            int notificationId = 001;
            // Gets an instance of the NotificationManager service
            // Builds the notification and issues it.
            if(notificationManager != null) {
                notificationManager.notify(notificationId, builder.build());
            }
            updateBadgeCounters();

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }

    private NotificationModel getRemoteMessageInfo(Map<String, String> data) {
        NotificationModel notificationModel = new NotificationModel();
        notificationModel.setAlert(data.get("alert"));
        notificationModel.setAppointmentId(data.get("appointment_id"));
        notificationModel.setPatientId(data.get("patient_id"));
        notificationModel.setPracticeId(data.get("practice_id"));
        notificationModel.setPracticeMgmt(data.get("practice_mgmt"));
        return notificationModel;
    }

    public void updateBadgeCounters() {
        Intent intent = new Intent(CarePayConstants.UPDATE_BADGES_BROADCAST);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);
        saveTokenToSP(token);
    }

    private void saveTokenToSP(String refreshedToken) {
        ((AndroidPlatform) Platform.get()).openDefaultSharedPreferences()
                .edit().putString(CarePayConstants.FCM_TOKEN, refreshedToken).apply();
    }


}
