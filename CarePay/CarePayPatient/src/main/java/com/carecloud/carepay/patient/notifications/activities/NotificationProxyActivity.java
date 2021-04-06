package com.carecloud.carepay.patient.notifications.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.menu.MenuPatientActivity;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.messages.activities.MessagesActivity;
import com.carecloud.carepay.patient.patientsplash.SplashActivity;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.SharedPreferenceLabelProvider;
import com.carecloud.carepaylibray.profile.Profile;
import com.carecloud.carepaylibray.profile.ProfileDto;
import com.carecloud.carepaylibray.utils.StringUtil;

/**
 * Created by lmenendez on 7/20/17
 */

public class NotificationProxyActivity extends MenuPatientActivity {

    private Bundle bundle;
    private Intent actualIntent;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.dialog_progress);

        actualIntent = getIntent();
        handleDeepLink(actualIntent);

        if (getAppAuthorizationHelper().getAccessToken() == null) {
            Intent intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra(CarePayConstants.OPEN_NOTIFICATIONS, true);

            if (actualIntent.hasExtra(MessagesActivity.KEY_MESSAGE_ID)) {
                ApplicationPreferences.getInstance().setMessageId(actualIntent.getStringExtra(MessagesActivity.KEY_MESSAGE_ID));
                intent.putExtra(CarePayConstants.OPEN_NOTIFICATIONS, false);
            }
            startActivity(intent);
        } else {
            if (actualIntent.hasExtra(MessagesActivity.KEY_MESSAGE_ID)) {
                Intent intent = new Intent(this, MessagesActivity.class);
                intent.putExtra(MessagesActivity.KEY_MESSAGE_ID, actualIntent.getStringExtra(MessagesActivity.KEY_MESSAGE_ID));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            } else if (StringUtil.isNullOrEmpty(ApplicationPreferences.getInstance().getProfileId())) {
                TransitionDTO transition = getTransitionNotifications();
                getWorkflowServiceHelper().execute(transition, notificationCallback);

            } else {
                //disable notification click functionality when the user is on a different profile than the logged one
                finish();
            }
        }
    }

    private void handleDeepLink(Intent intent) {
        String appLinkAction = intent.getAction();
        Uri appLinkData = intent.getData();
        if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null) {
            String messageId = appLinkData.getQueryParameter(MessagesActivity.KEY_MESSAGE_DEEPLINK_ID);             //Link: https://web.development.gobreeze.com/messages?message_id=121421342134
            if (messageId != null || !messageId.isEmpty()) {
                actualIntent.putExtra(MessagesActivity.KEY_MESSAGE_ID, messageId);
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
            finish();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            showErrorNotification(exceptionMessage);
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
