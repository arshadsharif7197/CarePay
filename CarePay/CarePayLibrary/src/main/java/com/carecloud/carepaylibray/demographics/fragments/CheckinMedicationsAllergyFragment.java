package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowCallback;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.medications.fragments.MedicationsAllergyFragment;

/**
 * Created by lmenendez on 3/5/17.
 */

public class CheckinMedicationsAllergyFragment extends MedicationsAllergyFragment {

    private CheckinFlowCallback callback;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            if (context instanceof DemographicsView) {
                callback = ((DemographicsView) context).getPresenter();
            } else {
                callback = (CheckinFlowCallback) context;
            }
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached context must implement CheckinFlowCallback");
        }
    }


    @Override
    public void onViewCreated(View view, Bundle icicle){
        super.onViewCreated(view, icicle);
        callback.setCheckinFlow(CheckinFlowState.MEDICATIONS_AND_ALLERGIES, 1, 1);
    }

}
