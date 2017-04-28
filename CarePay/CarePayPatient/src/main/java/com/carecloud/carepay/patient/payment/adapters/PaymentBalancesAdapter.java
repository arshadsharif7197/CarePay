package com.carecloud.carepay.patient.payment.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PaymentBalancesAdapter extends RecyclerView.Adapter<PaymentBalancesAdapter.ViewHolder> {
    private final String practiceName;
    private Context context;
    private List<PendingBalancePayloadDTO> paymentsPatientBalances = new ArrayList<>();
    private OnBalanceListItemClickListener listener;

    /**
     * @param context    The context
     * @param paymentDTO The payment DTO
     * @param listener   the listener
     */
    public PaymentBalancesAdapter(Context context, PaymentsModel paymentDTO, OnBalanceListItemClickListener listener) {
        this.context = context;
        if (paymentDTO.getPaymentPayload().getPatientBalances().size() > 0 &&
                paymentDTO.getPaymentPayload().getPatientBalances().get(0).getBalances().size() > 0) {
            this.paymentsPatientBalances = paymentDTO.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getPayload();
        }
        practiceName = paymentDTO.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getMetadata().getPracticeName();
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View paymentHistoryListItemView = LayoutInflater.from(context).inflate(
                R.layout.balances_list_item, parent, false);
        return new ViewHolder(paymentHistoryListItemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final PendingBalancePayloadDTO charge = paymentsPatientBalances.get(position);
        String locationName = practiceName;
        holder.shortName.setText(StringUtil.getShortName(locationName));
        holder.locationName.setText(locationName);
        holder.amount.setText(StringUtil.getFormattedBalanceAmount(charge.getAmount()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View listItem) {
                listener.onBalanceListItemClickListener(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return paymentsPatientBalances.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private CarePayTextView shortName;
        private CarePayTextView locationName;
        private CarePayTextView amount;

        ViewHolder(View itemView) {
            super(itemView);
            locationName = (CarePayTextView) itemView.findViewById(com.carecloud.carepaylibrary.R.id.balancesLocation);
            amount = (CarePayTextView) itemView.findViewById(com.carecloud.carepaylibrary.R.id.balancesTotalAmount);
            shortName = (CarePayTextView) itemView.findViewById(com.carecloud.carepaylibrary.R.id.balancesAvatarTextView);
        }
    }

    public interface OnBalanceListItemClickListener {
        void onBalanceListItemClickListener(int position);
    }
}
