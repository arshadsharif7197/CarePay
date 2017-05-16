package com.carecloud.carepaylibray.appointments.presenter;

import android.content.Context;

import com.carecloud.carepaylibray.appointments.AppointmentNavigationCallback;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;

/**
 * Created by lmenendez on 5/15/17
 */

public abstract class AppointmentPresenter implements AppointmentNavigationCallback {

    protected AppointmentsResultModel appointmentsResultModel;
    protected AppointmentViewHandler viewHandler;



    public AppointmentPresenter(AppointmentViewHandler viewHandler, AppointmentsResultModel appointmentsResultModel){
        this.viewHandler = viewHandler;
        this.appointmentsResultModel = appointmentsResultModel;
    }

    protected Context getContext(){
        return viewHandler.getContext();
    }


}
