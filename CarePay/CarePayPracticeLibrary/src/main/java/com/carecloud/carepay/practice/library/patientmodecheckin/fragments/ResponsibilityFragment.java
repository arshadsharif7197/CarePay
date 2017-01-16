package com.carecloud.carepay.practice.library.patientmodecheckin.fragments;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.patientmodecheckin.activities.PatientModeCheckinActivity;
import com.carecloud.carepay.practice.library.payments.dialogs.PartialPaymentDialog;
import com.carecloud.carepay.practice.library.payments.fragments.PatientPaymentMethodFragment;
import com.carecloud.carepay.service.library.BaseServiceGenerator;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.adapters.PaymentLineItemsListAdapter;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;
import com.carecloud.carepaylibray.payments.models.PatiencePayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMetadataModel;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.services.PaymentsService;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;

public class ResponsibilityFragment extends BaseCheckinFragment implements com.carecloud.carepaylibray.customdialogs.PaymentDetailsDialog.PayNowClickListener {

    private static final String LOG_TAG = ResponsibilityFragment.class.getSimpleName();
    private AppCompatActivity appCompatActivity;
    private String copayStr = "";
    private String previousBalanceStr = "";

    private PaymentsModel paymentsModel;
    private String totalResponsibilityString;
    private String paymentDetailsString;
    private String previousBalanceString;
    private String insuranceCopayString;
    private String payTotalAmountString;
    private String payPartialAmountString;
    private String payLaterString;
    private double total;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCompatActivity = (AppCompatActivity) getActivity();
        flowStateInfo = new PatientModeCheckinActivity.FlowStateInfo(PatientModeCheckinActivity.SUBFLOW_PAYMENTS, 0, 0);
    }

    @SuppressWarnings("deprecation")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_responsibility, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.respons_toolbar);
        toolbar.setTitle("");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

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

        Button payLaterButton = (Button) view.findViewById(R.id.pay_later_button);
        payLaterButton.setClickable(false);
        payLaterButton.setEnabled(false);
        setGothamRoundedMediumTypeface(appCompatActivity, payLaterButton);
        payLaterButton.setBackgroundColor(getResources().getColor(R.color.light_gray));

        Bundle arguments = getArguments();
        if (arguments != null) {
            Gson gson = new Gson();
            String paymentInfo = arguments.getString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO);
            paymentsModel = gson.fromJson(paymentInfo, PaymentsModel.class);

            getPaymentLabels();

            if (paymentsModel != null) {
/*
                List<PaymentPatientBalancesPayloadDTO> paymentList2 =
                        paymentsModel.getPaymentPayload().getPatientBalances().get(0) .getPayload();
*/
               // List<PaymentPatientBalancesPayloadDTO> paymentList =
                        List<PatiencePayloadDTO> paymentList =       paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getPayload();
                                //.getPayload().getSummaryBalance();

                if (paymentList != null && paymentList.size() > 0) {
                    for (PatiencePayloadDTO payment : paymentList) {
                        if (payment.getType().equalsIgnoreCase("Patient Balance")) {
                            previousBalanceStr = payment.getAmount().toString();   //.getTotal();
                        } else if (payment.getType().equalsIgnoreCase(CarePayConstants.COPAY)) {
                            copayStr = payment.getAmount().toString();
                        }
                    }
                    RecyclerView paymentDetailsListRecyclerView = ((RecyclerView) view.findViewById(R.id.responsibility_line_item_recycle_view));
                    paymentDetailsListRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

                    PaymentLineItemsListAdapter adapter = new PaymentLineItemsListAdapter(this.getContext(), paymentsModel, paymentList, this);
                    paymentDetailsListRecyclerView.setAdapter(adapter);

                    try {
                        double copay = Double.parseDouble(copayStr!=null &&  !copayStr.isEmpty()?copayStr : "0.0" );
                        double previousBalance = Double.parseDouble(previousBalanceStr);
                        total = copay + previousBalance;

                        if (total > 0) {
                            payTotalButton.setClickable(true);
                            payTotalButton.setEnabled(true);
                            payPartialButton.setEnabled(true);
                            payPartialButton.setEnabled(true);
                            payLaterButton.setEnabled(true);
                            payLaterButton.setEnabled(true);

                            payTotalButton.setBackgroundColor(getResources().getColor(R.color.blue_cerulian));
                            payTotalButton.setTextColor(Color.WHITE);
                            payPartialButton.setTextColor(getResources().getColor(R.color.bright_cerulean));
                            payPartialButton.setBackgroundColor(Color.WHITE);
                            GradientDrawable border = new GradientDrawable();
                            border.setColor(Color.WHITE);
                            border.setStroke(1, getResources().getColor(R.color.bright_cerulean));
                            payPartialButton.setBackground(border);

                            payLaterButton.setTextColor(getResources().getColor(R.color.bright_cerulean));
                            payLaterButton.setBackgroundColor(Color.WHITE);
                            payLaterButton.setBackground(border);
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
            }

            //paymentDetails.setText(paymentDetailsString);
            totalResponsibility.setText(totalResponsibilityString);
            //prevBalanceResponsibility.setText(previousBalanceString);
            //coPayResponsibility.setText(insuranceCopayString);

            payTotalButton.setText(payTotalAmountString);
            payPartialButton.setText(payPartialAmountString);
            payLaterButton.setText(payLaterString);
        }

        //paymentDetails.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
                // Call for payment details dialog
        //        PaymentDetailsDialog detailsDialog = new PaymentDetailsDialog(getActivity(),
        //                paymentsModel, ResponsibilityFragment.this);
        //        detailsDialog.show();
        //    }
        //});

        payTotalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPaymentInformation();
                doPayment();
            }
        });

        payPartialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PartialPaymentDialog(getActivity(), paymentsModel).show();
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

    private void doPayment() {
        Bundle bundle = new Bundle();
        Gson gson = new Gson();
        String paymentsDTOString = gson.toJson(paymentsModel);
        bundle.putString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO,
                paymentsDTOString);
        bundle.putString(CarePayConstants.INTAKE_BUNDLE,
                paymentsDTOString);
        bundle.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, total);

        PatientPaymentMethodFragment fragment = new PatientPaymentMethodFragment();
        fragment.setArguments(bundle);
        ((PatientModeCheckinActivity) getActivity()).navigateToFragment(fragment, true);
    }

    private void getPaymentInformation() {
        PaymentsService paymentService = (new BaseServiceGenerator(getActivity()))
                .createService(PaymentsService.class);
        Call<PaymentsModel> call = paymentService.fetchPaymentInformation();
        call.enqueue(new Callback<PaymentsModel>() {
            @Override
            public void onResponse(Call<PaymentsModel> call, Response<PaymentsModel> response) {
                PaymentsModel paymentsDTO = response.body();
            }

            @Override
            public void onFailure(Call<PaymentsModel> call, Throwable throwable) {
                SystemUtil.showFaultDialog(getActivity());
                Log.e(getActivity().getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), throwable.getMessage());
            }
        });
    }

    private void getPaymentLabels() {
        if (paymentsModel != null) {
            PaymentsMetadataModel paymentsMetadataDTO = paymentsModel.getPaymentsMetadata();
            if (paymentsMetadataDTO != null) {
                PaymentsLabelDTO paymentsLabelsDTO = paymentsMetadataDTO.getPaymentsLabel();
                if (paymentsLabelsDTO != null) {
                    paymentDetailsString = paymentsLabelsDTO.getPaymentResponsibilityDetails();
                    totalResponsibilityString = paymentsLabelsDTO.getPaymentTotalResponsibility();
                    previousBalanceString = paymentsLabelsDTO.getPaymentPreviousBalance();
                    insuranceCopayString = paymentsLabelsDTO.getPaymentInsuranceCopay();

                    payTotalAmountString = paymentsLabelsDTO.getPaymentPayTotalAmountButton();
                    payPartialAmountString = paymentsLabelsDTO.getPaymentPartialAmountButton();
                    payLaterString = paymentsLabelsDTO.getPaymentResponsibilityPayLater();
                }
            }
        }
    }

    /**
     * For tests
     *
     * @param activity The activity
     */
    public void setActivity(KeyboardHolderActivity activity) {
        appCompatActivity = activity;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((PatientModeCheckinActivity) getActivity()).updateSection(flowStateInfo);
    }

    @Override
    public void onPayNowButtonClicked() {
        doPayment();
    }
}