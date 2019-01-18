package com.carecloud.carepay.practice.library.payments;

import android.content.Intent;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.payments.models.IntegratedPaymentQueueRecord;
import com.carecloud.carepay.practice.library.payments.models.ShamrockPaymentMetadata;
import com.carecloud.carepay.practice.library.payments.models.ShamrockPaymentModel;
import com.carecloud.carepay.practice.library.payments.models.ShamrockPaymentsPostModel;
import com.carecloud.carepay.service.library.RestCallServiceCallback;
import com.carecloud.carepay.service.library.RestCallServiceHelper;
import com.carecloud.carepay.service.library.RestDef;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.payments.interfaces.PaymentConfirmationInterface;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PatientPaymentsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentMetadata;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.shamrocksdk.payment.DevicePayment;
import com.carecloud.shamrocksdk.payment.interfaces.PaymentActionCallback;
import com.carecloud.shamrocksdk.payment.interfaces.PaymentRequestCallback;
import com.carecloud.shamrocksdk.payment.models.PaymentRequest;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lmenendez on 3/28/17.
 */

public class CloverPaymentAdapter {

    private PaymentsModel paymentsModel;
    private BaseActivity activity;
    private String appointmentId;
    private PaymentConfirmationInterface callback;
    private UserPracticeDTO practiceInfo;
    private String userId;

    /**
     * Constructor
     * @param activity Activity
     * @param paymentsModel payment model
     */
    public CloverPaymentAdapter(BaseActivity activity, PaymentsModel paymentsModel, String appointmentId, PaymentConfirmationInterface callback){
        this.activity = activity;
        this.paymentsModel = paymentsModel;
        this.appointmentId = appointmentId;
        this.callback = callback;
        practiceInfo = paymentsModel.getPaymentPayload().getUserPractices().get(0);
        userId = activity.getApplicationMode().getUserPracticeDTO().getUserId();
    }

    /**
     * Set Generic Payment
     * @param amount amount to pay
     */
    public void setCloverPayment(double amount){
        IntegratedPaymentLineItem paymentLineItem = new IntegratedPaymentLineItem();
        paymentLineItem.setAmount(amount);
        paymentLineItem.setItemType(IntegratedPaymentLineItem.TYPE_UNAPPLIED);
        paymentLineItem.setDescription("Unapplied Amount");

        IntegratedPaymentPostModel paymentPostModel = new IntegratedPaymentPostModel();
        paymentPostModel.setExecution(IntegratedPaymentPostModel.EXECUTION_PAYEEZY);
        paymentPostModel.setAmount(amount);
        paymentPostModel.addLineItem(paymentLineItem);

        IntegratedPaymentMetadata postModelMetadata = paymentPostModel.getMetadata();
        postModelMetadata.setAppointmentId(appointmentId);

        setCloverPayment(paymentPostModel);

    }

