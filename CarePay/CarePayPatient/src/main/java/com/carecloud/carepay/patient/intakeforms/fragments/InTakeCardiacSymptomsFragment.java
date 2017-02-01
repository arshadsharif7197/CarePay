package com.carecloud.carepay.patient.intakeforms.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Created by prem_mourya on 9/28/2016.
 */

public class InTakeCardiacSymptomsFragment extends InTakeFragment {

    CheckBox chestPainCheckBox, nauseaCheckBox, perspirationCheckBox, SOBCheckBox, swellingCheckBox, palpitationsCheckBox, syncopeCheckBox, nearSyncopeCheckBox;
    private View mainView;
    private Context context;

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
        View childActionView = inflater.inflate(R.layout.fragment_intake_cardiac_symptoms_form, null);
        TextView haveyouTreatedHeaderTextView = (TextView) childActionView.findViewById(R.id.haveyouTreatedHeaderTextView);
        SystemUtil.setProximaNovaSemiboldTypeface(this.context, haveyouTreatedHeaderTextView);
        chestPainCheckBox = (CheckBox) childActionView.findViewById(R.id.chestPainCheckBox);
        nauseaCheckBox = (CheckBox) childActionView.findViewById(R.id.nauseaCheckBox);
        perspirationCheckBox = (CheckBox) childActionView.findViewById(R.id.perspirationCheckBox);
        SOBCheckBox = (CheckBox) childActionView.findViewById(R.id.SOBCheckBox);
        swellingCheckBox = (CheckBox) childActionView.findViewById(R.id.swellingCheckBox);
        palpitationsCheckBox = (CheckBox) childActionView.findViewById(R.id.palpitationsCheckBox);
        syncopeCheckBox = (CheckBox) childActionView.findViewById(R.id.syncopeCheckBox);
        nearSyncopeCheckBox = (CheckBox) childActionView.findViewById(R.id.nearSyncopeCheckBox);
        onSetCheckBoxListner();
        onSetCheckBoxTypeFace();
        ((LinearLayout) mainView.findViewById(R.id.intakeQuestionsContainer)).addView(childActionView);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        int viewId = v.getId();
        if (viewId == R.id.chestPainCheckBox) {
            onCheckBoxtextColor(chestPainCheckBox);
        } else if (viewId == R.id.nauseaCheckBox) {
            onCheckBoxtextColor(nauseaCheckBox);
        } else if (viewId == R.id.perspirationCheckBox) {
            onCheckBoxtextColor(perspirationCheckBox);
        } else if (viewId == R.id.SOBCheckBox) {
            onCheckBoxtextColor(SOBCheckBox);
        } else if (viewId == R.id.swellingCheckBox) {
            onCheckBoxtextColor(swellingCheckBox);
        } else if (viewId == R.id.palpitationsCheckBox) {
            onCheckBoxtextColor(palpitationsCheckBox);
        } else if (viewId == R.id.syncopeCheckBox) {
            onCheckBoxtextColor(syncopeCheckBox);
        } else if (viewId == R.id.nearSyncopeCheckBox) {
            onCheckBoxtextColor(nearSyncopeCheckBox);
        }
    }

    private void onSetCheckBoxListner() {
        chestPainCheckBox.setOnClickListener(this);
        nauseaCheckBox.setOnClickListener(this);
        perspirationCheckBox.setOnClickListener(this);
        SOBCheckBox.setOnClickListener(this);
        swellingCheckBox.setOnClickListener(this);
        palpitationsCheckBox.setOnClickListener(this);
        syncopeCheckBox.setOnClickListener(this);
        nearSyncopeCheckBox.setOnClickListener(this);
    }

    private void onSetCheckBoxTypeFace() {
        SystemUtil.setProximaNovaRegularTypeface(this.context, chestPainCheckBox);
        SystemUtil.setProximaNovaRegularTypeface(this.context, nauseaCheckBox);
        SystemUtil.setProximaNovaRegularTypeface(this.context, perspirationCheckBox);
        SystemUtil.setProximaNovaRegularTypeface(this.context, SOBCheckBox);
        SystemUtil.setProximaNovaRegularTypeface(this.context, swellingCheckBox);
        SystemUtil.setProximaNovaRegularTypeface(this.context, palpitationsCheckBox);
        SystemUtil.setProximaNovaRegularTypeface(this.context, syncopeCheckBox);
        SystemUtil.setProximaNovaRegularTypeface(this.context, nearSyncopeCheckBox);
    }

    private void onCheckBoxtextColor(CheckBox checkBox) {
        if (checkBox.isChecked()) {
            checkBox.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } else {
            checkBox.setTextColor(ContextCompat.getColor(context, R.color.slateGray));
        }
    }
}
