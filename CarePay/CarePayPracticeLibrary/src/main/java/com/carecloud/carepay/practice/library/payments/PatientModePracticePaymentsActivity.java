package com.carecloud.carepay.practice.library.payments;

import android.os.Bundle;
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
import com.carecloud.carepay.practice.library.payments.dialogs.PaymentAmountReceiptDialog;
import com.carecloud.carepay.practice.library.payments.dialogs.PaymentDetailsFragmentDialog;
import com.carecloud.carepay.practice.library.payments.dialogs.ResponsibilityFragmentDialog;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeChooseCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentMethodDialogFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.payments.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.fragments.AddNewCreditCardFragment;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.google.gson.Gson;

/**
 * Created by pjohnson on 16/03/17.
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
        //TODO: this should be replaced by the proper key
        logoutTextview.setText("Log Out");
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
        PaymentBalancesAdapter paymentBalancesAdapter = new PaymentBalancesAdapter(paymentsModel.getPaymentPayload().getPatientBalances(),
                paymentsModel.getPaymentPayload().getUserPractices().get(0));
        paymentBalancesAdapter.setCallback(this);
        recyclerView.setAdapter(paymentBalancesAdapter);
        //TODO: Should change this to the proper keys
        ((TextView) findViewById(R.id.title)).setText("Select a pending payment");
        ((TextView) findViewById(R.id.subtitle)).setText(Label.getLabel("no_payment_description"));

    }

    private void showNoPaymentsImage() {
        findViewById(R.id.emptyPaymentsImageView).setVisibility(View.VISIBLE);
        findViewById(R.id.appointmentsRecyclerView).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.title)).setText(Label.getLabel("no_payment_title"));
        ((TextView) findViewById(R.id.subtitle)).setText(Label.getLabel("no_payment_description"));
    }

    @Override
    public void onBalancePayButtonClicked(PatientBalanceDTO patientBalanceDTO) {
        String tag = ResponsibilityFragmentDialog.class.getSimpleName();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        ResponsibilityHeaderModel headerModel = ResponsibilityHeaderModel.newClinicHeader(paymentResultModel);
        ResponsibilityFragmentDialog dialog = ResponsibilityFragmentDialog
                .newInstance(paymentResultModel, Label.getLabel("payment_partial_label"),
                        Label.getLabel("payment_pay_total_amount_button"), headerModel);
        dialog.show(ft, tag);
    }

    @Override
    public void startPartialPayment() {

    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        String paymentsDTOString = gson.toJson(paymentsModel);
        bundle.putString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO, paymentsDTOString);
        bundle.putString(CarePayConstants.INTAKE_BUNDLE, paymentsDTOString);
        bundle.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);

        PracticePaymentMethodDialogFragment fragment = new PracticePaymentMethodDialogFragment();
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
    }

    @Override
    public void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod, double amount, PaymentsModel paymentsModel) {
        boolean isCloverDevice = HttpConstants.getDeviceInformation().getDeviceType().equals(CarePayConstants.CLOVER_DEVICE);
        if (!isCloverDevice && paymentsModel.getPaymentPayload().getPatientCreditCards() != null && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            DialogFragment fragment = PracticeChooseCreditCardFragment.newInstance(paymentsModel, selectedPaymentMethod.getLabel(), amount);
            fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
        } else {
            showAddCard(amount, paymentsModel);
        }
    }

    @Override
    public void onPaymentPlanAction() {

    }

    @Override
    public void showReceipt(PaymentsModel paymentsModel) {
        PaymentAmountReceiptDialog receiptDialog = new PaymentAmountReceiptDialog(this, paymentsModel, paymentsModel);
        receiptDialog.show();
    }

    @Override
    public void showAddCard(double amount, PaymentsModel paymentsModel) {
        Gson gson = new Gson();
        Bundle args = new Bundle();
        String paymentsDTOString = gson.toJson(paymentsModel);
        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, paymentsDTOString);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        DialogFragment fragment = new AddNewCreditCardFragment();
        fragment.setArguments(args);
        fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
    }

    @Override
    public void onLeftActionTapped() {

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
                .newInstance(paymentsModel, paymentLineItem);
        dialog.show(ft, tag);
    }
}
