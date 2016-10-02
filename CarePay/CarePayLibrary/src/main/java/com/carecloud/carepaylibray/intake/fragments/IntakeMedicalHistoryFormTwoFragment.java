package com.carecloud.carepaylibray.intake.fragments;

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
import com.carecloud.carepaylibray.intake.activities.InTakeActivity;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Created by sudhir_pingale on 9/29/2016.
 */

public class IntakeMedicalHistoryFormTwoFragment extends InTakeFragment {

    private View madicalHistoryForm2Parent;
    private Context context;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        madicalHistoryForm2Parent = super.onCreateView(inflater, container, savedInstanceState);
        onAddChildView();
        return madicalHistoryForm2Parent;
    }
    private void onAddChildView(){
        LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.fragment_intake_medical_history_form_2, null);
        TextView doYouSmokeRadioCaptionTextView = (TextView)childActionView.findViewById(R.id.doYouSmokeRadioCaptionTextView);
        TextView doYouHaveDiabetesRadioCaptionTextView = (TextView)childActionView.findViewById(R.id.doYouHaveDiabetesRadioCaptionTextView);
        TextView yesRadioOptionTextView = (TextView)childActionView.findViewById(R.id.yesRadioOptionTextView);
        TextView noRadioOptionTextView = (TextView)childActionView.findViewById(R.id.noRadioOptionTextView);

        RadioGroup doYouSmokeRadioGroup = (RadioGroup)childActionView.findViewById(R.id.doYouSmokeRadioGroup);
        RadioGroup doYouHaveDiabetesRadioGroup = (RadioGroup)childActionView.findViewById(R.id.doYouHaveDiabetesRadioGroup);

        SystemUtil.setProximaNovaRegularTypeface(this.context,doYouSmokeRadioCaptionTextView);
        SystemUtil.setProximaNovaRegularTypeface(this.context,doYouHaveDiabetesRadioCaptionTextView);
        SystemUtil.setProximaNovaSemiboldTypeface(this.context,yesRadioOptionTextView);
        SystemUtil.setProximaNovaSemiboldTypeface(this.context,noRadioOptionTextView);

        ((LinearLayout)madicalHistoryForm2Parent.findViewById(R.id.intakeQuestionsContainer)).addView(childActionView);

        doYouSmokeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ((InTakeActivity)context).isQuestionAnswered = true;
            }
        });

        doYouHaveDiabetesRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ((InTakeActivity)context).isQuestionAnswered = true;
            }
        });
    }
}
