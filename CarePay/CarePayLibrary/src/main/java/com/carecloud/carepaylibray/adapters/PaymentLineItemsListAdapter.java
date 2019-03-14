package com.carecloud.carepaylibray.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.List;

public class PaymentLineItemsListAdapter extends RecyclerView.Adapter<PaymentLineItemsListAdapter.ViewHolder> {

    private boolean canViewBalanceDetails;
    private Context context;
    private List<PendingBalancePayloadDTO> detailsList;
    private PaymentLineItemCallback callback;

    /**
     * Constructor
     *
     * @param context               context
     * @param detailsList           details list
     * @param canViewBalanceDetails
     */
    public PaymentLineItemsListAdapter(Context context, List<PendingBalancePayloadDTO> detailsList,
                                       PaymentLineItemCallback callback, boolean canViewBalanceDetails) {
        this.context = context;
        this.detailsList = detailsList;
        this.callback = callback;
        this.canViewBalanceDetails = canViewBalanceDetails;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View paymentDetailsListItemView = LayoutInflater.from(context).inflate(
                R.layout.payment_line_item, parent, false);
        return new ViewHolder(paymentDetailsListItemView);
    }

    @Override
    public int getItemCount() {
        return detailsList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final PendingBalancePayloadDTO paymentLineItem = detailsList.get(position);
        holder.paymentDetailLabel.setText(paymentLineItem.getType());
        holder.paymentDetailAmount.setText(StringUtil.getFormattedBalanceAmount(paymentLineItem.getAmount()));
        if (callback != null) {
            holder.lineItemNameLabelDetails.setText(Label.getLabel("payment_responsibility_details"));
            canViewBalanceDetails = canViewBalanceDetails && paymentLineItem.getType()
                    .equals(PendingBalancePayloadDTO.PATIENT_BALANCE);
            holder.lineItemNameLabelDetails.setVisibility(canViewBalanceDetails ? View.VISIBLE : View.INVISIBLE);
            holder.lineItemNameLabelDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onDetailItemClick(paymentLineItem);
                }
            });
        } else if (holder.lineItemNameLabelDetails.getVisibility() == View.VISIBLE) {
            holder.lineItemNameLabelDetails.setVisibility(View.INVISIBLE);
        }
    }

    public interface PaymentLineItemCallback {
        void onDetailItemClick(PendingBalancePayloadDTO paymentLineItem);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CarePayTextView paymentDetailLabel;
        private CarePayTextView paymentDetailAmount;
        private CarePayTextView lineItemNameLabelDetails;

        ViewHolder(View itemView) {
            super(itemView);

            paymentDetailLabel = itemView.findViewById(R.id.lineItemNameLabel);
            paymentDetailAmount = itemView.findViewById(R.id.lineItemAmountLabel);
            lineItemNameLabelDetails = itemView.findViewById(R.id.lineItemNameLabelDetails);
        }
    }
}
