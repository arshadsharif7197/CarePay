package com.carecloud.carepaylibray.demographics.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.fragments.review.ReviewFragment;
import com.carecloud.carepaylibray.demographics.models.DemographicModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadAddressModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadInsuranceModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadPersonalDetailsModel;


public class DemographicReviewActivity extends AppCompatActivity {

    private DemographicPayloadPersonalDetailsModel demographicPayloadPersonalDetailsModel;
    private DemographicPayloadAddressModel demographicPayloadAddressModel;
    private DemographicPayloadInsuranceModel demographicPayloadInsuranceModel;

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

    public DemographicPayloadAddressModel getDemographicPayloadAddressModel() {
        return demographicPayloadAddressModel;
    }

    public void setDemographicPayloadAddressModel(DemographicPayloadAddressModel demographicPayloadAddressModel) {
        this.demographicPayloadAddressModel = demographicPayloadAddressModel;
    }
    public DemographicPayloadInsuranceModel getDemographicPayloadInsuranceModel() {
        return demographicPayloadInsuranceModel;
    }

    public void setDemographicPayloadInsuranceModel(DemographicPayloadInsuranceModel demographicPayloadInsuranceModel) {
        this.demographicPayloadInsuranceModel = demographicPayloadInsuranceModel;
    }
}
