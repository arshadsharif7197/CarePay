package com.carecloud.carepay.practice.library.checkin.dialog;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.Toast;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.checkin.dtos.AppointmentPayloadDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInLabelDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInStatusDataPayloadValueDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.CheckInStatusPayloadDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.QueryStrings;
import com.carecloud.carepay.practice.library.checkin.dtos.QueueDTO;
import com.carecloud.carepay.practice.library.checkin.dtos.QueueStatusPayloadDTO;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
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

    private static final String TAG = "AppointmentDetailDialog";

    public interface AppointmentDialogCallback {
        void showPaymentDistributionDialog(PaymentsModel paymentsModel);

        void onFailure(String errorMessage);
    }

    private Context context;
    private CheckInDTO checkInDTO;
    private AppointmentPayloadDTO appointmentPayloadDTO;
    private PendingBalanceDTO pendingBalanceDTO;
    private CheckInLabelDTO checkInLabelDTO;

    private CarePayTextView checkingInLabel;
    private CarePayTextView hourLabel;
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
    private ImageView profilePhoto;
    private ImageView bgImage;
    private TextView shortName;
    private View checkboxLayout;
    private View queueTextLayout;
    private TextView queueText;

    private boolean isWaitingRoom;
    private Vector<CheckBox> checkBoxes = new Vector<>();
    private Vector<View> spacers = new Vector<>();

    private ISession sessionHandler;

    private AppointmentDialogCallback callback;

    /**
     * Constructor.
     *
     * @param context context
     */
    public AppointmentDetailDialog(Context context, CheckInDTO checkInDTO, PendingBalanceDTO pendingBalanceDTO,
                                   AppointmentPayloadDTO payloadDTO, boolean isWaitingRoom, AppointmentDialogCallback callback) {
        super(context);
        this.context = context;
        this.checkInDTO = checkInDTO;
        this.pendingBalanceDTO = pendingBalanceDTO;
        this.appointmentPayloadDTO = payloadDTO;
        this.isWaitingRoom = isWaitingRoom;
        this.callback = callback;

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

        View spacer = findViewById(R.id.spacer_one);
        spacers.add(spacer);
        spacer = findViewById(R.id.spacer_two);
        spacers.add(spacer);
        spacer = findViewById(R.id.spacer_three);
        spacers.add(spacer);
    }

    /**
     * for setting  UI Component Style .
     */
    private void onSettingStyle() {
        checkingInLabel.setTextColor(ContextCompat.getColor(context, R.color.charcoal_78));
        checkingInLabel.setTextColor(ContextCompat.getColor(context, R.color.white));

        GradientDrawable bgShapeAssistButton = (GradientDrawable) assistButton.getBackground();
        if (checkInDTO.getMetadata().hasAssistEnabled()) {
            bgShapeAssistButton.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } else {
            bgShapeAssistButton.setColor(ContextCompat.getColor(context, R.color.light_gray));
        }

        GradientDrawable bgShapePageButton = (GradientDrawable) pageButton.getBackground();
        if (checkInDTO.getMetadata().hasPageEnabled()) {
            bgShapePageButton.setColor(ContextCompat.getColor(context, R.color.rose_madder));
        } else {
            bgShapePageButton.setColor(ContextCompat.getColor(context, R.color.light_gray));
        }
    }

    /**
     * for setting values to UI Component from DTO .
     */
    private void onSetValuesFromDTO() {
        checkInLabelDTO = checkInDTO.getMetadata().getLabel();
        if (!isWaitingRoom) {
            demographicsCheckbox.setText(StringUtil.getFormatedLabal(context, checkInLabelDTO.getPracticeCheckinDetailDialogDemographics()));
            consentFormsCheckbox.setText(StringUtil.getFormatedLabal(context, checkInLabelDTO.getPracticeCheckinDetailDialogConsentForms()));
            intakeCheckbox.setText(StringUtil.getFormatedLabal(context, checkInLabelDTO.getPracticeCheckinDetailDialogIntake()));
            responsibilityCheckbox.setText(StringUtil.getFormatedLabal(context, checkInLabelDTO.getPracticeCheckinDetailDialogResponsibility()));
        } else {
            checkBoxes.add(demographicsCheckbox);
            checkBoxes.add(consentFormsCheckbox);
            checkBoxes.add(intakeCheckbox);
            checkBoxes.add(responsibilityCheckbox);
        }

        checkingInLabel.setText(StringUtil.getFormatedLabal(context, isWaitingRoom ?
                checkInLabelDTO.getPracticeCheckinDetailDialogWaitingRoom() : checkInLabelDTO.getPracticeCheckinDetailDialogCheckingIn()));
        balanceTextLabel.setText(StringUtil.getFormatedLabal(context, checkInLabelDTO.getPracticeCheckinDetailDialogBalance()));
        assistButton.setText(StringUtil.getFormatedLabal(context, checkInLabelDTO.getPracticeCheckinDetailDialogAssist()));
        pageButton.setText(StringUtil.getFormatedLabal(context, checkInLabelDTO.getPracticeCheckinDetailDialogPage()));
        paymentButton.setText(StringUtil.getFormatedLabal(context, checkInLabelDTO.getPracticeCheckinDetailDialogPayment()));

        balanceValueLabel.setText(StringUtil.getFormattedBalanceAmount(getPatientBalance()));
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

        String photoUrl = appointmentPayloadDTO.getPatient().getProfilePhoto();
        if (!TextUtils.isEmpty(photoUrl)) {
            Picasso.Builder builder = new Picasso.Builder(context);
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    shortName.setText(appointmentPayloadDTO.getPatient().getShortName());
                }
            });

            builder.build().load(photoUrl).transform(new CircleImageTransform()).resize(88, 88).into(profilePhoto);

            RequestCreator load = builder.build().load(photoUrl);
            load.fit().into(bgImage);

            profilePhoto.setVisibility(View.VISIBLE);
            bgImage.setScaleX(5);
            bgImage.setScaleY(5);
        } else {
            shortName.setText(appointmentPayloadDTO.getPatient().getShortName());
        }

        checkingInLabel.bringToFront();
        hourLabel.bringToFront();

        enableActionItems();
    }

    private void enableActionItems() {
        paymentButton.setEnabled(checkInDTO.getMetadata().hasPaymentEnabled());
        assistButton.setEnabled(checkInDTO.getMetadata().hasAssistEnabled());
        pageButton.setEnabled(checkInDTO.getMetadata().hasPageEnabled());
    }

    private View.OnClickListener paymentActionListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
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
            updateUI(workflowDTO);
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
        Gson gson = new Gson();
        QueueStatusPayloadDTO queueStatusPayloadDTO = gson.fromJson(workflowDTO.getPayload(), QueueStatusPayloadDTO.class);
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
                    spacers.get(1).setVisibility(View.GONE);
                    spacers.get(2).setVisibility(View.GONE);
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
                    spacers.get(2).setVisibility(View.GONE);

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

