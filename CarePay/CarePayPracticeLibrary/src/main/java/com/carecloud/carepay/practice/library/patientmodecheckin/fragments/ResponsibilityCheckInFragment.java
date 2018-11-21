package com.carecloud.carepay.practice.library.patientmodecheckin.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity;
import com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckoutActivity;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.demographics.DemographicsView;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowCallback;
import com.carecloud.carepaylibray.demographics.misc.CheckinFlowState;
import com.carecloud.carepaylibray.payments.fragments.ResponsibilityBaseFragment;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.practice.FlowStateInfo;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class ResponsibilityCheckInFragment extends ResponsibilityBaseFragment {

    private CheckinFlowCallback flowCallback;

    @Override
    public void attachCallback(Context context) {
        super.attachCallback(context);
        try {
            if (context instanceof DemographicsView) {
                flowCallback = ((DemographicsView) context).getPresenter();
            } else if (!(context instanceof PatientModeCheckoutActivity)) {
                flowCallback = (CheckinFlowCallback) context;
            }
        } catch (ClassCastException cce) {
            cce.printStackTrace();
            //this callback can be optional so lets swallow the exception
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flowStateInfo = new FlowStateInfo(PatientModeCheckinActivity.SUBFLOW_PAYMENTS, 0, 0);
    }

    @SuppressWarnings("deprecation")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_responsibility_practice, container, false);

        Toolbar toolbar = view.findViewById(com.carecloud.carepaylibrary.R.id.toolbar_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(0);
        }
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back_white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemUtil.hideSoftKeyboard(getActivity());
                getActivity().onBackPressed();
            }
        });
        toolbar.setTitle("");

        TextView responseTotal = view.findViewById(R.id.respons_total);
        TextView totalResponsibility = view.findViewById(R.id.respons_total_label);

        Button paymentOptionsButton = view.findViewById(R.id.paymentOptionsButton);
        paymentOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentOptionsFragmentDialog fragment = PaymentOptionsFragmentDialog.newInstance(paymentDTO);
                fragment.setCallback(new PaymentOptionsFragmentDialog.PaymentOptionsInterface() {
                    @Override
                    public void onOptionSelected(int option) {
                        switch (option) {
                            case PaymentOptionsFragmentDialog.PAYMENT_OPTION_TOTAL_AMOUNT:
                                doPayment();
                                break;
                            case PaymentOptionsFragmentDialog.PAYMENT_OPTION_PARTIAL_AMOUNT:
                                final PendingBalanceDTO selectedBalance = paymentDTO.getPaymentPayload()
                                        .getPatientBalances().get(0).getBalances().get(0);
                                actionCallback.onPartialPaymentClicked(total, selectedBalance);
                                break;
                            case PaymentOptionsFragmentDialog.PAYMENT_OPTION_PAYMENT_PLAN:
                                actionCallback.onPaymentPlanAction(paymentDTO);
                                break;
                            case PaymentOptionsFragmentDialog.PAYMENT_OPTION_PAY_LATER:
                                //Not Supported
                                break;
                        }
                    }
                });
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                fragment.show(transaction, fragment.getClass().getCanonicalName());
            }
        });

        getPaymentInformation();

        if (paymentDTO != null) {
            getPaymentLabels();
            try {
                ((TextView) view.findViewById(R.id.respons_title)).setText(Label.getLabel("payment_title"));
                List<PendingBalanceDTO> paymentList = paymentDTO.getPaymentPayload()
                        .getPatientBalances().get(0).getBalances();

                total = 0;
                if (paymentList != null && paymentList.size() > 0) {
                    for (PendingBalancePayloadDTO payment : paymentList.get(0).getPayload()) {
                        total = SystemUtil.safeAdd(total, payment.getAmount());
                        if(!payment.getType().equals(PendingBalancePayloadDTO.PATIENT_BALANCE)){
                            //not an amount that can be added to a plan
                            nonBalanceTotal = SystemUtil.safeAdd(nonBalanceTotal, payment.getAmount());
                        }
                    }

                    fillDetailAdapter(view, getAllPendingBalancePayloads(paymentList));

                    try {
                        NumberFormat formatter = new DecimalFormat(CarePayConstants.RESPONSIBILITY_FORMATTER);
                        responseTotal.setText(CarePayConstants.DOLLAR.concat(formatter.format(total)));
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                        Log.e(LOG_TAG, ex.getMessage());
                    }
                }

                totalResponsibility.setText(totalResponsibilityString);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return view;
    }

    protected void doPayment() {
        createPaymentModel(total);
        actionCallback.onPayButtonClicked(total, paymentDTO);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (flowCallback == null) {
            attachCallback(getContext());
        }
        if (flowCallback != null) {
            flowCallback.setCheckinFlow(CheckinFlowState.PAYMENT, 1, 1);
        }
    }

    @Override
    public void onDetailItemClick(PendingBalancePayloadDTO paymentLineItem) {
        actionCallback.displayBalanceDetails(paymentDTO, paymentLineItem, null);
    }
}
