package com.carecloud.carepay.practice.library.patientmodecheckin.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity;

/**
 * Created by lsoco_user on 11/17/2016.
 */

public class CheckinConsentForm1Fragment extends Fragment {

    private Button signConsentForm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkin_consent_form1, container, false);

        signConsentForm = (Button) view.findViewById(R.id.checkinConsentForm1SignClickable);
        signConsentForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // transition
                CheckinConsentForm2Fragment fragment = new CheckinConsentForm2Fragment();
                ((PatientModeCheckinActivity)getActivity()).navigateToFragment(fragment, true);
            }
        });

        return view;
    }


}