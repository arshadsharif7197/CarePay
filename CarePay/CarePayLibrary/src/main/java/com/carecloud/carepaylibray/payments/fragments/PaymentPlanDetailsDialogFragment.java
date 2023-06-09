package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customdialogs.BasePaymentDetailsFragmentDialog;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDetailsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentModel;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.PicassoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by lmenendez on 1/26/18
 */

public abstract class PaymentPlanDetailsDialogFragment extends BasePaymentDetailsFragmentDialog {

    protected PaymentPlanDTO paymentPlanDTO;
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

    protected FragmentActivityInterface callback;
    protected View payButton;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (FragmentActivityInterface) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement PaymentPlanEditInterface");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
//        paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, args);
        paymentPlanDTO = DtoHelper.getConvertedDTO(PaymentPlanDTO.class, args);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_plan_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PaymentPlanPayloadDTO planPayload = paymentPlanDTO.getPayload();
        UserPracticeDTO userPracticeDTO = paymentsModel.getPaymentPayload()
                .getUserPractice(paymentPlanDTO.getMetadata().getPracticeId());

        TextView installmentDetail = view.findViewById(R.id.planInstallmentDetail);
        installmentDetail.setText(currencyFormatter.format(planPayload.getPaymentPlanDetails().getAmount()));

        TextView installmentFrequency = view.findViewById(R.id.planInstallmentFrequency);
        installmentFrequency.setText(planPayload.getPaymentPlanDetails().getFrequencyString());

        String practiceName = userPracticeDTO.getPracticeName();
        TextView practiceNameText = view.findViewById(R.id.practice_name);
        practiceNameText.setText(practiceName);
        TextView practiceInitials = view.findViewById(R.id.avTextView);
        practiceInitials.setText(StringUtil.getShortName(practiceName));

        UserPracticeDTO practice = paymentReceiptModel.getPaymentPayload()
                .getUserPractice(paymentPlanDTO.getMetadata().getPracticeId());
        if (!StringUtil.isNullOrEmpty(practice.getPracticePhoto())) {
            PicassoHelper.get().loadImage(getContext(), view.findViewById(R.id.practiceImageView),
                    practiceInitials, practice.getPracticePhoto());
        }

        TextView planName = view.findViewById(R.id.paymentPlanNameTextView);
        planName.setText(paymentPlanDTO.getPayload().getDescription());


        String paymentsMadeOf = Label.getLabel("payment_plan_payments_made_value");
        int paymentCount = planPayload.getPaymentPlanDetails().getFilteredHistory().size();
        int installmentTotal = planPayload.getPaymentPlanDetails().getInstallments();
        int oneTimePayments = paymentPlanDTO.getPayload().getPaymentPlanDetails()
                .getOneTimePayments().size();
        StringBuilder paymentsMadeBuilder = new StringBuilder().append(String.format(paymentsMadeOf, paymentCount, installmentTotal));
        if (oneTimePayments > 0) {
            paymentsMadeBuilder.append(" + ")
                    .append(oneTimePayments)
                    .append(" ")
                    .append(Label.getLabel("payment_history_detail_extra"));
        }
        TextView installmentCount = view.findViewById(R.id.paymentsInstallmentsCount);
        installmentCount.setText(paymentsMadeBuilder.toString());

        TextView nextInstallment = view.findViewById(R.id.nexyPaymentDate);
        nextInstallment.setText(getNextDate(planPayload));

        double totalAmount = planPayload.getAmount();
        TextView planTotal = view.findViewById(R.id.transaction_total);
        planTotal.setText(currencyFormatter.format(totalAmount));

        double amountPaid = planPayload.getAmountPaid();
        TextView balance = view.findViewById(R.id.planBalance);
        balance.setText(currencyFormatter.format(totalAmount - amountPaid));

        ProgressBar planProgress = view.findViewById(R.id.paymentPlanProgress);
        planProgress.setProgress(planPayload.getPaymentPlanProgress());

