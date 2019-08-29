package com.carecloud.carepay.patient.payment.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.payments.fragments.PaymentHistoryDetailFragment;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryItem;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by lmenendez on 10/4/17
 */

public class PaymentHistoryDetailDialogFragment extends PaymentHistoryDetailFragment {

    private FragmentActivityInterface callback;
    private UserPracticeDTO userPracticeDTO;
    private PaymentsModel paymentsModel;

    /**
     * Get new instance of PaymentHistoryDetailDialogFragment
     *
     * @param historyItem     history item
     * @param userPracticeDTO user practice info
     * @return new PaymentHistoryDetailDialogFragment
     */
    public static PaymentHistoryDetailDialogFragment newInstance(PaymentHistoryItem historyItem,
                                                                 UserPracticeDTO userPracticeDTO) {
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, historyItem);
        DtoHelper.bundleDto(args, userPracticeDTO);

        PaymentHistoryDetailDialogFragment fragment = new PaymentHistoryDetailDialogFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    protected void attachCallback(Context context) {
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
        userPracticeDTO = DtoHelper.getConvertedDTO(UserPracticeDTO.class, args);
        paymentsModel = (PaymentsModel) callback.getDto();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_payment_history_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle icicle) {
        View closeButton = view.findViewById(R.id.dialog_close_header);
        closeButton.setOnClickListener(view1 -> dismiss());

        String paymentMethod = getPaymentMethod(historyItem.getPayload().getPapiPaymentMethod());
        View paymentPlanDetailsButton = view.findViewById(R.id.paymentPlanDetailsButton);
        TextView paymentPlanNameTextView = view.findViewById(R.id.paymentPlanNameTextView);
        if (historyItem.getPayload().getMetadata().getPaymentPlan() != null) {
            paymentPlanDetailsButton.setVisibility(View.VISIBLE);
            paymentPlanDetailsButton.setOnClickListener(view12 -> displayPaymentPlanHistoryDetails(historyItem,
                    historyItem.getPayload().getMetadata().getPaymentPlan()));
            paymentMethod = Label.getLabel("payment_plan_payment_text");
            paymentPlanNameTextView.setText(historyItem.getPayload().getMetadata()
                    .getPaymentPlan().getDescription());
            paymentPlanNameTextView.setVisibility(View.VISIBLE);
        } else if (historyItem.getPayload().getExecution().equals(PaymentConstants.ANDROID_PAY_PAYMENT_TYPE)) {
            paymentMethod = CarePayConstants.TYPE_GOOGLE_PAY;
        } else {
            paymentPlanDetailsButton.setVisibility(View.GONE);
            paymentPlanNameTextView.setVisibility(View.GONE);
        }
        TextView transactionType = view.findViewById(R.id.transaction_type);
        transactionType.setText(paymentMethod);

        TextView practiceName = view.findViewById(R.id.practice_name);
        practiceName.setText(userPracticeDTO.getPracticeName());

        DateUtil dateUtil = DateUtil.getInstance().setDateRaw(historyItem.getPayload().getDate())
                .shiftDateToGMT();
        TextView transactionDate = view.findViewById(R.id.transaction_date);
        transactionDate.setText(dateUtil.getDateAsMonthLiteralDayOrdinalYear());

        TextView transactionNumber = view.findViewById(R.id.transaction_number);
        transactionNumber.setText(historyItem.getPayload().getConfirmation());

        TextView transactionTotal = view.findViewById(R.id.transaction_total);
        transactionTotal.setText(NumberFormat.getCurrencyInstance(Locale.US).format(totalPaid));

        RecyclerView itemsRecycler = view.findViewById(R.id.items_recycler);
        itemsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        initItemsRecycler(itemsRecycler);

        final NestedScrollView scrollView = view.findViewById(R.id.scrollView);
        new Handler().postDelayed(() -> scrollView.scrollTo(0, 0), 100);

        double refundedAmount = historyItem.getPayload().getTotalRefunded();
        if (refundedAmount > 0D) {
            View refundLayout = view.findViewById(R.id.refund_layout);
            refundLayout.setVisibility(View.VISIBLE);

            TextView refundAmount = view.findViewById(R.id.transaction_refunded);
            refundAmount.setText(NumberFormat.getCurrencyInstance(Locale.US)
                    .format(historyItem.getPayload().getTotalRefunded()));
        }

    }

    private void displayPaymentPlanHistoryDetails(final PaymentHistoryItem historyItem,
                                                  PaymentPlanPayloadDTO paymentPlan) {
        String taskId = paymentPlan.getMetadata().getTaskId();
        PaymentPlanDTO selectedPaymentPlan = null;
        for (PaymentPlanDTO paymentPlanDTO : paymentsModel.getPaymentPayload().getPatientPaymentPlans()) {
            if (taskId.equals(paymentPlanDTO.getPayload().getMetadata().getTaskId())) {
                selectedPaymentPlan = paymentPlanDTO;
                break;
            }
        }

        final UserPracticeDTO selectedUserPractice = paymentsModel.getPaymentPayload()
                .getUserPractice(historyItem.getMetadata().getPracticeId());
        PaymentPlanHistoryDetailDialogFragment planHistoryFragment = PaymentPlanHistoryDetailDialogFragment
                .newInstance(selectedPaymentPlan, selectedUserPractice);
        planHistoryFragment.setOnCancelListener(dialog -> showDialog());
        callback.displayDialogFragment(planHistoryFragment, false);
        hideDialog();
    }

    @Override
    protected void initItemsRecycler(RecyclerView recycler) {
        if (paymentsModel.getPaymentPayload().canViewBalanceDetails(userPracticeDTO.getPracticeId())) {
            itemsRecycler = recycler;
            setAdapter();
        }
    }
}
