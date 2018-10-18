package com.carecloud.carepay.mini.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
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

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by lmenendez on 8/24/17
 */

public class QueueUploadService extends IntentService {
    public static final int INTERVAL = 1000 * 60 * 15;

    public QueueUploadService(){
        super(QueueUploadService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        List<QueuePaymentRecord> queuedRecords = QueuePaymentRecord.listAll(QueuePaymentRecord.class);
        for(QueuePaymentRecord record : queuedRecords){
            if(record.isRefund()){
                if (postRefundRequest(record.getPaymentRequestId(), record.getRequestObject())) {
                    record.delete();
                }
            }else {
                if (postPaymentRequest(record.getPaymentRequestId(), record.getRequestObject())) {
                    record.delete();
                }
            }
        }


//        List<QueueUnprocessedPaymentRecord> unprocessedPaymentRecords = QueueUnprocessedPaymentRecord.listAll(QueueUnprocessedPaymentRecord.class);
//        for(QueueUnprocessedPaymentRecord record : unprocessedPaymentRecords){
//            if(record.isRefund()){
//                processQueuedRefundRecord(record);
//            }else {
//                processQueuedPaymentRecord(record);
//            }
//        }

        Intent scheduledService = new Intent(getBaseContext(), QueueUploadService.class);
        PendingIntent pendingIntent = PendingIntent.getService(getBaseContext(), 0x222, scheduledService, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+INTERVAL, pendingIntent);
    }

    private boolean postPaymentRequest(String paymentRequestId, JsonElement requestObject){
        if(StringUtil.isNullOrEmpty(paymentRequestId)){
            return true;// this will clear the empty record
        }

        Gson gson = new Gson();
        if(requestObject == null){
            StreamRecord streamRecord = new StreamRecord();
            streamRecord.setDeepstreamRecordId(paymentRequestId);

            requestObject = gson.toJsonTree(streamRecord);
        }

        String token = AuthorizationUtil.getAuthorizationToken(this).replace("\n", "");

        Log.i(QueueUploadService.class.getName(), "Post Payment Request: " + paymentRequestId);
        Log.i(QueueUploadService.class.getName(), requestObject.toString());

        Call<JsonElement> call = getApplicationHelper().getRestHelper().getPostPaymentCall(token, gson.toJson(requestObject));
        scheduleCallTimeout(call);
        try{
            Response<JsonElement> response = call.execute();
            if(response.isSuccessful()){
                return true;
            }else{
                String errorMessage = RestCallServiceHelper.parseError(response, "error", "message");
                logNewRelicPaymentError(errorMessage, requestObject, false);
                return errorMessage.contains("payment request has already been completed"); //this processing error indicates that the payment should not be retried
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    private boolean postRefundRequest(String paymentRequestId, JsonElement requestObject){
        if(StringUtil.isNullOrEmpty(paymentRequestId)){
            return true;// this will clear the empty record
        }

        Gson gson = new Gson();
        if(requestObject == null){
            StreamRecord streamRecord = new StreamRecord();
            streamRecord.setDeepstreamRecordId(paymentRequestId);

            requestObject = gson.toJsonTree(streamRecord);
        }

        String token = AuthorizationUtil.getAuthorizationToken(this).replace("\n", "");

        Log.i(QueueUploadService.class.getName(), "Post Refund Request: " + paymentRequestId);
        Log.i(QueueUploadService.class.getName(), requestObject.toString());

        Call<JsonElement> call = getApplicationHelper().getRestHelper().getPostRefundCall(token, gson.toJson(requestObject));
        scheduleCallTimeout(call);
        try{
            Response<JsonElement> response = call.execute();
            if(response.isSuccessful()){
                return true;
            }else{
                String errorMessage = RestCallServiceHelper.parseError(response, "error", "message");
                logNewRelicPaymentError(errorMessage, requestObject, true);
                return errorMessage.contains("payment request has already been completed"); //this processing error indicates that the payment should not be retried
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    private void scheduleCallTimeout(final Call call){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!call.isExecuted() && !call.isCanceled()){
                    call.cancel();
                }
            }
        }, 1000 * 30);
    }


    protected ApplicationHelper getApplicationHelper(){
        return (ApplicationHelper) getApplication();
    }

//    private void processQueuedPaymentRecord(final QueueUnprocessedPaymentRecord record){
//        Gson gson = new Gson();
//        final PaymentRequest queuedPaymentRequest = gson.fromJson(record.getPayload(), PaymentRequest.class);
//        DevicePayment.updatePaymentRequest(this, record.getPaymentRequestId(), queuedPaymentRequest, new PaymentRequestCallback() {
//            @Override
//            public void onPaymentRequestUpdate(String paymentRequestId, PaymentRequest paymentRequest) {
//                PaymentRequest ackRequest = DevicePayment.getPaymentAck(paymentRequestId);
//
//                //confirm that we are working with an updated record
//                if(ackRequest != null &&
//                        ackRequest.getTransactionResponse() != null &&
//                        ackRequest.getPaymentMethod() != null &&
//                        ackRequest.getPaymentMethod().getCardData() != null){
//
//
//                    //Lets add this updated record to the send queue
//                    QueuePaymentRecord queuePaymentRecord = new QueuePaymentRecord();
//                    queuePaymentRecord.setPaymentRequestId(paymentRequestId);
//                    queuePaymentRecord.setRefund(false);
//                    queuePaymentRecord.save();
//
//                   //Record is now updated so we no longer need to be keeping this in the queue
//                    record.delete();
//
//                    if(postPaymentRequest(paymentRequestId)){
//                        queuePaymentRecord.delete();
//                    }
//
//                }else{
//                    logNewRelicPaymentError("Queue ACK Failed", paymentRequest, false);
//                }
//            }
//
//            @Override
//            public void onPaymentRequestUpdateFail(String paymentRequestId, JsonElement recordObject) {
//                logNewRelicPaymentError("Queue Update Payment Request Failed", recordObject, false);
//            }
//
//            @Override
//            public void onPaymentConnectionFailure(String message) {
//
//            }
//
//            @Override
//            public void onPaymentRequestDestroyed(String paymentRequestId) {
//
//            }
//        });
//    }

//    private void processQueuedRefundRecord(final QueueUnprocessedPaymentRecord record){
//        Gson gson = new Gson();
//        final RefundRequest queuedRefundRequest = gson.fromJson(record.getPayload(), RefundRequest.class);
//        DeviceRefund.updateRefundRequest(this, record.getPaymentRequestId(), queuedRefundRequest, new RefundRequestCallback() {
//            @Override
//            public void onRefundRequestUpdate(String refundRequestId, RefundRequest refundRequest) {
//                RefundRequest ackRequest = DeviceRefund.getRefundAck(refundRequestId);
//
//                //confirm that we are working with an updated record
//                if(ackRequest != null &&
//                        ackRequest.getTransactionResponse() != null){
//
//                    //Lets add this updated record to the send queue
//                    QueuePaymentRecord queuePaymentRecord = new QueuePaymentRecord();
//                    queuePaymentRecord.setPaymentRequestId(refundRequestId);
//                    queuePaymentRecord.setRefund(true);
//                    queuePaymentRecord.save();
//
//                    //Record is now updated so we no longer need to be keeping this in the queue
//                    record.delete();
//
//                    if(postRefundRequest(refundRequestId)){
//                        queuePaymentRecord.delete();
//                    }
//
//                }else{
//                    logNewRelicPaymentError("Queue ACK Failed", refundRequest, true);
//                }
//            }
//
//            @Override
//            public void onRefundRequestUpdateFail(String refundRequestId, JsonElement recordObject) {
//                logNewRelicPaymentError("Queue Update Refund Request Failed", recordObject, true);
//            }
//
//            @Override
//            public void onRefundConnectionFailure(String message) {
//
//            }
//
//            @Override
//            public void onRefundRequestDestroyed(String refundRequestId) {
//
//            }
//        });
//    }

    private void logNewRelicPaymentError(String errorMessage, Object payload, boolean isRefund){
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("Error Message", errorMessage);
        eventMap.put("Request Payload", payload);

        String eventType = isRefund ? "RefundRequestFail" : "PaymentRequestFail";

        NewRelic.recordCustomEvent(eventType, eventMap);

    }


}
