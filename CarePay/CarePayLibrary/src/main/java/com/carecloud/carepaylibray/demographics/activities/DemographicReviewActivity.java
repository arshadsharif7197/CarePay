package com.carecloud.carepaylibray.demographics.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.fragments.review.ReviewFragment;
import com.carecloud.carepaylibray.demographics.models.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.demographics.models.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.models.DemographicPersDetailsPayloadDTO;
import com.carecloud.carepaylibray.demographics.models.DemographicInsurancePayloadDTO;

import java.util.List;


public class DemographicReviewActivity extends AppCompatActivity {

    private DemographicPersDetailsPayloadDTO     demographicPersDetailsPayloadDTO;
    private DemographicAddressPayloadDTO         demographicAddressPayloadDTO;
    private List<DemographicInsurancePayloadDTO> insurances;
    private DemographicIdDocPayloadDTO           demPayloadIdDocPojo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demographic_review);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.root_layout, ReviewFragment.newInstance(), ReviewFragment.class.getName())
                    .commit();
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

    public DemographicIdDocPayloadDTO getDemPayloadIdDocPojo() {
        return demPayloadIdDocPojo;
    }

    public void setDemPayloadIdDocPojo(DemographicIdDocPayloadDTO demPayloadIdDocPojo) {
        this.demPayloadIdDocPojo = demPayloadIdDocPojo;
    }
}
