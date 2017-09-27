package com.carecloud.carepay.practice.library.payments.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;

/**
 * Created by lmenendez on 9/27/17
 */

public class PaymentHistoryFragment extends BaseDialogFragment{



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_payment_history, container, false);
    }


}
