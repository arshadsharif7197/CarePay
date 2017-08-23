package com.carecloud.carepay.patient.purchases.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepaylibray.base.BaseFragment;

/**
 * Created by lmenendez on 2/8/17
 */

public class PurchaseFragment extends BaseFragment {

    private View noPurchaseLayout;// this should be available here to access it for show/hide from other methods

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_purchase, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        noPurchaseLayout = view.findViewById(R.id.no_purchase_layout);
        noPurchaseLayout.setVisibility(View.VISIBLE);
    }
}
