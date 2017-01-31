package com.carecloud.carepay.patient.demographics.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.demographics.fragments.review.CheckinDemographicsFragment;
import com.carecloud.carepay.patient.demographics.fragments.review.CheckinDemographicsRevFragment;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.misc.DemographicsLabelsHolder;

/**
 * Created by lsoco_user on 11/28/2016.
 */

public class NewReviewDemographicsActivity extends BasePatientActivity implements DemographicsLabelsHolder {

    private DemographicDTO demographicDTO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.carecloud.carepaylibrary.R.layout.activity_demographic_review);

        // demographics DTO
        demographicDTO = getConvertedDTO(DemographicDTO.class);

        // place the initial fragment
        CheckinDemographicsFragment fragment = new CheckinDemographicsFragment();
        navigateToFragment(fragment, false);
        Log.v(NewReviewDemographicsActivity.class.getSimpleName(), "NewReviewDemographicsActivity");
    }

    /**
     * Helper method to replace fragments
     *
     * @param fragment       The fragment
     * @param addToBackStack Whether to add the transaction to back-stack
     */
    public void navigateToFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.root_layout, fragment, fragment.getClass().getSimpleName());
        if (addToBackStack) {
            transaction.addToBackStack(fragment.getClass().getName());
        }
        transaction.commit();
    }

    @Override
    public DemographicLabelsDTO getLabelsDTO() {
        if(demographicDTO != null && demographicDTO.getMetadata() != null && demographicDTO.getMetadata().getLabels() != null) {
            return demographicDTO.getMetadata().getLabels();
        }
        return null;
    }

    /**
     * Getter
     * @return The main DTO
     */
    public DemographicDTO getDemographicDTO() {
        return demographicDTO;
    }

    /**
     * Re-sets the global DTO from a string JSON
     *
     * @param jsonString The main DTO as string
     */
    public void resetDemographicDTO(String jsonString) {
        this.demographicDTO = getConvertedDTO(DemographicDTO.class, jsonString);
    }

}
