package com.carecloud.carepay.practice.library.checkin;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.checkin.adapters.CheckedInAdapter;
import com.carecloud.carepay.practice.library.checkin.adapters.CheckedInAppointmentAdapter;
import com.carecloud.carepay.practice.library.customcomponent.AppointmentStatusCartView;
import com.carecloud.carepay.service.library.BaseServiceGenerator;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.services.AppointmentService;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckInActivity extends AppCompatActivity {

    private RecyclerView checkinginRecyclerView;

    private RecyclerView waitingRoomRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        checkinginRecyclerView = (RecyclerView) findViewById(R.id.checkinginRecyclerView);
        waitingRoomRecyclerView = (RecyclerView) findViewById(R.id.waitingRoomRecyclerView);
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
        AppointmentService apptService = (new BaseServiceGenerator(this)).createService(AppointmentService.class); //, String token, String searchString
        Call<AppointmentsResultModel> call = apptService.fetchCheckedInAppointments();
        call.enqueue(new Callback<AppointmentsResultModel>() {
            @Override
            public void onResponse(Call<AppointmentsResultModel> call, Response<AppointmentsResultModel> response) {

                if(response.code()==200 && response.body().getPayload()!=null && response.body().getPayload().getAppointments()!=null) {
                    CheckedInAppointmentAdapter checkedInAdapter = new CheckedInAppointmentAdapter(CheckInActivity.this, new ArrayList<>(response.body().getPayload().getAppointments()));
                    checkinginRecyclerView.setLayoutManager(new LinearLayoutManager(CheckInActivity.this));
                    checkinginRecyclerView.setAdapter(checkedInAdapter);

                    waitingRoomRecyclerView.setLayoutManager(new LinearLayoutManager(CheckInActivity.this));
                    waitingRoomRecyclerView.setAdapter(checkedInAdapter);
                }
            }

            @Override
            public void onFailure(Call<AppointmentsResultModel> call, Throwable throwable) {

            }
        });
    }
}