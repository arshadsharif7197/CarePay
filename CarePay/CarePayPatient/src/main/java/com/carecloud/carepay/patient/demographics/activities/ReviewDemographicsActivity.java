package com.carecloud.carepay.patient.demographics.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.demographics.DemographicsPresenter;
import com.carecloud.carepaylibray.demographics.DemographicsPresenterImpl;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;

public class ReviewDemographicsActivity extends BasePatientActivity implements DemographicsView {

    private DemographicsPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demographic_review);

        presenter = new DemographicsPresenterImpl(this, savedInstanceState, false);
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
    public void navigateToConsentForms(WorkflowDTO workflowDTO) {
        presenter.navigateToConsentForms(workflowDTO);
    }

    @Override
    public void navigateToIntakeForms(WorkflowDTO workflowDTO) {
        presenter.navigateToIntakeForms(workflowDTO);
    }

    @Override
    public void navigateToMedicationsAllergy(WorkflowDTO workflowDTO) {
        presenter.navigateToMedicationsAllergy(workflowDTO);
    }

    @Override
    public void updateCheckInFlow(CheckinFlowState flowState, int totalPages, int currentPage) {
        TextView textView = (TextView) findViewById(R.id.toolbar_title);
        switch (flowState) {
            case DEMOGRAPHICS:
                textView.setText(String.format(Label.getLabel("demographics_heading"), currentPage, totalPages));
                break;
            default:
        }
    }

    @Override
    public DemographicsPresenter getPresenter() {
        return presenter;
    }
}
