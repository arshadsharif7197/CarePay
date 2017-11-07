package com.carecloud.carepay.practice.library.payments.dialogs;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseDialogFragment;

/**
 * Created by lmenendez on 4/18/17
 */

public class PaymentQueuedDialogFragment extends BaseDialogFragment {
    private static final String KEY_IS_REFUND = "key_is_refund";

    int displayTimeMillis = 2000;
    boolean isRefund = false;

    public static PaymentQueuedDialogFragment newInstance(boolean isRefund){
        Bundle args = new Bundle();
        args.putBoolean(KEY_IS_REFUND, isRefund);

        PaymentQueuedDialogFragment fragment = new PaymentQueuedDialogFragment();
        fragment.setArguments(args);
        return fragment;
    };

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        Bundle args = getArguments();
        if(args != null){
            isRefund = args.getBoolean(KEY_IS_REFUND, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_queued_payment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        TextView message = (TextView) view.findViewById(R.id.success_message);
        if(getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE){
            if(isRefund){
                message.setText(Label.getLabel("refund_queued_practice"));
            }else {
                message.setText(Label.getLabel("payment_queued_practice"));
            }
            displayTimeMillis = 4000;
        }else{
            message.setText(Label.getLabel("payment_queued_patient"));
            displayTimeMillis = 2000;
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, displayTimeMillis);
    }

}
