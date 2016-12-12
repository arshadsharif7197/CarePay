package com.carecloud.carepay.practice.library.checkin.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.checkin.dtos.AppointmentPayloadDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInLabelDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInStatusDataPayloadValueDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInStatusPayloadDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.PatientBalanceDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.PatientBalancePayloadDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.QueryStrings;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.customcomponents.CarePayButton;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sudhir_pingale on 10/26/2016.
 */

public class AppointmentDetailDialog extends Dialog {

    private Context context;
    private CheckInDTO checkInDTO;
    private PatientBalanceDTO patientBalanceDTO;
    private AppointmentPayloadDTO appointmentPayloadDTO;
    private CarePayTextView checkingInLabel;
    private CarePayTextView patientNameLabel;
    private CarePayTextView doctorNameLabel;
    private CarePayTextView balanceTextLabel;
    private CarePayTextView balanceValueLabel;
    private CheckBox demographicsCheckbox;
    private CheckBox consentFormsCheckbox;
    private CheckBox intakeCheckbox;
    private CheckBox responsibilityCheckbox;
    private CarePayButton paymentButton;
    private CarePayButton assistButton;
    private CarePayButton pageButton;
    private ImageView patientImageView;

    /**
     * Constructor.
     *
     * @param context context
     */
    public AppointmentDetailDialog(Context context, CheckInDTO checkInDTO, PatientBalanceDTO patientBalanceDTO,
                                   AppointmentPayloadDTO payloadDTO) {
        super(context);
        this.context = context;
        this.checkInDTO = checkInDTO;
        this.patientBalanceDTO = patientBalanceDTO;
        this.appointmentPayloadDTO = payloadDTO;
    }

    /**
     * for initialization UI .
     *
     * @param savedInstanceState for saving state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_checkin_detail);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCancelable(true);
        callGetCheckinStatusAPI(); //API call for getting checkin status
        onInitialization();
        onSettingStyle();
        onSetValuesFromDTO();
        if(balanceValueLabel.getText().toString().trim().equalsIgnoreCase(CarePayConstants.ZERO_BALANCE)){
            paymentButton.setVisibility(View.GONE);
        }
    }

    /**
     * for initialization UI components  .
     */
    private void onInitialization() {
        checkingInLabel = (CarePayTextView) findViewById(R.id.checkingInLabel);
        patientNameLabel = (CarePayTextView) findViewById(R.id.patientNameLabel);
        doctorNameLabel = (CarePayTextView) findViewById(R.id.doctorNameLabel);
        balanceTextLabel = (CarePayTextView) findViewById(R.id.balanceTextLabel);
        balanceValueLabel = (CarePayTextView) findViewById(R.id.balanceValueLabel);
        demographicsCheckbox = (CheckBox) findViewById(R.id.demographicsCheckbox);
        consentFormsCheckbox = (CheckBox) findViewById(R.id.consentFormsCheckbox);
        intakeCheckbox = (CheckBox) findViewById(R.id.intakeCheckbox);
        responsibilityCheckbox = (CheckBox) findViewById(R.id.responsibilityCheckbox);
        paymentButton = (CarePayButton) findViewById(R.id.paymentButton);
        assistButton = (CarePayButton) findViewById(R.id.assistButton);
        pageButton = (CarePayButton) findViewById(R.id.pageButton);
        patientImageView = (ImageView) findViewById(R.id.patientImageView);

        paymentButton.setOnClickListener(paymentActionListener);
        assistButton.setOnClickListener(assistActionListener);
        pageButton.setOnClickListener(pageActionListener);
    }

    /**
     * for setting  UI Component Style .
     */
    private void onSettingStyle() {
        checkingInLabel.setTextColor(ContextCompat.getColor(context, R.color.charcoal_78));
        patientNameLabel.setTextColor(ContextCompat.getColor(context, R.color.charcoal));
        doctorNameLabel.setTextColor(ContextCompat.getColor(context, R.color.taupe_gray_78));
        balanceTextLabel.setTextColor(ContextCompat.getColor(context, R.color.charcoal));
        balanceValueLabel.setTextColor(ContextCompat.getColor(context, R.color.charcoal));

        GradientDrawable bgShapePaymentButton = (GradientDrawable) paymentButton.getBackground();
        bgShapePaymentButton.setColor(ContextCompat.getColor(context, R.color.yellowGreen));

        GradientDrawable bgShapeAssistButton = (GradientDrawable) assistButton.getBackground();
        bgShapeAssistButton.setColor(ContextCompat.getColor(context, R.color.bright_cerulean));

        GradientDrawable bgShapePageButton = (GradientDrawable) pageButton.getBackground();
        bgShapePageButton.setColor(ContextCompat.getColor(context, R.color.rose_madder));
    }

