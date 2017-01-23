package com.carecloud.carepaylibray.practice;

import android.support.v4.app.Fragment;

import com.carecloud.carepaylibray.practice.FlowStateInfo;

/**
 * Created by lsoco_user on 11/29/2016.
 */

public class BaseCheckinFragment extends Fragment {

    protected FlowStateInfo flowStateInfo;

    public FlowStateInfo getFlowStateInfo() {
        return flowStateInfo;
    }

    public void setFlowStateInfo(FlowStateInfo flowStateInfo) {
        this.flowStateInfo = flowStateInfo;
    }
}
