package com.carecloud.carepaylibray.appointments.presenter;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

/**
 * Created by lmenendez on 5/15/17.
 */

public interface AppointmentViewHandler extends ISession, FragmentActivityInterface {

    AppointmentPresenter getAppointmentPresenter();

    Context getContext();

    void displayDialogFragment(DialogFragment fragment, boolean addToBackStack);

    void confirmAppointment(boolean showSuccess, boolean isAutoScheduled);

    void refreshAppointments();
}
