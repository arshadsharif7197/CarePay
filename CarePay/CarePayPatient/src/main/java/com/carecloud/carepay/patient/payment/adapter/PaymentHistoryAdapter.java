package com.carecloud.carepay.patient.payment.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.payment.dialogs.PaymentAmountReceiptDialog;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.List;

/**
 * Created by jorge on 31/12/16.
 */

public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.PaymentHistoryViewHolder> {

    private Context context;
    private PaymentsModel paymentsModel;
    private List<BalanceItemDTO> historyList;

    /**
     * Constructor
     * @param context context
     * @param paymentsModel payment model
     */
    public PaymentHistoryAdapter(Context context, PaymentsModel paymentsModel) {

        this.context = context;
        this.paymentsModel = paymentsModel;
        this.historyList = paymentsModel.getPaymentPayload().getPatientHistory()
                .getPaymentsPatientCharges().getCharges();

    }

    @Override
    public PaymentHistoryAdapter.PaymentHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View paymentHistoryListItemView = LayoutInflater.from(context).inflate(
                R.layout.history_list_item, parent, false);
        return new PaymentHistoryAdapter.PaymentHistoryViewHolder(paymentHistoryListItemView);
    }

    @Override
    public void onBindViewHolder(final PaymentHistoryAdapter.PaymentHistoryViewHolder holder, int position) {
        final BalanceItemDTO charge = historyList.get(position);
        String locationName = CarePayConstants.NOT_DEFINED;//charge.getLocation().getName();

        holder.shortName.setText(StringUtil.getShortName(locationName));
        holder.locationName.setText(locationName);
        holder.amount.setText(StringUtil.getFormattedBalanceAmount(Double.parseDouble(charge.getAmount())));
        holder.paymentDate.setText(DateUtil.getInstance().setDateRaw(charge.getPostingDate())
                .getDateAsMonthLiteralDayOrdinalYear());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PaymentAmountReceiptDialog receiptDialog = new PaymentAmountReceiptDialog(context, paymentsModel);
                receiptDialog.show();
            }
        });
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
