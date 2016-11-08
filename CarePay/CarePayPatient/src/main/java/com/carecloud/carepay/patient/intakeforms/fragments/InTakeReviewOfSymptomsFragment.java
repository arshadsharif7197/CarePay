package com.carecloud.carepay.patient.intakeforms.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepay.patient.intakeforms.activities.InTakeActivity;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Created by sudhir_pingale on 9/29/2016.
 */

public class InTakeReviewOfSymptomsFragment extends InTakeFragment {

    private View reviewOfSymptomsFormParent;
    private Context context;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        reviewOfSymptomsFormParent = super.onCreateView(inflater, container, savedInstanceState);
        onAddChildView();
        return reviewOfSymptomsFormParent;
    }
    private void onAddChildView(){
        LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.fragment_intake_review_of_symptoms, null);

        RadioGroup claudationRadioGroup = (RadioGroup)childActionView.findViewById(R.id.claudationRadioGroup);
        RadioGroup edemaRadioGroup = (RadioGroup)childActionView.findViewById(R.id.edemaRadioGroup);
        RadioGroup snoringRadioGroup = (RadioGroup)childActionView.findViewById(R.id.snoringRadioGroup);
        RadioGroup dyspneaRadioGroup = (RadioGroup)childActionView.findViewById(R.id.dyspneaRadioGroup);

        SystemUtil.setProximaNovaRegularTypeface(this.context,(TextView)childActionView.findViewById(R.id.claudationRadioCaptionTextView));
        SystemUtil.setProximaNovaRegularTypeface(this.context,(TextView)childActionView.findViewById(R.id.edemaRadioCaptionTextView));
        SystemUtil.setProximaNovaRegularTypeface(this.context,(TextView)childActionView.findViewById(R.id.snoringRadioCaptionTextView));
        SystemUtil.setProximaNovaRegularTypeface(this.context,(TextView)childActionView.findViewById(R.id.dyspneaRadioCaptionTextView));

        SystemUtil.setProximaNovaSemiboldTypeface(this.context,(TextView)childActionView.findViewById(R.id.vascularRadioCaptionTextView));
        SystemUtil.setProximaNovaSemiboldTypeface(this.context,(TextView)childActionView.findViewById(R.id.noRadioOptionTextView));
        SystemUtil.setProximaNovaSemiboldTypeface(this.context,(TextView)childActionView.findViewById(R.id.yesRadioOptionTextView));
        SystemUtil.setProximaNovaSemiboldTypeface(this.context,(TextView)childActionView.findViewById(R.id.notApplicableRadioOptionTextView));
        SystemUtil.setProximaNovaSemiboldTypeface(this.context,(TextView)childActionView.findViewById(R.id.pulmonaryRadioCaptionTextView));
        SystemUtil.setProximaNovaSemiboldTypeface(this.context,(TextView)childActionView.findViewById(R.id.pulmonaryNoRadioOptionTextView));
        SystemUtil.setProximaNovaSemiboldTypeface(this.context,(TextView)childActionView.findViewById(R.id.pulmonaryYesRadioOptionTextView));
        SystemUtil.setProximaNovaSemiboldTypeface(this.context,(TextView)childActionView.findViewById(R.id.pulmonaryNotApplicableRadioOptionTextView));

        ((LinearLayout)reviewOfSymptomsFormParent.findViewById(R.id.intakeQuestionsContainer)).addView(childActionView);

        claudationRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ((InTakeActivity)context).isQuestionAnswered = true;
                ((InTakeActivity)context).setIntakeNextEnabled(true);
            }
        });

        edemaRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ((InTakeActivity)context).isQuestionAnswered = true;
                ((InTakeActivity)context).setIntakeNextEnabled(true);
            }
        });

        snoringRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ((InTakeActivity)context).isQuestionAnswered = true;
                ((InTakeActivity)context).setIntakeNextEnabled(true);
            }
        });

        dyspneaRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ((InTakeActivity)context).isQuestionAnswered = true;
                ((InTakeActivity)context).setIntakeNextEnabled(true);
            }
        });
    }
}
