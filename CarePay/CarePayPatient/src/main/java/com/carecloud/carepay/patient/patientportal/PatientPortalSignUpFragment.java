package com.carecloud.carepay.patient.patientportal;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Created by Rahul on 10/13/16.
 */

public class PatientPortalSignUpFragment extends BaseFragment implements View.OnClickListener {

    private View view;
    private Button signupPortalButton;
    private Button passNowButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragmet_patient_portal_signup, container, false);


        Toolbar toolbar = (Toolbar) view.findViewById(R.id.patient_portal_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.patientportal_toolbar_title);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        initializeviews();

        return view;
    }


    @Override
    public void onClick(View view) {

    }

    private void initializeviews() {
        signupPortalButton = (Button) view.findViewById(R.id.signupportal_button);
        passNowButton = (Button) view.findViewById(R.id.passfornow_button);

    }


}
