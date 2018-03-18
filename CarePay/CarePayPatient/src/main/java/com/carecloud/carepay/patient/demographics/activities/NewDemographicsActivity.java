package com.carecloud.carepay.patient.demographics.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.demographics.NewDemographicsPresenterImpl;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.demographics.DemographicsPresenter;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.media.MediaResultListener;
import com.carecloud.carepaylibray.utils.SystemUtil;

public class NewDemographicsActivity extends BasePatientActivity implements DemographicsView {

    private DemographicsPresenter presenter;

    private MediaResultListener resultListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demographic_review);
        presenter = new NewDemographicsPresenterImpl(this, savedInstanceState, false);
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
        switch (currentPage) {
            case 1:
                updateCheckInFlow("demographics_review_peronsonalinfo_section");
                break;
            case 2:
                updateCheckInFlow("demographics_address_section");
                break;
            case 3:
                updateCheckInFlow("demographics_review_demographics");
                break;
            case 4:
                updateCheckInFlow("demographics_review_identification");
                break;
            case 5:
                updateCheckInFlow("demographics_insurance_label");
                break;
            default:
        }
    }

    public void updateCheckInFlow(String key) {
        TextView textView = (TextView) findViewById(R.id.toolbar_title);
        textView.setText(Label.getLabel(key));
    }

    public DemographicsPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void setMediaResultListener(MediaResultListener resultListener) {
        this.resultListener = resultListener;
    }

    @Override
    public void completeCheckIn(WorkflowDTO workflowDTO) {
        SystemUtil.showSuccessToast(getContext(), Label.getLabel("confirm_appointment_checkin"));
        finish();
        navigateToWorkflow(workflowDTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultListener!=null){
            resultListener.handleActivityResult(requestCode, resultCode, data);
        }
    }

}
