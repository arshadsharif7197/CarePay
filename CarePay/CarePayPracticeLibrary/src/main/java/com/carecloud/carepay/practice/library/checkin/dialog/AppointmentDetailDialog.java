package com.carecloud.carepay.practice.library.checkin.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.checkin.dtos.AppointmentDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.AppointmentPayloadDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInLabelDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInMetadataDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInStatusDataPayloadValueDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInStatusPayloadDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.PatientBalanceDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.QueryStrings;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.practice.library.checkin.dtos.QueueDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.QueueStatusPayloadDTO;
import com.carecloud.carepaylibray.customcomponents.CarePayButton;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by sudhir_pingale on 10/26/2016.
 */

public class AppointmentDetailDialog extends Dialog {

    private Context context;
    private CheckInDTO checkInDTO;
    private PatientBalanceDTO patientBalanceDTO;
    private AppointmentPayloadDTO appointmentPayloadDTO;
    private CarePayTextView checkingInLabel;
    private CarePayTextView hourLabel;
    private CarePayTextView patientNameLabel;
    private CarePayTextView doctorNameLabel;
    private CarePayTextView balanceTextLabel;
    private CarePayTextView balanceValueLabel;
    private CheckBox demographicsCheckbox;
    private CheckBox consentFormsCheckbox;
    private CheckBox intakeCheckbox;
    //private CarePayTextView demographicsLabel;
    //private CarePayTextView consentFormsLabel;
    //private CarePayTextView intakeLabel;
    private CheckBox responsibilityCheckbox;
    private CarePayButton paymentButton;
    private CarePayButton assistButton;
    private CarePayButton pageButton;
    private ImageView profilePhoto;
    private ImageView bgImage;
    private TextView shortName;
    private AppointmentDTO appointmentDTO;
    private boolean isWaitingroom;
    private Vector<CheckBox> checkBoxes = new Vector<>();
    //private Vector<CarePayTextView> textViews = new Vector<>();


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
        this.isWaitingroom = isWaitingroom;
    }

    /**
     * Constructor.
     *
     * @param context context
     */
    public AppointmentDetailDialog(Context context, CheckInDTO checkInDTO, AppointmentDTO patientBalanceDTO,
                                   AppointmentPayloadDTO payloadDTO,  boolean isWaitingroom) {
        super(context);
        this.context = context;
        this.checkInDTO = checkInDTO;
        this.appointmentDTO = patientBalanceDTO;
        this.appointmentPayloadDTO = payloadDTO;
        this.isWaitingroom = isWaitingroom;
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
        hourLabel = (CarePayTextView) findViewById(R.id.hourLabel);
        patientNameLabel = (CarePayTextView) findViewById(R.id.patientNameLabel);
        doctorNameLabel = (CarePayTextView) findViewById(R.id.doctorNameLabel);
        balanceTextLabel = (CarePayTextView) findViewById(R.id.balanceTextLabel);
        balanceValueLabel = (CarePayTextView) findViewById(R.id.balanceValueLabel);
        demographicsCheckbox = (CheckBox) findViewById(R.id.demographicsCheckbox);
        consentFormsCheckbox = (CheckBox) findViewById(R.id.consentFormsCheckbox);
        intakeCheckbox = (CheckBox) findViewById(R.id.intakeFormsCheckbox);
        responsibilityCheckbox = (CheckBox) findViewById(R.id.responsibilityCheckbox);
        //demographicsLabel = (CarePayTextView) findViewById(R.id.demographicsLabel);
        //consentFormsLabel = (CarePayTextView) findViewById(R.id.consentFormsLabel);
        //intakeLabel = (CarePayTextView) findViewById(R.id.intakeLabel);
        paymentButton = (CarePayButton) findViewById(R.id.paymentButton);
        assistButton = (CarePayButton) findViewById(R.id.assistButton);
        pageButton = (CarePayButton) findViewById(R.id.pageButton);

        paymentButton.setOnClickListener(paymentActionListener);
        assistButton.setOnClickListener(assistActionListener);
        pageButton.setOnClickListener(pageActionListener);

        profilePhoto = (ImageView) findViewById(R.id.patient_profile_photo);
        bgImage = (ImageView) findViewById(R.id.profile_bg_image);
        shortName = (TextView) findViewById(R.id.patient_profile_short_name);

    }

    /**
     * for setting  UI Component Style .
     */
    private void onSettingStyle() {
        checkingInLabel.setTextColor(ContextCompat.getColor(context, R.color.charcoal_78));
        //patientNameLabel.setTextColor(ContextCompat.getColor(context, R.color.charcoal));
        //doctorNameLabel.setTextColor(ContextCompat.getColor(context, R.color.taupe_gray_78));
        //balanceTextLabel.setTextColor(ContextCompat.getColor(context, R.color.charcoal));
        //balanceValueLabel.setTextColor(ContextCompat.getColor(context, R.color.charcoal));
        checkingInLabel.setTextColor(ContextCompat.getColor(context, R.color.white));

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
        if (!isWaitingroom) {
            demographicsCheckbox.setText(StringUtil.getFormatedLabal(context, checkInLabelDTO.getPracticeCheckinDetailDialogDemographics()));
            consentFormsCheckbox.setText(StringUtil.getFormatedLabal(context, checkInLabelDTO.getPracticeCheckinDetailDialogConsentForms()));
            intakeCheckbox.setText(StringUtil.getFormatedLabal(context, checkInLabelDTO.getPracticeCheckinDetailDialogIntake()));
            responsibilityCheckbox.setText(StringUtil.getFormatedLabal(context, checkInLabelDTO.getPracticeCheckinDetailDialogResponsibility()));
        } else {
            checkBoxes.add(demographicsCheckbox);
            //textViews.add(demographicsLabel);
            checkBoxes.add(consentFormsCheckbox);
            //textViews.add(consentFormsLabel);
            checkBoxes.add(intakeCheckbox);
            //textViews.add(intakeLabel);
            checkBoxes.add(responsibilityCheckbox);
        }
        checkingInLabel.setText(StringUtil.getFormatedLabal(context, isWaitingroom?
                checkInLabelDTO.getPracticeCheckinDetailDialogWaitingRoom() : checkInLabelDTO.getPracticeCheckinDetailDialogCheckingIn()));
        balanceTextLabel.setText(StringUtil.getFormatedLabal(context, checkInLabelDTO.getPracticeCheckinDetailDialogBalance()));
        assistButton.setText(StringUtil.getFormatedLabal(context, checkInLabelDTO.getPracticeCheckinDetailDialogAssist()));
        pageButton.setText(StringUtil.getFormatedLabal(context, checkInLabelDTO.getPracticeCheckinDetailDialogPage()));
        paymentButton.setText(StringUtil.getFormatedLabal(context, checkInLabelDTO.getPracticeCheckinDetailDialogPayment()));

        balanceValueLabel.setText(StringUtil.getFormatedLabal(context, getPatientBalance()));
        patientNameLabel.setText(StringUtil.getFormatedLabal(context, appointmentPayloadDTO.getPatient().getFullName()));
        doctorNameLabel.setText(StringUtil.getFormatedLabal(context, appointmentPayloadDTO.getProvider().getName()));
        long hour = DateUtil.getInstance().setDateRaw(appointmentPayloadDTO.getStartTime()).getDate().getTime();
        final DateTime appointmentDateTime = new DateTime(hour);
        hourLabel.setText(appointmentDateTime.toString("hh:mm a"));

        findViewById(R.id.checkin_close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        String photoUrl = ""; //NOT PHOTO YET
        if (!TextUtils.isEmpty(photoUrl)) {
            Picasso.Builder builder = new Picasso.Builder(context);
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    shortName.setText(StringUtil.onShortDrName(appointmentPayloadDTO.getPatient().getFullName()));
                }
            });

            builder.build().load(photoUrl).transform(new CircleImageTransform()).resize(88, 88).into(profilePhoto);

            RequestCreator load = builder.build().load(photoUrl);
            load.fit().into(bgImage);

            profilePhoto.setVisibility(View.VISIBLE);
        }
        checkingInLabel.bringToFront();
        hourLabel.bringToFront();
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

            JsonObject queryStringObject;
            Gson gson = new Gson();
            QueryStrings queryStrings;
            TransitionDTO transition;
            Map<String, String> querymap;
            WorkflowServiceCallback callback;
            if (isWaitingroom) {
                queryStringObject = checkInDTO.getMetadata().getLinks().getQueueStatus().getQueryString();
                queryStrings = gson.fromJson(queryStringObject, QueryStrings.class);
                querymap = getQueueQueryParam(queryStrings);
                transition = checkInDTO.getMetadata().getLinks().getQueueStatus();
                callback = getQueueCallBack;
            }else{
                queryStringObject = checkInDTO.getMetadata().getLinks().getCheckinStatus().getQueryString();
                queryStrings = gson.fromJson(queryStringObject, QueryStrings.class);
                querymap = getQueryParam(queryStrings);
                transition = checkInDTO.getMetadata().getLinks().getCheckinStatus();
                callback = getStatusCallBack;
            }

            WorkflowServiceHelper.getInstance().execute(transition, callback, querymap);
        }
    }

    /**
     * @param queryStrings the query strings for the url
     * @return queryMap
     */
    private Map<String, String> getQueryParam(QueryStrings queryStrings) {
        Map<String, String> queryMap = new HashMap<String, String>();
        queryMap.put(queryStrings.getAppointmentId().getName(), appointmentDTO.getMetadata().getAppointmentId());
        queryMap.put(queryStrings.getPracticeManagement().getName(), appointmentDTO.getMetadata().getPracticeMgmt());
        queryMap.put(queryStrings.getPracticeId().getName(), appointmentDTO.getMetadata().getPracticeId());

        return queryMap;
    }

    /**
     * @param queryStrings the query strings for the url
     * @return queryMap
     */
    private Map<String, String> getQueueQueryParam(QueryStrings queryStrings) {
        Map<String, String> queryMap = new HashMap<String, String>();
        queryMap.put(queryStrings.getPatientId().getName(), appointmentDTO.getMetadata().getPatientId());
        queryMap.put(queryStrings.getPracticeManagement().getName(), appointmentDTO.getMetadata().getPracticeMgmt());
        queryMap.put(queryStrings.getPracticeId().getName(), appointmentDTO.getMetadata().getPracticeId());

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

    WorkflowServiceCallback getQueueCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            updateQueueStatus(workflowDTO);
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
    private void updateQueueStatus(WorkflowDTO workflowDTO) {
        JsonObject jsonObject = (JsonObject) workflowDTO.getPayload();

        Gson gson = new Gson();
        try {

            QueueStatusPayloadDTO queueStatusPayloadDTO = gson.fromJson(jsonObject, QueueStatusPayloadDTO.class);
            Log.d(this.getClass().getSimpleName(), "queue size: " + queueStatusPayloadDTO.getQueueStatus().getQueueStatusInnerPayload().getQueueList().size());

            if (queueStatusPayloadDTO != null) {
                CheckInMetadataDTO metadata = checkInDTO.getMetadata();
                List<QueueDTO> queueList = queueStatusPayloadDTO.getQueueStatus().getQueueStatusInnerPayload().getQueueList();
                int maxIndex = queueList.size() - 1;
                int index = maxIndex;
                QueueDTO queue;
                CheckBox checkBox;
                //CarePayTextView textView;
                String[] sufixes = getOrdinalSufix(metadata.getLabel());
                for(int chkindex = index >= 3 ? 3 : index; chkindex >=0; chkindex--){
                    queue = queueList.get(index);
                    checkBox = checkBoxes.get(chkindex);
                    if(index == maxIndex){
                        checkBox.setChecked(true);
                        checkBox.setText(ordinal(queue.getRank(), sufixes)+" "+metadata.getLabel().getPracticeCheckinDetailDialogQueue());
                        checkBox.setTextColor(ContextCompat.getColor(context, R.color.bright_cerulean));
                        checkBox.setTypeface(checkBox.getTypeface(), Typeface.BOLD);
                    }else{
                        //textView = textViews.get(chkindex);
                        checkBox.setText(ordinal(queue.getRank(), sufixes)+" "+queue.getFirstName());
                        //textView.setText(queue.getFirstName());
                        //textView.setVisibility(View.VISIBLE);
                    }
                    Log.d(this.getClass().getSimpleName(), "queue practice id: " + queue.getFirstName());
                    index --;
                }
            }
        }catch (Exception ex){
           SystemUtil.showFaultDialog(context);
           Log.e(context.getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * @param workflowDTO workflow model returned by server.
     */
    private void updateUI(WorkflowDTO workflowDTO) {
        JsonObject jsonObject = (JsonObject) workflowDTO.getPayload();

        Gson gson = new Gson();
        try {
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
        }catch (Exception ex){
           SystemUtil.showFaultDialog(context);
           Log.e(context.getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), ex.getMessage());
        }
    }

    private String getPatientBalance(){
        double totalBalance=100;
/*
        for(PatientBalancePayloadDTO balancePayloadDTO : patientBalanceDTO.getPayload()){
            totalBalance+=balancePayloadDTO.getTotal();
        }
*/
        return " "+StringUtil.getFormattedBalanceAmount(totalBalance);
    }

    private String[] getOrdinalSufix(CheckInLabelDTO labels){
        String th = labels.getPracticeCheckinDetailDialogOrdinalTh();
        String st = labels.getPracticeCheckinDetailDialogOrdinalSt();
        String nd = labels.getPracticeCheckinDetailDialogOrdinalNd();
        String rd = labels.getPracticeCheckinDetailDialogOrdinalRd();
        return new String[] { th, st, nd, rd, th, th, th, th, th, th };
    }
    private String ordinal(int i, String[] sufixes) {
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + sufixes[0];
            default:
                return i + sufixes[i % 10];

        }
    }
}