package com.carecloud.carepaylibray.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.customdialogs.PaymentDetailsDialog;
import com.carecloud.carepaylibray.payments.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.models.PatienceBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PatiencePayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.List;

public class PaymentLineItemsListAdapter extends RecyclerView.Adapter<PaymentLineItemsListAdapter.PaymentDetailsListViewHolder> {

    private Context context;
    private List<PatienceBalanceDTO> detailsList;
    private PaymentsModel paymentReceiptModel;
    private PaymentNavigationCallback payListener;
    private String detailsLabel;

    /**
     * Constructor
     * @param context context
     * @param paymentReceiptModel payment model
     * @param detailsList details list
     * @param payListener listener
     */
    public PaymentLineItemsListAdapter(Context context, PaymentsModel paymentReceiptModel,
                                       List<PatienceBalanceDTO> detailsList,
                                       PaymentNavigationCallback payListener, String detailsLabel) {

        this.context = context;
        this.detailsList = detailsList;
        this.payListener = payListener;
        this.paymentReceiptModel = paymentReceiptModel;
        this.detailsLabel = detailsLabel;
    }

    @Override
    public PaymentDetailsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View paymentDetailsListItemView = LayoutInflater.from(context).inflate(
                R.layout.payment_line_item, parent, false);
        return new PaymentDetailsListViewHolder(paymentDetailsListItemView);
    }

    @Override
    public int getItemCount() {
        return detailsList.size();
    }

    class PaymentDetailsListViewHolder extends RecyclerView.ViewHolder {

        private CarePayTextView paymentDetailLabel;
        private CarePayTextView paymentDetailAmount;
        private CarePayTextView lineItemNameLabelDetails;

        PaymentDetailsListViewHolder(View itemView) {
            super(itemView);

            paymentDetailLabel = (CarePayTextView) itemView.findViewById(R.id.lineItemNameLabel);
            paymentDetailAmount = (CarePayTextView) itemView.findViewById(R.id.lineItemAmountLabel);
            lineItemNameLabelDetails = (CarePayTextView) itemView.findViewById(R.id.lineItemNameLabelDetails);
        }
    }

    @Override
    public void onBindViewHolder(final PaymentDetailsListViewHolder holder, int position) {

        PatienceBalanceDTO patienceBalanceDTO = detailsList.get(position);

        if (!patienceBalanceDTO.getPayload().isEmpty()) {
            final PatiencePayloadDTO paymentLineItem = patienceBalanceDTO.getPayload().get(0);
            holder.paymentDetailLabel.setText(paymentLineItem.getType());
            holder.paymentDetailAmount.setText(StringUtil.getFormattedBalanceAmount(paymentLineItem.getAmount()));

            if (paymentLineItem.getDetails() != null && !paymentLineItem.getDetails().isEmpty()) {
                holder.lineItemNameLabelDetails.setVisibility(View.VISIBLE);
                holder.lineItemNameLabelDetails.setText(detailsLabel);

                if (paymentLineItem.getDetails().size() > 1) {
                    holder.lineItemNameLabelDetails.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Call for payment details dialog
                            PaymentDetailsDialog detailsDialog = new PaymentDetailsDialog(context,
                                    paymentReceiptModel, paymentLineItem, payListener);
                            detailsDialog.show();
                        }
                    });
                } else {
                    holder.lineItemNameLabelDetails.setTextColor(context.getResources().getColor(R.color.light_gray));
                }
            } else if (holder.lineItemNameLabelDetails.getVisibility() == View.VISIBLE) {
                holder.lineItemNameLabelDetails.setVisibility(View.GONE);
            }
        }
    }
}
