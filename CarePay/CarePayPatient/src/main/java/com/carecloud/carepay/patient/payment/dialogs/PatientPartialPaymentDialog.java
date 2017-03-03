package com.carecloud.carepay.patient.payment.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.carecloud.carepaylibray.payments.fragments.PartialPaymentDialog;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;


/**
 * Created by prem_mourya on 10/4/2016.
 */
public class PatientPartialPaymentDialog extends PartialPaymentDialog {
    private Context context;

    public PatientPartialPaymentDialog(Context context, PaymentsModel paymentsDTO) {
        super(context, paymentsDTO);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90);
        getWindow().setAttributes(params);
    }

}