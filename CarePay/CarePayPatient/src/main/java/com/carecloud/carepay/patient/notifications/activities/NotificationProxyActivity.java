package com.carecloud.carepay.patient.notifications.activities;

import android.content.Intent;
import android.os.Bundle;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.menu.MenuPatientActivity;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.patientsplash.SplashActivity;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.ServerErrorDTO;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.profile.Profile;
import com.carecloud.carepaylibray.profile.ProfileDto;
import com.carecloud.carepaylibray.utils.StringUtil;

/**
 * Created by lmenendez on 7/20/17
 */

public class NotificationProxyActivity extends MenuPatientActivity {

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.dialog_progress);
        if (getAppAuthorizationHelper().getAccessToken() == null) {
            Intent intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(CarePayConstants.OPEN_NOTIFICATIONS, true);
            startActivity(intent);
        } else {
            if (StringUtil.isNullOrEmpty(ApplicationPreferences.getInstance().getProfileId())) {
                TransitionDTO transition = getTransitionNotifications();
                getWorkflowServiceHelper().execute(transition, notificationCallback);
            } else {
                //disable notification click functionality when the user is on a different profile than the logged one
                finish();
            }
        }
    }

    private WorkflowServiceCallback notificationCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PatientNavigationHelper.setAccessPaymentsBalances(false);
            navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(ServerErrorDTO serverErrorDto) {
            showErrorNotification(serverErrorDto.getMessage().getBody().getError().getMessage());
            finish();
        }
    };

    @Override
    protected void onProfileChanged(ProfileDto profile) {
        //NA
    }

    @Override
    protected Profile getCurrentProfile() {
        return null;
    }

}
