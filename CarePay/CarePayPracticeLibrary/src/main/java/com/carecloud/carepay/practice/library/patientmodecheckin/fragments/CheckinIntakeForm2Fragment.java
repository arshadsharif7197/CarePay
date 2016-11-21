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

public class CheckinIntakeForm2Fragment extends Fragment {

    private Button finishIntakeButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkin_intake_form2, container, false);

        finishIntakeButton = (Button) view.findViewById(R.id.checkinIntakeForm2FinishClickable);
        finishIntakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // transition
                CheckinPaymentFragment fragment = new CheckinPaymentFragment();
                ((PatientModeCheckinActivity) getActivity()).navigateToFragment(fragment, true);
                ((PatientModeCheckinActivity)getActivity()).toggleHighlight(PatientModeCheckinActivity.SUBFLOW_PAYMENTS, true);
                ((PatientModeCheckinActivity)getActivity()).toggleVisibleFormCounter(PatientModeCheckinActivity.SUBFLOW_INTAKE, false);
            }
        });

        return view;
    }
}
