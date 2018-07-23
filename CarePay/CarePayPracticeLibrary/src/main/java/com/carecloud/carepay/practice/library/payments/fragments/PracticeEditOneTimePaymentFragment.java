package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.common.ConfirmationCallback;
import com.carecloud.carepaylibray.demographics.fragments.ConfirmDialogFragment;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentModel;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentPayload;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PracticeEditOneTimePaymentFragment extends PracticeOneTimePaymentFragment {

    private ScheduledPaymentModel scheduledPaymentModel;

    /**
     * @param paymentResultModel the payment model
     * @param owedAmount         fullAmount owed
     * @param paymentPlanDTO     payment plan dto
     * @param scheduledPaymentModel scheduled payment
     * @return an instance of PracticeEditOneTimePaymentFragment
     */
    public static PracticeEditOneTimePaymentFragment newInstance(PaymentsModel paymentResultModel,
                                                                 double owedAmount,
                                                                 PaymentPlanDTO paymentPlanDTO,
                                                                 ScheduledPaymentModel scheduledPaymentModel) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentResultModel);
        DtoHelper.bundleDto(args, paymentPlanDTO);
        DtoHelper.bundleDto(args, scheduledPaymentModel);
        args.putDouble(KEY_FULL_AMOUNT, owedAmount);
        PracticeEditOneTimePaymentFragment fragment = new PracticeEditOneTimePaymentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.dialog_fragment_edit_one_time_payment, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        scheduledPaymentModel = DtoHelper.getConvertedDTO(ScheduledPaymentModel.class, args);

        String dateString = scheduledPaymentModel.getPayload().getPaymentDate();
        paymentDate = DateUtil.getInstance().setDateRaw(dateString).getDate();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        minDate = calendar.getTimeInMillis();
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);

        applyButton.setText(Label.getLabel("payment_plan_reschedule_payment"));
        String dateString = scheduledPaymentModel.getPayload().getPaymentDate();
        schedulePaymentDateText.setText(DateUtil.getInstance().setDateRaw(dateString).toStringWithFormatMmSlashDdSlashYyyy());
        schedulePaymentDateText.setOnClickListener(selectDateButtonListener);

        numberStr = String.valueOf(scheduledPaymentModel.getPayload().getAmount());
        char last = numberStr.charAt(numberStr.length()-1);
        while(last == '0' || last == '.'){
            numberStr = numberStr.substring(0, numberStr.length()-1);
            last = numberStr.charAt(numberStr.length()-1);
        }
        amountTextView = (EditText) findViewById(R.id.enter_amount_text);
        amountTextView.setText(currencyFormat.format(scheduledPaymentModel.getPayload().getAmount()));

        View deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePayment();
            }
        });

        updateLayout();
    }

    private void deletePayment(){
        ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance(
                Label.getLabel("payment.oneTimePayment.scheduled.delete.title"),
                Label.getLabel("payment.oneTimePayment.scheduled.delete.subtitle"),
                Label.getLabel("button_no"),
                Label.getLabel("button_yes"));
        confirmDialogFragment.setCallback(confirmDeleteCallback);
        confirmDialogFragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showDialog();
            }
        });
        confirmDialogFragment.show(getFragmentManager(), null);
        hideDialog();
    }

    private ConfirmationCallback confirmDeleteCallback = new ConfirmationCallback() {
        @Override
        public void onConfirm() {
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("practice_mgmt", scheduledPaymentModel.getMetadata().getPracticeMgmt());
            queryMap.put("practice_id", scheduledPaymentModel.getMetadata().getPracticeId());
            queryMap.put("patient_id", scheduledPaymentModel.getMetadata().getPatientId());
            queryMap.put("one_time_payment_id", scheduledPaymentModel.getMetadata().getOneTimePaymentId());

            TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata()
                    .getPaymentsTransitions().getDeleteScheduledPayment();
            getWorkflowServiceHelper().execute(transitionDTO, deleteScheduledPaymentCallback, queryMap);
        }
    };

    @Override
    protected void onPaymentClick(double amount) {
        createPaymentModel(amount);

        IntegratedPaymentPostModel postModel = paymentsModel.getPaymentPayload().getPaymentPostModel();
        postModel.setPapiPaymentMethod(scheduledPaymentModel.getPayload().getPaymentMethod());
        postModel.setExecution(scheduledPaymentModel.getPayload().getExecution());

        DateUtil.getInstance().setDate(paymentDate);
        postModel.setPaymentDate(DateUtil.getInstance().toStringWithFormatYyyyDashMmDashDd());

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("practice_mgmt", scheduledPaymentModel.getMetadata().getPracticeMgmt());
        queryMap.put("practice_id", scheduledPaymentModel.getMetadata().getPracticeId());
        queryMap.put("patient_id", scheduledPaymentModel.getMetadata().getPatientId());
        queryMap.put("one_time_payment_id", scheduledPaymentModel.getMetadata().getOneTimePaymentId());

        Gson gson = new Gson();
        String payload = gson.toJson(postModel);

        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata()
                .getPaymentsTransitions().getUpdateScheduledPayment();
        getWorkflowServiceHelper().execute(transitionDTO, editScheduledPaymentCallback, payload, queryMap);
    }

    private WorkflowServiceCallback deleteScheduledPaymentCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            callback.showDeleteScheduledPaymentConfirmation(workflowDTO);
            dismiss();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);

        }
    };

    private WorkflowServiceCallback editScheduledPaymentCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            callback.showScheduledPaymentConfirmation(workflowDTO);
            dismiss();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);

        }
    };

    @Override
    protected void updateLayout() {
        super.updateLayout();
        validatePaymentRescheduled();
    }

    @Override
    protected void setSelectedDate(Date selectedDate) {
        super.setSelectedDate(selectedDate);
        applyButton.setText(Label.getLabel("payment_plan_reschedule_payment"));
        validatePaymentRescheduled();
    }

    private void validatePaymentRescheduled() {
        String amountText = numberStr;
        if (amountText != null && amountText.length() > 0) {
            if (amountText.length() == 1 && amountText.equalsIgnoreCase(".")) {
                amountText = "0.";
            }

            ScheduledPaymentPayload scheduledPayload = scheduledPaymentModel.getPayload();
            Date originalDate = DateUtil.getInstance().setDateRaw(scheduledPayload.getPaymentDate()).getDate();
            double originalAmount = scheduledPayload.getAmount();

            double amountPay = Double.parseDouble(amountText);
            applyButton.setEnabled(paymentDate != null &&
                    (!DateUtil.isSameDay(originalDate, paymentDate) ||
                            originalAmount != amountPay));

        }

    }

}
