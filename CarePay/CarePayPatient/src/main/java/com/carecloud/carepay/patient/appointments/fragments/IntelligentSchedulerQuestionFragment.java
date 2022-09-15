package com.carecloud.carepay.patient.appointments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.patient.appointments.interfaces.IntelligentSchedulerCallback;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.createappointment.visittype.VisitTypeOptionsListAdapter;
import com.carecloud.carepaylibray.appointments.models.VisitTypeQuestions;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.google.gson.Gson;

public class IntelligentSchedulerQuestionFragment extends BaseDialogFragment implements VisitTypeOptionsListAdapter.OptionSelectionListener {

    private String questionString = "";
    private IntelligentSchedulerCallback callback;
    private VisitTypeQuestions visitTypeQuestion;
    private VisitTypeQuestions selectedVisitTypeOption;
    private VisitTypeOptionsListAdapter visitTypeOptionsListAdapterListAdapter;

    public static IntelligentSchedulerQuestionFragment newInstance(String intelligentQuestions) {
        Bundle args = new Bundle();
        args.putString(CarePayConstants.INTELLIGENT_SCHEDULER_QUESTIONS_KEY, intelligentQuestions);
        IntelligentSchedulerQuestionFragment intelligentSchedulerFragment = new IntelligentSchedulerQuestionFragment();
        intelligentSchedulerFragment.setArguments(args);
        return intelligentSchedulerFragment;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Bundle arguments = getArguments();
        if (arguments != null) {
            questionString = arguments.getString(CarePayConstants.INTELLIGENT_SCHEDULER_QUESTIONS_KEY);
            visitTypeQuestion = new Gson().fromJson(questionString, VisitTypeQuestions.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_intelligent_scheduler_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle icicle) {
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

    private void initializeViews(View view) {
        TextView tvQuestionTitle = view.findViewById(R.id.intelligent_scheduler_question_title);
        tvQuestionTitle.setText(visitTypeQuestion.getName());

        RecyclerView rvOptions = view.findViewById(R.id.list_items);
        rvOptions.setLayoutManager(new LinearLayoutManager(getContext()));
        visitTypeOptionsListAdapterListAdapter = new VisitTypeOptionsListAdapter(getContext(),
                visitTypeQuestion.getChildrens(), this, true);
        rvOptions.setAdapter(visitTypeOptionsListAdapterListAdapter);
    }

    @Override
    public void onOptionSelected(VisitTypeQuestions visitTypeQuestions) {
        selectedVisitTypeOption = visitTypeQuestions;
        callback.onOptionSelected(visitTypeQuestions);
    }

    public VisitTypeQuestions getVisitTypeQuestion() {
        return visitTypeQuestion;
    }

    public VisitTypeQuestions getVisitTypeOption() {
        return selectedVisitTypeOption;
    }

    public void setVisitTypeOption(VisitTypeQuestions selectedVisitTypeOption) {
        visitTypeOptionsListAdapterListAdapter.setSelectedItem(selectedVisitTypeOption);
    }
}