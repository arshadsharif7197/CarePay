package com.carecloud.carepaylibray.demographics.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.fragments.review.ReviewFragment;
import com.carecloud.carepaylibray.demographics.models.DemAddressPayloadDto;
import com.carecloud.carepaylibray.demographics.models.DemIdDocPayloadDto;
import com.carecloud.carepaylibray.demographics.models.DemPersDetailsPayloadDto;
import com.carecloud.carepaylibray.demographics.models.DemInsurancePayloadPojo;

import java.util.List;


public class DemographicReviewActivity extends AppCompatActivity {

    private DemPersDetailsPayloadDto      demPersDetailsPayloadDto;
    private DemAddressPayloadDto          demAddressPayloadDto;
    private List<DemInsurancePayloadPojo> insurances;
    private DemIdDocPayloadDto            demPayloadIdDocPojo;

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

    public DemPersDetailsPayloadDto getDemPersDetailsPayloadDto() {
        return demPersDetailsPayloadDto;
    }

    public void setDemPersDetailsPayloadDto(DemPersDetailsPayloadDto demPersDetailsPayloadDto) {
        this.demPersDetailsPayloadDto = demPersDetailsPayloadDto;
    }

    public DemAddressPayloadDto getDemAddressPayloadDto() {
        return demAddressPayloadDto;
    }

    public void setDemAddressPayloadDto(DemAddressPayloadDto demAddressPayloadDto) {
        this.demAddressPayloadDto = demAddressPayloadDto;
    }


    public void setInsurances(List<DemInsurancePayloadPojo> insurances) {
        this.insurances = insurances;
    }

    public List<DemInsurancePayloadPojo> getInsurances() {
        return insurances;
    }

    public DemIdDocPayloadDto getDemPayloadIdDocPojo() {
        return demPayloadIdDocPojo;
    }

    public void setDemPayloadIdDocPojo(DemIdDocPayloadDto demPayloadIdDocPojo) {
        this.demPayloadIdDocPojo = demPayloadIdDocPojo;
    }
}
