package com.carecloud.carepaylibray.payment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.services.AppointmentService;
import com.carecloud.carepaylibray.base.BaseServiceGenerator;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.intake.models.PayloadPaymentModel;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;
import com.carecloud.carepaylibray.payment.fragments.PaymentMethodFragment;
import com.google.gson.JsonObject;

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

/**
 * Created by lsoco_user on 9/2/2016.
 * Responsibility screen
 */
public class ResponsibilityFragment extends Fragment {

    private static final String LOG_TAG = ResponsibilityFragment.class.getSimpleName();
    private AppCompatActivity mActivity;
    private String copayStr = "", previousBalanceStr = "";
    private TextView responseTotal, responseCopay, responsePreviousBalance;
    private AppointmentsResultModel appointmentsModel = null;

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

        Button payTotalAmountButton = (Button) view.findViewById(R.id.pay_total_amount_button);
        payTotalAmountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentmanager = getActivity().getSupportFragmentManager();
                PaymentMethodFragment fragment = (PaymentMethodFragment) fragmentmanager.findFragmentByTag(PaymentMethodFragment.class.getSimpleName());
                if (fragment == null) {
                    fragment = new PaymentMethodFragment();
                }

                //Bundle bundle = new Bundle();
                //bundle.putSerializable(CarePayConstants.INTAKE_BUNDLE, intent.getSerializableExtra(CarePayConstants.INTAKE_BUNDLE));
                //fragment.setArguments(bundle);
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

            }
        });

        responseTotal = (TextView) view.findViewById(R.id.respons_total);
        responseCopay = (TextView) view.findViewById(R.id.respons_copay);
        responsePreviousBalance = (TextView) view.findViewById(R.id.respons_prev_balance);

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
        /*AppointmentModel appointmentModel = AppointmentsActivity.model;
        ArrayList<AppointmentModel> appointmentsItems = new ArrayList<>();
        appointmentModel.getAppointmentId();
        appointmentModel.getDoctorName();
        appointmentModel.getAppointmentType();
        appointmentsItems.add(appointmentModel);*/
//TODO need to chages the actual model
        String body= "{\"appointment_id\": \""+ AppointmentsActivity.model.getAppointmentId() +"\"}";

        AppointmentService aptService = (new BaseServiceGenerator(getActivity()).createService(AppointmentService.class));
        Call<JsonObject> call = aptService.confirmAppointment(body);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
               /* appointmentsModel = new AppointmentsResultModel();
                appointmentsModel  = response.body();*/

                Intent intent = new Intent(ResponsibilityFragment.this.getActivity(), AppointmentsActivity.class);
                AppointmentModel appointmentModel = AppointmentsActivity.model;
                if(appointmentModel!=null) {
                    appointmentModel.setCheckedIn(true);
                    appointmentModel.setPending(false);
                }

                // appointment clicked item is cleared once payment is done.
                AppointmentsActivity.model = null;
                intent.putExtra(CarePayConstants.CHECKED_IN_APPOINTMENT_BUNDLE,appointmentModel);
                startActivity(intent);
                getActivity().finish();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
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
        setGothamRoundedMediumTypeface(mActivity, (Button) view.findViewById(R.id.pay_total_amount_button));
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