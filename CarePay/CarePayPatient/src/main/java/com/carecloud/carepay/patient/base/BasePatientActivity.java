package com.carecloud.carepay.patient.base;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.BaseActivity;

public abstract class BasePatientActivity extends BaseActivity {

    @Override
    public void navigateToWorkflow(WorkflowDTO workflowDTO) {
        PatientNavigationHelper.getInstance(this).navigateToWorkflow(workflowDTO);
    }
}
