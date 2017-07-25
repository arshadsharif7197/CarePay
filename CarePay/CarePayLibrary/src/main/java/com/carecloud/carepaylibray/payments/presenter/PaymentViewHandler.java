package com.carecloud.carepaylibray.payments.presenter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

/**
 * Created by lmenendez on 5/18/17
 */

public interface PaymentViewHandler {

    PaymentPresenter getPaymentPresenter();

    Context getContext();

    void navigateToFragment(Fragment fragment, boolean addToBackStack);

    void displayDialogFragment(DialogFragment fragment, boolean addToBackStack);

    void exitPaymentProcess(boolean cancelled);

    @Nullable String getAppointmentId();

}
