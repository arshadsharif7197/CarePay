package com.carecloud.carepay.patient.payment.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.payments.adapter.PaymentPlanHistoryDetailAdapter;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
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
    private FragmentActivityInterface callback;
    private PaymentsModel paymentsDto;

    /**
     * Get new instance of PaymentHistoryDetailDialogFragment
     *
     * @param paymentPlanDTO payent plan
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
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (FragmentActivityInterface) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement PaymentFragmentActivityInterface");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
        paymentPlanDTO = DtoHelper.getConvertedDTO(PaymentPlanDTO.class, args);
        userPracticeDTO = DtoHelper.getConvertedDTO(UserPracticeDTO.class, args);
        paymentsDto = (PaymentsModel) callback.getDto();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_payment_plan_history_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle icicle) {
        View closeButton = view.findViewById(R.id.dialog_close_header);
        closeButton.setOnClickListener(view1 -> cancel());

        TextView totalPaid = view.findViewById(R.id.planTotalPaid);
        totalPaid.setText(currencyFormatter.format(paymentPlanDTO.getPayload().getAmountPaid()));

        String practiceName = userPracticeDTO.getPracticeName();
        TextView practiceNameText = view.findViewById(com.carecloud.carepaylibrary.R.id.practice_name);
        practiceNameText.setText(practiceName);
        TextView practiceInitials = view.findViewById(com.carecloud.carepaylibrary.R.id.avTextView);
        practiceInitials.setText(StringUtil.getShortName(practiceName));

        ProgressBar planProgress = view.findViewById(com.carecloud.carepaylibrary.R.id.paymentPlanProgress);
        planProgress.setProgress(paymentPlanDTO.getPayload().getPaymentPlanProgress());

        TextView planName = view.findViewById(R.id.paymentPlanNameTextView);
        planName.setText(paymentPlanDTO.getPayload().getDescription());

        TextView totalAmount = view.findViewById(R.id.totalAmountTextView);
        totalAmount.setText(currencyFormatter.format(paymentPlanDTO.getPayload().getAmount()));

        String paymentsMadeOf = Label.getLabel("payment_plan_payments_made_value");
        int installments = paymentPlanDTO.getPayload().getPaymentPlanDetails().getInstallments();
        int payments = paymentPlanDTO.getPayload().getPaymentPlanDetails().getFilteredHistory().size();
        int oneTimePayments = paymentPlanDTO.getPayload().getPaymentPlanDetails()
                .getOneTimePayments().size();
        StringBuilder paymentsMadeBuilder = new StringBuilder().append(String.format(paymentsMadeOf, payments, installments));
        if (oneTimePayments > 0) {
            paymentsMadeBuilder.append(" + ")
                    .append(oneTimePayments)
                    .append(" ")
                    .append(Label.getLabel("payment_history_detail_extra"));
        }
        TextView paymentsMadeTextView = view.findViewById(R.id.paymentsMadeTextView);
        paymentsMadeTextView.setText(paymentsMadeBuilder.toString());

        if (paymentsDto.getPaymentPayload().canViewBalanceDetails(userPracticeDTO.getPracticeId())) {
            RecyclerView itemsRecycler = view.findViewById(R.id.items_recycler);
            itemsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            setAdapter(itemsRecycler);
        } else {
            view.findViewById(R.id.separator).setVisibility(View.GONE);
        }

        final NestedScrollView scrollView = view.findViewById(R.id.scrollView);
        new Handler().postDelayed(() -> scrollView.scrollTo(0, 0), 100);

    }

    private void setAdapter(RecyclerView itemsRecycler) {
        PaymentPlanHistoryDetailAdapter adapter = new PaymentPlanHistoryDetailAdapter(getContext(),
                paymentPlanDTO.getPayload().getPaymentPlanDetails().getSuccessfulPaymentHistory());
        itemsRecycler.setAdapter(adapter);
    }


}