    /**
     * Set Applied Payment
     * @param postModel payment model for payment application
     */
    public void setCloverPayment(IntegratedPaymentPostModel postModel) {
        initCloverPayment(postModel);

//        Intent intent = new Intent();
//        intent.setAction(CarePayConstants.CLOVER_PAYMENT_INTENT);
//
//        double paymentAmount = postModel.getAmount();
//        intent.putExtra(CarePayConstants.CLOVER_PAYMENT_AMOUNT, paymentAmount);
//
//        if(appointmentId != null) {
//            intent.putExtra(CarePayConstants.APPOINTMENT_ID, appointmentId);
//        }
//
//        Gson gson = new Gson();
//        String paymentTransitionString = gson.toJson(paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getMakePayment());
//        intent.putExtra(CarePayConstants.CLOVER_PAYMENT_TRANSITION, paymentTransitionString);
//
//        String queueTransitionString = gson.toJson(paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getQueuePayment());
//        intent.putExtra(CarePayConstants.CLOVER_QUEUE_PAYMENT_TRANSITION, queueTransitionString);
//
//
//        String paymentMetadata = gson.toJson(paymentsModel.getPaymentPayload().getPatientBalances().get(0));
//        intent.putExtra(CarePayConstants.CLOVER_PAYMENT_METADATA, paymentMetadata);
//
//        if (postModel.getAmount() > 0) {
//            String paymentPostModelString = gson.toJson(postModel);
//            intent.putExtra(CarePayConstants.CLOVER_PAYMENT_POST_MODEL, paymentPostModelString);
//        }
//
//        List<PaymentLineItem> paymentLineItems = new ArrayList<>();
//        for (IntegratedPaymentLineItem lineItem : postModel.getLineItems()) {
//            PaymentLineItem paymentLineItem = new PaymentLineItem();
//            paymentLineItem.setAmount(lineItem.getAmount());
//            paymentLineItem.setDescription(lineItem.getDescription());
//
//            PaymentLineItemMetadata metadata = new PaymentLineItemMetadata();
//            metadata.setPatientID(paymentsModel.getPaymentPayload().getPaymentSettings().get(0).getMetadata().getPatientId());
//            metadata.setPracticeID(paymentsModel.getPaymentPayload().getPaymentSettings().get(0).getMetadata().getPracticeId());
//            metadata.setLocationID(lineItem.getLocationID());
//            metadata.setProviderID(lineItem.getProviderID());
//            paymentLineItem.setMetadata(metadata);
//
//            paymentLineItems.add(paymentLineItem);
//
//        }
//        intent.putExtra(CarePayConstants.CLOVER_PAYMENT_LINE_ITEMS, gson.toJson(paymentLineItems));
//        activity.startActivityForResult(intent, CarePayConstants.CLOVER_PAYMENT_INTENT_REQUEST_CODE, new Bundle());
//
//        String[] params = {activity.getString(R.string.param_payment_amount), activity.getString(R.string.param_payment_type)};
//        Object[] values = {postModel.getAmount(), activity.getString(R.string.payment_clover)};
//        MixPanelUtil.logEvent(activity.getString(R.string.event_payment_started), params, values);
//
    }

    private void initCloverPayment(IntegratedPaymentPostModel postModel){
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("patient_id", practiceInfo.getPatientId());

        ShamrockPaymentsPostModel shamrockPaymentsPostModel = new ShamrockPaymentsPostModel().setIntegratedPaymentPostModel(postModel);
        shamrockPaymentsPostModel.setOrganizationId(paymentsModel.getPaymentPayload().getOrganizationId());
        shamrockPaymentsPostModel.setPaymentProfileId(paymentsModel.getPaymentPayload().getPaymentProfileId());
        shamrockPaymentsPostModel.setExecution(IntegratedPaymentPostModel.EXECUTION_CLOVER);

        ShamrockPaymentMetadata metadata = shamrockPaymentsPostModel.getMetadata();
        metadata.setPracticeId(practiceInfo.getPracticeId());
        metadata.setPracticeMgmt(practiceInfo.getPracticeMgmt());
        metadata.setPatientId(practiceInfo.getPatientId());
        metadata.setUserId(userId);

        if (activity.getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE) {
            metadata.setBreezeUserId(activity.getAppAuthorizationHelper().getPatientUser());
        } else {
            metadata.setBreezeUserId(userId);
        }

        TransitionDTO initializePaymentRequest = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getInitializePaymentRequest();
        String payload = DtoHelper.getStringDTO(shamrockPaymentsPostModel);
        activity.getWorkflowServiceHelper().execute(initializePaymentRequest, initPaymentRequestCallback, payload, queryMap);

    }

    private WorkflowServiceCallback initPaymentRequestCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            activity.showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            activity.hideProgressDialog();
            ShamrockPaymentModel shamrockPaymentModel = DtoHelper.getConvertedDTO(ShamrockPaymentModel.class, workflowDTO);
            ShamrockPaymentsPostModel postModel = shamrockPaymentModel.getPayload().getPostModel();

            DevicePayment.handlePaymentRequest(activity,
                    userId,
                    activity.getAppAuthorizationHelper().getAccessToken(),
                    postModel.getDeepstreamId(),
                    paymentRequestCallback,
                    paymentActionCallback);

