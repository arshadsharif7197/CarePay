package com.carecloud.carepay.practice.library.patientmodecheckin.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.carecloud.carepay.practice.library.payments.dialogs.PaymentDetailsFragmentDialog;
import com.carecloud.carepay.service.library.CarePayConstants;
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
            } else {
                flowCallback = (CheckinFlowCallback) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement CheckinFlowCallback");
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

        Toolbar toolbar = (Toolbar) view.findViewById(com.carecloud.carepaylibrary.R.id.toolbar_layout);
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

        TextView responseTotal = (TextView) view.findViewById(R.id.respons_total);
        TextView totalResponsibility = (TextView) view.findViewById(R.id.respons_total_label);

        Button payTotalButton = (Button) view.findViewById(R.id.pay_total_amount_button);
        payTotalButton.setClickable(false);
        payTotalButton.setEnabled(false);

        Button payPartialButton = (Button) view.findViewById(R.id.make_partial_payment_button);
        payPartialButton.setClickable(false);
        payPartialButton.setEnabled(false);

        getPaymentInformation();

        if (paymentDTO != null) {
            getPaymentLabels();
            try {
                ((TextView) view.findViewById(R.id.respons_title)).setText(paymentsTitleString);

                List<PendingBalanceDTO> paymentList = paymentDTO.getPaymentPayload().getPatientBalances().get(0).getBalances();

                total = 0;
                if (paymentList != null && paymentList.size() > 0) {
                    for (PendingBalancePayloadDTO payment : paymentList.get(0).getPayload()) {
                        total += payment.getAmount();
                    }

                    fillDetailAdapter(view, paymentList);

                    try {
                        if (total > 0) {
                            payTotalButton.setEnabled(true);
                            payTotalButton.setClickable(true);

                            payPartialButton.setEnabled(true);
                            payPartialButton.setClickable(true);

                        }

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

        payTotalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doPayment();
            }
        });

        payPartialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionCallback.startPartialPayment(total);
            }
        });

        return view;
    }

    protected void doPayment() {
        actionCallback.onPayButtonClicked(total, paymentDTO);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(flowCallback == null){
            attachCallback(getContext());
        }
        flowCallback.setCheckinFlow(CheckinFlowState.PAYMENT, 1, 1);
    }

    @Override
    public void onDetailItemClick(PendingBalancePayloadDTO paymentLineItem) {
        String tag = PaymentDetailsFragmentDialog.class.getSimpleName();
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        Fragment prev = getChildFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        PaymentDetailsFragmentDialog dialog = PaymentDetailsFragmentDialog
                .newInstance(paymentDTO, paymentLineItem, false);
        dialog.show(ft, tag);
    }
}
