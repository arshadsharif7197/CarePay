package com.carecloud.carepay.patient.payment.fragments;

import android.graphics.Color;
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

import com.carecloud.carepay.patient.payment.dialogs.PatientPartialPaymentDialog;
import com.carecloud.carepay.patient.payment.dialogs.PaymentDetailsFragmentDialog;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.payments.fragments.ResponsibilityBaseFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class ResponsibilityFragment extends ResponsibilityBaseFragment {

    /**
     * @param paymentsDTO              the payments DTO
     * @param payLaterButtonVisibility a boolean that indicates the visibility of the pay later button
     * @return an instance of ResponsibilityFragment
     */
    public static ResponsibilityFragment newInstance(PaymentsModel paymentsDTO, boolean payLaterButtonVisibility) {
        return newInstance(paymentsDTO, payLaterButtonVisibility, null);
    }

    /**
     * @param paymentsDTO              the payments DTO
     * @param payLaterButtonVisibility a boolean that indicates the visibility of the pay later button
     * @param title                    the toolbar title
     * @return an instance of ResponsibilityFragment
     */
    public static ResponsibilityFragment newInstance(PaymentsModel paymentsDTO, boolean payLaterButtonVisibility,
                                                     String title) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsDTO);
        args.putBoolean("payLaterButtonVisibility", payLaterButtonVisibility);
        if (title != null) {
            args.putString("title", title);
        }
        ResponsibilityFragment fragment = new ResponsibilityFragment();
        fragment.setArguments(args);
        return fragment;
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


        Button payTotalAmountButton = (Button) view.findViewById(R.id.pay_total_amount_button);
        Button makePartialPaymentButton = (Button) view.findViewById(R.id.make_partial_payment_button);
        Button payLaterButton = (Button) view.findViewById(R.id.pay_later_button);
        payLaterButton.setVisibility(getArguments().getBoolean("payLaterButtonVisibility") ? View.VISIBLE : View.GONE);
        payLaterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionCallback.onPayLaterClicked(paymentDTO);
            }
        });

        List<PendingBalanceDTO> paymentList = paymentDTO.getPaymentPayload()
                .getPatientBalances().get(0).getBalances();

        total = 0;
        if (paymentList != null && paymentList.size() > 0) {
            fillDetailAdapter(view, paymentList);
            for (PendingBalancePayloadDTO payment : paymentList.get(0).getPayload()) {
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
                makePartialPaymentButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                payLaterButton.setTextColor(getResources().getColor(R.color.colorPrimary));
            }

            try {
                NumberFormat formatter = new DecimalFormat(CarePayConstants.RESPONSIBILITY_FORMATTER);

                TextView responseTotal = (TextView) view.findViewById(R.id.respons_total);
                responseTotal.setText(CarePayConstants.DOLLAR.concat(formatter.format(total)));

            } catch (NumberFormatException ex) {
                ex.printStackTrace();
                Log.e(LOG_TAG, ex.getMessage());
            }
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
                new PatientPartialPaymentDialog(getActivity(), paymentDTO).show();
            }
        });
    }

    protected void doPayment() {
        actionCallback.onPayButtonClicked(total, paymentDTO);
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

        PaymentDetailsFragmentDialog dialog = PaymentDetailsFragmentDialog.newInstance(paymentDTO, paymentLineItem);
        dialog.show(ft, tag);
    }
}