package com.carecloud.carepaylibray.payments.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentModel;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentPayload;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
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
     * @param paymentsDTO    payment model
     * @param paymentPlanDTO payment plan
     */
    public static EditOneTimePaymentDialog newInstance(PaymentsModel paymentsDTO,
                                                       PaymentPlanDTO paymentPlanDTO,
                                                       ScheduledPaymentModel scheduledPaymentModel) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsDTO);
        DtoHelper.bundleDto(args, paymentPlanDTO);
        DtoHelper.bundleDto(args, scheduledPaymentModel);
        EditOneTimePaymentDialog dialog = new EditOneTimePaymentDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getContentLayout(), container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
        if (args != null) {
            paymentsDTO = DtoHelper.getConvertedDTO(PaymentsModel.class, args);
            paymentPlanDTO = DtoHelper.getConvertedDTO(PaymentPlanDTO.class, args);
            scheduledPaymentModel = DtoHelper.getConvertedDTO(ScheduledPaymentModel.class, args);
        }
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
        schedulePaymentDateText.setText(DateUtil.getInstance().setDateRaw(dateString)
                .toStringWithFormatMmSlashDdSlashYyyy());
        schedulePaymentDateText.setOnClickListener(selectDateButtonListener);
        schedulePaymentDateText.setCompoundDrawablesWithIntrinsicBounds(null, null,
                ContextCompat.getDrawable(getContext(), R.drawable.icon_drop_down), null);

        amountEditText = (EditText) findViewById(R.id.enterPartialAmountEditText);
        amountEditText.setText(String.valueOf(scheduledPaymentModel.getPayload().getAmount()));

        View deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemUtil.hideSoftKeyboard(getContext(), view);
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
        confirmDialogFragment.setNegativeAction(true);
        confirmDialogFragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showDialog();
            }
        });
        confirmDialogFragment.show(((AppCompatActivity) getContext()).getSupportFragmentManager(), null);
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

            TransitionDTO transitionDTO = paymentsDTO.getPaymentsMetadata()
                    .getPaymentsTransitions().getDeleteScheduledPayment();
            ((ISession) getContext()).getWorkflowServiceHelper().execute(transitionDTO,
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
            ((ISession) getContext()).getWorkflowServiceHelper().execute(transitionDTO,
                    editScheduledPaymentCallback, payload, queryMap);
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            Toast.makeText(getContext(), "Please enter valid amount!", Toast.LENGTH_LONG).show();
        }
    }

    private WorkflowServiceCallback deleteScheduledPaymentCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ((ISession) getContext()).showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ((ISession) getContext()).hideProgressDialog();
            dismiss();
            callback.showDeleteScheduledPaymentConfirmation(workflowDTO, scheduledPaymentModel.getPayload());
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ((ISession) getContext()).hideProgressDialog();
            ((ISession) getContext()).showErrorNotification(exceptionMessage);

        }
    };

    private WorkflowServiceCallback editScheduledPaymentCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ((ISession) getContext()).showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ((ISession) getContext()).hideProgressDialog();
            callback.showScheduledPaymentConfirmation(workflowDTO);
            dismiss();
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ((ISession) getContext()).hideProgressDialog();
            ((ISession) getContext()).showErrorNotification(exceptionMessage);

        }
    };

    @Override
    protected void onPendingAmountValidation(String amountEditText,
                                             Button payPartialButton,
                                             TextView partialPaymentTotalAmountTitle) {
        super.onPendingAmountValidation(amountEditText, payPartialButton, partialPaymentTotalAmountTitle);

        validatePaymentRescheduled();
    }

    @Override
    protected void setSelectedDate(Date selectedDate) {
        super.setSelectedDate(selectedDate);
        paymentButton.setText(Label.getLabel("payment_plan_reschedule_payment_short"));
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