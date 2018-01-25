package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Context;

import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.interfaces.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;

public class PracticePaymentPlanFragment extends BaseCheckinFragment {

    private PaymentsModel paymentsModel;
    private PaymentNavigationCallback callback;

    @Override
    public void attachCallback(Context context) {
        try {
            callback = (PaymentNavigationCallback) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement PaymentNavigationCallback");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (callback == null) {
            attachCallback(getContext());
        }
    }

    @Override
    public DTO getDto() {
        return paymentsModel;
    }
}
