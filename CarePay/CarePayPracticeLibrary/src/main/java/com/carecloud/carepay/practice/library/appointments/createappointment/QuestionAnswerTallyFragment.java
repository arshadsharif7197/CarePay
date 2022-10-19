package com.carecloud.carepay.practice.library.appointments.createappointment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.practice.library.appointments.interfaces.IntelligentSchedulerCallback;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.createappointment.visittype.QuestionAnswerTallyListAdapter;
import com.carecloud.carepaylibray.appointments.models.SchedulerAnswerTally;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

import java.io.Serializable;
import java.util.List;

public class QuestionAnswerTallyFragment extends BaseDialogFragment {

    private IntelligentSchedulerCallback callback;
    private List<SchedulerAnswerTally> schedulerAnswerTallyList;

    public static QuestionAnswerTallyFragment newInstance(List<SchedulerAnswerTally> allQuestionsAnswers) {
        Bundle args = new Bundle();
        args.putSerializable(CarePayConstants.INTELLIGENT_SCHEDULER_QUESTIONS_ANSWERS_TALLY_KEY, (Serializable) allQuestionsAnswers);
        QuestionAnswerTallyFragment questionAnswerTallyFragment = new QuestionAnswerTallyFragment();
        questionAnswerTallyFragment.setArguments(args);
        return questionAnswerTallyFragment;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Bundle arguments = getArguments();
        if (arguments != null) {
            schedulerAnswerTallyList = (List<SchedulerAnswerTally>) arguments.get(CarePayConstants.INTELLIGENT_SCHEDULER_QUESTIONS_ANSWERS_TALLY_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_intelligent_scheduler_question_answer_tally, container, false);
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
        ImageView cancel_img = view.findViewById(R.id.cancel_img);
        if (toolbar != null) {
            TextView title = toolbar.findViewById(R.id.intelligent_scheduler_title);
            title.setText(Label.getLabel("answer_label"));
            toolbar.setTitle("");
        }
        cancel_img.setOnClickListener(v -> {
            cancel();
            callback.fromView();
        });
    }

    private void initializeViews(View view) {
        RecyclerView rvOptions = view.findViewById(R.id.list_items);
        rvOptions.setLayoutManager(new LinearLayoutManager(getContext()));
        QuestionAnswerTallyListAdapter questionAnswerTallyListAdapter = new QuestionAnswerTallyListAdapter(getContext(),
                schedulerAnswerTallyList, true);
        rvOptions.setAdapter(questionAnswerTallyListAdapter);
    }
}