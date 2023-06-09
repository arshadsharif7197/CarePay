package com.carecloud.carepay.patient.survey;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BackPressedFragmentInterface;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.common.NonSwipeableViewPager;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.survey.model.SurveyDTO;
import com.carecloud.carepaylibray.survey.model.SurveyModel;
import com.marcok.stepprogressbar.StepProgressBar;

/**
 * @author pjohnson on 10/09/18.
 */
public class SurveyFragment extends BaseFragment implements BackPressedFragmentInterface {

    private FragmentActivityInterface callback;
    private StepProgressBar stepProgressBar;
    private SurveyDTO surveyDto;
    private int currentQuestion;
    private NonSwipeableViewPager viewPager;
    private Toolbar toolbar;
    private Button submitButton;
    private TextView progressIndicatorTextView;
    private boolean comesFromNotification;

    public static SurveyFragment newInstance(String patientId, boolean comesFromNotifications) {
        Bundle args = new Bundle();
        args.putString(CarePayConstants.PATIENT_ID, patientId);
        args.putBoolean(CarePayConstants.NOTIFICATIONS_FLOW, comesFromNotifications);
        SurveyFragment fragment = new SurveyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivityInterface) {
            callback = (FragmentActivityInterface) context;
        } else {
            throw new ClassCastException("attached context must implement FragmentActivityInterface");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        surveyDto = (SurveyDTO) callback.getDto();
        comesFromNotification = getArguments().getBoolean(CarePayConstants.NOTIFICATIONS_FLOW);
        currentQuestion = 0;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_survey, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final SurveyModel surveyModel = surveyDto.getPayload().getSurvey();
        TextView practiceNameTextView = view.findViewById(R.id.practiceNameTextView);
        practiceNameTextView.setText(surveyDto.getPayload().getPracticeInformation().get(0).getPracticeName());
        stepProgressBar = view.findViewById(R.id.surveyProgressBarIndicator);
        stepProgressBar.setNumDots(surveyModel.getQuestions().size());
        stepProgressBar.setCurrentProgressDot(0);
        viewPager = view.findViewById(R.id.surveyViewPager);
        viewPager.setAdapter(new SurveyPagerAdapter(surveyDto.getPayload().getSurvey().getQuestions()));

        toolbar = setUpToolbar(view);

        submitButton = view.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuestion < surveyModel.getQuestions().size() - 1) {
                    changeToNextQuestion();
                    if (currentQuestion == surveyModel.getQuestions().size() - 1) {
                        submitButton.setText(Label.getLabel("survey.patientMode.form.button.submitButton"));
                    }
                    updateProgressTextView();
                } else {
                    Bundle args = getArguments();
                    callback.addFragment(SurveyResultFragment.newInstance(args
                                    .getString(CarePayConstants.PATIENT_ID), comesFromNotification)
                            , true);
                }
            }
        });
        progressIndicatorTextView = view.findViewById(R.id.progressIndicatorTextView);
        updateProgressTextView();

    }

    @NonNull
    protected Toolbar setUpToolbar(View view) {
        final Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
        callback.setToolbar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icn_patient_mode_nav_close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentQuestion > 0) {
                    changeToPreviousQuestion();
                    updateProgressTextView();
                } else {
                    getActivity().onBackPressed();
                }
            }
        });
        TextView title = toolbar.findViewById(R.id.respons_toolbar_title);
        title.setText(Label.getLabel("survey.form.screen.title.survey"));
        return toolbar;
    }

    private void changeToNextQuestion() {
        currentQuestion++;
        stepProgressBar.setCurrentProgressDot(currentQuestion);
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        viewPager.setCurrentItem(currentQuestion, true);
    }

    private void changeToPreviousQuestion() {
        currentQuestion--;
        stepProgressBar.setCurrentProgressDot(currentQuestion);
        viewPager.setCurrentItem(currentQuestion, true);
        if (currentQuestion == 0) {
            toolbar.setNavigationIcon(R.drawable.icn_patient_mode_nav_close);
        }
        submitButton.setText(Label.getLabel("survey.form.button.title.nextQuestion"));
    }

    private void updateProgressTextView() {
        progressIndicatorTextView.setText(String
                .format(Label.getLabel("survey.form.pageIndicator.text.questionNumberOfNumber"),
                        currentQuestion + 1, surveyDto.getPayload().getSurvey().getQuestions().size()));
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }

    @Override
    public boolean onBackPressed() {
        if (currentQuestion > 0) {
            changeToPreviousQuestion();
            updateProgressTextView();
            return true;
        }
        return false;
    }
}
