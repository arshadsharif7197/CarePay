package com.carecloud.carepay.patient.payment.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.List;

/**
 * Created by jorge on 31/12/16.
 */

public class PaymentHistoryDetailAdapter extends RecyclerView.Adapter<PaymentHistoryDetailAdapter.PaymentHistoryDetailViewHolder> {
    private Context context;
    private List<PendingBalanceDTO> detail;

    public PaymentHistoryDetailAdapter(Context context, List<PendingBalanceDTO> detail) {
        this.context = context;
        this.detail = detail;
    }

    @Override
    public PaymentHistoryDetailAdapter.PaymentHistoryDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View paymentHistoryListItemView = LayoutInflater.from(context).inflate(
                R.layout.history_detail_list_item, parent, false);
        return new PaymentHistoryDetailAdapter.PaymentHistoryDetailViewHolder(paymentHistoryListItemView);
    }

    @Override
    public void onBindViewHolder(final PaymentHistoryDetailAdapter.PaymentHistoryDetailViewHolder holder, int position) {
        final PendingBalanceDTO charge = detail.get(position);
        String locationName = CarePayConstants.NOT_DEFINED;
        holder.description.setText(locationName);
        //mock model not final
        if (charge.getPayload().size() > 0) {// getSummaryBalance().size()>0)
            holder.amount.setText(StringUtil.getFormattedBalanceAmount(Double.parseDouble(
                    charge.getPayload().get(0).getAmount().toString())));//    getSummaryBalance().get(0).getTotal())));
        }

    }

    @Override
    public int getItemCount() {
        return detail.size();
    }

    static class PaymentHistoryDetailViewHolder extends RecyclerView.ViewHolder {


        private CarePayTextView description;
        private CarePayTextView amount;

        PaymentHistoryDetailViewHolder(View itemView) {
            super(itemView);

            description = (CarePayTextView) itemView.findViewById(com.carecloud.carepaylibrary.R.id.historyDetailDesc);
            amount = (CarePayTextView) itemView.findViewById(com.carecloud.carepaylibrary.R.id.historyDetailTotalAmount);

        }
    }

}
