package com.carecloud.carepay.practice.library.patientmodecheckin.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckinDemographicsFragment;
import com.carecloud.carepay.practice.library.patientmodecheckin.fragments.CheckinIntakeForm1Fragment;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;

import org.w3c.dom.Text;

/**
 * Created by lsoco_user on 11/16/2016.
 * Main activity for patient check in flow
 */

public class PatientModeCheckinActivity extends BasePracticeActivity{

    private DemographicDTO demographicDTO;
    private TextView startIntakeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_mode_checkin);

        demographicDTO = getConvertedDTO(DemographicDTO.class);
        startIntakeTextView = (TextView) findViewById(R.id.startIntakeId);
        startIntakeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToFragment(new CheckinIntakeForm1Fragment(),false);
            }
        });
        CheckinDemographicsFragment fragment = new CheckinDemographicsFragment();
        navigateToFragment(fragment, false);
    }

    /**
     * Helper method to replace fragments
     * @param fragment The fragment
     * @param addToBackStack Whether to add the transaction to back-stack
     */
    public void navigateToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.checkInContentHolderId, fragment, fragment.getClass().getSimpleName());
        if(addToBackStack) {
            transaction.addToBackStack(fragment.getClass().getName());
        }
        transaction.commit();
    }

    public DemographicDTO getDemographicDTO() {
        return demographicDTO;
    }
}
