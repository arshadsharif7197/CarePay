package com.carecloud.carepayclover;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.carecloud.carepayclover.adapters.CheckedInAdapter;
import com.carecloud.carepayclover.models.AppointmentsCheckedInModel;
import com.carecloud.carepayclover.models.AppointmentsCheckedInPayloadAppointmentModel;
import com.carecloud.carepaylibray.appointments.adapters.AppointmentsAdapter;
import com.carecloud.carepaylibray.base.BaseServiceGenerator;
import com.carecloud.carepaylibray.demographics.models.DemographicModel;
import com.carecloud.carepaylibray.demographics.services.DemographicService;

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
        TextView titleHeaderTextview= (TextView) findViewById(R.id.titleHeaderTextview);
        String sourceString = "care<b>pay</b>";
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(sourceString,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(sourceString);
        }
        titleHeaderTextview.setText(result);
        getDemographicInformation();
    }

    private void getDemographicInformation() {
        CheckedInService apptService = (new BaseServiceGenerator(this)).createServicePractice(CheckedInService.class); //, String token, String searchString
        Call<AppointmentsCheckedInModel> call = apptService.fetchCheckedInAppointments();
        call.enqueue(new Callback<AppointmentsCheckedInModel>() {
            @Override
            public void onResponse(Call<AppointmentsCheckedInModel> call, Response<AppointmentsCheckedInModel> response) {
                CheckedInAdapter CheckedInAdapter = new CheckedInAdapter(CheckedInActivity.this,new ArrayList<AppointmentsCheckedInPayloadAppointmentModel>( response.body().getPayload().getAppointments()));
                appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(CheckedInActivity.this));
                appointmentsRecyclerView.setAdapter(CheckedInAdapter);
            }

            @Override
            public void onFailure(Call<AppointmentsCheckedInModel> call, Throwable t) {

            }
        });
    }
}
