package com.carecloud.carepay.practice.library.payments.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * Created by lmenendez on 9/27/17
 */

public class PaymentHistoryFragment extends BaseDialogFragment{

    private PaymentsModel paymentsModel;



    public static PaymentHistoryFragment newInstance(PaymentsModel paymentsModel){
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);

        PaymentHistoryFragment fragment = new PaymentHistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_payment_history, container, false);
    }


}
