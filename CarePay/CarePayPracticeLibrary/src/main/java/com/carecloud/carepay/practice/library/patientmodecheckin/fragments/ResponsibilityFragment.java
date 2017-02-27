package com.carecloud.carepay.practice.library.patientmodecheckin.fragments;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity;
import com.carecloud.carepay.practice.library.payments.dialogs.PartialPaymentDialog;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.customdialogs.PaymentDetailsDialog;
import com.carecloud.carepaylibray.payments.fragments.ResponsibilityBaseFragment;
import com.carecloud.carepaylibray.payments.models.PatienceBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PatiencePayloadDTO;
import com.carecloud.carepaylibray.practice.FlowStateInfo;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class ResponsibilityFragment extends ResponsibilityBaseFragment implements PaymentDetailsDialog.PayNowClickListener {


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

        TextView responseTotal = (TextView) view.findViewById(R.id.respons_total);
        TextView totalResponsibility = (TextView) view.findViewById(R.id.respons_total_label);

        Button payTotalButton = (Button) view.findViewById(R.id.pay_total_amount_button);
        payTotalButton.setClickable(false);
        payTotalButton.setEnabled(false);
        setGothamRoundedMediumTypeface(appCompatActivity, payTotalButton);

        Button payPartialButton = (Button) view.findViewById(R.id.make_partial_payment_button);
        payPartialButton.setClickable(false);
        payPartialButton.setEnabled(false);
        setGothamRoundedMediumTypeface(appCompatActivity, payPartialButton);

        getPaymentInformation();

        if (paymentDTO != null) {
            getPaymentLabels();
            try {
                ((TextView) view.findViewById(R.id.respons_title)).setText(paymentsTitleString);

                List<PatienceBalanceDTO> paymentList = paymentDTO.getPaymentPayload().getPatientBalances().get(0).getBalances();

                total = 0;
                if (paymentList != null && paymentList.size() > 0) {
                    for (PatiencePayloadDTO payment : paymentList.get(0).getPayload()) {
                        total += payment.getAmount();
                    }

                    fillDetailAdapter(view, paymentList);

                    try {
                        if (total > 0) {
                            payTotalButton.setClickable(true);
                            payTotalButton.setEnabled(true);
                            payPartialButton.setEnabled(true);
                            payPartialButton.setClickable(true);

                            payTotalButton.setTextColor(Color.WHITE);
                            payPartialButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                            GradientDrawable border = new GradientDrawable();
                            border.setColor(Color.WHITE);
                            border.setStroke(1, getResources().getColor(R.color.colorPrimary));
                        }

                        NumberFormat formatter = new DecimalFormat(CarePayConstants.RESPONSIBILITY_FORMATTER);
                        responseTotal.setText(CarePayConstants.DOLLAR.concat(formatter.format(total)));
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                        Log.e(LOG_TAG, ex.getMessage());
                    }
                }

                totalResponsibility.setText(totalResponsibilityString);
                payTotalButton.setText(payTotalAmountString);
                payPartialButton.setText(payPartialAmountString);
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
                new PartialPaymentDialog(getActivity(), paymentDTO).show();
            }
        });

        return view;
    }

    protected void doPayment() {
        actionCallback.makePayment(total);
//        Bundle bundle = new Bundle();
//        Gson gson = new Gson();
//        String paymentsDTOString = gson.toJson(paymentDTO);
//        bundle.putString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO, paymentsDTOString);
//        bundle.putString(CarePayConstants.INTAKE_BUNDLE, paymentsDTOString);
//        bundle.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, total);
//
//        PatientPaymentMethodFragment fragment = new PatientPaymentMethodFragment();
//        fragment.setArguments(bundle);
//        ((PatientModeCheckinActivity) getActivity()).navigateToFragment(fragment, true);
    }

    @Override
    public void onStart() {
        super.onStart();
        ((PatientModeCheckinActivity) getActivity()).updateSection(flowStateInfo);
    }
}