    /**
     * for setting values to UI Component from DTO .
     */
    private void onSetValuesFromDTO() {
        CheckInLabelDTO checkInLabelDTO = checkInDTO.getMetadata().getLabel();
        checkingInLabel.setText(StringUtil.getFormatedLabal(context, checkInLabelDTO.getPracticeCheckinDetailDialogCheckingIn()));
        balanceTextLabel.setText(StringUtil.getFormatedLabal(context, checkInLabelDTO.getPracticeCheckinDetailDialogBalance()));
        demographicsCheckbox.setText(StringUtil.getFormatedLabal(context, checkInLabelDTO.getPracticeCheckinDetailDialogDemographics()));
        consentFormsCheckbox.setText(StringUtil.getFormatedLabal(context, checkInLabelDTO.getPracticeCheckinDetailDialogConsentForms()));
        intakeCheckbox.setText(StringUtil.getFormatedLabal(context, checkInLabelDTO.getPracticeCheckinDetailDialogIntake()));
        responsibilityCheckbox.setText(StringUtil.getFormatedLabal(context, checkInLabelDTO.getPracticeCheckinDetailDialogResponsibility()));
        paymentButton.setText(StringUtil.getFormatedLabal(context, checkInLabelDTO.getPracticeCheckinDetailDialogPayment()));
        assistButton.setText(StringUtil.getFormatedLabal(context, checkInLabelDTO.getPracticeCheckinDetailDialogAssist()));
        pageButton.setText(StringUtil.getFormatedLabal(context, checkInLabelDTO.getPracticeCheckinDetailDialogPage()));

        balanceValueLabel.setText(StringUtil.getFormatedLabal(context, getPatientBalance()));
        patientNameLabel.setText(StringUtil.getFormatedLabal(context, appointmentPayloadDTO.getPatient().getFullName()));
        doctorNameLabel.setText(StringUtil.getFormatedLabal(context, appointmentPayloadDTO.getProvider().getName()));
    }

    private View.OnClickListener paymentActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener assistActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener pageActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    /**
     * Method to get checkin status API
     */
    private void callGetCheckinStatusAPI() {
        if (checkInDTO != null && checkInDTO.getMetadata().getLinks() != null &&
                checkInDTO.getMetadata().getLinks().getCheckinStatus() != null) {

            JsonObject queryStringObject = checkInDTO.getMetadata().getLinks().getCheckinStatus().getQueryString();
            Gson gson = new Gson();
            QueryStrings queryStrings = gson.fromJson(queryStringObject, QueryStrings.class);

            WorkflowServiceHelper.getInstance().execute(checkInDTO.getMetadata().getLinks()
                    .getCheckinStatus(), getStatusCallBack, getQueryParam(queryStrings));
        }
    }

    /**
     * @param queryStrings the query strings for the url
     * @return queryMap
     */
    private Map<String, String> getQueryParam(QueryStrings queryStrings) {
        Map<String, String> queryMap = new HashMap<String, String>();
        queryMap.put(queryStrings.getAppointmentId().getName(), appointmentPayloadDTO.getId());
        queryMap.put(queryStrings.getPracticeManagement().getName(), patientBalanceDTO.getMetadata().getPracticeMgmt());
        queryMap.put(queryStrings.getPracticeId().getName(), patientBalanceDTO.getMetadata().getPracticeId());

        return queryMap;
    }

    WorkflowServiceCallback getStatusCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            updateUI(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showFaultDialog(context);
            Log.e(context.getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    /**
     * @param workflowDTO workflow model returned by server.
     */
    private void updateUI(WorkflowDTO workflowDTO) {
        JsonObject jsonObject = (JsonObject) workflowDTO.getPayload();

        Gson gson = new Gson();
        CheckInStatusPayloadDTO checkInStatusPayloadDTO = gson.fromJson(jsonObject, CheckInStatusPayloadDTO.class);

        if (checkInStatusPayloadDTO != null) {
            CheckInStatusDataPayloadValueDTO payloadValueDTO = checkInStatusPayloadDTO
                    .getCheckInStatusData().getPayload();
            demographicsCheckbox.setChecked(payloadValueDTO.getDemographicsVerifyComplete()
                    .equalsIgnoreCase(CarePayConstants.APPOINTMENTS_STATUS_COMPLETED));
            consentFormsCheckbox.setChecked(payloadValueDTO.getConsentFormsComplete()
                    .equalsIgnoreCase(CarePayConstants.APPOINTMENTS_STATUS_COMPLETED));
            intakeCheckbox.setChecked(payloadValueDTO.getIntakeFormsComplete()
                    .equalsIgnoreCase(CarePayConstants.APPOINTMENTS_STATUS_COMPLETED));
            responsibilityCheckbox.setChecked(payloadValueDTO.getRespsonsibility()
                    .equalsIgnoreCase(CarePayConstants.APPOINTMENTS_STATUS_COMPLETED));
        }
    }

    private String getPatientBalance(){
        double totalBalance=0;
        for(PatientBalancePayloadDTO balancePayloadDTO : patientBalanceDTO.getPayload()){
            totalBalance+=balancePayloadDTO.getTotal();
        }
        return " "+StringUtil.getFormattedBalanceAmount(totalBalance);
    }
}