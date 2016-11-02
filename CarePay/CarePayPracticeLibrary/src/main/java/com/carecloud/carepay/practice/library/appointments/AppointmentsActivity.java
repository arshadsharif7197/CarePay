package com.carecloud.carepay.practice.library.appointments;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
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
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.customcomponents.CustomGothamRoundedMediumLabel;
import com.carecloud.carepaylibray.customcomponents.CustomProxyNovaRegularLabel;
import com.carecloud.carepaylibray.utils.DateUtil;

import java.util.ArrayList;
import java.util.Date;
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
    private CustomGothamRoundedMediumLabel noAppointmentsLabel;
    private Bundle bundle;
    private LinearLayout noAppointmentView;
    private List<com.carecloud.carepaylibray.appointments.models.AppointmentDTO> appointmentListWithToday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments_practice);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        appointmentsRecyclerView = (RecyclerView) findViewById(R.id.appointments_recycler_view);
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
        noAppointmentsLabel = (CustomGothamRoundedMediumLabel) findViewById(R.id.no_apt_message_title);

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

                                 appointmentListWithToday = new ArrayList<>();

                                 for (AppointmentDTO appointmentDTO : appointmentsItems) {
                                     String title = getToday(appointmentDTO.getPayload().getStartTime());
                                     if (title.equalsIgnoreCase(CarePayConstants.DAY_TODAY)) {
                                         appointmentListWithToday.add(appointmentDTO);
                                     }

                                 }
                                 if (appointmentListWithToday != null) {
                                     appointmentsListAdapter = new AppointmentsListAdapter(AppointmentsActivity.this, appointmentListWithToday);
                                     appointmentsRecyclerView.setAdapter(appointmentsListAdapter);
                                 }

                                 //Layout manager for the Recycler View
                                 appointmentsLayoutManager = new LinearLayoutManager(AppointmentsActivity.this, LinearLayoutManager.HORIZONTAL, false);
                                 appointmentsRecyclerView.setLayoutManager(appointmentsLayoutManager);
                             }
                         }

                         @Override
                         public void onFailure(Call<AppointmentsResultModel> call, Throwable throwable) {
                             appointmentProgressBar.setVisibility(View.GONE);
                         }
                     }

        );
    }

    private String getToday(String appointmentRawDate) {
        // Current date
        DateUtil.getInstance().setFormat(CarePayConstants.APPOINTMENT_DATE_TIME_FORMAT);
        String currentDate = DateUtil.getInstance().setToCurrent().getDateAsMMddyyyy();
        Date currentConvertedDate = DateUtil.getInstance().setDateRaw(currentDate).getDate();

        // Appointment date
        String appointmentDate = DateUtil.getInstance().setDateRaw(appointmentRawDate).getDateAsMMddyyyy();
        Date convertedAppointmentDate = DateUtil.getInstance().setDateRaw(appointmentDate).getDate();

        String strDay;
        if (convertedAppointmentDate.after(currentConvertedDate)
                && !appointmentDate.equalsIgnoreCase(currentDate)) {
            strDay = CarePayConstants.DAY_UPCOMING;

        } else if (convertedAppointmentDate.before(currentConvertedDate)) {
            strDay = CarePayConstants.DAY_TODAY;
        } else {
            strDay = CarePayConstants.DAY_TODAY;
        }

        return strDay;
    }
}
