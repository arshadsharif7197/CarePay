package com.carecloud.carepay.practice.library.payments.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryItem;
import com.carecloud.carepaylibray.utils.DtoHelper;

/**
 * Created by lmenendez on 11/1/17
 */

public class RefundDetailFragment extends PracticePaymentHistoryDetailFragment {

    /**
     * Get new instance of PracticePaymentHistoryDetailFragment
     * @param historyItem history item
     * @return new instance of PracticePaymentHistoryDetailFragment
     */
    public static RefundDetailFragment newInstance(PaymentHistoryItem historyItem, PaymentsModel paymentsModel){
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, historyItem);
        DtoHelper.bundleDto(args, paymentsModel);

        RefundDetailFragment fragment = new RefundDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onViewCreated(View view, Bundle bundle){
        super.onViewCreated(view, bundle);

        View closeButton = view.findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        ImageView cancelImage = (ImageView) view.findViewById(R.id.cancel_img);
        cancelImage.setImageResource(R.drawable.icn_close);

        Button refundButton = (Button) view.findViewById(R.id.refund_button);
        refundButton.setText(Label.getLabel("payment_ok"));
        refundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        String refundId = historyItem.getPayload().getLastRefundRequest();
        if(refundId != null){
            int index = refundId.indexOf('/');
            if(index + 1 < refundId.length()) {
                TextView transactionNumber = (TextView) view.findViewById(R.id.transaction_number);
                transactionNumber.setText(refundId.substring(index+1));
            }
        }

    }
}
