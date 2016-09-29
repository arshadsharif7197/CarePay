package com.carecloud.carepaylibray.payment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.activities.AppointmentsActivity;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;
import com.carecloud.carepaylibray.appointments.services.AppointmentService;
import com.carecloud.carepaylibray.base.BaseServiceGenerator;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;

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

        return view;
    }

    private void payAndFetchCheckedInAppointment() {
        AppointmentModel appointmentModel = AppointmentsActivity.model;
        ArrayList<AppointmentModel> appointmentsItems = new ArrayList<>();
        appointmentModel.getAppointmentId();
        appointmentModel.getDoctorName();
        appointmentModel.getAppointmentType();
        appointmentsItems.add(appointmentModel);

        AppointmentService aptService = (new BaseServiceGenerator(getActivity()).createService(AppointmentService.class));
        Call<AppointmentsResultModel> call = aptService.confirmAppointment(appointmentsItems);
        call.enqueue(new Callback<AppointmentsResultModel>() {
            @Override
            public void onResponse(Call<AppointmentsResultModel> call, Response<AppointmentsResultModel> response) {
                appointmentsModel = new AppointmentsResultModel();
                appointmentsModel  = response.body();

                Log.d(LOG_TAG, response.isSuccessful()+"");
                Intent intent = new Intent(ResponsibilityFragment.this.getActivity(), AppointmentsActivity.class);
                AppointmentModel appointmentModel = AppointmentsActivity.model;
                if(appointmentModel!=null) {
                    appointmentModel.setCheckedIn(true);
                }

                // appointment clicked item is cleared once payment is done.
                AppointmentsActivity.model = null;
                intent.putExtra(CarePayConstants.CHECKED_IN_APPOINTMENT_BUNDLE,appointmentModel);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<AppointmentsResultModel> call, Throwable t) {

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