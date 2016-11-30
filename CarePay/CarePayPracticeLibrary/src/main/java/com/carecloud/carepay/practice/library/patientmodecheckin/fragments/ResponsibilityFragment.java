package com.carecloud.carepay.practice.library.patientmodecheckin.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.service.library.BaseServiceGenerator;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.intake.models.PayloadPaymentModel;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;
import com.carecloud.carepaylibray.payments.models.PaymentsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMetadataDTO;
import com.carecloud.carepaylibray.payments.services.PaymentsService;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedBookTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;


public class ResponsibilityFragment extends Fragment {

    private static final String LOG_TAG = ResponsibilityFragment.class.getSimpleName();
    private AppCompatActivity appCompatActivity;
    private String copayStr = "";
    private String previousBalanceStr = "";
    private PaymentsMetadataDTO metadataDTO;
    PaymentsDTO paymentsDTO;
    private TextView responseTotal;
    private TextView responseCopay;
    private TextView responsePreviousBalance;


    public ResponsibilityFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCompatActivity = (AppCompatActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_responsibility, container, false);

        Bundle bundle = getArguments();
        ArrayList<PayloadPaymentModel> paymentList2 = (ArrayList<PayloadPaymentModel>) bundle.getSerializable(CarePayConstants.INTAKE_BUNDLE);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.respons_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);

        setGothamRoundedMediumTypeface(appCompatActivity, title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        setTypefaces(view);

        Button payTotalAmountButton = (Button) view.findViewById(R.id.pay_total_amount_button);
        payTotalAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPaymentInformation();
                FragmentManager fragmentmanager = getActivity().getSupportFragmentManager();


            }
        });

        Button makePartialPaymentButton = (Button) view.findViewById(R.id.make_partial_payment_button);
        makePartialPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        TextView responseTotal = (TextView) view.findViewById(R.id.respons_total);
        TextView responseCopay = (TextView) view.findViewById(R.id.respons_copay);
        TextView responsePreviousBalance = (TextView) view.findViewById(R.id.respons_prev_balance);

        if (bundle == null) {
            ArrayList<PayloadPaymentModel> paymentList = (ArrayList<PayloadPaymentModel>) bundle.getSerializable(CarePayConstants.INTAKE_BUNDLE);

            if (paymentList != null && paymentList.size() > 0) {
                for (PayloadPaymentModel payment : paymentList) {
                    if (payment.getBalanceType().equalsIgnoreCase(CarePayConstants.COPAY)) {
                        copayStr = payment.getTotal();
                    } else if (payment.getBalanceType().equalsIgnoreCase(CarePayConstants.ACCOUNT)) {
                        previousBalanceStr = payment.getTotal();
                    }
                }

                try {
                    double copay = Double.parseDouble(copayStr);
                    double previousBalance = Double.parseDouble(previousBalanceStr);
                    double total = copay + previousBalance;

                    NumberFormat formatter = new DecimalFormat(CarePayConstants.RESPONSIBILITY_FORMATTER);
                    responseTotal.setText(CarePayConstants.DOLLAR.concat(formatter.format(total)));
                    responseCopay.setText(CarePayConstants.DOLLAR.concat(copayStr));
                    responsePreviousBalance.setText(CarePayConstants.DOLLAR.concat(previousBalanceStr));

                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                    Log.e(LOG_TAG, ex.getMessage());
                }
            }
        }
        return view;
    }

    private void getPaymentInformation() {
        PaymentsService paymentService = (new BaseServiceGenerator(getActivity()))
                .createService(PaymentsService.class);
        Call<PaymentsDTO> call = paymentService.fetchPaymentInformation();
        call.enqueue(new Callback<PaymentsDTO>() {
            @Override
            public void onResponse(Call<PaymentsDTO> call, Response<PaymentsDTO> response) {
                PaymentsDTO paymentsDTO = response.body();
            }

            @Override
            public void onFailure(Call<PaymentsDTO> call, Throwable throwable) {
            }
        });
    }

    /**
     * Helper to set the typefaces
     *
     * @param view The parent view
     */
    private void setTypefaces(View view) {
        // set the typefaces
        setGothamRoundedBookTypeface(appCompatActivity, (TextView) view.findViewById(R.id.respons_total_label));
        setGothamRoundedMediumTypeface(appCompatActivity, (TextView) view.findViewById(R.id.respons_total));
        setProximaNovaRegularTypeface(appCompatActivity, (TextView) view.findViewById(R.id.respons_prev_balance_label));
        setProximaNovaRegularTypeface(appCompatActivity, (TextView) view.findViewById(R.id.respons_copay_label));
        setProximaNovaSemiboldTypeface(appCompatActivity, (TextView) view.findViewById(R.id.respons_prev_balance));
        setProximaNovaSemiboldTypeface(appCompatActivity, (TextView) view.findViewById(R.id.respons_copay));
        setGothamRoundedMediumTypeface(appCompatActivity, (Button) view.findViewById(R.id.pay_total_amount_button));
    }

    /**
     * For tests
     *
     * @param activity The activity
     */
    public void setActivity(KeyboardHolderActivity activity) {
        appCompatActivity = activity;
    }


}