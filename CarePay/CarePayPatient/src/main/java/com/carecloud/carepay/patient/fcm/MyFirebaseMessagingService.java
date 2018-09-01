package com.carecloud.carepay.patient.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.CarePayPatientApplication;
import com.carecloud.carepay.patient.notifications.activities.NotificationProxyActivity;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.unifiedauth.UnifiedSignInResponse;
import com.carecloud.carepaylibray.fcm.NotificationModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pjohnson on 4/04/17
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
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
            }
            // Sets an ID for the notification
            int notificationId = 001;
            // Gets an instance of the NotificationManager service
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // Builds the notification and issues it.
            notificationManager.notify(notificationId, builder.build());
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

//        TransitionDTO badgeCounterTransition = ApplicationPreferences.getInstance().getBadgeCounterTransition();
//        Map<String, String> queryMap = new HashMap<>();
//        ((CarePayPatientApplication) getApplicationContext()).getWorkflowServiceHelper()
//                .execute(badgeCounterTransition, new WorkflowServiceCallback() {
//                    @Override
//                    public void onPreExecute() {
//
//                    }
//
//                    @Override
//                    public void onPostExecute(WorkflowDTO workflowDTO) {
//                        UnifiedSignInResponse dto = DtoHelper
//                                .getConvertedDTO(UnifiedSignInResponse.class, workflowDTO);
//                        ApplicationPreferences.getInstance()
//                                .setMessagesBadgeCounter(dto.getPayload().getBadgeCounter().getMessages());
//                        ApplicationPreferences.getInstance()
//                                .setFormsBadgeCounter(dto.getPayload().getBadgeCounter().getPendingForms());
//                    }
//
//                    @Override
//                    public void onFailure(String exceptionMessage) {
//
//                    }
//                }, queryMap);
    }


}
