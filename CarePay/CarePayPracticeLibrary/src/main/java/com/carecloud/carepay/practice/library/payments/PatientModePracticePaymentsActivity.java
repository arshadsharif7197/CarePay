package com.carecloud.carepay.practice.library.payments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.models.ResponsibilityHeaderModel;
import com.carecloud.carepay.practice.library.payments.adapter.PaymentBalancesAdapter;
import com.carecloud.carepay.practice.library.payments.dialogs.PaymentDetailsFragmentDialog;
import com.carecloud.carepay.practice.library.payments.dialogs.PaymentQueuedDialogFragment;
import com.carecloud.carepay.practice.library.payments.dialogs.ResponsibilityFragmentDialog;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeAddNewCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeChooseCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePartialPaymentDialogFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentMethodDialogFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.payments.interfaces.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.fragments.PaymentConfirmationFragment;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentExecution;
import com.carecloud.carepaylibray.payments.models.updatebalance.UpdatePatientBalancesDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pjohnson on 16/03/17
 */
public class PatientModePracticePaymentsActivity extends BasePracticeActivity implements PaymentBalancesAdapter.PaymentRecyclerViewCallback,
        PaymentNavigationCallback, ResponsibilityFragmentDialog.PayResponsibilityCallback {

    private PaymentsModel paymentResultModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_payment);
        paymentResultModel = getConvertedDTO(PaymentsModel.class);

        setUpUI();
    }

    private void setUpUI() {
        if (hasNoPayments()) {
            showNoPaymentsImage();
        } else {
            showPayments(paymentResultModel);
        }
        findViewById(R.id.btnHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHome(paymentResultModel.getPaymentsMetadata().getPaymentsTransitions().getLogout());
            }
        });
        TextView logoutTextview = (TextView) findViewById(R.id.logoutTextview);
        logoutTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHome(paymentResultModel.getPaymentsMetadata().getPaymentsTransitions().getLogout());
            }
        });
        logoutTextview.setText(Label.getLabel("practice_app_logout_text"));
    }

    private boolean hasNoPayments() {
        boolean hasNoPayments = true;
        if (paymentResultModel.getPaymentPayload().getPatientBalances().isEmpty()) {
            return true;
        }
        for (PatientBalanceDTO patientBalanceDTO : paymentResultModel.getPaymentPayload().getPatientBalances()) {
            if (Double.parseDouble(patientBalanceDTO.getPendingRepsonsibility()) > 0) {
                hasNoPayments = false;
                break;
            }
        }
        return hasNoPayments;
    }

    private void showPayments(PaymentsModel paymentsModel) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.appointmentsRecyclerView);
        recyclerView.setVisibility(View.VISIBLE);
        findViewById(R.id.emptyPaymentsImageView).setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        PaymentBalancesAdapter paymentBalancesAdapter = new PaymentBalancesAdapter(this, paymentsModel.getPaymentPayload().getPatientBalances(),
                paymentsModel.getPaymentPayload().getUserPractices().get(0));
        paymentBalancesAdapter.setCallback(this);
        recyclerView.setAdapter(paymentBalancesAdapter);

        ((TextView) findViewById(R.id.title)).setText(Label.getLabel("select_pending_payment_title"));
        ((TextView) findViewById(R.id.subtitle)).setText(Label.getLabel("no_pending_payment_description"));

    }

    private void showNoPaymentsImage() {
        findViewById(R.id.emptyPaymentsImageView).setVisibility(View.VISIBLE);
        findViewById(R.id.appointmentsRecyclerView).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.title)).setText(Label.getLabel("no_payment_title"));
        ((TextView) findViewById(R.id.subtitle)).setText(Label.getLabel("no_pending_payment_description"));
    }

    @Override
    public void onBalancePayButtonClicked(PatientBalanceDTO patientBalanceDTO) {
        startPaymentProcess(paymentResultModel);
    }

    @Override
    public void startPaymentProcess(PaymentsModel paymentsModel) {
        String tag = ResponsibilityFragmentDialog.class.getSimpleName();
        ResponsibilityHeaderModel headerModel = ResponsibilityHeaderModel.newClinicHeader(paymentsModel);
        ResponsibilityFragmentDialog dialog = ResponsibilityFragmentDialog
                .newInstance(paymentsModel, Label.getLabel("payment_partial_payment_text"),
                        Label.getLabel("payment_details_pay_now"), headerModel);
        dialog.setLeftButtonEnabled(true);
        dialog.show(getSupportFragmentManager(), tag);

    }

    @Override
    public void onPartialPaymentClicked(double owedAmount) {
        String tag = PracticePartialPaymentDialogFragment.class.getSimpleName();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        PracticePartialPaymentDialogFragment dialog = PracticePartialPaymentDialogFragment
                .newInstance(paymentResultModel, owedAmount);
        dialog.show(ft, tag);
    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        PracticePaymentMethodDialogFragment fragment = PracticePaymentMethodDialogFragment
                .newInstance(paymentsModel, amount);
        fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
    }

    @Override
    public void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod, double amount, PaymentsModel paymentsModel) {
//        boolean isCloverDevice = HttpConstants.getDeviceInformation().getDeviceType().equals(CarePayConstants.CLOVER_DEVICE);
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            PracticeChooseCreditCardFragment fragment = PracticeChooseCreditCardFragment.newInstance(paymentsModel, selectedPaymentMethod.getLabel(), amount);
            fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
        } else {
            showAddCard(amount, paymentsModel);
        }
    }

    @Override
    public void onPaymentPlanAction(PaymentsModel paymentsModel) {

    }

    @Override
    public void showPaymentConfirmation(WorkflowDTO workflowDTO) {
//        Gson gson = new Gson();
        Bundle args = new Bundle();
//        String paymentsDTOString = gson.toJson(paymentsModel);
        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, workflowDTO.toString());

        PaymentConfirmationFragment confirmationFragment = new PaymentConfirmationFragment();
        confirmationFragment.setArguments(args);
        confirmationFragment.show(getSupportFragmentManager(), confirmationFragment.getClass().getSimpleName());
    }

    @Override
    public void showAddCard(double amount, PaymentsModel paymentsModel) {
        Gson gson = new Gson();
        Bundle args = new Bundle();
        String paymentsDTOString = gson.toJson(paymentsModel);
        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, paymentsDTOString);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        DialogFragment fragment = new PracticeAddNewCreditCardFragment();
        fragment.setArguments(args);
        fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
    }

    @Override
    public void completePaymentProcess(WorkflowDTO workflowDTO) {
        refreshBalance();
    }

    @Override
    public void onPayLaterClicked(PaymentsModel paymentsModel) {

    }

    @Override
    public UserPracticeDTO getPracticeInfo(PaymentsModel paymentsModel) {
        if(paymentsModel!=null && !paymentsModel.getPaymentPayload().getUserPractices().isEmpty()){
            return paymentsModel.getPaymentPayload().getUserPractices().get(0);
        }
        return null;
    }

    @Override
    public void onLeftActionTapped(PaymentsModel paymentsModel, double owedAmount) {
        onPartialPaymentClicked(owedAmount);
    }

    @Override
    public void onRightActionTapped(PaymentsModel paymentsModel, double amount) {
        onPayButtonClicked(amount, paymentsModel);
    }

    @Override
    public void onDetailItemClick(PaymentsModel paymentsModel, PendingBalancePayloadDTO paymentLineItem) {
        String tag = PaymentDetailsFragmentDialog.class.getSimpleName();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        PaymentDetailsFragmentDialog dialog = PaymentDetailsFragmentDialog
                .newInstance(paymentsModel, paymentLineItem, false);
        dialog.show(ft, tag);
    }

    private void refreshBalance() {
        Map<String, String> queryMap = createQueryMap();
        TransitionDTO transitionDTO = paymentResultModel.getPaymentsMetadata().getPaymentsLinks().getPaymentsPatientBalances();
        getWorkflowServiceHelper().execute(transitionDTO, patientBalancesCallback, queryMap);
    }

    @NonNull
    private Map<String, String> createQueryMap() {
        PendingBalanceDTO patientBalanceDTO = paymentResultModel.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0);

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("patient_id", patientBalanceDTO.getMetadata().getPatientId());
        queryMap.put("practice_mgmt", patientBalanceDTO.getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", patientBalanceDTO.getMetadata().getPracticeId());
        return queryMap;
    }

    private WorkflowServiceCallback patientBalancesCallback = new WorkflowServiceCallback() {

        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            paymentResultModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO.toString());
            if (hasNoPayments()) {
                showNoPaymentsImage();
            } else {
                showPayments(paymentResultModel);
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
        }
    };

    @Override
    protected void processExternalPayment(PaymentExecution execution, Intent data) {
        switch (execution) {
            case clover: {
                String jsonPayload = data.getStringExtra(CarePayConstants.CLOVER_PAYMENT_SUCCESS_INTENT_DATA);
                if (jsonPayload != null) {
                    Gson gson = new Gson();
//                    PaymentsModel paymentsModel = gson.fromJson(jsonPayload, PaymentsModel.class);
                    WorkflowDTO workflowDTO = gson.fromJson(jsonPayload, WorkflowDTO.class);
                    showPaymentConfirmation(workflowDTO);
                }
                break;
            }
            default:
                //nothing
                return;
        }
    }

    @Override
    protected void processExternalPaymentFailure(PaymentExecution paymentExecution, int resultCode) {
        if(resultCode == CarePayConstants.PAYMENT_RETRY_PENDING_RESULT_CODE){
            //Display a success notification and do some cleanup
            PaymentQueuedDialogFragment dialogFragment = new PaymentQueuedDialogFragment();
            DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    Intent intent = getIntent();
                    setResult(CarePayConstants.HOME_PRESSED, intent);
                    finish();
                }
            };
            dialogFragment.setOnDismissListener(dismissListener);
            dialogFragment.show(getSupportFragmentManager(), dialogFragment.getClass().getName());

        }
    }

    @Override
    public void onDetailCancelClicked(PaymentsModel paymentsModel) {
        startPaymentProcess(paymentsModel);
    }
}
