package com.carecloud.carepay.practice.library.patientmodecheckin.fragments;

import android.support.v4.app.Fragment;

import com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity;

/**
 * Created by lsoco_user on 11/29/2016.
 */

public class BaseCheckinFragment extends Fragment {

    protected PatientModeCheckinActivity.FlowStateInfo flowStateInfo;

    public PatientModeCheckinActivity.FlowStateInfo getFlowStateInfo() {
        return flowStateInfo;
    }

    public void setFlowStateInfo(PatientModeCheckinActivity.FlowStateInfo flowStateInfo) {
        this.flowStateInfo = flowStateInfo;
    }
}
