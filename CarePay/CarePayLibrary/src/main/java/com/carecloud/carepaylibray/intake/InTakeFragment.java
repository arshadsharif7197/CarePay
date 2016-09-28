package com.carecloud.carepaylibray.intake;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Created by lsoco_user on 9/11/2016.
 * Generic fragment for an intake form
 */
public class InTakeFragment extends Fragment{
    private IntakeFormModel form;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intake, container, false);

        TextView tvTitle = (TextView) view.findViewById(R.id.intakeFragTitle);
        tvTitle.setText(form.getIntakeModelTitle());

        TextView tvCaption = (TextView) view.findViewById(R.id.intakeFragCaption);
        tvCaption.setText(form.getIntakeModelCaption());
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(),tvTitle);
        SystemUtil.setProximaNovaRegularTypeface(getActivity(),tvCaption);
        return view;
    }

    public void setFormModel(IntakeFormModel question) {
        this.form = question;
    }

}
