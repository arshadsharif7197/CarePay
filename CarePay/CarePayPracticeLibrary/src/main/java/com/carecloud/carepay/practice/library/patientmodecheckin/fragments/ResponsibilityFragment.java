package com.carecloud.carepay.practice.library.patientmodecheckin.fragments;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity;
import com.carecloud.carepay.practice.library.payments.dialogs.PartialPaymentDialog;
import com.carecloud.carepay.practice.library.payments.fragments.PatientPaymentMethodFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.adapters.PaymentLineItemsListAdapter;
import com.carecloud.carepaylibray.payments.fragments.ResponsibilityBaseFragment;
import com.carecloud.carepaylibray.payments.models.PatiencePayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.practice.FlowStateInfo;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;

public class ResponsibilityFragment extends ResponsibilityBaseFragment implements com.carecloud.carepaylibray.customdialogs.PaymentDetailsDialog.PayNowClickListener {


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
        //TextView paymentDetails = (TextView) view.findViewById(R.id.response_payment_details);
        //TextView responseCopay = (TextView) view.findViewById(R.id.respons_copay);
        //TextView responsePreviousBalance = (TextView) view.findViewById(R.id.respons_prev_balance);
        TextView totalResponsibility = (TextView) view.findViewById(R.id.respons_total_label);
        //TextView prevBalanceResponsibility = (TextView) view.findViewById(R.id.respons_prev_balance_label);
        //TextView coPayResponsibility = (TextView) view.findViewById(R.id.respons_copay_label);
        Button payTotalButton = (Button) view.findViewById(R.id.pay_total_amount_button);
        payTotalButton.setClickable(false);
        payTotalButton.setEnabled(false);
        setGothamRoundedMediumTypeface(appCompatActivity, payTotalButton);
        payTotalButton.setBackgroundColor(getResources().getColor(R.color.light_gray));

        Button payPartialButton = (Button) view.findViewById(R.id.make_partial_payment_button);
        payPartialButton.setClickable(false);
        payPartialButton.setEnabled(false);
        setGothamRoundedMediumTypeface(appCompatActivity, payPartialButton);
        payPartialButton.setBackgroundColor(getResources().getColor(R.color.light_gray));


        getPaymentInformation();
        if (paymentDTO != null) {
            getPaymentLabels();
            ((TextView)view.findViewById(R.id.respons_title)).setText(paymentsTitleString);
/*
                List<PaymentPatientBalancesPayloadDTO> paymentList2 =
                        paymentsModel.getPaymentPayload().getPatientBalances().get(0) .getPayload();
*/
               // List<PaymentPatientBalancesPayloadDTO> paymentList =
             List<PatiencePayloadDTO> paymentList =       paymentDTO.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getPayload();
                                //.getPayload().getSummaryBalance();

                if (paymentList != null && paymentList.size() > 0) {
                    for (PatiencePayloadDTO payment : paymentList) {
                        if (payment.getType().equalsIgnoreCase("Patient Balance")) {
                            previousBalanceStr = payment.getAmount().toString();   //.getTotal();
                        } else if (payment.getType().equalsIgnoreCase(CarePayConstants.COPAY)) {
                            copayStr = payment.getAmount().toString();
                        }
                    }

                    fillDetailAdapter(view, paymentList);

                    try {
                        double copay = Double.parseDouble(copayStr!=null &&  !copayStr.isEmpty()?copayStr : "0.0" );
                        double previousBalance = Double.parseDouble(previousBalanceStr);
                        total = copay + previousBalance;

                        if (total > 0) {
                            payTotalButton.setClickable(true);
                            payTotalButton.setEnabled(true);
                            payPartialButton.setEnabled(true);
                            payPartialButton.setClickable(true);

                            payTotalButton.setBackgroundColor(getResources().getColor(R.color.yellowGreen));
                            payTotalButton.setTextColor(Color.WHITE);
                            payPartialButton.setTextColor(getResources().getColor(R.color.bright_cerulean));
                            payPartialButton.setBackgroundColor(Color.WHITE);
                            GradientDrawable border = new GradientDrawable();
                            border.setColor(Color.WHITE);
                            border.setStroke(1, getResources().getColor(R.color.bright_cerulean));
                            payPartialButton.setBackground(border);
                        }

                        NumberFormat formatter = new DecimalFormat(CarePayConstants.RESPONSIBILITY_FORMATTER);
                        responseTotal.setText(CarePayConstants.DOLLAR.concat(formatter.format(total)));
                        //responseCopay.setText(CarePayConstants.DOLLAR.concat(copayStr));
                        //responsePreviousBalance.setText(CarePayConstants.DOLLAR.concat(previousBalanceStr));
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                        Log.e(LOG_TAG, ex.getMessage());
                    }
                }


            //paymentDetails.setText(paymentDetailsString);
            totalResponsibility.setText(totalResponsibilityString);
            //prevBalanceResponsibility.setText(previousBalanceString);
            //coPayResponsibility.setText(insuranceCopayString);

            payTotalButton.setText(payTotalAmountString);
            payPartialButton.setText(payPartialAmountString);
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
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        String paymentsDTOString = gson.toJson(paymentDTO);
        bundle.putString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO,
                paymentsDTOString);
        bundle.putString(CarePayConstants.INTAKE_BUNDLE,
                paymentsDTOString);
        bundle.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, total);

        PatientPaymentMethodFragment fragment = new PatientPaymentMethodFragment();
        fragment.setArguments(bundle);
        ((PatientModeCheckinActivity) getActivity()).navigateToFragment(fragment, true);
    }


    @Override
    public void onStart() {
        super.onStart();
        ((PatientModeCheckinActivity) getActivity()).updateSection(flowStateInfo);
    }


}