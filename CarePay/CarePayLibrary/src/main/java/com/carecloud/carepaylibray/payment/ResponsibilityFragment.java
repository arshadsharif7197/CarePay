package com.carecloud.carepaylibray.payment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.activities.AppointmentsActivity;
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.intake.models.PayloadPaymentModel;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedBookTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

/**
 * Created by lsoco_user on 9/2/2016.
 * Responsibility screen
 */
public class ResponsibilityFragment extends Fragment {

    private static final String LOG_TAG = ResponsibilityFragment.class.getSimpleName();
    AppCompatActivity mActivity;
    private String copayStr = "", previousBalanceStr = "";
    private TextView responseTotal, responseCopay, responsePreviousBalance;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (AppCompatActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_responsibility, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.respons_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);

        setGothamRoundedMediumTypeface(mActivity, title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        setTypefaces(view);

        Button payAtPracticeButton = (Button) view.findViewById(R.id.respons_pay);

        payAtPracticeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payAndFetchCheckedInAppointment();
            }
        });

        Button payNowButton = (Button) view.findViewById(R.id.respons_pay_now);
        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payAndFetchCheckedInAppointment();
            }
        });

        responseTotal = (TextView) view.findViewById(R.id.respons_total);
        responseCopay = (TextView) view.findViewById(R.id.respons_prev_balance);
        responsePreviousBalance = (TextView) view.findViewById(R.id.respons_copay);

        Bundle bundle = getArguments();
        if(bundle != null) {
            ArrayList<PayloadPaymentModel> paymentList = (ArrayList<PayloadPaymentModel>) bundle.getSerializable(CarePayConstants.INTAKE_BUNDLE);

            if(paymentList != null && paymentList.size() > 0) {
                for(PayloadPaymentModel payment : paymentList) {
                    if(payment.getBalanceType().equalsIgnoreCase(CarePayConstants.COPAY)) {
                        copayStr = payment.getTotal();
                    } else if(payment.getBalanceType().equalsIgnoreCase(CarePayConstants.ACCOUNT)) {
                        previousBalanceStr = payment.getTotal();
                    }
                }

                try {
                    double copay = Double.parseDouble(copayStr);
                    double previousBalance = Double.parseDouble(previousBalanceStr);
                    double total = copay + previousBalance;

                    NumberFormat formatter = new DecimalFormat(CarePayConstants.RESPONSIBILITY_FORMATTER);

                    responseTotal.setText(CarePayConstants.DOLLAR + formatter.format(total));
                    responseCopay.setText(CarePayConstants.DOLLAR + copayStr);
                    responsePreviousBalance.setText(CarePayConstants.DOLLAR + previousBalanceStr);

                } catch(NumberFormatException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return view;
    }

    private void payAndFetchCheckedInAppointment() {
        Intent intent = new Intent(ResponsibilityFragment.this.getActivity(), AppointmentsActivity.class);
        AppointmentModel appointmentModel = AppointmentsActivity.model;
        if(appointmentModel!=null) {
            appointmentModel.setCheckedIn(true);
        }

        AppointmentsActivity.model = null; // appointment clicked item is cleared once payment is done.
        intent.putExtra(CarePayConstants.CHECKED_IN_APPOINTMENT_BUNDLE,appointmentModel);
        startActivity(intent);
    }

    /**
     * Helper to set the typefaces
     * @param view The parent view
     */
    private void setTypefaces(View view) {
        // set the typefaces
        setGothamRoundedBookTypeface(mActivity, (TextView) view.findViewById(R.id.respons_total_label));
        setGothamRoundedMediumTypeface(mActivity, (TextView) view.findViewById(R.id.respons_total));
        setProximaNovaRegularTypeface(mActivity, (TextView) view.findViewById(R.id.respons_prev_balance_label));
        setProximaNovaRegularTypeface(mActivity, (TextView) view.findViewById(R.id.respons_copay_label));
        setProximaNovaSemiboldTypeface(mActivity, (TextView) view.findViewById(R.id.respons_prev_balance));
        setProximaNovaSemiboldTypeface(mActivity, (TextView) view.findViewById(R.id.respons_copay));
        setGothamRoundedMediumTypeface(mActivity, (Button) view.findViewById(R.id.respons_pay));
    }

    /**
     * For tests
     *
     * @param activity The activity
     */
    public void setActivity(KeyboardHolderActivity activity) {
        mActivity = activity;
    }


}