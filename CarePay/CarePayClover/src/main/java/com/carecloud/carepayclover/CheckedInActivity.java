package com.carecloud.carepayclover;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.carecloud.carepay.service.library.BaseServiceGenerator;
import com.carecloud.carepayclover.adapters.CheckedInAdapter;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.services.AppointmentService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckedInActivity extends AppCompatActivity {

    private RecyclerView appointmentsRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checked_in);
        appointmentsRecyclerView = (RecyclerView) findViewById(com.carecloud.carepaylibrary.R.id.appointments_recycler_view);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        findViewById(R.id.goBackTextview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDemographicInformation();
    }

    private void getDemographicInformation() {
        AppointmentService apptService = (new BaseServiceGenerator(this)).createServicePractice(AppointmentService.class); //, String token, String searchString
        Call<AppointmentsResultModel> call = apptService.fetchCheckedInAppointments();
        call.enqueue(new Callback<AppointmentsResultModel>() {
            @Override
            public void onResponse(Call<AppointmentsResultModel> call, Response<AppointmentsResultModel> response) {

                if(response.code()==200 && response.body().getPayload()!=null && response.body().getPayload().getAppointments()!=null) {
                    CheckedInAdapter checkedInAdapter = new CheckedInAdapter(CheckedInActivity.this, new ArrayList<>(response.body().getPayload().getAppointments()));
                    appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(CheckedInActivity.this));
                    appointmentsRecyclerView.setAdapter(checkedInAdapter);
                }
            }

            @Override
            public void onFailure(Call<AppointmentsResultModel> call, Throwable throwable) {

            }
        });
    }
}
