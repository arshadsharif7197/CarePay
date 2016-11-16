package com.carecloud.carepay.patient.demographics.misc;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.carecloud.carepay.patient.demographics.fragments.viewpager.DemographicsDocumentsFragmentWthWrapper;

/**
 * Created by lsoco_user on 11/15/2016.
 * Custom click listener for remove insurance
 */

class OnClickRemoveListener implements View.OnClickListener {

    private InsuranceWrapper           insuranceWrapper;
    private InsuranceWrapperCollection wrapperCollection;
    private OnClickRemoveOrAddCallback callback;

    OnClickRemoveListener(InsuranceWrapperCollection wrapperCollection,
                          @Nullable OnClickRemoveOrAddCallback callback) {
        this.wrapperCollection = wrapperCollection;
        this.callback = callback;
    }

    @Override
    public void onClick(View view) {
        Log.v(DemographicsDocumentsFragmentWthWrapper.class.getSimpleName(), "remove clicked");
        wrapperCollection.remove(insuranceWrapper);
        if (callback != null) {
            callback.onAfterRemove();
        }
    }

    /**
     * Set the wrapper from whom view 'Remove' is clicked
     * @param insuranceWrapper The wrapper
     */
    void setInsuranceWrapper(InsuranceWrapper insuranceWrapper) {
        this.insuranceWrapper = insuranceWrapper;
    }
}