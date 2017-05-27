package com.carecloud.carepaylibray.payments.presenter;

import com.carecloud.carepaylibray.payments.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;

/**
 * Created by lmenendez on 5/18/17
 */

public abstract class PaymentPresenter implements PaymentNavigationCallback {

    protected PaymentsModel paymentsModel;
    protected PaymentViewHandler viewHandler;
    protected String patientId;

    public PaymentPresenter(PaymentViewHandler viewHandler, PaymentsModel paymentsModel, String patientId){
        this.viewHandler = viewHandler;
        this.paymentsModel = paymentsModel;
        this.patientId = patientId;
    }

}
