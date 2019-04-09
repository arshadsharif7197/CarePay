package com.carecloud.carepay.practice.library.homescreen;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.carecloud.carepay.practice.library.homescreen.dtos.AppointmentCountsModel;
import com.carecloud.carepay.practice.library.homescreen.dtos.HomeScreenAppointmentCountsDTO;
import com.carecloud.carepay.service.library.ServiceGenerator;
import com.carecloud.carepay.service.library.WorkflowService;
import com.carecloud.carepay.service.library.base.IApplicationSession;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Response;

public class AppointmentCountUpdateService extends IntentService {
    public static final String KEY_TRANSITION = "transition";
    public static final String KEY_PRACTICE_ID = "practice_id";
    public static final String KEY_PRACTICE_MGMT = "practice_mgmt";

    public static final int INTERVAL = 1000 * 60; //30s

    private String transition;
    private String practiceId;
    private String practiceMgmt;

    public AppointmentCountUpdateService() {
        super(AppointmentCountUpdateService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            practiceId = intent.getStringExtra(KEY_PRACTICE_ID);
            practiceMgmt = intent.getStringExtra(KEY_PRACTICE_MGMT);
            IApplicationSession applicationSession = (IApplicationSession) getApplication();

            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("practice_id", practiceId);
            queryMap.put("practice_mgmt", practiceMgmt);

            Set<String> locationsSavedFilteredIds = applicationSession.getApplicationPreferences()
                    .getSelectedLocationsIds(practiceId,
                            applicationSession.getApplicationMode().getUserPracticeDTO().getUserId());
            if (locationsSavedFilteredIds != null && !locationsSavedFilteredIds.isEmpty()) {
                queryMap.put("location_ids", StringUtil.getListAsCommaDelimitedString(locationsSavedFilteredIds));
            }

            String username = applicationSession.getApplicationMode().getUserPracticeDTO().getUserName();
            if(username == null){
                username = applicationSession.getAppAuthorizationHelper().getCurrUser();
            }

            Map<String, String> header = new HashMap<>();
            header.put("x-api-key", HttpConstants.getApiStartKey());
            header.put("username", username);
            header.put("Authorization", applicationSession.getAppAuthorizationHelper().getIdToken());

            transition = intent.getStringExtra(KEY_TRANSITION);
            TransitionDTO transitionDTO = DtoHelper.getConvertedDTO(TransitionDTO.class, transition);

            if (transitionDTO != null) {
                WorkflowService workflowService = ServiceGenerator.getInstance().createService(WorkflowService.class, header);
                Call<WorkflowDTO> call = workflowService.executeGet(transitionDTO.getUrl(), queryMap);

                try {
                    Response<WorkflowDTO> response = call.execute();
                    if (response.isSuccessful()) {
                        AppointmentCountsModel countsModel = DtoHelper.getConvertedDTO(AppointmentCountsModel.class, response.body());
                        HomeScreenAppointmentCountsDTO appointmentCounts = countsModel.getAppointmentCountsPayload().getAppointmentCounts();
                        ((IApplicationSession) getApplication()).getApplicationPreferences().setAppointmentCounts(appointmentCounts);
                        scheduleAppointmentCountUpdate();
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            scheduleAppointmentCountUpdate();

        }
    }

    private static PendingIntent getPendingService(Context context, IntentParams params) {
        Intent scheduledService = new Intent(context, AppointmentCountUpdateService.class);
        if(params != null) {
            scheduledService.putExtra(AppointmentCountUpdateService.KEY_TRANSITION, params.transition);
            scheduledService.putExtra(AppointmentCountUpdateService.KEY_PRACTICE_ID, params.practiceId);
            scheduledService.putExtra(AppointmentCountUpdateService.KEY_PRACTICE_MGMT, params.practiceMgmt);
        }
        return PendingIntent.getService(context, 0x222, scheduledService, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void scheduleAppointmentCountUpdate() {
        IntentParams params = new IntentParams();
        params.transition = transition;
        params.practiceId = practiceId;
        params.practiceMgmt = practiceMgmt;

        PendingIntent pendingIntent = getPendingService(getBaseContext(), params);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + INTERVAL, pendingIntent);
        }
    }

    public static void cancelScheduledServiceRun(Context context) {
        PendingIntent pendingIntent = getPendingService(context, null);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    private class IntentParams {
        String transition;
        String practiceId;
        String practiceMgmt;
    }

}
