package com.carecloud.carepaylibray.intake;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.adapters.CustomAlertAdapter;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.Arrays;

/**
 * Created by prem_mourya on 9/28/2016.
 */

public class IntakeReviewvisitFragment extends InTakeFragment {

    private View mainView;
    private Context context;
    private TextView chooseAllergyTextView;
    private String[] allergiesArray = {"Milk", "Fish","Dust"};
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mainView = super.onCreateView(inflater, container, savedInstanceState);
        onAddChildView();
        return mainView;
    }
    private void onAddChildView(){
        LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.fragment_intake_review_visit_form, null);
        TextView whatBringstodayHeaderTextView = (TextView)childActionView.findViewById(R.id.whatBringstodayHeaderTextView);
        TextView howfeelingTextView = (TextView)childActionView.findViewById(R.id.howfeelingTextView);
        TextView anyConcernHeaderTextView = (TextView)childActionView.findViewById(R.id.anyConcernHeaderTextView);
        EditText describeReasonsEditText = (EditText)childActionView.findViewById(R.id.describeReasonsEditText);
        describeReasonsEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        EditText anyOtherConcernEditText = (EditText)childActionView.findViewById(R.id.anyOtherConcernEditText);
        anyOtherConcernEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        SystemUtil.setProximaNovaSemiboldTypeface(this.context,whatBringstodayHeaderTextView);
        SystemUtil.setProximaNovaSemiboldTypeface(this.context,howfeelingTextView);
        SystemUtil.setProximaNovaSemiboldTypeface(this.context,anyConcernHeaderTextView);
        ((LinearLayout)mainView.findViewById(R.id.intakeQuestionsContainer)).addView(childActionView);
    }

}
