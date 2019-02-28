package com.carecloud.carepay.patient.payment.androidpay;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.carecloud.carepay.patient.payment.androidpay.models.AndroidPayQueuePaymentRecord;
import com.carecloud.carepay.service.library.ServiceGenerator;
import com.carecloud.carepay.service.library.WorkflowService;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.utils.EncryptionUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by lmenendez on 4/19/17
 */

public class AndroidPayQueueUploadService extends IntentService {
    public static final int INTERVAL = 1000 * 60 * 3;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public AndroidPayQueueUploadService() {
        super(AndroidPayQueueUploadService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Gson gson = new Gson();
        BreezeDataBase database = BreezeDataBase.getDatabase(getApplicationContext());
        List<AndroidPayQueuePaymentRecord> queueRecords = database.getAndroidPayDao().getAllRecords();
        for (AndroidPayQueuePaymentRecord queueRecord : queueRecords) {

            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("patient_id", queueRecord.getPatientID());
            queryMap.put("practice_id", queueRecord.getPracticeID());
            queryMap.put("practice_mgmt", queueRecord.getPracticeMgmt());

            String jsonBody = EncryptionUtil.decrypt(this, queueRecord.getPaymentModelJsonEnc(), queueRecord.getPracticeID());
            if (jsonBody == null) {
                jsonBody = queueRecord.getPaymentModelJson();
            }

            TransitionDTO transitionDTO = gson.fromJson(queueRecord.getQueueTransition(), TransitionDTO.class);
            if (transitionDTO.getUrl() == null) {
                database.getAndroidPayDao().delete(queueRecord);
                continue;
            }
            boolean isSubmitted = executeWebCall(transitionDTO, jsonBody, queryMap, queueRecord.getUsername());
            if (isSubmitted) {
                database.getAndroidPayDao().delete(queueRecord);
            }
        }

        queueRecords = database.getAndroidPayDao().getAllRecords();
        if (!queueRecords.isEmpty()) {//only reschedule the service if required
            Intent scheduledService = new Intent(getBaseContext(), AndroidPayQueueUploadService.class);
            PendingIntent pendingIntent = PendingIntent.getService(getBaseContext(), 0x222, scheduledService, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INTERVAL, pendingIntent);
        }
    }

    private boolean executeWebCall(TransitionDTO transitionDTO, String jsonBody, Map<String, String> queryMap, String username) {
        if (StringUtil.isNullOrEmpty(jsonBody)) {
            return false;
        }

        Map<String, String> header = new HashMap<>();
        header.put("x-api-key", HttpConstants.getApiStartKey());
        header.put("username", username);

        WorkflowService workflowService = ServiceGenerator.getInstance().createService(WorkflowService.class, header);
        Call<WorkflowDTO> call = workflowService.executePost(transitionDTO.getUrl(), jsonBody, queryMap);

        try {
            Response<WorkflowDTO> response = call.execute();
            return response.isSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }


}
