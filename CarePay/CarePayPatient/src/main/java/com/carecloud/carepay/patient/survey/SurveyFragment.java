package com.carecloud.carepay.patient.survey;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BackPressedFragment;
import com.carecloud.carepay.patient.survey.model.SurveyDTO;
import com.carecloud.carepay.patient.survey.model.SurveyModel;
import com.carecloud.carepay.patient.survey.model.SurveyQuestionDTO;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.common.NonSwipeableViewPager;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.marcok.stepprogressbar.StepProgressBar;

/**
 * @author pjohnson on 10/09/18.
 */
public class SurveyFragment extends BaseFragment implements BackPressedFragment {

    private FragmentActivityInterface callback;
    private StepProgressBar stepProgressBar;
    private SurveyDTO surveyDto;
    private int currentQuestion;
    private NonSwipeableViewPager viewPager;
    private Toolbar toolbar;

    public static SurveyFragment newInstance() {
        return new SurveyFragment();
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
        currentQuestion = 0;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_survey, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final SurveyModel surveyModel = surveyDto.getPayload().getSurvey();
        TextView practiceNameTextView = (TextView) view.findViewById(R.id.practiceNameTextView);
        practiceNameTextView.setText(surveyModel.getTitle());
        stepProgressBar = (StepProgressBar) view.findViewById(R.id.surveyProgressBarIndicator);
        stepProgressBar.setNumDots(surveyModel.getQuestions().size());
        stepProgressBar.setCurrentProgressDot(0);
        viewPager = (NonSwipeableViewPager) view.findViewById(R.id.surveyViewPager);
        viewPager.setAdapter(new SurveyPagerAdapter(surveyDto.getPayload().getSurvey().getQuestions()));

        toolbar = setUpToolbar(view);

        Button submitButton = (Button) view.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentQuestion < surveyModel.getQuestions().size() - 1) {
                    saveRate(surveyModel);
                    changeToNextQuestion();
                } else {
                    callback.addFragment(SurveyResultFragment.newInstance(), true);
                }
            }
        });

    }

    protected void saveRate(SurveyModel surveyModel) {
        SurveyQuestionDTO question = surveyModel.getQuestions().get(currentQuestion);
        View view = viewPager.findViewWithTag(question.getUuid());
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.surveyQuestionRatingBar);
        question.setRate(ratingBar.getRating());
    }

    protected void changeToNextQuestion() {
        currentQuestion++;
        stepProgressBar.setCurrentProgressDot(currentQuestion);
        toolbar.setNavigationIcon(R.drawable.icn_nav_back);
        viewPager.setCurrentItem(currentQuestion, true);
    }

    @NonNull
    protected Toolbar setUpToolbar(View view) {
        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        callback.setToolbar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icn_patient_mode_nav_close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentQuestion > 0) {
                    changeToPreviousQuestion();
                } else {
                    getActivity().onBackPressed();
                }
            }
        });
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        //TODO change this
        title.setText("Survey");
        return toolbar;
    }

    protected void changeToPreviousQuestion() {
        currentQuestion--;
        stepProgressBar.setCurrentProgressDot(currentQuestion);
        viewPager.setCurrentItem(currentQuestion, true);
        if (currentQuestion == 0) {
            toolbar.setNavigationIcon(R.drawable.icn_patient_mode_nav_close);
        }
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
            return true;
        }
        return false;
    }
}
