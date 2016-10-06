package com.carecloud.carepaylibray.demographics.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.fragments.review.ReviewFragment;
import com.carecloud.carepaylibray.demographics.models.DemAddressPayloadPojo;
import com.carecloud.carepaylibray.demographics.models.DemIdDocPayloadPojo;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadInsuranceModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadPersonalDetailsModel;

import java.util.List;


public class DemographicReviewActivity extends AppCompatActivity {

    private DemographicPayloadPersonalDetailsModel demographicPayloadPersonalDetailsModel;
    private DemAddressPayloadPojo                  demAddressPayloadPojo;
    private List<DemographicPayloadInsuranceModel> insurances;
    private DemIdDocPayloadPojo                    demPayloadIdDocPojo;

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

    public DemographicPayloadPersonalDetailsModel getDemographicPayloadPersonalDetailsModel() {
        return demographicPayloadPersonalDetailsModel;
    }

    public void setDemographicPayloadPersonalDetailsModel(DemographicPayloadPersonalDetailsModel demographicPayloadPersonalDetailsModel) {
        this.demographicPayloadPersonalDetailsModel = demographicPayloadPersonalDetailsModel;
    }

    public DemAddressPayloadPojo getDemAddressPayloadPojo() {
        return demAddressPayloadPojo;
    }

    public void setDemAddressPayloadPojo(DemAddressPayloadPojo demAddressPayloadPojo) {
        this.demAddressPayloadPojo = demAddressPayloadPojo;
    }


    public void setInsurances(List<DemographicPayloadInsuranceModel> insurances) {
        this.insurances = insurances;
    }

    public List<DemographicPayloadInsuranceModel> getInsurances() {
        return insurances;
    }

    public DemIdDocPayloadPojo getDemPayloadIdDocPojo() {
        return demPayloadIdDocPojo;
    }

    public void setDemPayloadIdDocPojo(DemIdDocPayloadPojo demPayloadIdDocPojo) {
        this.demPayloadIdDocPojo = demPayloadIdDocPojo;
    }
}
