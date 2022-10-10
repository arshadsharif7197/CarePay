package com.carecloud.carepay.practice.library.appointments.createappointment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;


import com.carecloud.carepay.practice.library.appointments.interfaces.IntelligentSchedulerCallback;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.createappointment.visittype.VisitTypePagerAdapter;
import com.carecloud.carepaylibray.appointments.models.SchedulerAnswerTally;
import com.carecloud.carepaylibray.appointments.models.VisitTypeQuestions;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.customcomponents.CarePayViewPager;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class IntelligentSchedulerFragment extends BaseDialogFragment {

    protected Button nextButton;
    private String allQuestions = "";
    private IntelligentSchedulerCallback callback;
    private CarePayViewPager viewPager;
    private VisitTypeQuestions currentQuestion;
    private VisitTypePagerAdapter questionPagerAdapter;
    private VisitTypeQuestions selectedOption;
    private ImageView cancel_img;
    private List<SchedulerAnswerTally> schedulerAnswerTallyList = new ArrayList<>();
    private SchedulerAnswerTally schedulerAnswerTally;
    private IntelligentSchedulerQuestionFragment questionsFragment;


    public static IntelligentSchedulerFragment newInstance(String intelligentQuestions) {
        Bundle args = new Bundle();
        args.putString(CarePayConstants.INTELLIGENT_SCHEDULER_QUESTIONS_KEY, intelligentQuestions);
        IntelligentSchedulerFragment intelligentSchedulerFragment = new IntelligentSchedulerFragment();
        intelligentSchedulerFragment.setArguments(args);
        return intelligentSchedulerFragment;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Bundle arguments = getArguments();
        if (arguments != null) {
            allQuestions = arguments.getString(CarePayConstants.INTELLIGENT_SCHEDULER_QUESTIONS_KEY);
            currentQuestion = new Gson().fromJson(allQuestions, VisitTypeQuestions.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_intelligent_scheduler, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle icicle) {
        setupTitleViews(view);
        initializeViews(view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivityInterface) {
            callback = (IntelligentSchedulerCallback) context;
        } else {
            throw new ClassCastException("context must implements FragmentActivityInterface");
        }
    }

    private void setupTitleViews(View view) {
        Toolbar toolbar = view.findViewById(R.id.intelligent_scheduler_toolbar);
        cancel_img = view.findViewById(R.id.cancel_img);
        cancel_img.setVisibility(View.INVISIBLE);
        if (toolbar != null) {
            TextView title = toolbar.findViewById(R.id.intelligent_scheduler_title);
            TextView exit = toolbar.findViewById(R.id.intelligent_scheduler_exit);
            title.setText(Label.getLabel("intelligent_scheduler_title"));
            exit.setText(Label.getLabel("demographics_exit"));
            toolbar.setTitle("");

            cancel_img.setOnClickListener(v -> callback.onBack());
            exit.setOnClickListener((view1) -> callback.onExit());
        }
    }

    private void initializeViews(View view) {
        viewPager = view.findViewById(R.id.visit_type_question_pager);
        viewPager.setPagingEnabled(false);
        nextButton = view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel_img.setVisibility(View.VISIBLE);
                schedulerAnswerTally = new SchedulerAnswerTally();
                schedulerAnswerTally.setQuestion(currentQuestion.getName());
                schedulerAnswerTally.setAnswer(selectedOption.getName());
                schedulerAnswerTallyList.add(schedulerAnswerTally);

                nextButton.setEnabled(false);
                if (nextButton.getText().toString().equalsIgnoreCase(Label.getLabel("next_question_button_text"))) {
                    startQuestionFragment(selectedOption.getChildrens().get(0));
                } else {
                    callback.onVisitTypeSelected(selectedOption.getVisittype());
                }

                IntelligentSchedulerQuestionFragment intelligentSchedulerQuestionFragment = ((IntelligentSchedulerQuestionFragment) questionPagerAdapter.getItem(questionPagerAdapter.getCount() - 1));
                intelligentSchedulerQuestionFragment.showViewAnswerButton(true);
            }
        });
        nextButton.setEnabled(false);
        setupViewPager();
    }

    private void setupViewPager() {
        questionPagerAdapter = new VisitTypePagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(questionPagerAdapter);
        startQuestionFragment(currentQuestion);
    }

    private void startQuestionFragment(VisitTypeQuestions currentVisitTypeQuestion) {
        currentQuestion = currentVisitTypeQuestion;
        questionsFragment = IntelligentSchedulerQuestionFragment
                .newInstance(new Gson().toJson(currentQuestion));
        questionPagerAdapter.addFragment(questionsFragment);
        viewPager.setCurrentItem(questionPagerAdapter.getCount() - 1, true);
    }

    public void onVisitOptionSelected(VisitTypeQuestions visitTypeQuestions) {
        selectedOption = visitTypeQuestions;
        // getting message from activity that some option has been selected
        updateNextButton(visitTypeQuestions);
        nextButton.setEnabled(true);
    }

    private void updateNextButton(VisitTypeQuestions visitTypeQuestions) {
        if (visitTypeQuestions.getChildrens() != null && visitTypeQuestions.getChildrens().size() != 0) {
            nextButton.setText(Label.getLabel("next_question_button_text"));
        } else {
            nextButton.setText(Label.getLabel("common.button.continue"));
        }
    }

    public void onBack() {
        if (questionPagerAdapter.getCount() > 1) {
            int previousIndex = questionPagerAdapter.getCount() - 2;
            questionPagerAdapter.removeFragment();
            schedulerAnswerTallyList.remove(schedulerAnswerTallyList.size() - 1);

            IntelligentSchedulerQuestionFragment intelligentSchedulerQuestionFragment = ((IntelligentSchedulerQuestionFragment) questionPagerAdapter.getItem(previousIndex));
            if (schedulerAnswerTallyList.size() == 0) {
                intelligentSchedulerQuestionFragment.showViewAnswerButton(false);
            } else {
                intelligentSchedulerQuestionFragment.showViewAnswerButton(true);
            }

            currentQuestion = intelligentSchedulerQuestionFragment.getVisitTypeQuestion();
            selectedOption = intelligentSchedulerQuestionFragment.getVisitTypeOption();
            viewPager.setCurrentItem(previousIndex, true);
            updateNextButton(intelligentSchedulerQuestionFragment.getVisitTypeQuestion());
            if (questionPagerAdapter.getCount() == 1) {
                cancel_img.setVisibility(View.INVISIBLE);
            }
            nextButton.setEnabled(true);
            intelligentSchedulerQuestionFragment.setVisitTypeOption(selectedOption);
        }

    }

    public List<SchedulerAnswerTally> getAllQuestionsAnswers() {
        return schedulerAnswerTallyList;
    }

    public void showQuestions(boolean isQuestionNeeded) {
        if ((isQuestionNeeded)) {
            questionsFragment.showDialog();
        } else {
            questionsFragment.hideDialog();
        }
    }
}