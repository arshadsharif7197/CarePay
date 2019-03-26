package com.carecloud.carepay.patient.demographics.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.payment.PatientPaymentPresenter;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.common.ConfirmationCallback;
import com.carecloud.carepaylibray.demographics.DemographicsPresenter;
import com.carecloud.carepaylibray.demographics.DemographicsPresenterImpl;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.fragments.AddressFragment;
import com.carecloud.carepaylibray.demographics.fragments.ConfirmDialogFragment;
import com.carecloud.carepaylibray.demographics.fragments.DemographicsFragment;
import com.carecloud.carepaylibray.demographics.fragments.FormsFragment;
import com.carecloud.carepaylibray.demographics.fragments.HealthInsuranceFragment;
import com.carecloud.carepaylibray.demographics.fragments.IdentificationFragment;
import com.carecloud.carepaylibray.demographics.fragments.InsuranceEditDialog;
import com.carecloud.carepaylibray.demographics.fragments.IntakeFormsFragment;
import com.carecloud.carepaylibray.demographics.fragments.PersonalInfoFragment;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.interfaces.IcicleInterface;
import com.carecloud.carepaylibray.media.MediaResultListener;
import com.carecloud.carepaylibray.medications.fragments.AllergiesFragment;
import com.carecloud.carepaylibray.medications.fragments.MedicationsAllergiesEmptyFragment;
import com.carecloud.carepaylibray.medications.fragments.MedicationsAllergyFragment;
import com.carecloud.carepaylibray.medications.fragments.MedicationsFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPayloadSettingsDTO;
import com.carecloud.carepaylibray.payments.presenter.PaymentPresenter;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.practice.BaseCheckinFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.clover.sdk.v3.payments.Payment;

