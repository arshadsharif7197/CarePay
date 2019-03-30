package com.carecloud.carepay.mini.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.carecloud.carepay.mini.interfaces.ApplicationHelper;
import com.carecloud.carepay.mini.models.queue.QueuePaymentRecord;
import com.carecloud.carepay.mini.services.carepay.RestCallServiceHelper;
import com.carecloud.carepay.mini.utils.StringUtil;
import com.carecloud.shamrocksdk.payment.models.StreamRecord;
import com.carecloud.shamrocksdk.utils.AuthorizationUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.newrelic.agent.android.NewRelic;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by lmenendez on 8/24/17
 */

public class QueueUploadService extends IntentService {
    public static final int INTERVAL = 1000 * 60 * 15;

    public QueueUploadService() {
        super(QueueUploadService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            List<QueuePaymentRecord> queuedRecords = QueuePaymentRecord.listAll(QueuePaymentRecord.class);
            for (QueuePaymentRecord record : queuedRecords) {
                if (record.isRefund()) {
                    if (postRefundRequest(record.getPaymentRequestId(), record.getRequestObject())) {
                        record.delete();
                    }
                } else {
                    if (postPaymentRequest(record.getPaymentRequestId(), record.getRequestObject())) {
                        record.delete();
                    }
                }
            }
        }catch (Exception ex){
            logNewRelicPaymentError(ex.getMessage(), "Queue Upload Service Exception", false);
        }

        Intent scheduledService = new Intent(getBaseContext(), QueueUploadService.class);
        PendingIntent pendingIntent = PendingIntent.getService(getBaseContext(), 0x222, scheduledService, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INTERVAL, pendingIntent);

        stopSelf();
    }

    private boolean postPaymentRequest(String paymentRequestId, JsonElement requestObject) {
        if (StringUtil.isNullOrEmpty(paymentRequestId)) {
            return true;// this will clear the empty record
        }

        Gson gson = new Gson();
        if (requestObject == null) {
            StreamRecord streamRecord = new StreamRecord();
            streamRecord.setDeepstreamRecordId(paymentRequestId);

            requestObject = gson.toJsonTree(streamRecord);
        }

        String token = AuthorizationUtil.getAuthorizationToken(this).replace("\n", "");

        Log.i(QueueUploadService.class.getName(), "Post Payment Request: " + paymentRequestId);
        Log.i(QueueUploadService.class.getName(), requestObject.toString());

        Call<JsonElement> call = getApplicationHelper().getRestHelper().getPostPaymentCall(token, gson.toJson(requestObject));
        scheduleCallTimeout(call);
        try {
            Response<JsonElement> response = call.execute();
            if (response.isSuccessful()) {
                return true;
            } else {
                String errorMessage = RestCallServiceHelper.parseError(response, "error", "message");
                logNewRelicPaymentError(errorMessage, requestObject, false);
                return errorMessage.contains("payment request has already been completed"); //this processing error indicates that the payment should not be retried
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    private boolean postRefundRequest(String paymentRequestId, JsonElement requestObject) {
        if (StringUtil.isNullOrEmpty(paymentRequestId)) {
            return true;// this will clear the empty record
        }

        Gson gson = new Gson();
        if (requestObject == null) {
            StreamRecord streamRecord = new StreamRecord();
            streamRecord.setDeepstreamRecordId(paymentRequestId);

            requestObject = gson.toJsonTree(streamRecord);
        }

        String token = AuthorizationUtil.getAuthorizationToken(this).replace("\n", "");

        Log.i(QueueUploadService.class.getName(), "Post Refund Request: " + paymentRequestId);
        Log.i(QueueUploadService.class.getName(), requestObject.toString());

        Call<JsonElement> call = getApplicationHelper().getRestHelper().getPostRefundCall(token, gson.toJson(requestObject));
        scheduleCallTimeout(call);
        try {
            Response<JsonElement> response = call.execute();
            if (response.isSuccessful()) {
                return true;
            } else {
                String errorMessage = RestCallServiceHelper.parseError(response, "error", "message");
                logNewRelicPaymentError(errorMessage, requestObject, true);
                return errorMessage.contains("payment request has already been completed"); //this processing error indicates that the payment should not be retried
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    private void scheduleCallTimeout(final Call call) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                call.cancel();
            }
        }, 1000 * 40);
    }

    protected ApplicationHelper getApplicationHelper() {
        return (ApplicationHelper) getApplication();
    }


    private void logNewRelicPaymentError(String errorMessage, Object payload, boolean isRefund) {
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("Error Message", errorMessage);
        eventMap.put("Request Payload", payload);

        String eventType = isRefund ? "RefundRequestFail" : "PaymentRequestFail";

        NewRelic.recordCustomEvent(eventType, eventMap);

    }


}
