package com.carecloud.carepay.patient.payment.androidpay;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.carecloud.carepay.patient.db.BreezeDataBase;
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

public class AndroidPayQueueUploadService extends JobIntentService {
    public static final int JOB_ID = 100;

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, AndroidPayQueueUploadService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
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
