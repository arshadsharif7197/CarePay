package com.carecloud.carepaylibray.appointments.presenter;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import com.carecloud.carepaylibray.base.ISession;

/**
 * Created by lmenendez on 5/15/17.
 */

public interface AppointmentViewHandler extends ISession {

    AppointmentPresenter getAppointmentPresenter();

    Context getContext();

    void navigateToFragment(Fragment fragment, boolean addToBackStack);

    void displayDialogFragment(DialogFragment fragment, boolean addToBackStack);

    void confirmAppointment();

    void refreshAppointments();
}
