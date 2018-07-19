package com.carecloud.carepaylibray.payments.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.payments.interfaces.OneTimePaymentInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentModel;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by lmenendez on 2/5/18
 */

public class OneTimePaymentDialog extends PartialPaymentDialog {

    private PaymentPlanDTO paymentPlanDTO;
    private PaymentsModel paymentsDTO;
    private Context context;
    private OneTimePaymentInterface callback;

    private Date paymentDate;
    private EditText schedulePaymentDateText;
    private Button paymentButton;

    /**
     * Contructor
     *
     * @param context        context must implement PayNowClickListener
     * @param paymentsDTO    payment model
     * @param paymentPlanDTO payment plan
     */
    public OneTimePaymentDialog(Context context,
                                PaymentsModel paymentsDTO,
                                PaymentPlanDTO paymentPlanDTO,
                                OneTimePaymentInterface callback) {
        super(context, paymentsDTO, null);
        this.context = context;
        this.paymentsDTO = paymentsDTO;
        this.paymentPlanDTO = paymentPlanDTO;
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DateUtil.getInstance().setDate(new Date());
        paymentDate = DateUtil.getInstance().getDate();
    }

    @Override
    protected void initViews(){
        setContentView(R.layout.dialog_one_time_payment);
        super.initViews();
        paymentButton = (Button) findViewById(R.id.payPartialButton);
        paymentButton.setText(Label.getLabel("payment_Pay_label"));

        schedulePaymentDateText = (EditText) findViewById(R.id.schedulePaymentDateEditText);
        schedulePaymentDateText.setText(Label.getLabel("today_label"));
        TextInputLayout inputLayout = (TextInputLayout) findViewById(R.id.schedulePaymentDateInputLayout);
        schedulePaymentDateText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(inputLayout, null));
        schedulePaymentDateText.getOnFocusChangeListener().onFocusChange(schedulePaymentDateText, true);

        ScheduledPaymentModel scheduledPayment = paymentsDTO.getPaymentPayload().
                findScheduledPayment(paymentPlanDTO);
        if(scheduledPayment == null) {//only allow scheduling payment if there is not one already scheduled
            schedulePaymentDateText.setOnClickListener(selectDateButtonListener);
        }

        TextView paymentHeader = (TextView) findViewById(R.id.partialPaymentHeader);
        String maxAmount = Label.getLabel("payment_partial_maximum_amount") +
                currencyFormat.format(calculateFullAmount());
        paymentHeader.setText(maxAmount);
    }

    @Override
    protected double getMinimumPayment(String practiceId) {
        return 0D;
    }

    @Override
    protected double calculateFullAmount() {
        return SystemUtil.safeSubtract(paymentPlanDTO.getPayload().getAmount(),
                paymentPlanDTO.getPayload().getAmountPaid());
    }

    @Override
    protected void onPaymentClick(EditText enterPartialAmountEditText) {
        try {
            double amount = Double.parseDouble(enterPartialAmountEditText.getText().toString());
            createPaymentModel(amount);
            if(DateUtil.isSameDay(paymentDate, new Date())) {
                callback.onStartOneTimePayment(paymentsDTO, paymentPlanDTO);
            }else{
                callback.onScheduleOneTimePayment(paymentsDTO, paymentPlanDTO, paymentDate);
            }
            dismiss();
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            Toast.makeText(context, "Please enter valid amount!", Toast.LENGTH_LONG).show();
        }
    }

    private void createPaymentModel(double amount) {
        IntegratedPaymentPostModel postModel = paymentsDTO.getPaymentPayload().getPaymentPostModel();
        if (postModel == null) {
            postModel = new IntegratedPaymentPostModel();
        }
        postModel.setAmount(amount);

        paymentsDTO.getPaymentPayload().setPaymentPostModel(postModel);
    }

    private View.OnClickListener selectDateButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            // Use the current date as the default date in the picker
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(paymentDate);

            DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                    android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, month, day);

                            DateUtil.getInstance().setDate(calendar);
                            paymentDate = DateUtil.getInstance().getDate();
                            if(DateUtil.isSameDay(paymentDate, new Date())){
                                schedulePaymentDateText.setText(Label.getLabel("today_label"));
                                paymentButton.setText(Label.getLabel("payment_Pay_label"));
                            }else {
                                schedulePaymentDateText.setText(DateUtil.getInstance().toStringWithFormatMmSlashDdSlashYyyy());
                                paymentButton.setText(Label.getLabel("payment_plan_schedule_payment"));
                            }
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            TextView title = new TextView(context);
            title.setText(Label.getLabel("payment.oneTimePayment.input.label.date"));
            title.setPadding(10,10,10,10);
            title.setTextColor(ContextCompat.getColor(context, R.color.textview_default_textcolor));
            datePickerDialog.setCustomTitle(title);

            int paymentDueDay = paymentPlanDTO.getPayload().getPaymentPlanDetails().getDayOfMonth();
            Calendar dueCal = Calendar.getInstance();
            dueCal.set(Calendar.DAY_OF_MONTH, paymentDueDay);
            int monthsRemaining = paymentPlanDTO.getPayload().getPaymentPlanDetails().getInstallments() -
                    paymentPlanDTO.getPayload().getPaymentPlanDetails().getFilteredHistory().size();
            dueCal.add(Calendar.MONTH, monthsRemaining);

            datePickerDialog.getDatePicker().setMaxDate(dueCal.getTimeInMillis());
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.getDatePicker().setCalendarViewShown(false);
            datePickerDialog.show();
        }
    };

}
