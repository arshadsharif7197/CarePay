package com.carecloud.carepay.patient.appointments;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.carecloud.carepay.patient.myhealth.BaseViewModel;
import com.carecloud.carepay.patient.notifications.models.NotificationsDTO;

/**
 * @author pjohnson on 2020-01-14.
 */
public class AppointmentViewModel extends BaseViewModel {

    private MutableLiveData<NotificationsDTO> appointmentsDtoObservable;

    public AppointmentViewModel(@NonNull Application application) {
        super(application);
    }
}
