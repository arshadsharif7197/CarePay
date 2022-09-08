package com.carecloud.carepay.patient.appointments.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.interfaces.IntelligentSchedulerCallback;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepay.patient.appointments.fragments.IntelligentSchedulerFragment;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeQuestions;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentPresenter;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.common.ConfirmationCallback;
import com.carecloud.carepaylibray.customdialogs.ExitAlertDialog;
import com.carecloud.carepaylibray.demographics.fragments.ConfirmDialogFragment;
import com.carecloud.carepaylibray.interfaces.DTO;

/**
 * @author pjohnson on 2019-06-13.
 */
public class IntelligentSchedulerActivity extends BasePatientActivity implements AppointmentViewHandler, IntelligentSchedulerCallback, ConfirmationCallback {

    private IntelligentSchedulerFragment fragment;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_intelligent_scheduler);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(CarePayConstants.INTELLIGENT_SCHEDULER_QUESTIONS_KEY)) {
            String allQuestions = intent.getExtras().getString(CarePayConstants.INTELLIGENT_SCHEDULER_QUESTIONS_KEY);
            fragment = IntelligentSchedulerFragment.newInstance(allQuestions);
            replaceFragment(fragment, false);
        }

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
    public void onViewAnswerClicked() {
        fragment.onViewAnswerClicked();
    }

    @Override
    public void onExit() {
        ConfirmDialogFragment fragment = ConfirmDialogFragment
                .newInstance(Label.getLabel("intelligent_scheduler_cancel_popup_title"),
                        Label.getLabel("intelligent_scheduler_cancel_popup_label"),
                        Label.getLabel("button_no"),
                        Label.getLabel("button_yes"));
        fragment.setCallback(this);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onBack() {
        fragment.onBack();
    }

    @Override
    public void onBackPressed() {
        //empty implementation
    }

    @Override
    public void onConfirm() {
        finish();
    }
}