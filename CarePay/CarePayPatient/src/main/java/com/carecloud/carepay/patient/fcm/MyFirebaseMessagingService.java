package com.carecloud.carepay.patient.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.carecloud.carepay.patient.patientsplash.SplashActivity;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.fcm.NotificationModel;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by pjohnson on 4/04/17.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "Pablo";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            NotificationModel notificationModel = getRemoteMessageInfo(remoteMessage.getData());
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                            .setAutoCancel(true)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Breeze")
                            .setContentText(notificationModel.getAlert());
            Intent resultIntent = new Intent(this, SplashActivity.class);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            builder.setContentIntent(resultPendingIntent);
            // Sets an ID for the notification
            int notificationId = 001;
            // Gets an instance of the NotificationManager service
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // Builds the notification and issues it.
            notificationManager.notify(notificationId, builder.build());
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


}