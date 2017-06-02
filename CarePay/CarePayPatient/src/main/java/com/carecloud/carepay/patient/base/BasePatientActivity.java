package com.carecloud.carepay.patient.base;

import android.os.Bundle;
import android.util.Log;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.newrelic.agent.android.NewRelic;

public abstract class BasePatientActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle icicle){
        super.onCreate(icicle);
        NewRelic.setInteractionName(getClass().getName());
        Log.d("New Relic", getClass().getName());
    }

    @Override
    public void navigateToWorkflow(WorkflowDTO workflowDTO) {
        PatientNavigationHelper.navigateToWorkflow(this, workflowDTO);
    }
}
