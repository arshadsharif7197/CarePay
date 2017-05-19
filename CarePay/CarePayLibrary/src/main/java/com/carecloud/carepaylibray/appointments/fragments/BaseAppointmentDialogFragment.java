package com.carecloud.carepaylibray.appointments.fragments;

import android.content.Context;

import com.carecloud.carepaylibray.base.BaseDialogFragment;

/**
 * Created by lmenendez on 5/15/17.
 */

public abstract class BaseAppointmentDialogFragment extends BaseDialogFragment {

    protected abstract void attachCallback(Context context);
}
