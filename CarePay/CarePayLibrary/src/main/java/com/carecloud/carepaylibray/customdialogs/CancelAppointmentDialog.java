package com.carecloud.carepaylibray.customdialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLabelDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.QueryStrings;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by prem_mourya on 10/12/2016.
 */

public class CancelAppointmentDialog extends BaseDoctorInfoDialog {

    private LinearLayout rootLayout;
    private LinearLayout mainLayout;
    private Context context;
    private AppointmentDTO appointmentDTO;
    private AppointmentLabelDTO appointmentLabels;
    private AppointmentsResultModel appointmentInfo;

    private boolean isCanceled = false;
    private boolean isMissed = false;

    /**
     * Contractor for   dialog.
     * @param context the String to evaluate
     * @param appointmentDTO the DTO to evaluate
     */
    public CancelAppointmentDialog(Context context, AppointmentDTO appointmentDTO) {

        super(context, appointmentDTO);
        this.isMissed = true;
        this.context = context;
        this.appointmentDTO = appointmentDTO;
    }

    /**
     * Contractor for dialog.
     * @param context context
     * @param appointmentDTO appointment item
     * @param isCanceled isCanceled
     * @param appointmentInfo Appointment info data
     */
    public CancelAppointmentDialog(Context context, AppointmentDTO appointmentDTO,
                                   boolean isCanceled, AppointmentsResultModel appointmentInfo) {

        super(context, appointmentDTO);
        this.isMissed = false;
        this.context = context;
        this.isCanceled = isCanceled;
        this.appointmentDTO = appointmentDTO;
        this.appointmentInfo = appointmentInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootLayout = (LinearLayout) getRootView();
        mainLayout = (LinearLayout) getAddActionChildView();
        appointmentLabels = appointmentInfo.getMetadata().getLabel();

        if (isCanceled || isMissed) {
            setActionButtonCanceled();
        } else {
            setActionButton();
        }
    }

    private void setActionButton() {
        CarePayTextView editAppointmentTextView = (CarePayTextView)
                rootLayout.findViewById(R.id.dialogCancelAppointTextView);
        editAppointmentTextView.setVisibility(View.VISIBLE);
        editAppointmentTextView.setText(appointmentLabels.getAppointmentsCancelHeading());
        editAppointmentTextView.setTextColor(context.getResources().getColor(R.color.glitter));
        editAppointmentTextView.setOnClickListener(this);
    }

    private void setActionButtonCanceled() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.dialog_canceled_appointment, null);

        CarePayTextView appointmentStatusLabel = (CarePayTextView)
                childActionView.findViewById(R.id.appointmentStatusLabel);

        if (isMissed) {
            appointmentStatusLabel.setText(appointmentLabels.getAppointmentsMissedHeading());
            appointmentStatusLabel.setTextColor(ContextCompat.getColor(context, R.color.lightningyellow));
        } else {
            appointmentStatusLabel.setText(appointmentLabels.getAppointmentsCanceledHeading());
            appointmentStatusLabel.setTextColor(ContextCompat.getColor(context, R.color.harvard_crimson));
        }

        findViewById(R.id.dialogHeaderlayout).setBackgroundResource(R.color.Feldgrau);
        ((CarePayTextView) findViewById(R.id.appointDateTextView)).setTextColor(ContextCompat.getColor(context, R.color.white));
        ((CarePayTextView) findViewById(R.id.appointTimeTextView)).setTextColor(ContextCompat.getColor(context, R.color.white));
        mainLayout.addView(childActionView);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int viewId = view.getId();
        if (viewId == R.id.dialogCancelAppointTextView) {
            onCancelAppointment();
            cancel();
        }
    }

    /**
     * call cancel appointment api.
     */
    private void onCancelAppointment() {
        Gson gson = new Gson();
        Map<String, String> queries = new HashMap<>();
        JsonObject queryStringObject = appointmentInfo.getMetadata().getTransitions().getCheckinAtOffice().getQueryString();
        QueryStrings queryStrings = gson.fromJson(queryStringObject, QueryStrings.class);

        queries.put(queryStrings.getPracticeMgmt().getName(), appointmentDTO.getMetadata().getPracticeMgmt());
        queries.put(queryStrings.getPracticeId().getName(), appointmentDTO.getMetadata().getPracticeId());
        queries.put(queryStrings.getPatientId().getName(), appointmentDTO.getMetadata().getPatientId());
        queries.put(queryStrings.getAppointmentId().getName(), appointmentDTO.getMetadata().getAppointmentId());

        Map<String, String> header = new HashMap<>();
        header.put("transition", "true");

        TransitionDTO transitionDTO = appointmentInfo.getMetadata().getTransitions().getCancel();
        WorkflowServiceHelper.getInstance().execute(transitionDTO, transitionToCancelCallback, queries, header);
    }

    private WorkflowServiceCallback transitionToCancelCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            new CancelReasonAppointmentDialog(context, appointmentDTO, appointmentInfo).show();
        }

        @Override
        public void onFailure(String exceptionMessage) {
        }
    };

}