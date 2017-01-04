package com.carecloud.carepay.patient.payment.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepaylibray.appointments.models.AppointmentChargeDTO;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.List;

/**
 * Created by jorge on 31/12/16.
 */

public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.PaymentHistoryViewHolder> {
    private Context context;
    private List<AppointmentChargeDTO> historyList;

    public PaymentHistoryAdapter(Context context, List<AppointmentChargeDTO> historyList) {

        this.context = context;
        this.historyList = historyList;
    }

    @Override
    public PaymentHistoryAdapter.PaymentHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View paymentHistoryListItemView = LayoutInflater.from(context).inflate(
                R.layout.history_list_item, parent, false);
        return new PaymentHistoryAdapter.PaymentHistoryViewHolder(paymentHistoryListItemView);
    }

    @Override
    public void onBindViewHolder(final PaymentHistoryAdapter.PaymentHistoryViewHolder holder, int position) {
        final AppointmentChargeDTO charge = historyList.get(position);
            String locationName = charge.getLocation().getName();

            holder.shortName.setText(StringUtil.onShortDrName(locationName));
            holder.locationName.setText(locationName);
            holder.amount.setText(StringUtil.getFormattedBalanceAmount(Double.parseDouble(charge.getAmount())));
            holder.paymentDate.setText(DateUtil.getInstance().setDateRaw(charge.getPostingDate()).getDateAsMonthLiteralDayOrdinalYear());
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class PaymentHistoryViewHolder extends RecyclerView.ViewHolder {

        private CarePayTextView shortName;
        private CarePayTextView locationName;
        private CarePayTextView amount;
        private CarePayTextView paymentDate;

        PaymentHistoryViewHolder(View itemView) {
            super(itemView);

            locationName = (CarePayTextView) itemView.findViewById(com.carecloud.carepaylibrary.R.id.historyLocation);
            amount = (CarePayTextView) itemView.findViewById(com.carecloud.carepaylibrary.R.id.historyTotalAmount);
            paymentDate = (CarePayTextView) itemView.findViewById(com.carecloud.carepaylibrary.R.id.historyDateTextView);
            shortName = (CarePayTextView) itemView.findViewById(com.carecloud.carepaylibrary.R.id.historyAvatarTextView);

        }
    }

}
