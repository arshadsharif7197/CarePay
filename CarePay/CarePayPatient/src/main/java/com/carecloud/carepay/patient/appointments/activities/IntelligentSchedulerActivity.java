package com.carecloud.carepay.patient.appointments.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.interfaces.IntelligentSchedulerCallback;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.CarePayApplication;
import com.carecloud.carepay.patient.appointments.fragments.IntelligentSchedulerFragment;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentPresenter;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.interfaces.DTO;

/**
 * @author pjohnson on 2019-06-13.
 */
public class IntelligentSchedulerActivity extends BasePatientActivity implements AppointmentViewHandler, IntelligentSchedulerCallback {

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_intelligent_scheduler);
        IntelligentSchedulerFragment fragment = IntelligentSchedulerFragment.newInstance(CarePayConstants.INTELLIGENT_SCHEDULER_QUESTIONS);
        replaceFragment(fragment, false);
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
    public void onVisitTypeSelected(VisitTypeDTO visitTypeDTO) {
        setResult(RESULT_OK, new Intent()
                .putExtra(CarePayConstants.INTELLIGENT_SCHEDULER_QUESTIONS_KEY, visitTypeDTO));
        finish();
    }

    @Override
    public void onCancel() {
        finish();
    }
}