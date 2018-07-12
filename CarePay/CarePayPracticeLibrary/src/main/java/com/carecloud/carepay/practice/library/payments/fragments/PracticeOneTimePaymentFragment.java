package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.customdialog.DateRangePickerDialog;
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
import com.squareup.timessquare.CalendarPickerView;

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
        if (scheduledPayment == null) {//only allow scheduling payment if there is not one already scheduled
            schedulePaymentDateText.setOnClickListener(selectDateButtonListener);
        }

        TextView paymentHeader = (TextView) findViewById(R.id.partialPaymentHeader);
        String maxAmount = Label.getLabel("payment_partial_maximum_amount") +
                currencyFormat.format(fullAmount);
        paymentHeader.setText(maxAmount);
    }

    @Override
    protected double getMinimumPayment() {
        return 0D;
    }

    protected double calculateFullAmount() {
        return SystemUtil.safeSubtract(paymentPlanDTO.getPayload().getAmount(), paymentPlanDTO.getPayload().getAmountPaid());
    }

    @Override
    protected void onPaymentClick(double amount) {
        createPaymentModel(amount);
        if (DateUtil.isSameDay(paymentDate, new Date())) {
            callback.onStartOneTimePayment(paymentsModel, paymentPlanDTO);
        } else {
            callback.onScheduleOneTimePayment(paymentsModel, paymentPlanDTO, paymentDate);
        }
        dismiss();
    }

    protected void createPaymentModel(double amount) {
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
        applyButton.setEnabled(entry > 0D && entry <= fullAmount);
    }


    protected View.OnClickListener selectDateButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            showCalendar();
        }
    };

    private void showCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(minDate);
        int paymentDueDay = paymentPlanDTO.getPayload().getPaymentPlanDetails().getDayOfMonth();
        Calendar dueCal = Calendar.getInstance();
        dueCal.set(Calendar.DAY_OF_MONTH, paymentDueDay);
        int monthsRemaining = paymentPlanDTO.getPayload().getPaymentPlanDetails().getInstallments() -
                paymentPlanDTO.getPayload().getPaymentPlanDetails().getFilteredHistory().size();
        int offset = 0;
        if (paymentDueDay >= calendar.get(Calendar.DAY_OF_MONTH)) {
            offset = 1;
        }
        dueCal.add(Calendar.MONTH, monthsRemaining - offset);
        dueCal.add(Calendar.DAY_OF_MONTH, 1);

        DateRangePickerDialog dialog = DateRangePickerDialog.newInstance(
                Label.getLabel("payment.oneTimePayment.input.label.date"),
                Label.getLabel("datepicker_cancel_option"),
                false,
                paymentDate,
                dueCal.getTime(),
                calendar.getTime(),
                dueCal.getTime(),
                new DateRangePickerDialog.DateRangePickerDialogListener() {
                    @Override
                    public void onRangeSelected(Date start, Date end) {
                        //Not Implemented
                    }

                    @Override
                    public void onDateRangeCancelled() {
                        //Not Implemented
                    }

                    @Override
                    public void onDateSelected(Date selectedDate) {
                        setSelectedDate(selectedDate);
                        showDialog();
                    }
                }, CalendarPickerView.SelectionMode.SINGLE.name());

        displayDialogFragment(dialog, false);

    }

    protected void setSelectedDate(Date selectedDate){
        paymentDate = selectedDate;
        DateUtil.getInstance().setDate(paymentDate);
        if (DateUtil.isSameDay(paymentDate, new Date())) {
            schedulePaymentDateText.setText(Label.getLabel("today_label"));
            applyButton.setText(Label.getLabel("payment_Pay_label"));
        } else {
            schedulePaymentDateText.setText(DateUtil.getInstance().toStringWithFormatMmSlashDdSlashYyyy());
            applyButton.setText(Label.getLabel("payment_plan_schedule_payment"));
        }
    }

    /**
     * Display a fragment as a Dialog
     *
     * @param fragment       must be a Dialog Fragment
     * @param addToBackStack optional flag to addFragment this transaction to back stack
     */
    public void displayDialogFragment(DialogFragment fragment, boolean addToBackStack) {
        String tag = fragment.getClass().getName();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        if (addToBackStack) {
            ft.addToBackStack(tag);
        }

        fragment.show(ft, tag);
    }

}
