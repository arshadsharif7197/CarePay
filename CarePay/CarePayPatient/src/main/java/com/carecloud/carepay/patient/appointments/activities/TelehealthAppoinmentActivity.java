package com.carecloud.carepay.patient.appointments.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.fragments.TelehealthAppointmentFragment;
import com.carecloud.carepay.patient.appointments.interfaces.TelehealthAppointmentCallback;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.delegate.fragments.ProfileListFragment;
import com.carecloud.carepay.patient.delegate.interfaces.ProfileManagementInterface;
import com.carecloud.carepay.patient.delegate.model.DelegateDto;
import com.carecloud.carepaylibray.CarePayApplication;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentPresenter;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.profile.UserLinks;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * @author pjohnson on 2019-06-13.
 */
public class TelehealthAppoinmentActivity extends BasePatientActivity implements AppointmentViewHandler, TelehealthAppointmentCallback {

    private AppointmentDTO appointmentDto;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_telehealth_appointment);

        appointmentDto = ((CarePayApplication) getApplicationContext()).getAppointmentDTO();
        replaceFragment(TelehealthAppointmentFragment.newInstance(appointmentDto), false);
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        addFragment(R.id.fragmentContainer, fragment, addToBackStack);
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.fragmentContainer, fragment, addToBackStack);
    }

    @Override
    public DTO getDto() {
        return null;
    }

    @Override
    public AppointmentPresenter getAppointmentPresenter() {
        return null;
    }

    @Override
    public void confirmAppointment(boolean showSuccess, boolean isAutoScheduled) {

    }

    @Override
    public void refreshAppointments() {

    }

    @Override
    public void onVideoVisit() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onCancel() {
        finish();
    }
}