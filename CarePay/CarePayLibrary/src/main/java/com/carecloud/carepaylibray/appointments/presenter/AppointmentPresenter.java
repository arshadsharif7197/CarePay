package com.carecloud.carepaylibray.appointments.presenter;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.carecloud.carepaylibray.appointments.interfaces.AppointmentNavigationCallback;
import com.carecloud.carepaylibray.appointments.interfaces.ScheduleAppointmentInterface;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;

/**
 * Created by lmenendez on 5/15/17
 */

public abstract class AppointmentPresenter implements AppointmentNavigationCallback, ScheduleAppointmentInterface {

    protected AppointmentsResultModel appointmentsResultModel;
    protected AppointmentViewHandler viewHandler;
    protected PaymentsModel paymentsModel;

    /**
     * Constructor
     *
     * @param viewHandler             viewHandler
     * @param appointmentsResultModel appointment model
     * @param paymentsModel           payment model
     */
    public AppointmentPresenter(AppointmentViewHandler viewHandler,
                                AppointmentsResultModel appointmentsResultModel,
                                PaymentsModel paymentsModel) {
        this.viewHandler = viewHandler;
        this.appointmentsResultModel = appointmentsResultModel;
        this.paymentsModel = paymentsModel;
    }

    protected Context getContext() {
        return viewHandler.getContext();
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        viewHandler.addFragment(fragment, addToBackStack);
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        viewHandler.replaceFragment(fragment, addToBackStack);
    }

    @Override
    public void showErrorToast(String exceptionMessage) {
        viewHandler.showErrorToast(exceptionMessage);
    }

    @Override
    public void setToolbar(Toolbar toolbar) {
        viewHandler.setToolbar(toolbar);
    }

    @Override
    public void showSuccessToast(String successMessage) {
        viewHandler.showSuccessToast(successMessage);
    }

    @Override
    public void setActionBarTitle(String title) {
        viewHandler.setActionBarTitle(title);
    }

    @Override
    public void displayDialogFragment(DialogFragment fragment, boolean addToBackStack) {
        viewHandler.displayDialogFragment(fragment, addToBackStack);
    }

    @Override
    public DTO getDto() {
        return appointmentsResultModel;
    }
}