        ImageView dialogCloseHeader = view.findViewById(R.id.dialog_close_header);
        if (dialogCloseHeader != null) {
            dialogCloseHeader.setOnClickListener(view1 -> dismiss());
        }

        payButton = view.findViewById(R.id.payment_details_pay_now_button);
        payButton.setOnClickListener(view12 -> onMakeOneTimePayment(paymentsModel, paymentPlanDTO));

        View editPlanButton = view.findViewById(R.id.editPlanButton);
        editPlanButton.setOnClickListener(v -> onEditPaymentPlan(paymentsModel, paymentPlanDTO));

        final ScheduledPaymentModel scheduledPayment = paymentsModel.getPaymentPayload().
                findScheduledPayment(paymentPlanDTO);
        View scheduledPaymentLayout = view.findViewById(R.id.scheduledPaymentLayout);
        if (scheduledPayment != null) {
            scheduledPaymentLayout.setVisibility(View.VISIBLE);
            TextView scheduledPaymentMessage = view.findViewById(R.id.scheduledPaymentMessage);
            DateUtil.getInstance().setDateRaw(scheduledPayment.getPayload().getPaymentDate());
            String message = String.format(Label.getLabel("payment.oneTimePayment.scheduled.details"),
                    StringUtil.getFormattedBalanceAmount(scheduledPayment.getPayload().getAmount()),
                    DateUtil.getInstance().toStringWithFormatMmSlashDdSlashYyyy());
            scheduledPaymentMessage.setText(message);
            scheduledPaymentLayout.setOnClickListener(view13 -> onStartEditScheduledPayment(paymentsModel,
                    paymentPlanDTO, scheduledPayment));
        }

        if (!paymentsModel.getPaymentPayload().havePermissionsToMakePayments(paymentPlanDTO
                .getMetadata().getPracticeId())
                || paymentsModel.getPaymentPayload().getDelegate() != null) {//TODO: SHMRK-9463 Take out last validation when MW handle PP
            payButton.setVisibility(View.GONE);
            editPlanButton.setVisibility(View.GONE);
            scheduledPaymentLayout.setVisibility(View.GONE);
        }
    }

    protected abstract void onMakeOneTimePayment(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO);

    protected abstract void onEditPaymentPlan(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO);

    protected void onStartEditScheduledPayment(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO,
                                               ScheduledPaymentModel scheduledPayment) {
        EditOneTimePaymentDialog dialog = EditOneTimePaymentDialog.newInstance(paymentsModel, paymentPlanDTO,
                scheduledPayment);
        callback.displayDialogFragment(dialog, false);
        dismiss();
    }

    private String getNextDate(PaymentPlanPayloadDTO planPayload) {
        int drawDay;
        Calendar calendar = Calendar.getInstance();
        if (planPayload.getPaymentPlanDetails().getFrequencyCode()
                .equals(PaymentPlanDetailsDTO.FREQUENCY_MONTHLY)) {
            drawDay = planPayload.getPaymentPlanDetails().getDayOfMonth();
            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
            if (currentDay > drawDay) {
                calendar.add(Calendar.MONTH, 1);
            }
            calendar.set(Calendar.DAY_OF_MONTH, drawDay);
        } else {
            int dayOfWeek = planPayload.getPaymentPlanDetails().getDayOfWeek() + 1; //Monday ==2

            if (calendar.get(Calendar.DAY_OF_WEEK) > dayOfWeek) {
                calendar.add(Calendar.DAY_OF_WEEK, dayOfWeek + 1);
            } else {
                calendar.add(Calendar.DAY_OF_WEEK, dayOfWeek - calendar.get(Calendar.DAY_OF_WEEK));
            }
            drawDay = calendar.get(Calendar.DAY_OF_MONTH);
        }
        return DateUtil.getInstance().setDate(calendar).getDateAsMonthDayYearString();
    }
}
