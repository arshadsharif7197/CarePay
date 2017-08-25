package com.carecloud.carepay.practice.library.checkin.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.checkin.adapters.PagePickerAdapter;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInStatusDataPayloadValueDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInStatusPayloadDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.QueryStrings;
import com.carecloud.carepay.practice.library.payments.dialogs.PopupPickerWindow;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;
import com.carecloud.carepaylibray.appointments.models.QueueDTO;
import com.carecloud.carepaylibray.appointments.models.QueueStatusPayloadDTO;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.customcomponents.CarePayButton;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by sudhir_pingale on 10/26/2016
 */
public class AppointmentDetailDialog extends Dialog implements PagePickerAdapter.PagePickerCallback {

    private static final String TAG = "AppointmentDetailDialog";

    public interface AppointmentDialogCallback {
        void showPaymentDistributionDialog(PaymentsModel paymentsModel);

        void onFailure(String errorMessage);
    }

    private Context context;
    private CheckInDTO checkInDTO;
    private AppointmentsPayloadDTO appointmentPayloadDTO;
    private PendingBalanceDTO pendingBalanceDTO;

    private CarePayTextView checkingInLabel;
    private CarePayTextView hourLabel;
    private CarePayTextView patientNameLabel;
    private CarePayTextView doctorNameLabel;
    private CarePayTextView balanceTextLabel;
    private CarePayTextView balanceValueLabel;
    private CheckBox demographicsCheckbox;
    private CheckBox consentFormsCheckbox;
    private CheckBox medicationsCheckbox;
    private CheckBox intakeCheckbox;
    private CheckBox responsibilityCheckbox;
    private CarePayButton paymentButton;
    private CarePayButton assistButton;
    private CarePayButton pageButton;
    private ImageView profilePhoto;
    private ImageView bgImage;
    private TextView shortName;
    private View checkboxLayout;
    private View queueTextLayout;
    private TextView queueText;

    private boolean isWaitingRoom;
    private Vector<CheckBox> checkBoxes = new Vector<>();

    private ISession sessionHandler;

    private AppointmentDialogCallback callback;
    private PopupPickerWindow pickerWindow;
    private String pushUserId;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");

    private Handler handler;

    /**
     * Constructor.
     *
     * @param context context
     */
    public AppointmentDetailDialog(Context context, CheckInDTO checkInDTO, PendingBalanceDTO pendingBalanceDTO,
                                   AppointmentsPayloadDTO payloadDTO, boolean isWaitingRoom, AppointmentDialogCallback callback) {
        super(context);
        this.context = context;
        this.checkInDTO = checkInDTO;
        this.pendingBalanceDTO = pendingBalanceDTO;
        this.appointmentPayloadDTO = payloadDTO;
        this.isWaitingRoom = isWaitingRoom;
        this.callback = callback;
        this.handler = new Handler();
        setHandlersAndListeners();
    }

    private void setHandlersAndListeners() {
        try {
            sessionHandler = (ISession) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Provided context must be an instance of ISession");
        }
    }

