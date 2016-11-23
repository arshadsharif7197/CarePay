package com.carecloud.carepaylibray.demographics.misc;

import android.support.annotation.Nullable;
import android.view.View;

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