            String[] params = {activity.getString(R.string.param_payment_amount), activity.getString(R.string.param_payment_type)};
            Object[] values = {postModel.getAmount(), activity.getString(R.string.payment_clover)};
            MixPanelUtil.logEvent(activity.getString(R.string.event_payment_started), params, values);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            activity.hideProgressDialog();
            activity.showErrorNotification(exceptionMessage);
        }
    };

    private PaymentRequestCallback paymentRequestCallback = new PaymentRequestCallback() {
        @Override
        public void onPaymentRequestUpdate(String paymentRequestId, PaymentRequest paymentRequest) {

        }

        @Override
        public void onPaymentRequestUpdateFail(String paymentRequestId, JsonElement jsonElement) {

        }

        @Override
        public void onPaymentConnectionFailure(String message) {

        }

        @Override
        public void onPaymentRequestDestroyed(String paymentRequestId) {

        }
    };

    private PaymentActionCallback paymentActionCallback = new PaymentActionCallback() {
        @Override
        public void onPaymentStarted(String paymentRequestId) {

        }

        @Override
        public void onPaymentComplete(String paymentRequestId, JsonElement paymentPayload) {
            postPaymentRequest(paymentRequestId, paymentPayload);

        }

        @Override
        public void onPaymentCompleteWithError(String paymentRequestId, JsonElement paymentPayload, String errorMessage) {
            postPaymentRequest(paymentRequestId, paymentPayload);

        }

        @Override
        public void onPaymentCanceled(String paymentRequestId, String message) {
            activity.showErrorNotification(message);
        }

        @Override
        public void onPaymentFailed(String paymentRequestId, String message) {
            activity.showErrorNotification(message);
        }

        @Override
        public void onConnectionFailed(String message) {
            activity.showErrorNotification(message);
        }
    };

    private void postPaymentRequest(String paymentRequestId, JsonElement paymentPayload){
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("x-api-key", HttpConstants.getPaymentsApiKey());

        RestCallServiceHelper restCallServiceHelper = new RestCallServiceHelper(activity.getAppAuthorizationHelper(), activity.getApplicationMode());
        restCallServiceHelper.executeRequest(RestDef.POST,
                HttpConstants.getPaymentsUrl(),
                getPostPaymentCallback(paymentRequestId),
                null,
                headerMap,
                new Gson().toJson(paymentPayload),
                "carepay", "payments");
    }

    private RestCallServiceCallback getPostPaymentCallback(final String paymentRequestId) {
        return new RestCallServiceCallback() {
            @Override
            public void onPreExecute() {
                activity.showProgressDialog();
            }

            @Override
            public void onPostExecute(JsonElement jsonElement) {
                activity.hideProgressDialog();
                postCompletedPayment(paymentRequestId, jsonElement);
            }

            @Override
            public void onFailure(String errorMessage) {
                activity.hideProgressDialog();
                activity.showErrorNotification(errorMessage);
            }
        };
    }

    private void postCompletedPayment(String paymentRequestId, JsonElement recordObject) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("patient_id", practiceInfo.getPatientId());
        queryMap.put("deepstream_record_id", paymentRequestId);

        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getRecordPayment();
        activity.getWorkflowServiceHelper().execute(transitionDTO, getPostPaymentCallback(paymentRequestId, recordObject), queryMap);
    }

    private WorkflowServiceCallback getPostPaymentCallback(final String paymentRequestId, final JsonElement recordObject) {
        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                activity.showProgressDialog();
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                activity.hideProgressDialog();
                callback.showPaymentConfirmation(workflowDTO);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                activity.hideProgressDialog();

                queuePayment(paymentRequestId);

                Gson gson = new Gson();
                IntegratedPatientPaymentPayload patientPaymentPayload = gson.fromJson(recordObject, IntegratedPatientPaymentPayload.class);
                PatientPaymentsDTO patientPayment = new PatientPaymentsDTO();
                patientPayment.setPayload(patientPaymentPayload);
                paymentsModel.getPaymentPayload().setPatientPayments(patientPayment);
                WorkflowDTO workflowDTO = DtoHelper.getConvertedDTO(WorkflowDTO.class, DtoHelper.getStringDTO(paymentsModel));
                callback.showPaymentConfirmation(workflowDTO);
            }
        };
    }

    private void queuePayment(String paymentRequestId) {
        IntegratedPaymentQueueRecord paymentQueueRecord = new IntegratedPaymentQueueRecord();
        paymentQueueRecord.setPracticeID(practiceInfo.getPracticeId());
        paymentQueueRecord.setPracticeMgmt(practiceInfo.getPracticeMgmt());
        paymentQueueRecord.setPatientID(practiceInfo.getPatientId());
        paymentQueueRecord.setDeepstreamId(paymentRequestId);
        paymentQueueRecord.setUsername(userId);

        Gson gson = new Gson();
        paymentQueueRecord.setQueueTransition(gson.toJson(paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getQueuePayment()));

        paymentQueueRecord.save();

        Intent intent = new Intent(activity, IntegratedPaymentsQueueUploadService.class);
        activity.startService(intent);

    }

}
