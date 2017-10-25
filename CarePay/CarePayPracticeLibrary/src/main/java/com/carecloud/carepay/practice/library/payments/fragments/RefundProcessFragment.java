package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.payments.interfaces.PracticePaymentNavigationCallback;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryItem;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * Created by lmenendez on 10/25/17
 */

public class RefundProcessFragment extends BaseDialogFragment {

    private PracticePaymentNavigationCallback callback;

    private PaymentHistoryItem historyItem;


    /**
     * Create a new instance of RefundProcessFragment
     * @param historyItem history item
     * @return new instance of RefundProcessFragment
     */
    public static RefundProcessFragment newInstance(PaymentHistoryItem historyItem){
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, historyItem);

        RefundProcessFragment fragment = new RefundProcessFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            callback = (PracticePaymentNavigationCallback) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached context must implement PracticePaymentNavigationCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        Bundle args = getArguments();
        historyItem = DtoHelper.getConvertedDTO(PaymentHistoryItem.class, args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_refund_process, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){

    }
}
