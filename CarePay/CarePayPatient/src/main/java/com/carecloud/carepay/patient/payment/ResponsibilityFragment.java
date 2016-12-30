package com.carecloud.carepay.patient.payment;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.patient.payment.dialogs.PartialPaymentDialog;
import com.carecloud.carepay.patient.payment.fragments.PaymentMethodFragment;
import com.carecloud.carepay.service.library.BaseServiceGenerator;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;
import com.carecloud.carepaylibray.payments.models.PaymentPatientBalancesPayloadDTO;
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

/**
 * Created by lsoco_user on 9/2/2016.
 * Responsibility screen
 */
public class ResponsibilityFragment extends Fragment {

    private static final String LOG_TAG = ResponsibilityFragment.class.getSimpleName();
    private AppCompatActivity appCompatActivity;
    private String copayStr = "";
    private String previousBalanceStr = "";

    private PaymentsModel paymentDTO = null;
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
    }

    @SuppressWarnings("deprecation")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_responsibility, container, false);

        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.respons_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        setGothamRoundedMediumTypeface(appCompatActivity, title);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("");

        TextView responseTotal = (TextView) view.findViewById(R.id.respons_total);
        TextView paymentDetails = (TextView) view.findViewById(R.id.response_payment_details);
        TextView responseCopay = (TextView) view.findViewById(R.id.respons_copay);
        TextView responsePreviousBalance = (TextView) view.findViewById(R.id.respons_prev_balance);
        TextView totalResponsibility = (TextView) view.findViewById(R.id.respons_total_label);
        TextView prevBalanceResponsibility = (TextView) view.findViewById(R.id.respons_prev_balance_label);
        TextView coPayResponsibility = (TextView) view.findViewById(R.id.respons_copay_label);

        Button payTotalAmountButton = (Button) view.findViewById(R.id.pay_total_amount_button);
        setGothamRoundedMediumTypeface(appCompatActivity, payTotalAmountButton);

        Button makePartialPaymentButton = (Button) view.findViewById(R.id.make_partial_payment_button);
        setGothamRoundedMediumTypeface(appCompatActivity, makePartialPaymentButton);

        Button payLaterButton = (Button) view.findViewById(R.id.pay_later_button);
        setGothamRoundedMediumTypeface(appCompatActivity, payLaterButton);

        Bundle bundle = getArguments();
        if (bundle != null) {
            Gson gson = new Gson();
            bundle = getArguments();
            String paymentsDTOString = bundle.getString(CarePayConstants.INTAKE_BUNDLE);
            paymentDTO = gson.fromJson(paymentsDTOString, PaymentsModel.class);

            List<PaymentPatientBalancesPayloadDTO> paymentList = paymentDTO.getPaymentPayload().getPatientBalances().get(0).getPayload();
            getPaymentLabels();

            if (paymentList != null && paymentList.size() > 1) {
                for (PaymentPatientBalancesPayloadDTO payment : paymentList) {
                    if (payment.getBalanceType().equalsIgnoreCase(CarePayConstants.PREVIOUS_BALANCE)) {
                        previousBalanceStr = payment.getTotal();
                    } else if (payment.getBalanceType().equalsIgnoreCase(CarePayConstants.COPAY)) {
                        copayStr = payment.getTotal();
                    }
                }

                try {
                    double copay = Double.parseDouble(copayStr);
                    double previousBalance = Double.parseDouble(previousBalanceStr);
                    total = copay + previousBalance;

                    if (total == 0) {
                        payTotalAmountButton.setClickable(false);
                        payTotalAmountButton.setEnabled(false);
                        makePartialPaymentButton.setClickable(false);
                        makePartialPaymentButton.setEnabled(false);
                        payLaterButton.setClickable(false);
                        payLaterButton.setEnabled(false);

                        payTotalAmountButton.setBackgroundColor(getResources().getColor(R.color.light_gray));
                        makePartialPaymentButton.setBackgroundColor(getResources().getColor(R.color.light_gray));
                        payLaterButton.setBackgroundColor(getResources().getColor(R.color.light_gray));
                    } else {
                        payTotalAmountButton.setClickable(true);
                        payTotalAmountButton.setEnabled(true);
                        makePartialPaymentButton.setEnabled(true);
                        makePartialPaymentButton.setEnabled(true);
                        payLaterButton.setEnabled(true);
                        payLaterButton.setEnabled(true);

                        payTotalAmountButton.setBackgroundColor(getResources().getColor(R.color.blue_cerulian));
                        payTotalAmountButton.setTextColor(Color.WHITE);
                        makePartialPaymentButton.setTextColor(getResources().getColor(R.color.bright_cerulean));
                        makePartialPaymentButton.setBackgroundColor(Color.WHITE);
                        GradientDrawable border = new GradientDrawable();
                        border.setColor(Color.WHITE);
                        border.setStroke(1, getResources().getColor(R.color.bright_cerulean));
                        makePartialPaymentButton.setBackground(border);
                    }

                    NumberFormat formatter = new DecimalFormat(CarePayConstants.RESPONSIBILITY_FORMATTER);
                    responseTotal.setText(CarePayConstants.DOLLAR.concat(formatter.format(total)));
                    responseCopay.setText(CarePayConstants.DOLLAR.concat(copayStr));
                    responsePreviousBalance.setText(CarePayConstants.DOLLAR.concat(previousBalanceStr));

                    paymentDetails.setText(paymentDetailsString);
                    totalResponsibility.setText(totalResponsibilityString);
                    prevBalanceResponsibility.setText(previousBalanceString);
                    coPayResponsibility.setText(insuranceCopayString);

                    payTotalAmountButton.setText(payTotalAmountString);
                    makePartialPaymentButton.setText(payPartialAmountString);
//                    payLaterButton.setText(payLaterString);
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                    Log.e(LOG_TAG, ex.getMessage());
                }
            }
        }

        paymentDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call for payment details dialog
            }
        });

        payTotalAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPaymentInformation();
                FragmentManager fragmentmanager = getActivity().getSupportFragmentManager();
                PaymentMethodFragment fragment = (PaymentMethodFragment)
                        fragmentmanager.findFragmentByTag(PaymentMethodFragment.class.getSimpleName());

                if (fragment == null) {
                    fragment = new PaymentMethodFragment();
                }

                Bundle bundle = new Bundle();
                Gson gson = new Gson();
                String paymentsDTOString = gson.toJson(paymentDTO);
                bundle.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, total);
                bundle.putString(CarePayConstants.INTAKE_BUNDLE, paymentsDTOString);
                fragment.setArguments(bundle);

                FragmentTransaction fragmentTransaction = fragmentmanager.beginTransaction();
                fragmentTransaction.replace(R.id.payment_frag_holder, fragment);
                fragmentTransaction.addToBackStack(PaymentMethodFragment.class.getSimpleName());
                fragmentTransaction.commit();
            }
        });

        makePartialPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PartialPaymentDialog(getActivity(), paymentDTO).show();
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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
                Log.e(getActivity().getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), "");
            }
        });
    }

    /**
     * For tests
     *
     * @param activity The activity
     */
    public void setActivity(KeyboardHolderActivity activity) {
        appCompatActivity = activity;
    }

    /**
     * payment labels
     */
    public void getPaymentLabels() {
        if (paymentDTO != null) {
            PaymentsMetadataModel paymentsMetadataDTO = paymentDTO.getPaymentsMetadata();
            if (paymentsMetadataDTO != null) {
                PaymentsLabelDTO paymentsLabelsDTO = paymentsMetadataDTO.getPaymentsLabel();
                if (paymentsLabelsDTO != null) {
                    paymentDetailsString = paymentsLabelsDTO.getPaymentResponsibilityDetails();
                    totalResponsibilityString = paymentsLabelsDTO.getPaymentTotalResponsibility();
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
}