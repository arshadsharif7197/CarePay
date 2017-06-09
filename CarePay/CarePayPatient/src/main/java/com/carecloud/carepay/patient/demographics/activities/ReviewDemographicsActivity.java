package com.carecloud.carepay.patient.demographics.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.payment.PatientPaymentPresenter;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.demographics.DemographicsPresenter;
import com.carecloud.carepaylibray.demographics.DemographicsPresenterImpl;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.media.MediaResultListener;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.presenter.PaymentPresenter;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.DtoHelper;

public class ReviewDemographicsActivity extends BasePatientActivity implements DemographicsView, PaymentViewHandler {
    private static final String KEY_PAYMENT_DTO = "KEY_PAYMENT_DTO";

    private DemographicsPresenter demographicsPresenter;
    private PatientPaymentPresenter paymentPresenter;

    private String paymentWorkflow;

    private MediaResultListener resultListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demographic_review);

        demographicsPresenter = new DemographicsPresenterImpl(this, savedInstanceState, false);
        if(savedInstanceState!=null && savedInstanceState.containsKey(KEY_PAYMENT_DTO)){
            paymentWorkflow = savedInstanceState.getString(KEY_PAYMENT_DTO);
            initPaymentPresenter(paymentWorkflow);
        }
    }

    @Override
    protected void onDestroy() {
        demographicsPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle icicle){
        demographicsPresenter.onSaveInstanceState(icicle);
        icicle.putString(KEY_PAYMENT_DTO, paymentWorkflow);
        super.onSaveInstanceState(icicle);
    }

    @Override
    public void onStop() {
        demographicsPresenter.onStop();
        super.onStop();
    }

    @Override
    public void navigateToConsentForms(WorkflowDTO workflowDTO) {
        demographicsPresenter.navigateToConsentForms(workflowDTO);
    }

    @Override
    public void navigateToIntakeForms(WorkflowDTO workflowDTO) {
        demographicsPresenter.navigateToIntakeForms(workflowDTO);
    }

    @Override
    public void navigateToMedicationsAllergy(WorkflowDTO workflowDTO) {
        demographicsPresenter.navigateToMedicationsAllergy(workflowDTO);
    }

    @Override
    public void updateCheckInFlow(CheckinFlowState flowState, int totalPages, int currentPage) {
        switch (flowState) {
            case DEMOGRAPHICS:
                updateCheckInFlow("demographics_heading", totalPages, currentPage);
                break;
            case CONSENT:
                updateCheckInFlow("consent_form_heading", totalPages, currentPage);
                break;
            case INTAKE:
                updateCheckInFlow("intake_form_heading", totalPages, currentPage);
                break;
            default:
        }
    }

    public void updateCheckInFlow(String key, int totalPages, int currentPage) {
        TextView textView = (TextView) findViewById(R.id.toolbar_title);
        textView.setText(String.format(Label.getLabel(key), currentPage, totalPages));
    }

    /**
     * Start the Payment portion of check-in
     * @param workflowJson workflow string
     */
    public void getPaymentInformation(String workflowJson) {
        paymentWorkflow = workflowJson;
        PaymentsModel paymentsModel = initPaymentPresenter(paymentWorkflow);
        paymentPresenter.startPaymentProcess(paymentsModel);
    }

    private PaymentsModel initPaymentPresenter(String workflowJson){
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowJson);
        String patientID = demographicsPresenter.getDemographicDTO().getPayload().getDemographics().getMetadata().getPatientId();
        paymentPresenter = new PatientPaymentPresenter(this, paymentsModel, patientID);
        return paymentsModel;
    }

    @Override
    public DemographicsPresenter getPresenter() {
        return demographicsPresenter;
    }

    @Override
    public void setMediaResultListener(MediaResultListener resultListener) {
        this.resultListener = resultListener;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultListener!=null){
            resultListener.handleActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public PaymentPresenter getPaymentPresenter() {
        return paymentPresenter;
    }

    @Override
    public void navigateToFragment(Fragment fragment, boolean addToBackStack) {
        demographicsPresenter.navigateToFragment(fragment, addToBackStack);
    }

    @Override
    public void exitPaymentProcess(boolean cancelled) {
        if(getCallingActivity()!=null){
            setResult(cancelled ? RESULT_CANCELED : RESULT_OK);
        }
        finish();
    }
}
