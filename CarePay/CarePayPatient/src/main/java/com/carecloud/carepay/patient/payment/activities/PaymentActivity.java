package com.carecloud.carepay.patient.payment.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.MenuItem;

import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.base.ToolbarInterface;
import com.carecloud.carepay.patient.payment.PatientPaymentPresenter;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanFragment;
import com.carecloud.carepaylibray.payments.fragments.ValidPlansFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.presenter.PaymentConnectivityHandler;
import com.carecloud.carepaylibray.payments.presenter.PaymentPresenter;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends BasePatientActivity implements PaymentConnectivityHandler, FragmentActivityInterface,
        ToolbarInterface {
    PaymentsModel paymentsDTO;
    AppointmentDTO appointmentDTO;
    PatientPaymentPresenter presenter;

    private boolean isFirstFragment = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        paymentsDTO = getConvertedDTO(PaymentsModel.class);
        initPresenter();
        Bundle info = getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO);
        appointmentDTO = DtoHelper.getConvertedDTO(AppointmentDTO.class, info);
        presenter.startPaymentProcess(paymentsDTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PaymentConstants.REQUEST_CODE_CHANGE_MASKED_WALLET:
            case PaymentConstants.REQUEST_CODE_FULL_WALLET:
            case PaymentConstants.REQUEST_CODE_GOOGLE_PAYMENT:
                presenter.forwardAndroidPayResult(requestCode, resultCode, data);
                break;
            case CarePayConstants.TELEHEALTH_APPOINTMENT_REQUEST:
                if (resultCode == RESULT_OK)
                    setResult(CarePayConstants.TELEHEALTH_APPOINTMENT_RESULT_CODE);
                finish();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            SystemUtil.hideSoftKeyboard(this);
            onBackPressed();
        }
        return true;
    }

    private void initPresenter() {
        Bundle info = getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO);
        AppointmentDTO appointmentDTO = DtoHelper.getConvertedDTO(AppointmentDTO.class, info);

        String defaultPatientId;
        if (appointmentDTO != null) {
            defaultPatientId = appointmentDTO.getMetadata().getPatientId();
        } else {
            defaultPatientId = paymentsDTO.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getMetadata().getPatientId();
        }
        this.presenter = new PatientPaymentPresenter(this, paymentsDTO, defaultPatientId);
    }


    @Override
    public void onBackPressed() {
        if (!isFragmentVisible()) {
            super.onBackPressed();
        }
    }


    private boolean isFragmentVisible() {
        Fragment fragment = getTopFragment();
        if (fragment != null) {
            if (fragment instanceof ValidPlansFragment) {
                if (((ValidPlansFragment) fragment).isOnBackPressCalled) {
                    return false;
                }
                ((ValidPlansFragment) fragment).onBackPressed();
                return true;
            } else if (fragment instanceof PaymentPlanFragment) {
                if (((PaymentPlanFragment) fragment).isOnBackPressCalled) {
                    return false;
                }
                ((PaymentPlanFragment) fragment).onBackPressed();
                return true;
            }
        }
        return false;
    }


    @Override
    public PaymentPresenter getPaymentPresenter() {
        return presenter;
    }

    /**
     * Helper method to replace fragments
     *
     * @param fragment       The fragment
     * @param addToBackStack Whether to add the transaction to back-stack
     */
    public void navigateToFragment(final Fragment fragment, final boolean addToBackStack) {
        replaceFragment(R.id.payment_frag_holder, fragment, addToBackStack && !isFirstFragment);
        isFirstFragment = false;
    }

    @Override
    public void exitPaymentProcess(boolean cancelled, boolean paymentPlanCreated, boolean paymentMade) {
        AppointmentDTO appointmentDTO = getAppointment();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", appointmentDTO.getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", appointmentDTO.getMetadata().getPracticeId());
        queryMap.put("patient_id", appointmentDTO.getMetadata().getPatientId());
        queryMap.put("appointment_id", appointmentDTO.getMetadata().getAppointmentId());

        TransitionDTO transition = paymentsDTO.getPaymentsMetadata().getPaymentsTransitions().getContinueTransition();
        getWorkflowServiceHelper().execute(transition, completePlanCallback, queryMap);
    }

    private WorkflowServiceCallback completePlanCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            Bundle info = new Bundle();
            if (getAppointment() != null) {
                DtoHelper.bundleDto(info, getAppointment());
            }
            PatientNavigationHelper.navigateToWorkflow(getContext(), workflowDTO, info);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
        }
    };

    @Nullable
    @Override
    public String getAppointmentId() {
        if (appointmentDTO != null) {
            return appointmentDTO.getMetadata().getAppointmentId();
        }
        return null;
    }

    @Nullable
    @Override
    public AppointmentDTO getAppointment() {
        return appointmentDTO;
    }

    @Override
    public ISession getISession() {
        return this;
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        addFragment(R.id.payment_frag_holder, fragment, addToBackStack && !isFirstFragment);
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.payment_frag_holder, fragment, addToBackStack && !isFirstFragment);
    }

    @Override
    public DTO getDto() {
        return paymentsDTO;
    }

    @Override
    public void displayToolbar(boolean display, String title) {
//        ((MenuPatientActivity) this).displayToolbar(display, title);
    }

    @Override
    public void onPaymentFlowFailure() {
        new Handler().postDelayed(() -> {
            setResult(RESULT_CANCELED);
            finish();
        }, 2000);
    }
}
