package com.carecloud.carepay.patient.appointments.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.fragments.QuestionAnswerTallyFragment;
import com.carecloud.carepay.patient.appointments.interfaces.QuestionAnswersTallyCallback;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.appointments.models.SchedulerAnswerTally;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentPresenter;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.interfaces.DTO;

import java.util.List;

/**
 * @author pjohnson on 2019-06-13.
 */
public class QuestionAnswerTallyActivity extends BasePatientActivity implements AppointmentViewHandler, QuestionAnswersTallyCallback {

    private List<SchedulerAnswerTally> allQuestions;
    private QuestionAnswerTallyFragment fragment;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_question_answer_tally);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(CarePayConstants.INTELLIGENT_SCHEDULER_QUESTIONS_ANSWERS_TALLY_KEY)) {
            allQuestions = (List<SchedulerAnswerTally>) intent.getExtras().get(CarePayConstants.INTELLIGENT_SCHEDULER_QUESTIONS_ANSWERS_TALLY_KEY);
            fragment = QuestionAnswerTallyFragment.newInstance(allQuestions);
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

    }

    @Override
    public void refreshAppointments() {

    }

    @Override
    public void onBack() {
        finish();
    }
}