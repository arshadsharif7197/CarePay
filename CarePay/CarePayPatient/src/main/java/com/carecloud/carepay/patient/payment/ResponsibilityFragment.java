package com.carecloud.carepay.patient.payment;

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

import com.carecloud.carepay.patient.appointments.activities.AppointmentsActivity;
import com.carecloud.carepay.patient.appointments.services.AppointmentService;
import com.carecloud.carepay.patient.payment.dialogs.PartialPaymentDialog;
import com.carecloud.carepay.service.library.BaseServiceGenerator;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.intake.models.PayloadPaymentModel;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;
import com.carecloud.carepay.patient.payment.fragments.PaymentMethodFragment;
import com.carecloud.carepaylibray.payments.models.PaymentPatientBalancesPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMetadataModel;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.services.PaymentsService;
import com.google.gson.JsonObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedBookTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lsoco_user on 9/2/2016.
 * Responsibility screen
 */
public class ResponsibilityFragment extends Fragment {

    private static final String LOG_TAG = ResponsibilityFragment.class.getSimpleName();
    private AppCompatActivity appCompatActivity;
    private String copayStr = "";
    private String previousBalanceStr = "";

    private TextView responseTotal;
    private TextView responseCopay;
    private TextView responsePreviousBalance;
    private PaymentsModel paymentDTO = null;
    private TextView totalResponsibility;
    private TextView prevBalanceResponsibility;
    private TextView coPayResponsibility;
    private Button payTotalButton;
    private Button payPartialButton;
    private String totalResponsibilityString;
    private String titleResponsibilityString;
    private String previousBalanceString;
    private String insuranceCopayString;
    private String payTotalAmountString;
    private String payPartialAmountString;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appCompatActivity = (AppCompatActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_responsibility, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.respons_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);

        setGothamRoundedMediumTypeface(appCompatActivity, title);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        setTypefaces(view);

        TextView responseTotal = (TextView) view.findViewById(R.id.respons_total);
        TextView responseCopay = (TextView) view.findViewById(R.id.respons_copay);
        TextView responsePreviousBalance = (TextView) view.findViewById(R.id.respons_prev_balance);
        totalResponsibility  = (TextView) view.findViewById(R.id.respons_total_label);
        prevBalanceResponsibility = (TextView) view.findViewById(R.id.respons_prev_balance_label);
        coPayResponsibility= (TextView) view.findViewById(R.id.respons_copay_label);
        payTotalButton = (Button) view.findViewById(R.id.pay_total_amount_button);
        payPartialButton = (Button) view.findViewById(R.id.make_partial_payment_button);

        Bundle bundle = getArguments();
        if (bundle != null) {
            paymentDTO = (PaymentsModel) bundle.getSerializable(CarePayConstants.INTAKE_BUNDLE);
            List<PaymentPatientBalancesPayloadDTO> paymentList = paymentDTO.getPaymentPayload().getPatientBalances().get(1).getPayload();
            getPaymentLabels();
            toolbar.setTitle(titleResponsibilityString);
            if (paymentList != null && paymentList.size() > 1) {
                for (PaymentPatientBalancesPayloadDTO payment : paymentList) {
                    if (payment.getBalanceType().equalsIgnoreCase(CarePayConstants.PATIENT)) {
                        copayStr = payment.getTotal();
                    } else if (payment.getBalanceType().equalsIgnoreCase(CarePayConstants.COPAY)) {
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
                    totalResponsibility.setText(totalResponsibilityString);
                    prevBalanceResponsibility.setText(previousBalanceString);
                    coPayResponsibility.setText(insuranceCopayString);
                    payTotalButton.setText(payTotalAmountString);
                    payPartialButton.setText(payPartialAmountString);

                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                    Log.e(LOG_TAG, ex.getMessage());
                }
            }
        }
        Button payTotalAmountButton = (Button) view.findViewById(R.id.pay_total_amount_button);
        payTotalAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPaymentInformation();
                FragmentManager fragmentmanager = getActivity().getSupportFragmentManager();
                PaymentMethodFragment fragment = (PaymentMethodFragment) fragmentmanager.findFragmentByTag(PaymentMethodFragment.class.getSimpleName());
                if (fragment == null) {
                    fragment = new PaymentMethodFragment();
                }

                Bundle arguments = getArguments();
                Bundle bundle = new Bundle();
                bundle.putSerializable(CarePayConstants.PAYMENT_CREDIT_CARD_INFO,
                        arguments.getSerializable(CarePayConstants.PAYMENT_CREDIT_CARD_INFO));
                bundle.putSerializable(CarePayConstants.INTAKE_BUNDLE,
                        paymentDTO);
                fragment.setArguments(bundle);

                FragmentTransaction fragmentTransaction = fragmentmanager.beginTransaction();
                fragmentTransaction.replace(R.id.payment_frag_holder, fragment);
                fragmentTransaction.addToBackStack(PaymentMethodFragment.class.getSimpleName());
                fragmentTransaction.commit();
            }
        });

        Button makePartialPaymentButton = (Button) view.findViewById(R.id.make_partial_payment_button);
        makePartialPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PartialPaymentDialog(getActivity(), paymentDTO).show();

            }
        });


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
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
            }
        });
    }

    private void payAndFetchCheckedInAppointment() {


        String body = "{\"appointment_id\": \"" + AppointmentsActivity.model.getPayload().getId() + "\"}";
        AppointmentService aptService = (new BaseServiceGenerator(getActivity()).createService(AppointmentService.class));

        Call<JsonObject> call = aptService.confirmAppointment(body);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                /**
                 * Redirect to AppointmentsActivity on Success
                 */
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable throwable) {

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

    /**
     *  payment labels
     */
    public void getPaymentLabels() {
        if (paymentDTO != null) {
            PaymentsMetadataModel paymentsMetadataDTO = paymentDTO.getPaymentsMetadata();
            if (paymentsMetadataDTO != null) {
                PaymentsLabelDTO paymentsLabelsDTO = paymentsMetadataDTO.getPaymentsLabel();
                if (paymentsLabelsDTO != null) {
                    totalResponsibilityString = paymentsLabelsDTO.getPaymentTotalResponsibility();
                    titleResponsibilityString = paymentsLabelsDTO.getPaymentResponsibilityTitle();
                    previousBalanceString = paymentsLabelsDTO.getPaymentPreviousBalance();
                    insuranceCopayString = paymentsLabelsDTO.getPaymentInsuranceCopay();
                    payTotalAmountString = paymentsLabelsDTO.getPaymentPayTotalAmountButton();
                    payPartialAmountString = paymentsLabelsDTO.getPaymentPartialAmountButton();
                }
            }
        }
    }
}