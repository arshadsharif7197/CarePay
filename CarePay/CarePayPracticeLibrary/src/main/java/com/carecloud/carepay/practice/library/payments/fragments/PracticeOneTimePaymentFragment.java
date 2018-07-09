package com.carecloud.carepay.practice.library.payments.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.payments.interfaces.OneTimePaymentInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentModel;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by lmenendez on 2/8/18
 */

public class PracticeOneTimePaymentFragment extends PracticePartialPaymentDialogFragment {

    protected PaymentPlanDTO paymentPlanDTO;
    protected OneTimePaymentInterface callback;

    protected Date paymentDate;
    protected EditText schedulePaymentDateText;
    protected long minDate;

    /**
     * @param paymentResultModel the payment model
     * @param owedAmount         fullAmount owed
     * @return an instance of PracticePartialPaymentDialogFragment
     */
    public static PracticeOneTimePaymentFragment newInstance(PaymentsModel paymentResultModel, double owedAmount, PaymentPlanDTO paymentPlanDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentResultModel);
        DtoHelper.bundleDto(args, paymentPlanDTO);
        args.putDouble(KEY_FULL_AMOUNT, owedAmount);
        PracticeOneTimePaymentFragment fragment = new PracticeOneTimePaymentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (OneTimePaymentInterface) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Provided context must implement OneTimePaymentInterface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.dialog_fragment_one_time_payment, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        paymentPlanDTO = DtoHelper.getConvertedDTO(PaymentPlanDTO.class, arguments);
        fullAmount = calculateFullAmount();
        DateUtil.getInstance().setDate(new Date());
        paymentDate = DateUtil.getInstance().getDate();
        minDate = System.currentTimeMillis();
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        applyButton.setText(Label.getLabel("payment_Pay_label"));

        TextView pendingAmountTextView = (TextView) view.findViewById(R.id.pendingAmountTextView);
        pendingAmountTextView.setVisibility(View.GONE);

        schedulePaymentDateText = (EditText) findViewById(R.id.schedulePaymentDateEditText);
        schedulePaymentDateText.setText(Label.getLabel("today_label"));
        TextInputLayout inputLayout = (TextInputLayout) findViewById(R.id.schedulePaymentDateInputLayout);
        schedulePaymentDateText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(inputLayout, null));
        schedulePaymentDateText.getOnFocusChangeListener().onFocusChange(schedulePaymentDateText, true);

        ScheduledPaymentModel scheduledPayment = paymentsModel.getPaymentPayload().
                findScheduledPayment(paymentPlanDTO);
        if(scheduledPayment == null) {//only allow scheduling payment if there is not one already scheduled
            schedulePaymentDateText.setOnClickListener(selectDateButtonListener);
        }

        TextView paymentHeader = (TextView) findViewById(R.id.partialPaymentHeader);
        String maxAmount = Label.getLabel("payment_partial_maximum_amount") +
                currencyFormat.format(fullAmount);
        paymentHeader.setText(maxAmount);
    }

    @Override
    protected double getMinimumPayment(){
        return 0D;
    }

    protected double calculateFullAmount() {
        return SystemUtil.safeSubtract(paymentPlanDTO.getPayload().getAmount(), paymentPlanDTO.getPayload().getAmountPaid());
    }

    @Override
    protected void onPaymentClick(double amount) {
        createPaymentModel(amount);
        if(DateUtil.isSameDay(paymentDate, new Date())) {
            callback.onStartOneTimePayment(paymentsModel, paymentPlanDTO);
        }else{
            callback.onScheduleOneTimePayment(paymentsModel, paymentPlanDTO, paymentDate);
        }
        dismiss();
    }

    protected void createPaymentModel(double amount){
        IntegratedPaymentPostModel postModel = paymentsModel.getPaymentPayload().getPaymentPostModel();
        if (postModel == null) {
            postModel = new IntegratedPaymentPostModel();
        }
        postModel.setAmount(amount);

        paymentsModel.getPaymentPayload().setPaymentPostModel(postModel);
    }

    @Override
    protected void updateLayout() {
        super.updateLayout();
        double entry = StringUtil.isNullOrEmpty(numberStr) ? 0D : Double.parseDouble(numberStr);
        applyButton.setEnabled(entry > 0D && entry < fullAmount);
    }


    protected View.OnClickListener selectDateButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            // Use the current date as the default date in the picker
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(paymentDate);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
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
                                applyButton.setText(Label.getLabel("payment_Pay_label"));
                            }else {
                                schedulePaymentDateText.setText(DateUtil.getInstance().toStringWithFormatMmSlashDdSlashYyyy());
                                applyButton.setText(Label.getLabel("payment_plan_schedule_payment"));
                            }
                            showDialog();
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            TextView title = new TextView(getContext());
            title.setText(Label.getLabel("payment.oneTimePayment.input.label.date"));
            title.setPadding(10,10,10,10);
            title.setTextColor(ContextCompat.getColor(getContext(), R.color.textview_default_textcolor));
            datePickerDialog.setCustomTitle(title);

            int paymentDueDay = paymentPlanDTO.getPayload().getPaymentPlanDetails().getDayOfMonth();
            Calendar dueCal = Calendar.getInstance();
            dueCal.set(Calendar.DAY_OF_MONTH, paymentDueDay);
            int monthsRemaining = paymentPlanDTO.getPayload().getPaymentPlanDetails().getInstallments() -
                    paymentPlanDTO.getPayload().getPaymentPlanDetails().getFilteredHistory().size();
            dueCal.add(Calendar.MONTH, monthsRemaining);

            datePickerDialog.getDatePicker().setMaxDate(dueCal.getTimeInMillis());
            datePickerDialog.getDatePicker().setMinDate(minDate);
            datePickerDialog.getDatePicker().setCalendarViewShown(false);
            datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    showDialog();
                }
            });
            datePickerDialog.show();
            hideDialog();
        }
    };

}
