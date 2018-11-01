package com.carecloud.carepay.patient.payment.dialogs;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.payments.adapter.PaymentPlanHistoryDetailAdapter;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by lmenendez on 10/4/17
 */

public class PaymentPlanHistoryDetailDialogFragment extends BaseDialogFragment {

    private PaymentPlanDTO paymentPlanDTO;
    private UserPracticeDTO userPracticeDTO;
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

    /**
     * Get new instance of PaymentHistoryDetailDialogFragment
     *
     * @param paymentPlanDTO     payent plan
     * @return new PaymentPlanHistoryDetailDialogFragment
     */
    public static PaymentPlanHistoryDetailDialogFragment newInstance(PaymentPlanDTO paymentPlanDTO, UserPracticeDTO userPracticeDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentPlanDTO);
        DtoHelper.bundleDto(args, userPracticeDTO);

        PaymentPlanHistoryDetailDialogFragment fragment = new PaymentPlanHistoryDetailDialogFragment();
        fragment.setArguments(args);
        return fragment;

    }


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
        paymentPlanDTO = DtoHelper.getConvertedDTO(PaymentPlanDTO.class, args);
        userPracticeDTO = DtoHelper.getConvertedDTO(UserPracticeDTO.class, args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_payment_plan_history_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        View closeButton = view.findViewById(R.id.dialog_close_header);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

        TextView totalPaid = (TextView) view.findViewById(R.id.planTotalPaid);
        totalPaid.setText(currencyFormatter.format(paymentPlanDTO.getPayload().getAmountPaid()));

        String practiceName = userPracticeDTO.getPracticeName();
        TextView practiceNameText = (TextView) view.findViewById(com.carecloud.carepaylibrary.R.id.practice_name);
        practiceNameText.setText(practiceName);
        TextView practiceInitials = (TextView) view.findViewById(com.carecloud.carepaylibrary.R.id.avTextView);
        practiceInitials.setText(StringUtil.getShortName(practiceName));

        ProgressBar planProgress = (ProgressBar) view.findViewById(com.carecloud.carepaylibrary.R.id.paymentPlanProgress);
        planProgress.setProgress(paymentPlanDTO.getPayload().getPaymentPlanProgress());

        TextView planName = (TextView) view.findViewById(R.id.paymentPlanNameTextView);
        planName.setText(paymentPlanDTO.getPayload().getDescription());

        TextView totalAmount = (TextView) view.findViewById(R.id.totalAmountTextView);
        totalAmount.setText(currencyFormatter.format(paymentPlanDTO.getPayload().getAmount()));

        String paymentsMadeOf = Label.getLabel("payment_plan_payments_made_value");
        int installments = paymentPlanDTO.getPayload().getPaymentPlanDetails().getInstallments();
        int payments = paymentPlanDTO.getPayload().getPaymentPlanDetails().getFilteredHistory().size();
        int oneTimePayments = paymentPlanDTO.getPayload().getPaymentPlanDetails()
                .getPaymentPlanHistoryList().size() - payments;
        StringBuilder paymentsMadeBuilder = new StringBuilder().append(String.format(paymentsMadeOf, payments, installments));
        if(oneTimePayments > 0){
            paymentsMadeBuilder.append(" + ")
                    .append(oneTimePayments)
                    .append(" ")
                    .append(Label.getLabel("payment_history_detail_extra"));
        }
        TextView paymentsMadeTextView = (TextView) view.findViewById(R.id.paymentsMadeTextView);
        paymentsMadeTextView.setText(paymentsMadeBuilder.toString());

        RecyclerView itemsRecycler = (RecyclerView) view.findViewById(R.id.items_recycler);
        itemsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        setAdapter(itemsRecycler);

        final NestedScrollView scrollView = (NestedScrollView) view.findViewById(R.id.scrollView);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, 0);
            }
        }, 100);

    }

    private void setAdapter(RecyclerView itemsRecycler) {
        PaymentPlanHistoryDetailAdapter adapter = new PaymentPlanHistoryDetailAdapter(getContext(),
                paymentPlanDTO.getPayload().getPaymentPlanDetails().getPaymentPlanHistoryList());
        itemsRecycler.setAdapter(adapter);
    }


}
