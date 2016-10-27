package com.carecloud.carepay.practice.library.appointments;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.adapters.AppointmentsListAdapter;

import com.carecloud.carepay.practice.library.appointments.services.AppointmentService;
import com.carecloud.carepay.service.library.BaseServiceGenerator;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLocationDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumLabel;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaRegularLabel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentsActivity extends AppCompatActivity {
    private RecyclerView appointmentsRecyclerView;
    private RecyclerView.LayoutManager appointmentsLayoutManager;
    private AppointmentsListAdapter appointmentsListAdapter;
    private AppointmentsResultModel appointmentsResultModel;
    private ProgressBar appointmentProgressBar;

    private List<com.carecloud.carepaylibray.appointments.models.AppointmentDTO> appointmentsItems;
    private TextView logOutTextview;
    private CustomProxyNovaRegularLabel appointmentForTextview;
    private CustomGothamRoundedMediumLabel selectAppointmentTextview;
    private Bundle bundle;
    private LinearLayout noAppointmentView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        appointmentsRecyclerView = (RecyclerView) findViewById(R.id.appointment_recycler_view);
        appointmentsRecyclerView.setHasFixedSize(true);
        appointmentsItems = new ArrayList<AppointmentDTO>();
        appointmentForTextview = (CustomProxyNovaRegularLabel) findViewById(R.id.titleSelectappointmentsubheader);
        appointmentForTextview.setTextColor(Color.WHITE);
        appointmentForTextview.setText(R.string.not_defined);
        logOutTextview = (TextView) findViewById(R.id.logoutTextview);
        logOutTextview.setText(R.string.not_defined);
        selectAppointmentTextview = (CustomGothamRoundedMediumLabel) findViewById(R.id.titleSelectappointmentcheckin);
        selectAppointmentTextview.setText(R.string.not_defined);
        appointmentProgressBar = (ProgressBar) findViewById(R.id.appointmentProgressBar);
        appointmentProgressBar.setVisibility(View.GONE);
        noAppointmentView = (LinearLayout) findViewById(R.id.no_appointment_layout);

        getAppointmentInformation();

    }

    private void getAppointmentInformation() {
        appointmentProgressBar.setVisibility(View.VISIBLE);
        AppointmentService aptService = (new BaseServiceGenerator(this)).createService(AppointmentService.class);
        Call<com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel> call = aptService.fetchAppointmentInformation();
        call.enqueue(new Callback<AppointmentsResultModel>() {

                         @Override
                         public void onResponse(Call<AppointmentsResultModel> call, Response<AppointmentsResultModel> response) {
                             appointmentProgressBar.setVisibility(View.GONE);
                             appointmentsResultModel = response.body();
                             if (appointmentsResultModel != null && appointmentsResultModel.getPayload() != null
                                     && appointmentsResultModel.getPayload().getAppointments() != null
                                     && appointmentsResultModel.getPayload().getAppointments().size() > 0) {

                                 noAppointmentView.setVisibility(View.GONE);
                                 appointmentsItems = appointmentsResultModel.getPayload().getAppointments();

                                 if (appointmentsItems != null) {
                                     appointmentsListAdapter = new AppointmentsListAdapter(AppointmentsActivity.this, appointmentsItems);
                                     appointmentsRecyclerView.setAdapter(appointmentsListAdapter);
                                 }
                                 //Layout manager for the Recycler View
                                 appointmentsLayoutManager = new LinearLayoutManager(AppointmentsActivity.this, LinearLayoutManager.HORIZONTAL, false);
                                 appointmentsRecyclerView.setLayoutManager(appointmentsLayoutManager);
                             }else {
                                 noAppointmentView.setVisibility(View.VISIBLE);                             }
                         }

                         @Override
                         public void onFailure(Call<AppointmentsResultModel> call, Throwable throwable) {
                             appointmentProgressBar.setVisibility(View.GONE);
                         }
                     }

        );
    }

}
