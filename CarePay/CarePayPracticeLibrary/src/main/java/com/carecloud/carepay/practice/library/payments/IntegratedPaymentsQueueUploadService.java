package com.carecloud.carepay.practice.library.payments;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import androidx.annotation.Nullable;

import com.carecloud.carepay.practice.library.base.BreezeDataBase;
import com.carecloud.carepay.practice.library.payments.models.IntegratedPaymentQueueRecord;
import com.carecloud.carepay.service.library.ServiceGenerator;
import com.carecloud.carepay.service.library.WorkflowService;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by lmenendez on 11/14/17
 */

public class IntegratedPaymentsQueueUploadService extends IntentService {

    public static final int INTERVAL = 1000 * 60 * 3;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public IntegratedPaymentsQueueUploadService() {
        super(IntegratedPaymentsQueueUploadService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Gson gson = new Gson();

        BreezeDataBase database = BreezeDataBase.getDatabase(getApplicationContext());
        List<IntegratedPaymentQueueRecord> queueRecords = database.getIntegratedAndroidPayDao().getAllRecords();
        for (IntegratedPaymentQueueRecord queueRecord : queueRecords) {

            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("patient_id", queueRecord.getPatientID());
            queryMap.put("practice_id", queueRecord.getPracticeID());
            queryMap.put("practice_mgmt", queueRecord.getPracticeMgmt());
            queryMap.put("deepstream_record_id", queueRecord.getDeepstreamId());

            if (queueRecord.isRecordOnly()) {
                queryMap.put("record_only", String.valueOf(queueRecord.isRecordOnly()));
            }


            TransitionDTO transitionDTO = gson.fromJson(queueRecord.getQueueTransition(), TransitionDTO.class);
            boolean isSubmitted = executeWebCall(transitionDTO, queryMap, queueRecord.getUsername());
            if (isSubmitted) {
                database.getIntegratedAndroidPayDao().delete(queueRecord);
            }
        }

        queueRecords = database.getIntegratedAndroidPayDao().getAllRecords();

        if (!queueRecords.isEmpty()) {//only schedule if required
            Intent scheduledService = new Intent(getBaseContext(), IntegratedPaymentQueueRecord.class);
            PendingIntent pendingIntent = PendingIntent.getService(getBaseContext(), 0x222, scheduledService, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INTERVAL, pendingIntent);
        }

    }


    private boolean executeWebCall(TransitionDTO transitionDTO, Map<String, String> queryMap, String username) {
        Map<String, String> header = new HashMap<>();
        header.put("x-api-key", HttpConstants.getApiStartKey());
        header.put("username", username);

        WorkflowService workflowService = ServiceGenerator.getInstance().createService(WorkflowService.class, header);
        Call<WorkflowDTO> call = workflowService.executePost(transitionDTO.getUrl(), queryMap);

        try {
            Response<WorkflowDTO> response = call.execute();
            return response.isSuccessful();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
    }

}
