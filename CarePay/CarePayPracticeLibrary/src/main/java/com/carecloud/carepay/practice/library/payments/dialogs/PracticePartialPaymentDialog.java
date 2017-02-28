package com.carecloud.carepay.practice.library.payments.dialogs;

import android.content.Context;

import com.carecloud.carepaylibray.payments.fragments.PartialPaymentDialog;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;


/**
 * Created by prem_mourya on 10/4/2016.
 */
public class PracticePartialPaymentDialog extends PartialPaymentDialog {

    public PracticePartialPaymentDialog(Context context, PaymentsModel paymentsDTO) {
        super(context, paymentsDTO);
    }
}