//            int maxIndex = queueList.size() - 1;
//            int index = maxIndex;
//            QueueDTO queue;
//            CheckBox checkBox;
//
//            for (int checkIndex = Math.min(index, 3); checkIndex >= 0; checkIndex--) {
//                queue = queueList.get(index);
//                checkBox = checkBoxes.get(checkIndex);
//                if (index == maxIndex) {
//                    checkBox.setChecked(true);
//                    checkBox.setText(ordinal(queue.getRank(), sufixes) + " " + metadata.getLabel().getPracticeCheckinDetailDialogQueue());
//                    checkBox.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
//                    checkBox.setTypeface(checkBox.getTypeface(), Typeface.BOLD);
//                } else {
//                    checkBox.setText(ordinal(queue.getRank(), sufixes) + " " + queue.getFirstName());
//                }
//
//                Log.d(this.getClass().getSimpleName(), "queue practice id: " + queue.getFirstName());
//                index--;
//            }
    }

    /**
     * @param workflowDTO workflow model returned by server.
     */
    private void updateUI(WorkflowDTO workflowDTO) {
        JsonObject jsonObject = workflowDTO.getPayload();

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


    private void showResponsibilityDialog(PaymentsModel paymentsModel) {
//        String tag = ResponsibilityFragmentDialog.class.getSimpleName();
//        FragmentTransaction ft = ((AppCompatActivity) getOwnerActivity()).getSupportFragmentManager().beginTransaction();
//        Fragment prev = ((AppCompatActivity) getOwnerActivity()).getSupportFragmentManager().findFragmentByTag(tag);
//        if (prev != null) {
//            ft.remove(prev);
//        }
//        ft.addToBackStack(null);
//
//        ResponsibilityHeaderModel headerModel = ResponsibilityHeaderModel.newPatientHeader(paymentsModel);
//        ResponsibilityFragmentDialog dialog = ResponsibilityFragmentDialog
//                .newInstance(paymentsModel, Label.getLabel("practice_payments_detail_dialog_payment_plan"),
//                        Label.getLabel("practice_payments_detail_dialog_pay"), headerModel);
//        dialog.show(ft, tag);
    }
}