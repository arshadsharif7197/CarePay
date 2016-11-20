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

public class CheckinDemographicsRevFragment extends Fragment {

    private Button areCorrectButton;
    private Button needUpdateButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkin_demographics_review, container, false);

        needUpdateButton = (Button) view.findViewById(R.id.checkinDemographicsReviewUpdateClickable);
        needUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // transition
                CheckinDemographicsFragment fragment = new CheckinDemographicsFragment();
                ((PatientModeCheckinActivity)getActivity()).navigateToFragment(fragment, true);
                ((PatientModeCheckinActivity)getActivity()).toggleVisibleBackButton(false);
            }
        });

        areCorrectButton = (Button) view.findViewById(R.id.checkinDemographicsReviewCorrectClickable);
        areCorrectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // transition
                CheckinConsentForm1Fragment fragment = new CheckinConsentForm1Fragment();
                ((PatientModeCheckinActivity)getActivity()).navigateToFragment(fragment, true);
                ((PatientModeCheckinActivity)getActivity()).toggleHighlight(PatientModeCheckinActivity.SUBFLOW_CONSENT, true);
                ((PatientModeCheckinActivity)getActivity()).changeCounterOfForm(PatientModeCheckinActivity.SUBFLOW_CONSENT, 1,
                                                                                PatientModeCheckinActivity.NUM_CONSENT_FORMS);
                ((PatientModeCheckinActivity)getActivity()).toggleVisibleFormCounter(PatientModeCheckinActivity.SUBFLOW_CONSENT, true);
            }
        });

        return view;
    }
}
