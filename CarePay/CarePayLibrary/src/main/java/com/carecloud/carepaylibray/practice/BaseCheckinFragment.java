package com.carecloud.carepaylibray.practice;

import android.content.Context;

import com.carecloud.carepaylibray.base.BaseFragment;

/**
 * Created by lsoco_user on 11/29/2016.
 */

public abstract class BaseCheckinFragment extends BaseFragment {

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

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        attachCallback(context);
    }

    public abstract void attachCallback(Context context);

}