public class ReviewDemographicsActivity extends BasePatientActivity implements DemographicsView,
        PaymentViewHandler, ConfirmationCallback {


    private static final String KEY_PAYMENT_DTO = "KEY_PAYMENT_DTO";
    private DemographicsPresenter demographicsPresenter;
    private PatientPaymentPresenter paymentPresenter;
    private String paymentWorkflow;
    private MediaResultListener resultListener;
    private PaymentsModel paymentsModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Bundle icicle = savedInstanceState;
        if (savedInstanceState != null) {
            String tag = savedInstanceState.getString(DemographicsPresenter.CURRENT_ICICLE_FRAGMENT);
            if (tag != null) {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment instanceof IcicleInterface) {
                    icicle = ((IcicleInterface) fragment).popData();
                    icicle.putAll(savedInstanceState);
                }
            }
        }
        super.onCreate(icicle);
        setContentView(R.layout.activity_demographic_review);

        demographicsPresenter = new DemographicsPresenterImpl(this, icicle, false);
        if (icicle != null && icicle.containsKey(KEY_PAYMENT_DTO)) {
            paymentWorkflow = icicle.getString(KEY_PAYMENT_DTO);
            initPaymentPresenter(paymentWorkflow);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Bundle icicle = savedInstanceState;
        if (savedInstanceState != null) {
            String tag = savedInstanceState.getString(DemographicsPresenter.CURRENT_ICICLE_FRAGMENT);
            if (tag != null) {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment instanceof IcicleInterface) {
                    icicle = ((IcicleInterface) fragment).popData();
                    icicle.putAll(savedInstanceState);
                }
            }
        }
        super.onRestoreInstanceState(icicle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PaymentConstants.REQUEST_CODE_CHANGE_MASKED_WALLET:
            case PaymentConstants.REQUEST_CODE_MASKED_WALLET:
            case PaymentConstants.REQUEST_CODE_FULL_WALLET:
            case PaymentConstants.REQUEST_CODE_GOOGLE_PAYMENT:
                paymentPresenter.forwardAndroidPayResult(requestCode, resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
        if (resultListener != null) {
            resultListener.handleActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.check_in_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.exitFlow) {
            ConfirmDialogFragment fragment = ConfirmDialogFragment
                    .newInstance(Label.getLabel("checkin_confirm_exit_title"),
                            Label.getLabel("checkin_confirm_exit_message"),
                            Label.getLabel("button_no"),
                            Label.getLabel("button_yes"));
            fragment.setCallback(this);
            displayDialogFragment(fragment, false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        demographicsPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle icicle) {
        super.onSaveInstanceState(icicle);
        Fragment fragment = demographicsPresenter.getCurrentFragment();
        if (fragment != null && fragment instanceof IcicleInterface) {
            ((IcicleInterface) fragment).pushData((Bundle) icicle.clone());
        }
        icicle.clear();
        demographicsPresenter.onSaveInstanceState(icicle);
        icicle.putString(KEY_PAYMENT_DTO, paymentWorkflow);
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
    public void navigateToThirdParty(WorkflowDTO workflowDTO) {
        demographicsPresenter.navigateToThirdParty(workflowDTO);
    }

    @Override
    public void navigateToMedicationsAllergy(WorkflowDTO workflowDTO) {
        demographicsPresenter.navigateToMedicationsAllergy(workflowDTO);
    }

    @Override
    public void navigateToMedications(WorkflowDTO workflowDTO) {
        demographicsPresenter.navigateToMedications(workflowDTO, true);
    }

    @Override
    public void navigateToAllergy(WorkflowDTO workflowDTO) {
        demographicsPresenter.navigateToAllergy(workflowDTO, true);
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
        if (textView != null) {
            textView.setText(String.format(Label.getLabel(key), currentPage, totalPages));
        }
    }

    /**
     * Start the Payment portion of check-in
     *
     * @param workflowJson workflow string
     */
    public void getPaymentInformation(String workflowJson) {
        paymentWorkflow = workflowJson;
        paymentsModel = initPaymentPresenter(paymentWorkflow);
        paymentPresenter.startPaymentProcess(paymentsModel);
    }

    private PaymentsModel initPaymentPresenter(String workflowJson) {
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowJson);
        String patientID = demographicsPresenter.getAppointment().getMetadata().getPatientId();
        if (paymentPresenter == null) {
            paymentPresenter = new PatientPaymentPresenter(this, paymentsModel, patientID);
        } else {
            paymentPresenter.setPaymentPresenter(this, paymentsModel, patientID);
        }
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
    public void completeCheckIn(WorkflowDTO workflowDTO) {
        SystemUtil.showSuccessToast(getContext(), Label.getLabel("confirm_appointment_checkin"));
        finish();
        Bundle bundle = new Bundle();
        bundle.putBoolean(CarePayConstants.REFRESH, true);
        navigateToWorkflow(workflowDTO, bundle);
        demographicsPresenter.logCheckinCompleted(false, false, null);
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
    public void exitPaymentProcess(boolean cancelled,  boolean paymentPlanCreated, boolean paymentMade) {
        if (getCallingActivity() != null) {
            setResult(cancelled ? RESULT_CANCELED : RESULT_OK);
        }
        demographicsPresenter.logCheckinCompleted(paymentPlanCreated, paymentMade, paymentsModel);
        finish();
    }

    @Nullable
    @Override
    public String getAppointmentId() {
        if (demographicsPresenter != null) {
            return demographicsPresenter.getAppointmentId();
        }
        return null;
    }

    @Nullable
    @Override
    public AppointmentDTO getAppointment() {
        if (demographicsPresenter != null) {
            return demographicsPresenter.getAppointment();
        }
        return null;
    }

    @Override
    public ISession getISession() {
        return this;
    }

    @Override
    public void onBackPressed() {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            BaseCheckinFragment fragment = (BaseCheckinFragment) fragmentManager
                    .findFragmentById(R.id.root_layout);
            if (fragment == null || !fragment.navigateBack()) {
                super.onBackPressed();
            }
        } catch (ClassCastException cce) {
            cce.printStackTrace();
            super.onBackPressed();
        }
    }

    @Override
    public void onConfirm() {
        demographicsPresenter.logCheckinCancelled();
        finish();
    }

}
