package com.carecloud.carepay.practice.library.patientmodecheckin.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.carecloud.carepaylibray.checkout.CheckOutInterface;

/**
 * Created by lmenendez on 6/13/17
 */

public class ResponsibilityCheckOutFragment extends ResponsibilityCheckInFragment {

    private CheckOutInterface callback;

    @Override
    public void attachCallback(Context context) {
        super.attachCallback(context);
        try {
                callback = (CheckOutInterface) context;

        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement CheckOutInterface");
        }
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        super.onViewCreated(view, icicle);
        Toolbar toolbar = (Toolbar) view.findViewById(com.carecloud.carepaylibrary.R.id.toolbar_layout);

        if(toolbar != null && !callback.shouldAllowNavigateBack()){
            toolbar.setNavigationIcon(null);
            toolbar.setNavigationOnClickListener(null);
        }
    }

}
