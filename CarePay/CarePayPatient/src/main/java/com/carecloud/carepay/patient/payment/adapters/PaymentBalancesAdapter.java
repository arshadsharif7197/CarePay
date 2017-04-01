package com.carecloud.carepay.patient.payment.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorge on 31/12/16.
 */

public class PaymentBalancesAdapter extends RecyclerView.Adapter<PaymentBalancesAdapter.PaymentHistoryViewHolder> {
    private Context context;
    private List<PendingBalancePayloadDTO> paymentsPatientBalances = new ArrayList<>();
    OnBalanceListItemClickListener listener;
    PaymentsModel paymentDTO;

    /**
     *
     * @param context The context
     * @param paymentDTO    The payment DTO
     * @param listener the listener
     */
    public PaymentBalancesAdapter(Context context, PaymentsModel paymentDTO, OnBalanceListItemClickListener listener) {
        this.paymentDTO = paymentDTO;
        this.context = context;
        if (paymentDTO.getPaymentPayload().getPatientBalances().size()>0 &&
                paymentDTO.getPaymentPayload().getPatientBalances().get(0).getBalances().size()>0) {
            this.paymentsPatientBalances = paymentDTO.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getPayload();
        }
        this.listener = listener;
    }

    @Override
    public PaymentBalancesAdapter.PaymentHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View paymentHistoryListItemView = LayoutInflater.from(context).inflate(
                R.layout.balances_list_item, parent, false);
        return new PaymentBalancesAdapter.PaymentHistoryViewHolder(paymentHistoryListItemView);
    }

    @Override
    public void onBindViewHolder(final PaymentBalancesAdapter.PaymentHistoryViewHolder holder, int position) {
        final PendingBalancePayloadDTO charge = paymentsPatientBalances.get(position);
        String locationName= CarePayConstants.NOT_DEFINED;
        holder.shortName.setText(StringUtil.getShortName(locationName));
        holder.locationName.setText(locationName);
        holder.amount.setText(StringUtil.getFormattedBalanceAmount(charge.getAmount()));
        holder.payNow.setText(paymentDTO.getPaymentsMetadata().getPaymentsLabel().getPaymentDetailsPayNow());

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

    static class PaymentHistoryViewHolder extends RecyclerView.ViewHolder {

        private CarePayTextView shortName;
        private CarePayTextView locationName;
        private CarePayTextView amount;
        private CarePayTextView payNow;

        PaymentHistoryViewHolder(View itemView) {
            super(itemView);

            locationName = (CarePayTextView) itemView.findViewById(com.carecloud.carepaylibrary.R.id.balancesLocation);
            amount = (CarePayTextView) itemView.findViewById(com.carecloud.carepaylibrary.R.id.balancesTotalAmount);
            payNow = (CarePayTextView) itemView.findViewById(com.carecloud.carepaylibrary.R.id.balancesPayNowTextView);
            shortName = (CarePayTextView) itemView.findViewById(com.carecloud.carepaylibrary.R.id.balancesAvatarTextView);

        }
    }


    public interface OnBalanceListItemClickListener {
        void onBalanceListItemClickListener(int position);
    }
}
