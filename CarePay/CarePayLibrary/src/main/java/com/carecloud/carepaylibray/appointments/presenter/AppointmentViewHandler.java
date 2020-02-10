package com.carecloud.carepaylibray.appointments.presenter;

import android.content.Context;
import androidx.fragment.app.DialogFragment;

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
