package com.carecloud.carepayclover;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.services.AppointmentService;
import com.carecloud.carepaylibray.base.BaseServiceGenerator;
import com.clover.sdk.util.CustomerMode;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CloverMainActivity extends AppCompatActivity {
public static int count;
TextView checkedInCounterTextview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CustomerMode.enable(this);
        setSystemUiVisibility();
        setContentView(R.layout.activity_main_clover);
        checkedInCounterTextview= (TextView) findViewById(R.id.checkedInCounterTextview);
        getDemographicInformation();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ((TextView) findViewById(R.id.checkinTextView)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent checkedInIntent = new Intent(CloverMainActivity.this, CheckedInActivity.class);
                checkedInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(checkedInIntent);
            }
        });
        registerReceiver(newCheckedInReceiver, new IntentFilter("NEW_CHECKEDIN_NOTIFICATION"));
    }

    public void setSystemUiVisibility() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    BroadcastReceiver newCheckedInReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            count=Integer.parseInt(checkedInCounterTextview.getText().toString())+1;
            checkedInCounterTextview.setText(String.valueOf( count));
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(newCheckedInReceiver);
    }

    private void getDemographicInformation() {
        AppointmentService apptService = (new BaseServiceGenerator(this)).createServicePractice(AppointmentService.class); //, String token, String searchString
        Call<AppointmentsResultModel> call = apptService.fetchCheckedInAppointments();
        call.enqueue(new Callback<AppointmentsResultModel>() {
            @Override
            public void onResponse(Call<AppointmentsResultModel> call, Response<AppointmentsResultModel> response) {

                if(response.code()==200 && response.body().getPayload()!=null && response.body().getPayload().getAppointments()!=null) {
                    checkedInCounterTextview.setText(String.valueOf( response.body().getPayload().getAppointments().size()));
                }
            }

            @Override
            public void onFailure(Call<AppointmentsResultModel> call, Throwable t) {

            }
        });
    }

}
