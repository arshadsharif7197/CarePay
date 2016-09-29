package com.carecloud.carepaylibray.intake;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Created by prem_mourya on 9/28/2016.
 */

public class InTakecardiacSymptomsfragment extends InTakeFragment {

    private View mainView;
    private Context context;
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
        View childActionView = inflater.inflate(R.layout.fragment_intake_cardiac_symptoms_form, null);
        TextView haveyouTreatedHeaderTextView = (TextView)childActionView.findViewById(R.id.haveyouTreatedHeaderTextView);
        SystemUtil.setProximaNovaSemiboldTypeface(this.context,haveyouTreatedHeaderTextView);
        ((LinearLayout)mainView.findViewById(R.id.intakeQuestionsContainer)).addView(childActionView);
    }

}
