package com.carecloud.carepay.patient.base;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.carecloud.carepay.patient.myhealth.BaseViewModel;
import com.carecloud.carepay.patient.session.PatientSessionService;
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

    public void setBasicObservers(BaseViewModel viewModel) {
        viewModel.getLoading().observe(this, isLoading -> {
            if (isLoading) {
                showProgressDialog();
            } else {
                hideProgressDialog();
            }
        });
        viewModel.getErrorMessage().observe(this, this::showErrorNotification);
        viewModel.getSuccessMessage().observe(this, this::showSuccessToast);
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
        stopService(new Intent(this, PatientSessionService.class));
    }
}
