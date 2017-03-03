package com.carecloud.carepay.patient.payment;

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

import com.carecloud.carepay.patient.payment.dialogs.PatientPartialPaymentDialog;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.payments.fragments.ResponsibilityBaseFragment;
import com.carecloud.carepaylibray.payments.models.PatienceBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PatiencePayloadDTO;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class ResponsibilityFragment extends ResponsibilityBaseFragment {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressWarnings("deprecation")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_responsibility, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        toolbar.setTitle("");

        TextView responseTotal = (TextView) view.findViewById(R.id.respons_total);
        TextView totalResponsibility = (TextView) view.findViewById(R.id.respons_total_label);

        Button payTotalAmountButton = (Button) view.findViewById(R.id.pay_total_amount_button);
        payTotalAmountButton.setClickable(false);
        payTotalAmountButton.setEnabled(false);
        setGothamRoundedMediumTypeface(appCompatActivity, payTotalAmountButton);
//        payTotalAmountButton.setBackgroundColor(getResources().getColor(R.color.light_gray));

        Button makePartialPaymentButton = (Button) view.findViewById(R.id.make_partial_payment_button);
        makePartialPaymentButton.setClickable(false);
        makePartialPaymentButton.setEnabled(false);
        setGothamRoundedMediumTypeface(appCompatActivity, makePartialPaymentButton);
//        makePartialPaymentButton.setBackgroundColor(getResources().getColor(R.color.light_gray));

        Button payLaterButton = (Button) view.findViewById(R.id.pay_later_button);
        payLaterButton.setClickable(false);
        payLaterButton.setEnabled(false);
        setGothamRoundedMediumTypeface(appCompatActivity, payLaterButton);
//        payLaterButton.setBackgroundColor(getResources().getColor(R.color.light_gray));

        try {
            getPaymentInformation();
            if (paymentDTO != null) {
                getPaymentLabels();
                List<PatienceBalanceDTO> paymentList = paymentDTO.getPaymentPayload()
                        .getPatientBalances().get(0).getBalances();

                total = 0;
                if (paymentList != null && paymentList.size() > 0) {
                    fillDetailAdapter(view, paymentList);
                    for (PatiencePayloadDTO payment : paymentList.get(0).getPayload()) {
                        total += payment.getAmount();
                    }
                    try {

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

//                            payTotalAmountButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
//                            makePartialPaymentButton.setBackgroundColor(Color.WHITE);
//                            payLaterButton.setBackgroundColor(Color.WHITE);

//                            GradientDrawable border = new GradientDrawable();
//                            border.setColor(Color.WHITE);
//                            border.setStroke(1, getResources().getColor(R.color.colorPrimary));
//                            makePartialPaymentButton.setBackground(border);
//                            payLaterButton.setBackground(border);
                        }

                        NumberFormat formatter = new DecimalFormat(CarePayConstants.RESPONSIBILITY_FORMATTER);
                        responseTotal.setText(CarePayConstants.DOLLAR.concat(formatter.format(total)));

                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                        Log.e(LOG_TAG, ex.getMessage());
                    }
                }

                totalResponsibility.setText(totalResponsibilityString);

                payTotalAmountButton.setText(payTotalAmountString);
                makePartialPaymentButton.setText(payPartialAmountString);
                payLaterButton.setText(payLaterString);

                title.setText(paymentsTitleString);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

        payLaterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Add code to pay later
            }
        });

        return view;
    }

    protected void doPayment() {
        actionCallback.onPayButtonClicked(total);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}