package com.carecloud.carepaylibray.payments.presenter;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.base.ISession;

/**
 * Created by lmenendez on 5/18/17
 */

public interface PaymentViewHandler {

    PaymentPresenter getPaymentPresenter();

    Context getContext();

    void navigateToFragment(Fragment fragment, boolean addToBackStack);

    void displayDialogFragment(DialogFragment fragment, boolean addToBackStack);

    void exitPaymentProcess(boolean cancelled, boolean paymentPlanCreated, boolean paymentMade);

    @Nullable
    String getAppointmentId();

    @Nullable
    AppointmentDTO getAppointment();

    ISession getISession();

}
