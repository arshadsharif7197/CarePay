package com.carecloud.carepay.patient.payment;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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

import com.carecloud.carepaylibray.adapters.PaymentLineItemsListAdapter;
import com.carecloud.carepay.patient.payment.dialogs.PartialPaymentDialog;
import com.carecloud.carepaylibray.customdialogs.PaymentDetailsDialog;
import com.carecloud.carepay.patient.payment.fragments.PaymentMethodFragment;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.payments.fragments.ResponsibilityBaseFragment;
import com.carecloud.carepaylibray.payments.models.PatiencePayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;

public class ResponsibilityFragment extends ResponsibilityBaseFragment implements PaymentDetailsDialog.PayNowClickListener {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        TextView totalResponsibility = (TextView) view.findViewById(R.id.respons_total_label);

        Button payTotalAmountButton = (Button) view.findViewById(R.id.pay_total_amount_button);
        payTotalAmountButton.setClickable(false);
        payTotalAmountButton.setEnabled(false);
        setGothamRoundedMediumTypeface(appCompatActivity, payTotalAmountButton);
        payTotalAmountButton.setBackgroundColor(getResources().getColor(R.color.light_gray));

        Button makePartialPaymentButton = (Button) view.findViewById(R.id.make_partial_payment_button);
        makePartialPaymentButton.setClickable(false);
        makePartialPaymentButton.setEnabled(false);
        setGothamRoundedMediumTypeface(appCompatActivity, makePartialPaymentButton);
        makePartialPaymentButton.setBackgroundColor(getResources().getColor(R.color.light_gray));

        Button payLaterButton = (Button) view.findViewById(R.id.pay_later_button);
        payLaterButton.setClickable(false);
        payLaterButton.setEnabled(false);
        setGothamRoundedMediumTypeface(appCompatActivity, payLaterButton);
        payLaterButton.setBackgroundColor(getResources().getColor(R.color.light_gray));

        try{
        getPaymentInformation();
        if (paymentDTO != null) {
            getPaymentLabels();
            List<PatiencePayloadDTO> paymentList =  paymentDTO.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getPayload();

            if (paymentList != null && paymentList.size() > 0) {
                fillDetailAdapter(view, paymentList);
                for (PatiencePayloadDTO payment : paymentList) {
                    total+= payment.getAmount();
                }
                try {

                    if (total > 0) {
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

                        payLaterButton.setTextColor(getResources().getColor(R.color.bright_cerulean));
                        payLaterButton.setBackgroundColor(Color.WHITE);
                        payLaterButton.setBackground(border);
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
         }
        }catch(Exception e){
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

    protected void doPayment() {
       try{
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
       }catch(Exception e){
            e.printStackTrace();
       }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}