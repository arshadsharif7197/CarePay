package com.carecloud.carepay.practice.library.patientmodecheckin.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.carecloud.carepay.practice.library.patientmodecheckin.interfaces.CheckinFlowCallback;
import com.carecloud.carepay.practice.library.patientmodecheckin.interfaces.CheckinFlowState;
import com.carecloud.carepaylibray.demographics.fragments.CheckinDemographicsFragment;

/**
 * Created by lmenendez on 3/6/17.
 */

public class CheckinDemographicsFlowFragment extends CheckinDemographicsFragment {

    private CheckinFlowCallback flowCallback;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            flowCallback = (CheckinFlowCallback) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached context must implement CheckinFlowCallback");
        }
    }


    @Override
    public void onViewCreated(View view, Bundle icicle){
        super.onViewCreated(view, icicle);
        flowCallback.setCheckinFlow(CheckinFlowState.DEMOGRAPHICS, 0, 0);
    }

}
