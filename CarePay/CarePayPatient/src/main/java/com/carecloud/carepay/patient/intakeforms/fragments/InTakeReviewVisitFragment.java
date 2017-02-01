package com.carecloud.carepay.patient.intakeforms.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Created by prem_mourya on 9/28/2016.
 */

public class InTakeReviewVisitFragment extends InTakeFragment {

    CheckBox optionHappyCheckBox, optionSadCheckBox, optionLethargicCheckBox, optionWorriedCheckBox;
    private View mainView;
    private Context context;
    private String[] allergiesArray = {"Milk", "Fish", "Dust"};

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mainView = super.onCreateView(inflater, container, savedInstanceState);
        onAddChildView();
        return mainView;
    }

    private void onAddChildView() {
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.fragment_intake_review_visit_form, null);
        TextView whatBringstodayHeaderTextView = (TextView) childActionView.findViewById(R.id.whatBringstodayHeaderTextView);
        TextView howfeelingTextView = (TextView) childActionView.findViewById(R.id.howfeelingTextView);
        TextView anyConcernHeaderTextView = (TextView) childActionView.findViewById(R.id.anyConcernHeaderTextView);
        TextView optionalTextView = (TextView) childActionView.findViewById(R.id.optionalTextView);
        EditText describeReasonsEditText = (EditText) childActionView.findViewById(R.id.describeReasonsEditText);
        optionHappyCheckBox = (CheckBox) childActionView.findViewById(R.id.optionHappyCheckBox);
        optionSadCheckBox = (CheckBox) childActionView.findViewById(R.id.optionSadCheckBox);
        optionLethargicCheckBox = (CheckBox) childActionView.findViewById(R.id.optionLethargicCheckBox);
        optionWorriedCheckBox = (CheckBox) childActionView.findViewById(R.id.optionWorriedCheckBox);
        describeReasonsEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        EditText anyOtherConcernEditText = (EditText) childActionView.findViewById(R.id.anyOtherConcernEditText);
        anyOtherConcernEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View onTouchListener, MotionEvent event) {
                onTouchListener.setFocusable(true);
                onTouchListener.setFocusableInTouchMode(true);
                return false;
            }
        });
        SystemUtil.setProximaNovaSemiboldTypeface(this.context, whatBringstodayHeaderTextView);
        SystemUtil.setProximaNovaSemiboldTypeface(this.context, howfeelingTextView);
        SystemUtil.setProximaNovaSemiboldTypeface(this.context, anyConcernHeaderTextView);
        SystemUtil.setProximaNovaRegularTypeface(this.context, anyOtherConcernEditText);
        SystemUtil.setProximaNovaRegularTypeface(this.context, describeReasonsEditText);
        SystemUtil.setProximaNovaSemiboldTypeface(this.context, optionalTextView);
        onSetCheckBoxListner();
        onSetCheckBoxTypeFace();
        ((LinearLayout) mainView.findViewById(R.id.intakeQuestionsContainer)).addView(childActionView);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int viewId = v.getId();
        if (viewId == R.id.optionHappyCheckBox) {
            onCheckBoxtextColor(optionHappyCheckBox);
        } else if (viewId == R.id.optionSadCheckBox) {
            onCheckBoxtextColor(optionSadCheckBox);
        } else if (viewId == R.id.optionLethargicCheckBox) {
            onCheckBoxtextColor(optionLethargicCheckBox);
        } else if (viewId == R.id.optionWorriedCheckBox) {
            onCheckBoxtextColor(optionWorriedCheckBox);
        }
    }

    private void onSetCheckBoxListner() {
        optionHappyCheckBox.setOnClickListener(this);
        optionSadCheckBox.setOnClickListener(this);
        optionLethargicCheckBox.setOnClickListener(this);
        optionWorriedCheckBox.setOnClickListener(this);
    }

    private void onSetCheckBoxTypeFace() {
        SystemUtil.setProximaNovaRegularTypeface(this.context, optionHappyCheckBox);
        SystemUtil.setProximaNovaRegularTypeface(this.context, optionSadCheckBox);
        SystemUtil.setProximaNovaRegularTypeface(this.context, optionLethargicCheckBox);
        SystemUtil.setProximaNovaRegularTypeface(this.context, optionWorriedCheckBox);
    }

    private void onCheckBoxtextColor(CheckBox checkBox) {
        if (checkBox.isChecked()) {
            checkBox.setTextColor(ContextCompat.getColor(context, R.color.bright_cerulean));
        } else {
            checkBox.setTextColor(ContextCompat.getColor(context, R.color.slateGray));
        }
    }
}
