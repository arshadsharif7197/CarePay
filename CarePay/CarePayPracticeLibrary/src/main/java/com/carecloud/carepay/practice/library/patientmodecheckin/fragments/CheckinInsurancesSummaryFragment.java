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

public class CheckinInsurancesSummaryFragment extends Fragment {

    private Button addDemogrButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkin_insurances_summary, container, false);

        // transition to next
        addDemogrButton = (Button) view.findViewById(R.id.checkinInsuranceSummaryAddInfoClickable);
        addDemogrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // transition
                CheckinDemographicsRevFragment fragment = new CheckinDemographicsRevFragment();
                ((PatientModeCheckinActivity)getActivity()).navigateToFragment(fragment, true);
            }
        });

        return view;
    }
}