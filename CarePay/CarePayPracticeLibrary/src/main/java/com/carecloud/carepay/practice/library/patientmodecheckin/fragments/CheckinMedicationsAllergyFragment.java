package com.carecloud.carepay.practice.library.patientmodecheckin.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.carecloud.carepay.practice.library.patientmodecheckin.interfaces.CheckinFlowCallback;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.medications.fragments.MedicationsAllergyFragment;

/**
 * Created by lmenendez on 3/5/17.
 */

public class CheckinMedicationsAllergyFragment extends MedicationsAllergyFragment {

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
        flowCallback.setCheckinFlow(CheckinFlowState.MEDICATIONS_AND_ALLERGIES, 0, 0);
    }

}
