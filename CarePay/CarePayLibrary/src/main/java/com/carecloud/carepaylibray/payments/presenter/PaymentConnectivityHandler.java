package com.carecloud.carepaylibray.payments.presenter;

import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;

public interface PaymentConnectivityHandler extends PaymentViewHandler {
    void onPaymentFlowFailure();
}
