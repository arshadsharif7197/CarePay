package com.carecloud.carepaylibray.practice;

import com.carecloud.carepaylibray.base.BaseFragment;

/**
 * Created by lsoco_user on 11/29/2016.
 */

public class BaseCheckinFragment extends BaseFragment {

    protected FlowStateInfo flowStateInfo;

    public FlowStateInfo getFlowStateInfo() {
        return flowStateInfo;
    }

    public void setFlowStateInfo(FlowStateInfo flowStateInfo) {
        this.flowStateInfo = flowStateInfo;
    }

    public boolean navigateBack(){
        return false;
    }
}
