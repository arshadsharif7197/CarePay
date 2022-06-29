package com.carecloud.carepay.patient.appointments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.carecloud.carepay.patient.appointments.interfaces.IntelligentSchedulerCallback;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.createappointment.visittype.VisitTypePagerAdapter;
import com.carecloud.carepaylibray.appointments.models.IntelligentSchedulerDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeQuestions;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.customcomponents.CarePayViewPager;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.google.gson.Gson;

public class IntelligentSchedulerFragment extends BaseDialogFragment {

    protected Button nextButton;
    private String allQuestions = "";
    private IntelligentSchedulerCallback callback;
    private IntelligentSchedulerDTO intelligentSchedulerDTO;
    private CarePayViewPager viewPager;
    private VisitTypeQuestions currentQuestion;
    private VisitTypePagerAdapter questionPagerAdapter;
    private VisitTypeQuestions selectedOption;


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
            intelligentSchedulerDTO = new Gson().fromJson(allQuestions, IntelligentSchedulerDTO.class);
            currentQuestion = intelligentSchedulerDTO.getQuestion();
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
        if (toolbar != null) {
            TextView title = toolbar.findViewById(R.id.intelligent_scheduler_title);
            TextView exit = toolbar.findViewById(R.id.intelligent_scheduler_exit);
            title.setText(Label.getLabel("add_appointment_header"));
            exit.setText(Label.getLabel("demographics_exit"));
            toolbar.setTitle("");

            if (getDialog() == null) {
                toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back));
                toolbar.setNavigationOnClickListener(view12 -> callback.onBack());
            }
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
                nextButton.setEnabled(false);
                if (nextButton.getText().toString().equalsIgnoreCase(Label.getLabel("next_question_button_text"))) {
                    startQuestionFragment(selectedOption);
                    viewPager.setCurrentItem(questionPagerAdapter.getCount() - 1, true);
                } else {
                    callback.onVisitTypeSelected(selectedOption.getVisittype());
                }

            }
        });
        nextButton.setEnabled(false);

        setupViewPager();

//        nextButton.setText(Label.getLabel("common.button.continue"));
    }

    private void setupViewPager() {
        questionPagerAdapter = new VisitTypePagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(questionPagerAdapter);
        startQuestionFragment(currentQuestion);
    }

    private void startQuestionFragment(VisitTypeQuestions currentQuestion) {
        IntelligentSchedulerQuestionFragment questionsFragment = IntelligentSchedulerQuestionFragment
                .newInstance(new Gson().toJson(currentQuestion));
        questionPagerAdapter.addFragment(questionsFragment);
    }

    public void onVisitOptionSelected(VisitTypeQuestions visitTypeQuestions) {
        selectedOption = visitTypeQuestions;
        // getting message from activity that some option has been selected
        if (visitTypeQuestions.getChildrens() != null && visitTypeQuestions.getChildrens().size() != 0) {
            nextButton.setText(Label.getLabel("next_question_button_text"));
        } else {
            nextButton.setText(Label.getLabel("common.button.continue"));
        }
        nextButton.setEnabled(true);

    }

    public void onBack() {
        if (questionPagerAdapter.getCount() > 1) {
            nextButton.setEnabled(true);
            viewPager.setCurrentItem(questionPagerAdapter.getCount() - 2, true);
            questionPagerAdapter.removeFragment();
        } else {
            callback.onExit();
        }

    }
}