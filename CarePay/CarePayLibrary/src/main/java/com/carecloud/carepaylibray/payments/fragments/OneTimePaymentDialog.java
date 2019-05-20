package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.common.DatePickerFragment;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.payments.interfaces.OneTimePaymentInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDetailsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentModel;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by lmenendez on 2/5/18
 */

public class OneTimePaymentDialog extends PartialPaymentDialog {

    protected PaymentPlanDTO paymentPlanDTO;
    protected PaymentsModel paymentsDTOpaymentsDTO;
    protected OneTimePaymentInterface callback;

    protected Date paymentDate;
    protected EditText schedulePaymentDateText;
    protected Button paymentButton;
    protected long minDate;

    /**
     * @param paymentsDTO    payment model
     * @param paymentPlanDTO payment plan
     */
    public static OneTimePaymentDialog newInstance(PaymentsModel paymentsDTO,
                                                   PaymentPlanDTO paymentPlanDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsDTO);
        DtoHelper.bundleDto(args, paymentPlanDTO);
        OneTimePaymentDialog dialog = new OneTimePaymentDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (OneTimePaymentInterface) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached Context must implement PaymentDetailInterface");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            paymentsDTO = DtoHelper.getConvertedDTO(PaymentsModel.class, args);
            paymentPlanDTO = DtoHelper.getConvertedDTO(PaymentPlanDTO.class, args);
        }
        DateUtil.getInstance().setDate(new Date());
        paymentDate = DateUtil.getInstance().getDate();
        minDate = System.currentTimeMillis();
    }

    @Override
    protected int getContentLayout() {
        return R.layout.dialog_one_time_payment;
    }

    @Override
    protected void initViews() {
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
        if (scheduledPayment == null) {//only allow scheduling payment if there is not one already scheduled
            schedulePaymentDateText.setOnClickListener(selectDateButtonListener);
        } else {
            schedulePaymentDateText.setCompoundDrawables(null, null, null, null);
        }

        TextView paymentHeader = (TextView) findViewById(R.id.partialPaymentHeader);
        String maxAmount = String.format(Label.getLabel("payment.partial.amountSelector.maximum.amount"),
                currencyFormat.format(calculateFullAmount()));
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
            if (DateUtil.isSameDay(paymentDate, new Date())) {
                callback.onStartOneTimePayment(paymentsDTO, paymentPlanDTO);
            } else {
                callback.onScheduleOneTimePayment(paymentsDTO, paymentPlanDTO, paymentDate);
            }
            dismiss();
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            Toast.makeText(getContext(), "Please enter valid amount!", Toast.LENGTH_LONG).show();
        }
    }

    protected void createPaymentModel(double amount) {
        IntegratedPaymentPostModel postModel = paymentsDTO.getPaymentPayload().getPaymentPostModel();
        if (postModel == null) {
            postModel = new IntegratedPaymentPostModel();
        }
        postModel.setAmount(amount);

        paymentsDTO.getPaymentPayload().setPaymentPostModel(postModel);
    }

    protected View.OnClickListener selectDateButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            showDatePickerFragment();
        }
    };

    private void showDatePickerFragment() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(minDate);
        Calendar dueCal = Calendar.getInstance();
        int offset = 0;
        int paymentHistoryCount = paymentPlanDTO.getPayload().getPaymentPlanDetails()
                .getFilteredHistory().size();
        if (paymentPlanDTO.getPayload().getPaymentPlanDetails().getFrequencyCode()
                .equals(PaymentPlanDetailsDTO.FREQUENCY_MONTHLY)) {
            int paymentDueDay = paymentPlanDTO.getPayload().getPaymentPlanDetails().getDayOfMonth();
            dueCal.set(Calendar.DAY_OF_MONTH, paymentDueDay);
            if (paymentHistoryCount > 0 && paymentDueDay >= calendar.get(Calendar.DAY_OF_MONTH)) {
                offset = 1;
            }
            int monthsRemaining = paymentPlanDTO.getPayload().getPaymentPlanDetails()
                    .getInstallments() - paymentHistoryCount;
            dueCal.add(Calendar.MONTH, monthsRemaining - offset);
        } else {
            //must be weekly
            int weeklyDueDay = paymentPlanDTO.getPayload().getPaymentPlanDetails().getDayOfWeek();
            if (weeklyDueDay == 7) {
                //sunday fix
                weeklyDueDay = 1;
            } else {
                weeklyDueDay += 1;
            }
            dueCal.set(Calendar.DAY_OF_WEEK, weeklyDueDay);
            if (paymentHistoryCount > 0 && weeklyDueDay >= calendar.get(Calendar.DAY_OF_WEEK)) {
                offset = 1;
            }
            int weeksRemaining = paymentPlanDTO.getPayload().getPaymentPlanDetails()
                    .getInstallments() - paymentHistoryCount;
            dueCal.add(Calendar.DAY_OF_YEAR, (weeksRemaining - offset) * 7);
        }

        DatePickerFragment fragment = DatePickerFragment
                .newInstance(Label.getLabel("payment.oneTimePayment.input.label.date"),
                        calendar.getTime(),
                        dueCal.getTime(),
                        new DatePickerFragment.DateRangePickerDialogListener() {
                            @Override
                            public void onDateSelected(Date selectedDate, int flag) {
                                setSelectedDate(selectedDate);
                            }
                        });
        SystemUtil.hideSoftKeyboard(getActivity());
        ((FragmentActivityInterface) callback).displayDialogFragment(fragment, true);
    }

    protected void setSelectedDate(Date selectedDate) {
        paymentDate = selectedDate;
        DateUtil.getInstance().setDate(paymentDate);
        if (DateUtil.isSameDay(paymentDate, new Date())) {
            schedulePaymentDateText.setText(Label.getLabel("today_label"));
            paymentButton.setText(Label.getLabel("payment_Pay_label"));
        } else {
            schedulePaymentDateText.setText(DateUtil.getInstance().toStringWithFormatMmSlashDdSlashYyyy());
            paymentButton.setText(Label.getLabel("payment_plan_schedule_payment"));
        }

    }

}
