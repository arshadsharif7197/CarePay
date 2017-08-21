package com.carecloud.carepaylibray.appointments.presenter;

import android.content.Context;

import com.carecloud.carepaylibray.appointments.interfaces.AppointmentNavigationCallback;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;

/**
 * Created by lmenendez on 5/15/17
 */

public abstract class AppointmentPresenter implements AppointmentNavigationCallback {

    protected AppointmentsResultModel appointmentsResultModel;
    protected AppointmentViewHandler viewHandler;
    protected PaymentsModel paymentsModel;


    public AppointmentPresenter(AppointmentViewHandler viewHandler, AppointmentsResultModel appointmentsResultModel, PaymentsModel paymentsModel){
        this.viewHandler = viewHandler;
        this.appointmentsResultModel = appointmentsResultModel;
        this.paymentsModel = paymentsModel;
    }

    protected Context getContext(){
        return viewHandler.getContext();
    }


}
