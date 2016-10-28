package com.carecloud.carepay.practice.library.checkin;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.NavigationHelper;
import com.carecloud.carepay.practice.library.checkin.adapters.CheckedInAppointmentAdapter;
import com.carecloud.carepay.service.library.BaseServiceGenerator;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.services.AppointmentService;

import java.util.ArrayList;

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
        checkinginRecyclerView.setHasFixedSize(true);
        checkinginRecyclerView.setItemAnimator(new DefaultItemAnimator());
        waitingRoomRecyclerView = (RecyclerView) findViewById(R.id.waitingRoomRecyclerView);
        waitingRoomRecyclerView.setHasFixedSize(true);
        waitingRoomRecyclerView.setItemAnimator(new DefaultItemAnimator());
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
        String url="https://g8r79tifa4.execute-api.us-east-1.amazonaws.com/dev/workflow/carepay/practice_mode/practice_checkin?practice_mgmt=carecloud&practice_id=77b81aa8-1155-4da7-9fd9-2f6967b09a93";
        WorkflowServiceHelper.getInstance().executeGetRequest(url,signinCallback);
        //getDemographicInformation();
    }

    WorkflowServiceCallback signinCallback=new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            NavigationHelper.getInstance().navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {

        }
    };

    /*private void getDemographicInformation() {
        AppointmentService apptService = (new BaseServiceGenerator(this)).createService(AppointmentService.class); //, String token, String searchString
        Call<AppointmentsResultModel> call = apptService.getCheckedInAppointments();
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
    }*/
}