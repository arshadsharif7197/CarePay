package com.carecloud.carepay.patient.base;

import android.os.Bundle;
import android.util.Log;

import com.carecloud.carepay.patient.myhealth.BaseViewModel;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.BaseActivity;

public abstract class BasePatientActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Log.d("New Relic", getClass().getName());
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
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            showErrorNotification(errorMessage);
        });
        viewModel.getSuccessMessage().observe(this, successMessage -> {
            showErrorNotification(successMessage);
        });
    }
}
