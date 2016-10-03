package com.carecloud.carepaylibray.demographics.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.fragments.review.ReviewFragment;
import com.carecloud.carepaylibray.demographics.models.DemographicModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadAddressModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadDriversLicenseModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadInsuranceModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadPersonalDetailsModel;

import java.util.List;


public class DemographicReviewActivity extends AppCompatActivity {

    private DemographicPayloadPersonalDetailsModel demographicPayloadPersonalDetailsModel;
    private DemographicPayloadAddressModel demographicPayloadAddressModel;
    private List<DemographicPayloadInsuranceModel> insurances;
    private DemographicPayloadDriversLicenseModel demographicPayloadDriversLicenseModel;

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


    public void setInsurances(List<DemographicPayloadInsuranceModel> insurances) {
        this.insurances = insurances;
    }

    public List<DemographicPayloadInsuranceModel> getInsurances() {
        return insurances;
    }

    public DemographicPayloadDriversLicenseModel getDemographicPayloadDriversLicenseModel() {
        return demographicPayloadDriversLicenseModel;
    }

    public void setDemographicPayloadDriversLicenseModel(DemographicPayloadDriversLicenseModel demographicPayloadDriversLicenseModel) {
        this.demographicPayloadDriversLicenseModel = demographicPayloadDriversLicenseModel;
    }
}
