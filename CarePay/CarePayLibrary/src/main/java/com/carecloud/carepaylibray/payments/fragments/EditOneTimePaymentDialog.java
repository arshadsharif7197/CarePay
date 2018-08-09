package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.common.ConfirmationCallback;
import com.carecloud.carepaylibray.demographics.fragments.ConfirmDialogFragment;
import com.carecloud.carepaylibray.payments.interfaces.OneTimePaymentInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentModel;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentPayload;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditOneTimePaymentDialog extends OneTimePaymentDialog {

    private ScheduledPaymentModel scheduledPaymentModel;
    private EditText amountEditText;

    /**
     * Contructor
     *
     * @param context        context must implement PayNowClickListener
     * @param paymentsDTO    payment model
     * @param paymentPlanDTO payment plan
     * @param callback
     */
    public EditOneTimePaymentDialog(Context context, PaymentsModel paymentsDTO, PaymentPlanDTO paymentPlanDTO, ScheduledPaymentModel scheduledPaymentModel, OneTimePaymentInterface callback) {
        super(context, paymentsDTO, paymentPlanDTO, callback);
        this.scheduledPaymentModel = scheduledPaymentModel;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        String dateString = scheduledPaymentModel.getPayload().getPaymentDate();
        paymentDate = DateUtil.getInstance().setDateRaw(dateString).getDate();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        minDate = calendar.getTimeInMillis();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.dialog_edit_one_time_payment;
    }

    @Override
    protected void initViews() {
        super.initViews();

        paymentButton.setText(Label.getLabel("payment_plan_reschedule_payment_short"));
        String dateString = scheduledPaymentModel.getPayload().getPaymentDate();
        schedulePaymentDateText.setText(DateUtil.getInstance().setDateRaw(dateString).toStringWithFormatMmSlashDdSlashYyyy());
        schedulePaymentDateText.setOnClickListener(selectDateButtonListener);

        amountEditText = (EditText) findViewById(R.id.enterPartialAmountEditText);
        amountEditText.setText(String.valueOf(scheduledPaymentModel.getPayload().getAmount()));

        View deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemUtil.hideSoftKeyboard(context, view);
                deletePayment();
            }
        });

    }

    private void deletePayment() {
        ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance(
                Label.getLabel("payment.oneTimePayment.scheduled.delete.title"),
                String.format(
                        Label.getLabel("payment.oneTimePayment.scheduled.delete.subtitle"),
                        StringUtil.getFormattedBalanceAmount(scheduledPaymentModel.getPayload().getAmount()),
                        DateUtil.getInstance()
                                .setDateRaw(scheduledPaymentModel.getPayload().getPaymentDate())
                                .toStringWithFormatMmSlashDdSlashYyyy()),
                Label.getLabel("button_no"),
                Label.getLabel("button_yes"));
        confirmDialogFragment.setCallback(confirmDeleteCallback);
        confirmDialogFragment.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                show();
            }
        });
        confirmDialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), null);
        hide();
    }

    private ConfirmationCallback confirmDeleteCallback = new ConfirmationCallback() {
        @Override
        public void onConfirm() {
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("practice_mgmt", scheduledPaymentModel.getMetadata().getPracticeMgmt());
            queryMap.put("practice_id", scheduledPaymentModel.getMetadata().getPracticeId());
            queryMap.put("patient_id", scheduledPaymentModel.getMetadata().getPatientId());
            queryMap.put("one_time_payment_id", scheduledPaymentModel.getMetadata().getOneTimePaymentId());

            TransitionDTO transitionDTO = paymentsDTO.getPaymentsMetadata()
                    .getPaymentsTransitions().getDeleteScheduledPayment();
            ((ISession) context).getWorkflowServiceHelper().execute(transitionDTO,
                    deleteScheduledPaymentCallback, queryMap);
        }
    };


    @Override
    protected void onPaymentClick(EditText enterPartialAmountEditText) {
        try {
            double amount = Double.parseDouble(enterPartialAmountEditText.getText().toString());
            createPaymentModel(amount);

            IntegratedPaymentPostModel postModel = paymentsDTO.getPaymentPayload().getPaymentPostModel();
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

            TransitionDTO transitionDTO = paymentsDTO.getPaymentsMetadata()
                    .getPaymentsTransitions().getUpdateScheduledPayment();
            ((ISession) context).getWorkflowServiceHelper().execute(transitionDTO,
                    editScheduledPaymentCallback, payload, queryMap);
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            Toast.makeText(context, "Please enter valid amount!", Toast.LENGTH_LONG).show();
        }
    }

    private WorkflowServiceCallback deleteScheduledPaymentCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ((ISession) context).showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ((ISession) context).hideProgressDialog();
            dismiss();
            callback.showDeleteScheduledPaymentConfirmation(workflowDTO, scheduledPaymentModel.getPayload());
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ((ISession) context).hideProgressDialog();
            ((ISession) context).showErrorNotification(exceptionMessage);

        }
    };

    private WorkflowServiceCallback editScheduledPaymentCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ((ISession) context).showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ((ISession) context).hideProgressDialog();
            callback.showScheduledPaymentConfirmation(workflowDTO);
            dismiss();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ((ISession) context).hideProgressDialog();
            ((ISession) context).showErrorNotification(exceptionMessage);

        }
    };

    @Override
    protected void onPendingAmountValidation(String amountEditText, Button payPartialButton, TextView partialPaymentTotalAmountTitle) {
        super.onPendingAmountValidation(amountEditText, payPartialButton, partialPaymentTotalAmountTitle);

        validatePaymentRescheduled();
    }

    @Override
    protected void setSelectedDate(Date selectedDate) {
        super.setSelectedDate(selectedDate);
        validatePaymentRescheduled();
    }

    private void validatePaymentRescheduled() {
        String amountText = amountEditText.getText().toString();
        if (amountText != null && amountText.length() > 0) {
            if (amountText.length() == 1 && amountText.equalsIgnoreCase(".")) {
                amountText = "0.";
            }

            ScheduledPaymentPayload scheduledPayload = scheduledPaymentModel.getPayload();
            Date originalDate = DateUtil.getInstance().setDateRaw(scheduledPayload.getPaymentDate()).getDate();
            double originalAmount = scheduledPayload.getAmount();

            double amountPay = Double.parseDouble(amountText);
            paymentButton.setEnabled(paymentDate != null &&
                    (!DateUtil.isSameDay(originalDate, paymentDate) ||
                            originalAmount != amountPay)
                    && amountPay <= calculateFullAmount());

        }

    }


}