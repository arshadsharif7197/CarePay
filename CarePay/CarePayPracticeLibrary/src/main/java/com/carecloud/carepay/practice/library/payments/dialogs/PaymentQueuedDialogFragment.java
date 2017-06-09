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
 * Created by lmenendez on 4/18/17.
 */

public class PaymentQueuedDialogFragment extends BaseDialogFragment {
    int displayTimeMillis = 2000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_queued_payment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        TextView message = (TextView) view.findViewById(R.id.success_message);
        if(getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE){
            message.setText(Label.getLabel("payment_queued_practice"));
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
