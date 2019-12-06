package com.carecloud.carepay.practice.library.session;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.ServerErrorDTO;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.session.WarningSessionActivity;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * @author pjohnson on 2019-07-03.
 */
public class PracticeWarningSessionActivity extends WarningSessionActivity {

    private static final int FULLSCREEN_VALUE = 0x10000000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | FULLSCREEN_VALUE;
        decorView.setSystemUiVisibility(uiOptions);
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onContinueButton() {
        Intent serviceIntent = new Intent(this, PracticeSessionService.class);
        startService(serviceIntent);
        finish();
    }

    @Override
    protected void restartApp() {
        TransitionDTO logoutTransition = DtoHelper.getConvertedDTO(TransitionDTO.class, getIntent().getExtras());
        logOut(logoutTransition, false);
    }

    private void logOut(TransitionDTO transition, boolean shouldContinue) {
        getWorkflowServiceHelper().execute(transition, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                getAppAuthorizationHelper().setUser(null);
                if (shouldContinue) {
                    PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
                } else {
                    TransitionDTO patientModeTransition = ApplicationPreferences.getInstance().getPatientModeTransition();
                    logOut(patientModeTransition, true);
                }
            }

            @Override
            public void onFailure(ServerErrorDTO serverErrorDto) {
                hideProgressDialog();
                Log.e(getString(R.string.alert_title_server_error), serverErrorDto.getMessage().getBody().getError().getMessage());
            }
        });
    }
}
