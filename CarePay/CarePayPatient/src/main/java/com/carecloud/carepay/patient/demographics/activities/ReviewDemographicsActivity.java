package com.carecloud.carepay.patient.demographics.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepaylibray.demographics.DemographicsPresenter;
import com.carecloud.carepaylibray.demographics.DemographicsPresenterImpl;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;

public class ReviewDemographicsActivity extends BasePatientActivity implements DemographicsView {

    private DemographicsPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.carecloud.carepaylibrary.R.layout.activity_demographic_review);

        presenter = new DemographicsPresenterImpl(this, savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle icicle){
        presenter.onSaveInstanceState(icicle);
        super.onSaveInstanceState(icicle);
    }

    @Override
    public void onStop() {
        presenter.onStop();
        super.onStop();
    }

    @Override
    public void updateCheckInFlow(CheckinFlowState flowState, int totalPages, int currentPage) {

    }

    @Override
    public DemographicsPresenter getPresenter() {
        return presenter;
    }
}
