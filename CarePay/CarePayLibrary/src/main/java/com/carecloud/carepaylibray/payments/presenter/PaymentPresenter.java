package com.carecloud.carepaylibray.payments.presenter;

import com.carecloud.carepaylibray.payments.interfaces.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;

/**
 * Created by lmenendez on 5/18/17
 */

public abstract class PaymentPresenter implements PaymentNavigationCallback {

    protected PaymentsModel paymentsModel;
    protected PaymentConnectivityHandler viewHandler;
    protected String patientId;

    /**
     * Constructor
     * @param viewHandler Payment View Handler
     * @param paymentsModel Payment Model DTO
     * @param patientId Selected Patient ID
     */
    public PaymentPresenter(PaymentConnectivityHandler viewHandler, PaymentsModel paymentsModel, String patientId){
        this.viewHandler = viewHandler;
        this.paymentsModel = paymentsModel;
        this.patientId = patientId;
    }

    public void setPaymentPresenter(PaymentConnectivityHandler viewHandler, PaymentsModel paymentsModel, String patientId){
        this.viewHandler = viewHandler;
        this.paymentsModel = paymentsModel;
        this.patientId = patientId;
    }

}
