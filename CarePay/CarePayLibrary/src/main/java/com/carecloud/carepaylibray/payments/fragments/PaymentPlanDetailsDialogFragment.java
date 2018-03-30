package com.carecloud.carepaylibray.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.ISession;
import com.carecloud.carepaylibray.customdialogs.BasePaymentDetailsFragmentDialog;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanInterface;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by lmenendez on 1/26/18
 */

public class PaymentPlanDetailsDialogFragment extends BasePaymentDetailsFragmentDialog {

    private PaymentsModel paymentsModel;
    private PaymentPlanDTO paymentPlanDTO;
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

    private PaymentPlanInterface callback;

    /**
     * @param paymentsModel  the payment model
     * @param paymentPlanDTO the Payment Plan Dto
     * @return new instance of a PaymentPlanDetailsDialogFragment
     */
    public static PaymentPlanDetailsDialogFragment newInstance(PaymentsModel paymentsModel,
                                                               PaymentPlanDTO paymentPlanDTO) {
        // Supply inputs as an argument
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        DtoHelper.bundleDto(args, paymentPlanDTO);

        PaymentPlanDetailsDialogFragment dialog = new PaymentPlanDetailsDialogFragment();
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (PaymentPlanInterface) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement PaymentPlanInterface");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
        paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, args);
        paymentPlanDTO = DtoHelper.getConvertedDTO(PaymentPlanDTO.class, args);
    }

    @Override
    protected void onInitialization(View view) {
        PaymentPlanPayloadDTO planPayload = paymentPlanDTO.getPayload();
        UserPracticeDTO userPracticeDTO = callback.getPracticeInfo(paymentsModel);

        TextView installmentDetail = (TextView) view.findViewById(R.id.planInstallmentDetail);
        installmentDetail.setText(currencyFormatter.format(planPayload.getPaymentPlanDetails().getAmount()));

        TextView installmentFrequency = (TextView) view.findViewById(R.id.planInstallmentFrequency);
        installmentFrequency.setText(planPayload.getPaymentPlanDetails().getFrequencyString());

        String practiceName = userPracticeDTO.getPracticeName();
        TextView practiceNameText = (TextView) view.findViewById(R.id.practice_name);
        practiceNameText.setText(practiceName);
        TextView practiceInitials = (TextView) view.findViewById(R.id.avTextView);
        practiceInitials.setText(StringUtil.getShortName(practiceName));

        int paymentCount = planPayload.getPaymentPlanDetails().getFilteredHistory().size();
        int installmentTotal = planPayload.getPaymentPlanDetails().getInstallments();
        TextView installmentCount = (TextView) view.findViewById(R.id.paymentsInstallmentsCount);
        installmentCount.setText(String.format(Label.getLabel("payment_plan_payments_made_value"), paymentCount, installmentTotal));

        TextView nextInstallment = (TextView) view.findViewById(R.id.nexyPaymentDate);
        nextInstallment.setText(getNextDate(planPayload));

        double totalAmount = planPayload.getAmount();
        TextView planTotal = (TextView) view.findViewById(R.id.transaction_total);
        planTotal.setText(currencyFormatter.format(totalAmount));

        double amountPaid = planPayload.getAmountPaid();
        TextView balance = (TextView) view.findViewById(R.id.planBalance);
        balance.setText(currencyFormatter.format(totalAmount - amountPaid));

        ProgressBar planProgress = (ProgressBar) view.findViewById(R.id.paymentPlanProgress);
        planProgress.setProgress(planPayload.getPaymentPlanProgress());

        ImageView dialogCloseHeader = (ImageView) view.findViewById(R.id.dialog_close_header);
        if (dialogCloseHeader != null) {
            dialogCloseHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }

        View payButton = view.findViewById(R.id.payment_details_pay_now_button);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onMakeOneTimePayment(paymentsModel, paymentPlanDTO);
                dismiss();
            }
        });

        View editButton = view.findViewById(R.id.editPlanButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onEditPaymentPlan(paymentsModel, paymentPlanDTO);
                dismiss();
            }
        });
    }

    @Override
    protected int getCancelImageResource() {
        return 0;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_payment_plan_details;
    }

    private String getNextDate(PaymentPlanPayloadDTO planPayload) {
        int drawDay = planPayload.getPaymentPlanDetails().getDayOfMonth();
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        if (currentDay > drawDay) {
            calendar.add(Calendar.MONTH, 1);
        }
        calendar.set(Calendar.DAY_OF_MONTH, drawDay);

        ApplicationPreferences preferences = ((ISession) getActivity()).getApplicationPreferences();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM ", new Locale(preferences.getUserLanguage()));
        return dateFormat.format(calendar.getTime()) + StringUtil.getOrdinal(preferences.getUserLanguage(), drawDay);
    }
}
