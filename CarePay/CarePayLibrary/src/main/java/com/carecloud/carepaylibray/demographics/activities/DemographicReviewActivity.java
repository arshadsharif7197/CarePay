package com.carecloud.carepaylibray.demographics.activities;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;

import com.carecloud.carepaylibrary.R;

import com.carecloud.carepaylibray.demographics.fragments.review.ReviewFragment;
import com.carecloud.carepaylibray.demographics.models.payload.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.demographics.models.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.models.payload.DemographicPersDetailsPayloadDTO;
import com.carecloud.carepaylibray.demographics.models.payload.DemographicInsurancePayloadDTO;

import java.util.List;



public class DemographicReviewActivity extends AppCompatActivity {

    private DemographicPersDetailsPayloadDTO     demographicPersDetailsPayloadDTO;
    private DemographicAddressPayloadDTO         demographicAddressPayloadDTO;
    private List<DemographicInsurancePayloadDTO> insurances;
    private DemographicIdDocPayloadDTO           demPayloadIdDocPojo;

    public static boolean isFromReview=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demographic_review);

        FragmentManager fm = getSupportFragmentManager();
        ReviewFragment fragment = (ReviewFragment) fm.findFragmentByTag(ReviewFragment.class.getSimpleName());
        if(fragment == null) {
            fragment = ReviewFragment.newInstance();
        }
            getSupportFragmentManager()
                    .beginTransaction().addToBackStack("reviewscreen")
                    .replace(R.id.root_layout, fragment, ReviewFragment.class.getName())
                    .commit();

    }

    @Override
    public void onBackPressed() {
        if(isFromReview){
            this.finish();

        }else {
            super.onBackPressed();
        }
    }

    public DemographicPersDetailsPayloadDTO getDemographicPersDetailsPayloadDTO() {
        return demographicPersDetailsPayloadDTO;
    }

    public void setDemographicPersDetailsPayloadDTO(DemographicPersDetailsPayloadDTO demographicPersDetailsPayloadDTO) {
        this.demographicPersDetailsPayloadDTO = demographicPersDetailsPayloadDTO;
    }

    public DemographicAddressPayloadDTO getDemographicAddressPayloadDTO() {
        return demographicAddressPayloadDTO;
    }

    public void setDemographicAddressPayloadDTO(DemographicAddressPayloadDTO demographicAddressPayloadDTO) {
        this.demographicAddressPayloadDTO = demographicAddressPayloadDTO;
    }


    public void setInsurances(List<DemographicInsurancePayloadDTO> insurances) {
        this.insurances = insurances;
    }

    public List<DemographicInsurancePayloadDTO> getInsurances() {
        return insurances;
    }

    public DemographicIdDocPayloadDTO getDemographicPayloadIdDocDTO() {
        return demPayloadIdDocPojo;
    }

    public void setDemographicPayloadIdDocDTO(DemographicIdDocPayloadDTO demPayloadIdDocPojo) {
        this.demPayloadIdDocPojo = demPayloadIdDocPojo;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}
