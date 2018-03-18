package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;

import com.carecloud.carepaylibray.base.BaseDialogFragment;

/**
 * Created by lmenendez on 5/18/17
 */

public abstract class BasePaymentDialogFragment extends BaseDialogFragment {

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        attachCallback(context);
    }

    protected abstract void attachCallback(Context context);
}
