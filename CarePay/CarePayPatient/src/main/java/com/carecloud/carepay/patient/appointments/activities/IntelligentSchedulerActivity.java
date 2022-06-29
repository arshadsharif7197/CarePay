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
import com.carecloud.carepaylibray.appointments.models.VisitTypeQuestions;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentPresenter;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.interfaces.DTO;

/**
 * @author pjohnson on 2019-06-13.
 */
public class IntelligentSchedulerActivity extends BasePatientActivity implements AppointmentViewHandler, IntelligentSchedulerCallback {

    private IntelligentSchedulerFragment fragment;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_intelligent_scheduler);
        fragment = IntelligentSchedulerFragment.newInstance(CarePayConstants.INTELLIGENT_SCHEDULER_QUESTIONS);
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
        // empty implementation
    }

    @Override
    public void refreshAppointments() {
        // empty implementation
    }

    @Override
    public void onVisitTypeSelected(VisitTypeDTO visitTypeDTO) {
        setResult(RESULT_OK, getIntent()
                .putExtra(CarePayConstants.INTELLIGENT_SCHEDULER_VISIT_TYPE_KEY, visitTypeDTO));
        finish();
    }

    @Override
    public void onOptionSelected(VisitTypeQuestions visitTypeQuestion) {
        fragment.onVisitOptionSelected(visitTypeQuestion);
    }

    @Override
    public void onExit() {
        finish();
    }

    @Override
    public void onBack() {
        fragment.onBack();
    }


}