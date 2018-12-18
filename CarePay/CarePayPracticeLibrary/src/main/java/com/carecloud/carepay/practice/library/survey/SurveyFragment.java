package com.carecloud.carepay.practice.library.survey;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.dtos.WorkFlowRecord;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.base.WorkflowSessionHandler;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.survey.model.SurveyDTO;
import com.carecloud.carepaylibray.survey.model.SurveyModel;
import com.google.gson.Gson;

/**
 * @author pjohnson on 9/10/18.
 */
public class SurveyFragment extends BaseFragment {

    private FragmentActivityInterface callback;
    private SurveyDTO surveyDto;
    private SurveyAdapter adapter;
    private Button nextButton;
    private TextView surveyTitle;

    public static SurveyFragment newInstance() {
        return new SurveyFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivityInterface) {
            callback = (FragmentActivityInterface) context;
        } else {
            throw new ClassCastException("context must implement FragmentActivityInterface.");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        surveyDto = (SurveyDTO) callback.getDto();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_survey, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SurveyModel survey = surveyDto.getPayload().getSurvey();
        surveyTitle = view.findViewById(R.id.surveyTitle);
        surveyTitle.setText(Label.getLabel("survey.form.screen.title.survey"));
        RecyclerView surveyRecycler = view.findViewById(R.id.surveyRecycler);
        surveyRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SurveyAdapter(survey.getQuestions());
        surveyRecycler.setAdapter(adapter);
        nextButton = view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                WorkflowDTO workflowDTO = gson.fromJson(gson.toJson(surveyDto), WorkflowDTO.class);
                WorkFlowRecord workFlowRecord = new WorkFlowRecord(workflowDTO);
                workFlowRecord.setSessionKey(WorkflowSessionHandler.getCurrentSession(getContext()));
                Bundle bundle = new Bundle();
                bundle.putLong(WorkflowDTO.class.getName(), workFlowRecord.save(getContext()));
                Intent intent = new Intent(getActivity(), SurveyResultActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    public void refreshQuestionTitles() {
        adapter.notifyDataSetChanged();
        nextButton.setText(Label.getLabel("survey.patientMode.form.button.submitButton"));
        surveyTitle.setText(Label.getLabel("survey.form.screen.title.survey"));
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }
}
