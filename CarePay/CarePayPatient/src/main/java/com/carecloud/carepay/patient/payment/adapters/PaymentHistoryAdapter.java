package com.carecloud.carepay.patient.payment.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryItem;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by jorge on 31/12/16
 */

public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.ViewHolder> {

    private final Map<String, UserPracticeDTO> practiceMap;
    private Context context;
    private HistoryItemClickListener callback;
    private List<PaymentHistoryItem> paymentHistoryItems;
    private boolean isLoading = false;
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

    private static final int VIEW_TYPE_LOADING = 1;

    public interface HistoryItemClickListener {
        void onHistoryItemClicked(PaymentHistoryItem item);
    }


    /**
     * Constructor
     *
     * @param context             context
     * @param paymentHistoryItems payment history
     * @param practiceMap
     */
    public PaymentHistoryAdapter(Context context,
                                 List<PaymentHistoryItem> paymentHistoryItems,
                                 HistoryItemClickListener callback,
                                 Map<String, UserPracticeDTO> practiceMap) {
        this.context = context;
        this.paymentHistoryItems = paymentHistoryItems;
        this.callback = callback;
        this.practiceMap = practiceMap;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_LOADING) {
            view = LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.history_list_item, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (position >= paymentHistoryItems.size()) {
            return;
        }

        final PaymentHistoryItem item = paymentHistoryItems.get(position);

        holder.picImageView.setVisibility(View.GONE);
        holder.historyPlanName.setVisibility(View.GONE);
        holder.dateLabel.setVisibility(View.GONE);
        double totalPaid = item.getPayload().getTotalPaid();
        if (item.getPayload().getMetadata().getPaymentPlan() != null) {
            if (!StringUtil.isNullOrEmpty(item.getPayload().getMetadata().getPaymentPlan().getDescription())) {
                holder.historyPlanName.setText(item.getPayload().getMetadata().getPaymentPlan().getDescription());
                holder.historyPlanName.setVisibility(View.VISIBLE);
            }
        }

        holder.amount.setText(currencyFormatter.format(totalPaid));

        DateUtil dateUtil = DateUtil.getInstance().setDateRaw(item.getPayload().getDate());
        holder.paymentDate.setText(dateUtil.getDateAsMonthAbbrDayOrdinalYear());

        double refunded = item.getPayload().getTotalRefunded();
        holder.transactionFlag.setVisibility(View.GONE);
        if (refunded > 0) {
            if (refunded < totalPaid) {
                holder.transactionFlag.setText(Label.getLabel("payment_history_partial_refund_flag"));
            } else {
                holder.transactionFlag.setText(Label.getLabel("payment_history_full_refund_flag"));
            }
            holder.transactionFlag.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onHistoryItemClicked(item);
            }
        });

        UserPracticeDTO practiceDTO = practiceMap.get(item.getMetadata().getPracticeId());
        holder.locationName.setText(practiceDTO.getPracticeName());
        holder.shortName.setText(StringUtil.getShortName(practiceDTO.getPracticeName()));
        loadImage(holder, practiceDTO.getPracticePhoto());
    }

    private void loadImage(final PaymentHistoryAdapter.ViewHolder holder, String photoUrl) {
        int size = context.getResources().getDimensionPixelSize(R.dimen.payment_details_dialog_icon_size);
        Picasso.with(context).load(photoUrl)
                .resize(size, size)
                .centerCrop()
                .transform(new CircleImageTransform())
                .into(holder.picImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.picImageView.setVisibility(View.VISIBLE);
                        holder.shortName.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        holder.picImageView.setVisibility(View.GONE);
                        holder.shortName.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public int getItemCount() {
        if (isLoading && !paymentHistoryItems.isEmpty()) {
            return paymentHistoryItems.size() + 1;
        }
        return paymentHistoryItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= paymentHistoryItems.size()) {
            return VIEW_TYPE_LOADING;
        }
        return 0;
    }

    /**
     * Set the list
     *
     * @param paymentHistoryItems list items
     */
    public void setPaymentHistoryItems(List<PaymentHistoryItem> paymentHistoryItems) {
        this.paymentHistoryItems = paymentHistoryItems;
        notifyDataSetChanged();
    }

    /**
     * Add items to existing list
     *
     * @param newItems new items
     */
    public void addHistoryList(List<PaymentHistoryItem> newItems) {
        this.paymentHistoryItems.addAll(newItems);
        notifyDataSetChanged();
    }

    /**
     * sets loading progress bar
     *
     * @param isLoading true to show progress bar
     */
    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView shortName;
        private TextView locationName;
        private TextView amount;
        private TextView paymentDate;
        private ImageView picImageView;
        private TextView transactionFlag;
        private TextView historyPlanName;
        private TextView dateLabel;

        ViewHolder(View itemView) {
            super(itemView);

            locationName = itemView.findViewById(R.id.historyLocation);
            amount = itemView.findViewById(R.id.historyTotalAmount);
            paymentDate = itemView.findViewById(R.id.historyDateTextView);
            shortName = itemView.findViewById(R.id.historyAvatarTextView);
            picImageView = itemView.findViewById(R.id.historyImageView);
            transactionFlag = itemView.findViewById(R.id.historyTransactionFlag);
            historyPlanName = itemView.findViewById(R.id.historyPlanName);
            dateLabel = itemView.findViewById(R.id.completedLabel);
        }
    }
}
