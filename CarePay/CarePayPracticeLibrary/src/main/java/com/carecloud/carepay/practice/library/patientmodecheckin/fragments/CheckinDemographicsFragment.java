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
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicsInsurancesListDTO;

/**
 * Created by lsoco_user on 11/17/2016.
 */

public class CheckinDemographicsFragment extends Fragment {

    private Button         addDemogrButton;
    private DemographicDTO demographicsDTO;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkin_demographics, container, false);

        // get the DTO
        demographicsDTO = ((PatientModeCheckinActivity)getActivity()).getDemographicDTO();

        // transition to next
        addDemogrButton = (Button) view.findViewById(R.id.checkinDemographicsAddClickable);
        addDemogrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // transition
                CheckinInsurancesSummaryFragment fragment = new CheckinInsurancesSummaryFragment();
                ((PatientModeCheckinActivity)getActivity()).navigateToFragment(fragment, true);
            }
        });

        return view;
    }
}
