package com.carecloud.carepay.patient.payment.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.payment.interfaces.PaymentFragmentActivityInterface;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.payments.fragments.PaymentHistoryDetailFragment;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryItem;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.text.NumberFormat;

/**
 * Created by lmenendez on 10/4/17
 */

public class PaymentHistoryDetailDialogFragment extends PaymentHistoryDetailFragment {

    private PaymentFragmentActivityInterface callback;
    private UserPracticeDTO userPracticeDTO;

    /**
     * Get new instance of PaymentHistoryDetailDialogFragment
     * @param historyItem history item
     * @param userPracticeDTO user practice info
     * @return new PaymentHistoryDetailDialogFragment
     */
    public static PaymentHistoryDetailDialogFragment newInstance(PaymentHistoryItem historyItem, UserPracticeDTO userPracticeDTO){
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, historyItem);
        DtoHelper.bundleDto(args, userPracticeDTO);

        PaymentHistoryDetailDialogFragment fragment = new PaymentHistoryDetailDialogFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        Bundle args = getArguments();
        userPracticeDTO = DtoHelper.getConvertedDTO(UserPracticeDTO.class, args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_payment_history_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle){
        View closeButton = view.findViewById(R.id.dialog_close_header);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        DateUtil dateUtil = DateUtil.getInstance().setDateRaw(historyItem.getPayload().getDate()).shiftDateToGMT();

        TextView transactionDate = (TextView) view.findViewById(R.id.transaction_date);
        transactionDate.setText(dateUtil.getDateAsMonthLiteralDayOrdinalYear());

        TextView transactionType = (TextView) view.findViewById(R.id.transaction_type);
        transactionType.setText(getPaymentMethod(historyItem.getPayload().getPapiPaymentMethod()));

        TextView transactionNumber = (TextView) view.findViewById(R.id.transaction_number);
        transactionNumber.setText(historyItem.getPayload().getConfirmation());

        TextView transactionTotal = (TextView) view.findViewById(R.id.transaction_total);
        transactionTotal.setText(NumberFormat.getCurrencyInstance().format(totalPaid));

        TextView practiceName = (TextView) view.findViewById(R.id.practice_name);
        practiceName.setText(userPracticeDTO.getPracticeName());

        RecyclerView itemsRecycler = (RecyclerView) view.findViewById(R.id.items_recycler);
        itemsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        initItemsRecycler(itemsRecycler);

        final NestedScrollView scrollView = (NestedScrollView) view.findViewById(R.id.scrollView);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0,0);
            }
        }, 100);

        double refundedAmount = historyItem.getPayload().getTotalRefunded();
        if(refundedAmount > 0D){
            View refundLayout = view.findViewById(R.id.refund_layout);
            refundLayout.setVisibility(View.VISIBLE);

            TextView refundAmount = (TextView) view.findViewById(R.id.transaction_refunded);
            refundAmount.setText(NumberFormat.getCurrencyInstance().format(historyItem.getPayload().getTotalRefunded()));
        }

    }

    @Override
    protected void attachCallback(Context context) {
        try{
            callback = (PaymentFragmentActivityInterface) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached context must implement PaymentFragmentActivityInterface");
        }
    }

    @Override
    protected void initItemsRecycler(RecyclerView recycler) {
        itemsRecycler = recycler;
        setAdapter();
    }
}
