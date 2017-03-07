package com.carecloud.carepay.patient.appointments.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentLabelDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.QueryStrings;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.customdialogs.BaseDoctorInfoDialog;
import com.carecloud.carepaylibray.customdialogs.QrCodeViewDialog;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class CheckInOfficeNowAppointmentDialog extends BaseDoctorInfoDialog {

    private final boolean canCheckIn;
    private final CheckInOfficeNowAppointmentDialogListener callback;
    private Context context;
    private LinearLayout mainLayout;
    private AppointmentDTO appointmentDTO;
    private AppointmentsResultModel appointmentInfo;
    private Button checkInNowButton;
    private Button checkInAtOfficeButton;

    public interface CheckInOfficeNowAppointmentDialogListener {
        void onPreRegisterTapped(AppointmentDTO appointmentDTO, AppointmentsResultModel appointmentInfo);
    }

    /**
     * @param context           context
     * @param appointmentDTO    appointment dto
     * @param appointmentInfo   transition dto
     */
    public CheckInOfficeNowAppointmentDialog(Context context,
                                             AppointmentDTO appointmentDTO,
                                             AppointmentsResultModel appointmentInfo,
                                             CheckInOfficeNowAppointmentDialogListener callback) {
        super(context, appointmentDTO, false);
        this.context = context;
        this.appointmentDTO = appointmentDTO;
        this.appointmentInfo = appointmentInfo;
        this.callback = callback;
        this.canCheckIn = appointmentDTO.getPayload().canCheckInNow(appointmentInfo);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainLayout = (LinearLayout) getAddActionChildView();
        setActionButton();
    }

    @SuppressLint("InflateParams")
    private void setActionButton() {
        AppointmentLabelDTO appointmentLabels = appointmentInfo.getMetadata().getLabel();

        LayoutInflater inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childActionView = inflater.inflate(R.layout.dialog_checkin_office_now_appointment, null);

        checkInAtOfficeButton = (Button) childActionView.findViewById(R.id.checkInAtOfficeButton);
        checkInAtOfficeButton.setText(StringUtil.getLabelForView(appointmentLabels.getAppointmentsCheckInAtOfficeButtonText()));
        checkInAtOfficeButton.setOnClickListener(this);

        checkInNowButton = (Button) childActionView.findViewById(R.id.checkInNowButton);
        checkInNowButton.setOnClickListener(this);

        String labelForCheckInNowButton;
        if (canCheckIn) {
            labelForCheckInNowButton = appointmentLabels.getAppointmentsCheckInNow();
        } else {
            labelForCheckInNowButton = appointmentLabels.getAppointmentsPreRegister();
        }

        checkInNowButton.setText(labelForCheckInNowButton);

        mainLayout.addView(childActionView);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int viewId = view.getId();
        if (viewId == R.id.checkInAtOfficeButton) {
            checkInAtOfficeButton.setEnabled(false);
            new QrCodeViewDialog(context, appointmentDTO, appointmentInfo.getMetadata()).show();
            checkInAtOfficeButton.setEnabled(true);
            cancel();
        } else if (viewId == R.id.checkInNowButton) {

            checkInNowButton.setEnabled(false);

            if (canCheckIn) {
                TransitionDTO transitionDTO = appointmentInfo.getMetadata().getTransitions().getCheckingIn();
                doTransition(transitionDTO, demographicsVerifyCallback);
            } else if (null != callback) {
                callback.onPreRegisterTapped(appointmentDTO, appointmentInfo);
            }

            cancel();
        }
    }

    private WorkflowServiceCallback testMedicationsCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ((ISession) context).showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ((ISession) context).hideProgressDialog();
            PatientNavigationHelper.getInstance(getContext()).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ((ISession) context).hideProgressDialog();
        }
    };

    /**
     * call to do transition.
     */
    private void doTransition(TransitionDTO transitionDTO, WorkflowServiceCallback callback) {
        Gson gson = new Gson();
        Map<String, String> queries = new HashMap<>();
        JsonObject queryStringObject = appointmentInfo.getMetadata().getTransitions().getCheckinAtOffice().getQueryString();
        QueryStrings queryStrings = gson.fromJson(queryStringObject, QueryStrings.class);

        queries.put(queryStrings.getPracticeMgmt().getName(), appointmentDTO.getMetadata().getPracticeMgmt());
        queries.put(queryStrings.getPracticeId().getName(), appointmentDTO.getMetadata().getPracticeId());
        queries.put(queryStrings.getAppointmentId().getName(), appointmentDTO.getMetadata().getAppointmentId());

        Map<String, String> header = ((ISession) context).getWorkflowServiceHelper().getPreferredLanguageHeader();
        header.put("transition", "true");

        ((ISession) context).getWorkflowServiceHelper().execute(transitionDTO, callback, queries, header);
    }

    private WorkflowServiceCallback demographicsVerifyCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ((ISession) context).showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ((ISession) context).hideProgressDialog();
            checkInNowButton.setEnabled(true);
            PatientNavigationHelper.getInstance(context).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ((ISession) context).hideProgressDialog();
            checkInNowButton.setEnabled(true);
            SystemUtil.showDefaultFailureDialog(getContext());
            Log.e(getContext().getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };
}