    /**
     * for initialization UI .
     *
     * @param savedInstanceState for saving state
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_checkin_detail);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCancelable(true);

        callGetCheckInStatusAPI(); //API call for getting check-in status
        onInitialization();
        onSetValuesFromDTO();
//        onSettingStyle();

        if (getPatientBalance() == 0) {
            paymentButton.setEnabled(false);
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
        medicationsCheckbox = (CheckBox) findViewById(R.id.medsAllergiesCheckbox);
        intakeCheckbox = (CheckBox) findViewById(R.id.intakeFormsCheckbox);
        responsibilityCheckbox = (CheckBox) findViewById(R.id.responsibilityCheckbox);

        paymentButton = (CarePayButton) findViewById(R.id.paymentButton);
        assistButton = (CarePayButton) findViewById(R.id.assistButton);
        pageButton = (CarePayButton) findViewById(R.id.pageButton);

        paymentButton.setOnClickListener(paymentActionListener);
        assistButton.setOnClickListener(assistActionListener);
        pageButton.setOnClickListener(pageActionListener);

        profilePhoto = (ImageView) findViewById(R.id.patient_profile_photo);
        bgImage = (ImageView) findViewById(R.id.profile_bg_image);
        shortName = (TextView) findViewById(R.id.patientNameLabelShort);

        checkboxLayout = findViewById(R.id.checkbox_layout);
        queueTextLayout = findViewById(R.id.queue_text_layout);
        queueText = (TextView) findViewById(R.id.queue_text);

        pickerWindow = new PopupPickerWindow(context);
        pickerWindow.flipPopup(true);
        pickerWindow.setAdapter(new PagePickerAdapter(context, checkInDTO.getPayload().getPageMessages(), this));

        if(!canSendPage()){
            pageButton.setEnabled(false);
        }else{
            View mainView = findViewById(R.id.dialog_checkin_main);
            mainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pickerWindow.dismiss();
                }
            });
        }

    }


    /**
     * for setting values to UI Component from DTO .
     */
    private void onSetValuesFromDTO() {
        if (!isWaitingRoom) {
            demographicsCheckbox.setText(Label.getLabel("practice_checkin_detail_dialog_demographics"));
            consentFormsCheckbox.setText(Label.getLabel("practice_checkin_detail_dialog_consent_forms"));
            medicationsCheckbox.setText(Label.getLabel("practice_checkin_detail_dialog_medications"));
            intakeCheckbox.setText(Label.getLabel("practice_checkin_detail_dialog_intake"));
            responsibilityCheckbox.setText(Label.getLabel("practice_checkin_detail_dialog_responsibility"));
        } else {
            checkboxLayout.setVisibility(View.INVISIBLE);
            checkBoxes.add(demographicsCheckbox);
            checkBoxes.add(consentFormsCheckbox);
            checkBoxes.add(intakeCheckbox);
            checkBoxes.add(responsibilityCheckbox);
            medicationsCheckbox.setVisibility(View.GONE);
        }

        checkingInLabel.setText(isWaitingRoom ?
                Label.getLabel("practice_checkin_detail_dialog_waiting_room") :
                Label.getLabel("practice_checkin_detail_dialog_checking_in"));

        balanceValueLabel.setText(StringUtil.getFormattedBalanceAmount(getPatientBalance()));
        patientNameLabel.setText(StringUtil.getFormatedLabal(context, appointmentPayloadDTO.getPatient().getFullName()));
        doctorNameLabel.setText(StringUtil.getFormatedLabal(context, appointmentPayloadDTO.getProvider().getName()));

        Date date = DateUtil.getInstance().setDateRaw(appointmentPayloadDTO.getStartTime()).getDate();
        hourLabel.setText(dateFormat.format(date));
        setHourLabelBackground(date);

        findViewById(R.id.checkin_close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        String photoUrl = appointmentPayloadDTO.getPatient().getProfilePhoto();
        if (!TextUtils.isEmpty(photoUrl)) {
            Picasso.with(context).load(photoUrl)
                    .transform(new CircleImageTransform())
                    .resize(88, 88)
                    .into(profilePhoto, new Callback() {
                        @Override
                        public void onSuccess() {
                            profilePhoto.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            shortName.setText(appointmentPayloadDTO.getPatient().getShortName());
                        }
                    });
            Picasso.with(context).load(photoUrl)
                    .fit()
                    .into(bgImage);

            bgImage.setScaleX(5);
            bgImage.setScaleY(5);
        } else {
            shortName.setText(appointmentPayloadDTO.getPatient().getShortName());
        }

        checkingInLabel.bringToFront();
        hourLabel.bringToFront();

        enableActionItems();
    }

    /**
     * Sets hour label background.
     *
     * If the appointment start time has passed curent time, then show the appointment time label background in red.
     * If the appointment start time is in future, then show the appointment time label background in green.
     *
     */
    private void setHourLabelBackground(Date appointmentDateTime) {

        if (appointmentDateTime.before(new Date())) {
            hourLabel.setBackgroundResource(R.drawable.right_rounded_background_red);
        } else {
            hourLabel.setBackgroundResource(R.drawable.right_rounded_background_green);
        }
    }

    private void enableActionItems() {
        assistButton.setEnabled(checkInDTO.getMetadata().hasAssistEnabled());
    }

    private View.OnClickListener paymentActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            pickerWindow.dismiss();
            getPatientBalanceDetails();
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
            int offset = view.getWidth()/2-pickerWindow.getWidth()/2;
            pickerWindow.showAsDropDown(view, offset, 10);
        }
    };

    /**
     * Method to get checkin status API
     */
    private void callGetCheckInStatusAPI() {
        if (checkInDTO != null && checkInDTO.getMetadata().getLinks() != null &&
                checkInDTO.getMetadata().getLinks().getCheckinStatus() != null) {

            JsonObject queryStringObject;
            Gson gson = new Gson();
            QueryStrings queryStrings;
            TransitionDTO transition;
            Map<String, String> querymap;
            WorkflowServiceCallback callback;

            if (isWaitingRoom) {
                queryStringObject = checkInDTO.getMetadata().getLinks().getQueueStatus().getQueryString();
                queryStrings = gson.fromJson(queryStringObject, QueryStrings.class);
                querymap = getQueueQueryParam(queryStrings);
                transition = checkInDTO.getMetadata().getLinks().getQueueStatus();
                callback = getQueueCallBack;
            } else {
                queryStringObject = checkInDTO.getMetadata().getLinks().getCheckinStatus().getQueryString();
                queryStrings = gson.fromJson(queryStringObject, QueryStrings.class);
                querymap = getStatusQueryParam(queryStrings);
                transition = checkInDTO.getMetadata().getLinks().getCheckinStatus();
                callback = getStatusCallBack;
            }

            ((ISession) context).getWorkflowServiceHelper().execute(transition, callback, querymap);
        }
    }

    /**
     * @param queryStrings the query strings for the queue url
     * @return queryMap
     */
    private Map<String, String> getQueueQueryParam(QueryStrings queryStrings) {
        Map<String, String> queryMap = new HashMap<>();
        if (appointmentPayloadDTO != null) {
            queryMap.put(queryStrings.getPatientId().getName(), appointmentPayloadDTO.getPatient().getPatientId());
            queryMap.put(queryStrings.getAppointmentId().getName(), appointmentPayloadDTO.getId());
        }

        return queryMap;
    }

    /**
     * @param queryStrings the query strings for the status url
     * @return queryMap
     */
    private Map<String, String> getStatusQueryParam(QueryStrings queryStrings) {
        Map<String, String> queryMap = new HashMap<>();
        if (appointmentPayloadDTO != null) {
            queryMap.put(queryStrings.getAppointmentId().getName(), appointmentPayloadDTO.getId());
        }

        return queryMap;
    }

    private WorkflowServiceCallback getStatusCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            updatePageOptions(workflowDTO);
            updateCheckinStatus(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            callback.onFailure(exceptionMessage);
            Log.e(context.getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private WorkflowServiceCallback getQueueCallBack = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            updatePageOptions(workflowDTO);
            updateQueueStatus(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            callback.onFailure(exceptionMessage);
            Log.e(context.getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    /**
     * @param workflowDTO workflow model returned by server.
     */
    private void updateQueueStatus(WorkflowDTO workflowDTO) {
        QueueStatusPayloadDTO queueStatusPayloadDTO = workflowDTO.getPayload(QueueStatusPayloadDTO.class);
        List<QueueDTO> queueList = queueStatusPayloadDTO.getQueueStatus().getQueueStatusInnerPayload().getQueueList();

        Map<Integer, QueueDTO> queueMap = new HashMap<>();
        QueueDTO placeInQueue = findPlaceInQueue(queueList, appointmentPayloadDTO.getId(), queueMap);

        String[] sufixes = getOrdinalSufix();
        if(placeInQueue!=null){
            String place = ordinal(placeInQueue.getRank(), sufixes)+" "+Label.getLabel("practice_checkin_detail_dialog_in_queue");
            int rank = placeInQueue.getRank();
            switch (rank){
                case 1:
                {
                    //show single place in queue
                    queueTextLayout.setVisibility(View.VISIBLE);
                    checkboxLayout.setVisibility(View.GONE);
                    queueText.setText(place);
                    break;
                }
                case 2:{
                    queueTextLayout.setVisibility(View.GONE);
                    checkboxLayout.setVisibility(View.VISIBLE);

                    //current user
                    CheckBox checkBox =  checkBoxes.get(3);
                    checkBox.setChecked(true);
                    checkBox.setText(place);
                    checkBox.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));

                    //first user
                    checkBox = checkBoxes.get(0);
                    placeInQueue = queueMap.get(1);
                    place = ordinal(1, sufixes)+"\n"+placeInQueue.getFirstName();
                    checkBox.setText(place);

                    //hide other checkboxes
                    checkBoxes.get(1).setVisibility(View.GONE);
                    checkBoxes.get(2).setVisibility(View.GONE);
                    break;
                }
                case 3:
                {
                    queueTextLayout.setVisibility(View.GONE);
                    checkboxLayout.setVisibility(View.VISIBLE);

                    //current user
                    CheckBox checkBox =  checkBoxes.get(3);
                    checkBox.setChecked(true);
                    checkBox.setText(place);
                    checkBox.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));

                    //first user
                    checkBox = checkBoxes.get(0);
                    placeInQueue = queueMap.get(1);
                    place = ordinal(1, sufixes)+"\n"+placeInQueue.getFirstName();
                    checkBox.setText(place);

                    //second user
                    checkBox = checkBoxes.get(1);
                    placeInQueue = queueMap.get(2);
                    place = ordinal(2, sufixes)+"\n"+placeInQueue.getFirstName();
                    checkBox.setText(place);


                    //hide other checkboxes
                    checkBoxes.get(2).setVisibility(View.GONE);

                    break;
                }

                default:
                {
                    //show checkbox bar
                    queueTextLayout.setVisibility(View.GONE);
                    checkboxLayout.setVisibility(View.VISIBLE);
                    int counter = 3;
                    CheckBox checkBox =  checkBoxes.get(counter);
                    checkBox.setChecked(true);
                    checkBox.setText(place);
                    checkBox.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));

                    counter--;
                    rank--;
                    while (counter > -1){
                        placeInQueue = queueMap.get(rank);
                        place = ordinal(rank, sufixes)+"\n"+placeInQueue.getFirstName();
                        checkBox = checkBoxes.get(counter);
                        checkBox.setText(place);

                        counter--;
                        rank--;
                    }
                }
            }
        }
    }

    /**
     * @param workflowDTO workflow model returned by server.
     */
    private void updateCheckinStatus(WorkflowDTO workflowDTO) {
        try {
            CheckInStatusPayloadDTO checkInStatusPayloadDTO = workflowDTO.getPayload(CheckInStatusPayloadDTO.class);

            if (checkInStatusPayloadDTO != null) {
                CheckInStatusDataPayloadValueDTO payloadValueDTO = checkInStatusPayloadDTO
                        .getCheckInStatusData().getPayload();
                boolean demographicsComplete = payloadValueDTO.getDemographicsVerifyComplete()
                        .equalsIgnoreCase(CarePayConstants.APPOINTMENTS_STATUS_COMPLETED);
                boolean consentComplete = payloadValueDTO.getConsentFormsComplete()
                        .equalsIgnoreCase(CarePayConstants.APPOINTMENTS_STATUS_COMPLETED);
                boolean medicationsComplete = payloadValueDTO.getMedicationsComplete()
                        .equalsIgnoreCase(CarePayConstants.APPOINTMENTS_STATUS_COMPLETED);
                boolean intakeComplete = payloadValueDTO.getIntakeFormsComplete()
                        .equalsIgnoreCase(CarePayConstants.APPOINTMENTS_STATUS_COMPLETED);
                boolean paymentComplete = payloadValueDTO.getRespsonsibility()
                        .equalsIgnoreCase(CarePayConstants.APPOINTMENTS_STATUS_COMPLETED);

                if(demographicsComplete){
                    demographicsCheckbox.setChecked(true);
                }else{
                    demographicsCheckbox.setChecked(false);
                    demographicsCheckbox.setSelected(true);
                }
                if(consentComplete){
                    consentFormsCheckbox.setChecked(true);
                }else{
                    consentFormsCheckbox.setChecked(false);
                    if(demographicsComplete){
                        consentFormsCheckbox.setSelected(true);
                    }
                }
                if(medicationsComplete){
                    medicationsCheckbox.setChecked(true);
                }else{
                    medicationsCheckbox.setChecked(false);
                    if(consentComplete){
                        medicationsCheckbox.setSelected(true);
                    }
                }
                if(intakeComplete){
                    intakeCheckbox.setChecked(true);
                }else{
                    intakeCheckbox.setChecked(false);
                    if(medicationsComplete){
                        intakeCheckbox.setSelected(true);
                    }
                }
                if(paymentComplete) {
                    responsibilityCheckbox.setChecked(true);
                }else{
                    responsibilityCheckbox.setChecked(false);
                    if(intakeComplete){
                        responsibilityCheckbox.setSelected(true);
                    }
                }
            }
        } catch (Exception ex) {
            callback.onFailure(ex.getMessage());
            Log.e(context.getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), ex.getMessage());
        }
    }

    private double getPatientBalance() {
        if (pendingBalanceDTO != null && !pendingBalanceDTO.getPayload().isEmpty()) {
            return pendingBalanceDTO.getPayload().get(0).getAmount();
        }

        return 0;
    }

    private String[] getOrdinalSufix() {
        String th = Label.getLabel("practice_checkin_detail_dialog_ordinal_th");
        String st = Label.getLabel("practice_checkin_detail_dialog_ordinal_st");
        String nd = Label.getLabel("practice_checkin_detail_dialog_ordinal_nd");
        String rd = Label.getLabel("practice_checkin_detail_dialog_ordinal_rd");
        return new String[]{th, st, nd, rd, th, th, th, th, th, th};
    }

    private QueueDTO findPlaceInQueue(List<QueueDTO> queueDTOList, String appointmentId, Map<Integer, QueueDTO> placeMap){
        QueueDTO patientQueueDTO = null;
        for(QueueDTO queueDTO: queueDTOList){
            placeMap.put(queueDTO.getRank(), queueDTO);
            if(queueDTO.getAppointmentId().equals(appointmentId)){
                patientQueueDTO = queueDTO;
            }
        }
        return patientQueueDTO;
    }

    private void updatePageOptions(final WorkflowDTO workflowDTO){
        handler.post(new Runnable() {
            @Override
            public void run() {
                PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
                pageButton.setEnabled(paymentsModel.getPaymentPayload().getPatientBalances().get(0).getDemographics().getPayload().getNotificationOptions().hasPushNotification());
            }
        });
    }

    private String ordinal(int number, String[] sufixes) {
        switch (number % 100) {
            case 11:
            case 12:
            case 13:
                return number + sufixes[0];
            default:
                return number + sufixes[number % 10];
        }
    }

    private void getPatientBalanceDetails() {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("patient_id", appointmentPayloadDTO.getPatient().getPatientId());

        TransitionDTO transitionDTO = checkInDTO.getMetadata().getLinks().getPatientBalances();
        sessionHandler.getWorkflowServiceHelper().interrupt();
        sessionHandler.getWorkflowServiceHelper().execute(transitionDTO, patientBalancesCallback, queryMap);

    }

    private boolean canSendPage(){
        String userId = pendingBalanceDTO.getMetadata().getUserId();
        String checkUserId;
        for(PatientBalanceDTO patientBalanceDTO : checkInDTO.getPayload().getPatientBalances()){
            checkUserId = patientBalanceDTO.getDemographics().getMetadata().getUserId();
            if(userId.equals(checkUserId)){
                pushUserId = checkUserId;
                return patientBalanceDTO.getDemographics().getPayload().getNotificationOptions().hasPushNotification();
            }
        }
        return false;
    }


    private WorkflowServiceCallback patientBalancesCallback = new WorkflowServiceCallback() {

        @Override
        public void onPreExecute() {
            sessionHandler.showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            sessionHandler.hideProgressDialog();
            PaymentsModel patientDetails = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO.toString());
            if (patientDetails != null && !patientDetails.getPaymentPayload().getPatientBalances().isEmpty()) {

                PatientBalanceDTO patientBalanceDTO = patientDetails.getPaymentPayload().getPatientBalances().get(0);
                if (patientBalanceDTO.getBalances().get(0).getPayload().isEmpty()) {
                    Toast.makeText(getContext(), "Patient has no balance", Toast.LENGTH_LONG).show();
                } else {
                    patientDetails.getPaymentPayload().setLocations(checkInDTO.getPayload().getLocations());
                    patientDetails.getPaymentPayload().setLocationIndex(checkInDTO.getPayload().getLocationIndex());
                    patientDetails.getPaymentPayload().setProviders(checkInDTO.getPayload().getProviders());
                    patientDetails.getPaymentPayload().setProviderIndex(checkInDTO.getPayload().getProviderIndex());
                    callback.showPaymentDistributionDialog(patientDetails);
                    cancel();
                }
            } else {
                Toast.makeText(getContext(), "Patient has no balance", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            sessionHandler.hideProgressDialog();
            callback.onFailure(exceptionMessage);
            Log.e(TAG, exceptionMessage);

        }
    };

    @Override
    public void sendMessage(String message) {
        pickerWindow.dismiss();

        TransitionDTO transitionDTO = checkInDTO.getMetadata().getLinks().getPagePatient();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("user_id", pushUserId);
        queryMap.put("message", message);

        sessionHandler.getWorkflowServiceHelper().execute(transitionDTO, pushNotificationCallback, queryMap);

    }

    private WorkflowServiceCallback pushNotificationCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            SystemUtil.showSuccessToast(context, Label.getLabel("push_notification_sent"));
        }

        @Override
        public void onFailure(String exceptionMessage) {
            sessionHandler.showErrorNotification(Label.getLabel("push_notification_failed"));
        }
    };

}