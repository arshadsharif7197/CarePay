package com.carecloud.carepay.practice.library.appointments;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.appointments.adapters.AppointmentsListAdapter;
import com.carecloud.carepay.practice.library.appointments.services.AppointmentService;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.service.library.BaseServiceGenerator;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLabelDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;

import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentsActivity extends BasePracticeActivity implements View.OnClickListener {
    private RecyclerView appointmentsRecyclerView;
    private RecyclerView.LayoutManager appointmentsLayoutManager;
    private AppointmentsListAdapter appointmentsListAdapter;
    private AppointmentsResultModel appointmentsResultModel;
    private ProgressBar appointmentProgressBar;
    private static final String TAG = "AppointmentsActivity";

    private List<com.carecloud.carepaylibray.appointments.models.AppointmentDTO> appointmentsItems;
    private TextView logOutTextview;
    private CarePayTextView appointmentForTextview;
    private CarePayTextView  selectAppointmentTextview;
    private CarePayTextView  noAppointmentsLabel;
    private CarePayTextView  noAppointmentsDescription;
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
        appointmentForTextview = (CarePayTextView ) findViewById(R.id.titleSelectappointmentsubheader);
        appointmentForTextview.setTextColor(Color.WHITE);
        logOutTextview = (TextView) findViewById(R.id.logoutTextview);
        selectAppointmentTextview = (CarePayTextView ) findViewById(R.id.titleSelectappointmentcheckin);
        appointmentProgressBar = (ProgressBar) findViewById(R.id.appointmentProgressBar);
        appointmentProgressBar.setVisibility(View.GONE);
        noAppointmentView = (LinearLayout) findViewById(R.id.no_appointment_layout);
        noAppointmentsLabel = (CarePayTextView ) findViewById(R.id.no_apt_message_title);
        noAppointmentsDescription = (CarePayTextView ) findViewById(R.id.no_apt_message_desc);
        findViewById(R.id.logoutTextview).setOnClickListener(this);
        findViewById(R.id.btnHome).setOnClickListener(this);
        try{
            appointmentsResultModel = getConvertedDTO(AppointmentsResultModel.class);
            getAppointments();
        }catch(JsonSyntaxException jsonSyntaxException){
            Log.e(TAG, "jsonSyntaxException: " + jsonSyntaxException.getMessage());
        }
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

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.logoutTextview) {
            Map<String, String> headers = new HashMap<>();
            headers.put("x-api-key", "*");
            headers.put("transition", "true");
            //headers.put("Authorization", CognitoAppHelper.getCurrSession().getIdToken().getJWTToken());
            WorkflowServiceHelper.getInstance().execute(appointmentsResultModel.getMetadata().getTransitions().getLogout(), logOutCall, headers);
        } else if (viewId == R.id.btnHome) {
            //WorkflowServiceHelper.getInstance().execute(appointmentsResultModel.getMetadata().getTransitions().getAuthenticate(), homeCall);
        }
    }

    WorkflowServiceCallback logOutCall = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            AppointmentsActivity.this.finish();
            PracticeNavigationHelper.getInstance().navigateToWorkflow(AppointmentsActivity.this, workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showDialogMessage(AppointmentsActivity.this,getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

    WorkflowServiceCallback homeCall = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            AppointmentsActivity.this.finish();
            PracticeNavigationHelper.getInstance().navigateToWorkflow(AppointmentsActivity.this, workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showDialogMessage(AppointmentsActivity.this,getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private void populateWithLabels() {
        AppointmentLabelDTO labels = appointmentsResultModel.getMetadata().getLabel();
        logOutTextview.setText(labels == null ? CarePayConstants.NOT_DEFINED : StringUtil.getLabelForView(labels.getAppointmentsBtnLogout()));
        appointmentForTextview.setText(labels == null ? CarePayConstants.NOT_DEFINED : StringUtil.getLabelForView(labels.getAppointmentsSubHeading()));
        selectAppointmentTextview.setText(labels == null ? CarePayConstants.NOT_DEFINED : StringUtil.getLabelForView(labels.getAppointmentsMainHeading()));
        noAppointmentsLabel.setText(labels == null ? CarePayConstants.NOT_DEFINED : StringUtil.getLabelForView(labels.getNoAppointmentsMessageTitle()));
        noAppointmentsDescription.setText(labels == null ? CarePayConstants.NOT_DEFINED : StringUtil.getLabelForView(labels.getNoAppointmentsMessageText()));
    }

    private void getAppointments(){
        if (appointmentsResultModel != null && appointmentsResultModel.getPayload() != null
                && appointmentsResultModel.getPayload().getAppointments() != null
                && appointmentsResultModel.getPayload().getAppointments().size() > 0) {

            noAppointmentView.setVisibility(View.GONE);
            appointmentsItems = appointmentsResultModel.getPayload().getAppointments();

            appointmentListWithToday = new ArrayList<>();
            populateWithLabels();
            for (AppointmentDTO appointmentDTO : appointmentsItems) {
                String title = getToday(appointmentDTO.getPayload().getStartTime());
                if (title.equalsIgnoreCase(CarePayConstants.DAY_TODAY)) {
                    appointmentListWithToday.add(appointmentDTO);
                }
                else{
                    appointmentForTextview.setVisibility(View.INVISIBLE);
                    selectAppointmentTextview.setVisibility(View.INVISIBLE);
                    noAppointmentView.setVisibility(View.VISIBLE);
                }

            }
            if (appointmentListWithToday != null) {
                appointmentsListAdapter = new AppointmentsListAdapter(AppointmentsActivity.this, appointmentListWithToday, appointmentsResultModel);
                appointmentsRecyclerView.setAdapter(appointmentsListAdapter);
            }

            //Layout manager for the Recycler View
            appointmentsLayoutManager = new LinearLayoutManager(AppointmentsActivity.this, LinearLayoutManager.HORIZONTAL, false);
            appointmentsRecyclerView.setLayoutManager(appointmentsLayoutManager);
        }
    }

}
