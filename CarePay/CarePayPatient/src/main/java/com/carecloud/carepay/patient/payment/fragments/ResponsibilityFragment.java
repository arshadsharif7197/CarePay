package com.carecloud.carepay.patient.payment.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.patient.payment.interfaces.PaymentFragmentActivityInterface;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.payments.fragments.ResponsibilityBaseFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ResponsibilityFragment extends ResponsibilityBaseFragment {

    private PendingBalanceDTO selectedBalance;
    private PaymentFragmentActivityInterface toolbarCallback;

    /**
     * @param paymentsDTO              the payments DTO
     * @param payLaterButtonVisibility a boolean that indicates the visibility of the pay later button
     * @return an instance of ResponsibilityFragment
     */
    public static ResponsibilityFragment newInstance(PaymentsModel paymentsDTO, PendingBalanceDTO selectedBalance,
                                                     boolean payLaterButtonVisibility) {
        return newInstance(paymentsDTO, selectedBalance, payLaterButtonVisibility, null);
    }

    /**
     * @param paymentsDTO              the payments DTO
     * @param payLaterButtonVisibility a boolean that indicates the visibility of the pay later button
     * @return an instance of ResponsibilityFragment
     */
    public static ResponsibilityFragment newInstance(PaymentsModel paymentsDTO, PendingBalanceDTO selectedBalance,
                                                     boolean payLaterButtonVisibility, String title) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsDTO);
        if (selectedBalance != null) {
            DtoHelper.bundleDto(args, selectedBalance);
        }
        args.putBoolean("payLaterButtonVisibility", payLaterButtonVisibility);
        if (title != null) {
            args.putString("title", title);
        }
        ResponsibilityFragment fragment = new ResponsibilityFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void attachCallback(Context context) {
        super.attachCallback(context);
        try {
            toolbarCallback = (PaymentFragmentActivityInterface) context;
        } catch (ClassCastException cce) {
            //this calback is optional
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPaymentInformation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_responsibility, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        toolbar.setTitle("");
        getPaymentLabels();
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        if (getArguments().getString("title") != null) {
            paymentsTitleString = getArguments().getString("title");
        }
        title.setText(paymentsTitleString);

        if (toolbarCallback != null) {
            toolbarCallback.displayToolbar(false, null);
        }


        Button payTotalAmountButton = (Button) view.findViewById(R.id.pay_total_amount_button);
        Button makePartialPaymentButton = (Button) view.findViewById(R.id.make_partial_payment_button);
        Button payLaterButton = (Button) view.findViewById(R.id.pay_later_button);
        payLaterButton.setVisibility(getArguments().getBoolean("payLaterButtonVisibility") ? View.VISIBLE : View.GONE);
        payLaterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionCallback.onPayLaterClicked(selectedBalance);
            }
        });

        total = 0;
        fillDetailAdapter(view, selectedBalance.getPayload());
        for (PendingBalancePayloadDTO payment : selectedBalance.getPayload()) {
            total += payment.getAmount();
        }
        if (total > 0) {
            payTotalAmountButton.setClickable(true);
            payTotalAmountButton.setEnabled(true);
            makePartialPaymentButton.setEnabled(true);
            makePartialPaymentButton.setEnabled(true);
            payLaterButton.setEnabled(true);
            payLaterButton.setEnabled(true);

            payTotalAmountButton.setTextColor(Color.WHITE);
            int color = ContextCompat.getColor(getContext(), R.color.colorPrimary);
            makePartialPaymentButton.setTextColor(color);
            payLaterButton.setTextColor(color);
        }

        try {
            NumberFormat formatter = new DecimalFormat(CarePayConstants.RESPONSIBILITY_FORMATTER);

            TextView responseTotal = (TextView) view.findViewById(R.id.respons_total);
            responseTotal.setText(CarePayConstants.DOLLAR.concat(formatter.format(total)));

        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            Log.e(LOG_TAG, ex.getMessage());
        }


        payTotalAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doPayment();
            }
        });

        makePartialPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionCallback.onPartialPaymentClicked(total, selectedBalance);
            }
        });

        makePartialPaymentButton.setVisibility(isPartialPayAvailable(selectedBalance.getMetadata().getPracticeId(), total) ? View.VISIBLE : View.GONE);

        boolean canMakePayments = paymentDTO.getPaymentPayload().canMakePayments(selectedBalance.getMetadata().getPracticeId());
        if(!canMakePayments){
            payTotalAmountButton.setVisibility(View.GONE);
            makePartialPaymentButton.setVisibility(View.GONE);
        }

        boolean paymentPlanEnabled = !paymentDTO.getPaymentPayload().isPaymentPlanCreated() &&
                isPaymentPlanAvailable(selectedBalance.getMetadata().getPracticeId(), total);
        TextView paymentPlanButton = (TextView) view.findViewById(com.carecloud.carepay.patient.R.id.create_payment_plan_button);
        paymentPlanButton.setVisibility(paymentPlanEnabled ? View.VISIBLE : View.GONE);
        paymentPlanButton.setEnabled(paymentPlanEnabled);
        paymentPlanButton.setClickable(paymentPlanEnabled);
        paymentPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionCallback.onPaymentPlanAction(paymentDTO);
            }
        });
        if(mustAddToExisting) {
            paymentPlanButton.setText(Label.getLabel("payment_plan_add_existing"));
        }

    }

    @Override
    protected void getPaymentInformation() {
        super.getPaymentInformation();
        Bundle args = getArguments();
        if (args != null) {
            selectedBalance = DtoHelper.getConvertedDTO(PendingBalanceDTO.class, args);
        }
        if (selectedBalance == null) {
            selectedBalance = paymentDTO.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0);
        }
    }

    protected void doPayment() {
        createPaymentModel(total);
        actionCallback.onPayButtonClicked(total, paymentDTO);
    }

    @Override
    public void onDetailItemClick(PendingBalancePayloadDTO paymentLineItem) {
        actionCallback.displayBalanceDetails(paymentDTO, paymentLineItem, selectedBalance);
    }


}