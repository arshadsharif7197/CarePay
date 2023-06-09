package com.carecloud.carepay.patient.base;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.carecloud.carepaylibray.CarePayApplication;
import com.carecloud.carepaylibray.common.BaseViewModel;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.session.SessionedActivityInterface;

public abstract class BasePatientActivity extends BaseActivity implements SessionedActivityInterface {

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Log.d("New Relic", getClass().getName());
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Log.e("Session", "manageSession");
            if (manageSession()) {
                ((CarePayPatientApplication) getApplicationContext()).restartSession(this);
            }
        });
    }

    @Override
    public void navigateToWorkflow(WorkflowDTO workflowDTO) {
        PatientNavigationHelper.navigateToWorkflow(this, workflowDTO);
    }

    /**
     * @param workflowDTO the workflow
     * @param info        a bundle with extra info
     */
    public void navigateToWorkflow(WorkflowDTO workflowDTO, Bundle info) {
        PatientNavigationHelper.navigateToWorkflow(this, workflowDTO, info);
    }

    @Override
    public boolean manageSession() {
        return true;
    }

    @Override
    public TransitionDTO getLogoutTransition() {
        return null;
    }

    @Override
    protected void stopSessionService() {
        ((CarePayApplication) getApplication()).cancelSession();
    }
}
