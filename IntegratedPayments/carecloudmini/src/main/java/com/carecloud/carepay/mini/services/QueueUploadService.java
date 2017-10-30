package com.carecloud.carepay.mini.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.carecloud.carepay.mini.interfaces.ApplicationHelper;
import com.carecloud.carepay.mini.models.queue.QueuePaymentRecord;
import com.carecloud.carepay.mini.services.carepay.RestCallServiceHelper;
import com.carecloud.carepay.mini.utils.StringUtil;
import com.carecloud.shamrocksdk.payment.models.StreamRecord;
import com.carecloud.shamrocksdk.utils.AuthorizationUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.IOException;
import java.util.List;

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
            if(postPaymentRequest(record.getPaymentRequestId())){
                record.delete();
            }
        }

        Intent scheduledService = new Intent(getBaseContext(), QueueUploadService.class);
        PendingIntent pendingIntent = PendingIntent.getService(getBaseContext(), 0x222, scheduledService, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+INTERVAL, pendingIntent);
    }

    private boolean postPaymentRequest(String paymentRequestId){
        if(StringUtil.isNullOrEmpty(paymentRequestId)){
            return true;// this will clear the empty record
        }

        StreamRecord streamRecord = new StreamRecord();
        streamRecord.setDeepstreamRecordId(paymentRequestId);

        Gson gson = new Gson();
        String token = AuthorizationUtil.getAuthorizationToken(this).replace("\n", "");

        Call<JsonElement> call = getApplicationHelper().getRestHelper().getPostPaymentCall(token, gson.toJson(streamRecord));
        try{
            Response<JsonElement> response = call.execute();
            if(response.isSuccessful()){
                return true;
            }else{
                String errorMessage = RestCallServiceHelper.parseError(response, "error", "message");
                return errorMessage.contains("payment request has already been completed"); //this processing error indicates that the payment should not be retried
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    protected ApplicationHelper getApplicationHelper(){
        return (ApplicationHelper) getApplication();
    }

}
