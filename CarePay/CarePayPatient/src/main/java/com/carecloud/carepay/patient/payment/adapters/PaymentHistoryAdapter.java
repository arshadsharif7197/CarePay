package com.carecloud.carepay.patient.payment.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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

/**
 * Created by jorge on 31/12/16
 */

public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.ViewHolder> {
    public interface HistoryItemClickListener{
        void onHistoryItemClicked(PaymentHistoryItem item);
    }

    private static final int VIEW_TYPE_LOADING = 1;

    private Context context;
    private HistoryItemClickListener callback;
    private List<PaymentHistoryItem> paymentHistoryItems = new ArrayList<>();
    private List<UserPracticeDTO> userPractices = new ArrayList<>();

    private boolean isLoading = false;
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

    /**
     * Constructor
     * @param context context
     * @param paymentHistoryItems payment history
     */
    public PaymentHistoryAdapter(Context context, List<PaymentHistoryItem> paymentHistoryItems, List<UserPracticeDTO> userPractices, HistoryItemClickListener callback) {
        this.context = context;
        this.paymentHistoryItems = paymentHistoryItems;
        this.userPractices = userPractices;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == VIEW_TYPE_LOADING){
            view = LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false);
        }else {
            view = LayoutInflater.from(context).inflate(R.layout.history_list_item, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(position >= paymentHistoryItems.size()){
            return;
        }

        holder.transactionFlag.setVisibility(View.GONE);

        final PaymentHistoryItem item = paymentHistoryItems.get(position);

        DateUtil dateUtil = DateUtil.getInstance().setDateRaw(item.getPayload().getDate()).shiftDateToGMT();
        holder.paymentDate.setText(dateUtil.getDateAsMonthAbbrDayOrdinalYear());

        double totalPaid = item.getPayload().getTotalPaid();
        holder.amount.setText(currencyFormatter.format(totalPaid));

        holder.image.setVisibility(View.GONE);
        UserPracticeDTO userPracticeDTO = getUserPractice(item.getMetadata().getPracticeId());
        if(userPracticeDTO != null) {
            holder.locationName.setText(userPracticeDTO.getPracticeName());
            holder.shortName.setText(StringUtil.getShortName(userPracticeDTO.getPracticeName()));
            Picasso.with(context)
                    .load(userPracticeDTO.getPracticePhoto())
                    .resize(60,60)
                    .centerCrop()
                    .transform(new CircleImageTransform())
                    .into(holder.image, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.image.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            holder.image.setVisibility(View.GONE);
                        }
                    });

        }

        double refunded = item.getPayload().getTotalRefunded();
        if(refunded > 0){
            if(refunded < totalPaid){
                holder.transactionFlag.setText(Label.getLabel("payment_history_partial_refund_flag"));
            }else{
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
    }

    @Override
    public int getItemCount() {
        if(isLoading && !paymentHistoryItems.isEmpty()){
            return paymentHistoryItems.size()+1;
        }
        return paymentHistoryItems.size();
    }

    @Override
    public int getItemViewType(int position){
        if(position >= paymentHistoryItems.size()){
            return VIEW_TYPE_LOADING;
        }
        return 0;
    }

    /**
     * Set the list
     * @param paymentHistoryItems list items
     */
    public void setPaymentHistoryItems(List<PaymentHistoryItem> paymentHistoryItems){
        this.paymentHistoryItems = paymentHistoryItems;
        notifyDataSetChanged();
    }

    /**
     * Add items to existing list
     * @param newItems new items
     */
    public void addHistoryList(List<PaymentHistoryItem> newItems){
        this.paymentHistoryItems.addAll(newItems);
        notifyDataSetChanged();
    }

    /**
     * sets loading progress bar
     * @param isLoading true to show progress bar
     */
    public void setLoading(boolean isLoading){
        this.isLoading = isLoading;
        notifyDataSetChanged();
    }

    private UserPracticeDTO getUserPractice(String practiceId){
        for(UserPracticeDTO userPracticeDTO : userPractices){
            if(userPracticeDTO.getPracticeId() != null && userPracticeDTO.getPracticeId().equals(practiceId)){
                return userPracticeDTO;
            }
        }
        return null;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView shortName;
        private TextView locationName;
        private TextView amount;
        private TextView paymentDate;
        private ImageView image;
        private TextView transactionFlag;

        ViewHolder(View itemView) {
            super(itemView);

            locationName = (TextView) itemView.findViewById(R.id.historyLocation);
            amount = (TextView) itemView.findViewById(R.id.historyTotalAmount);
            paymentDate = (TextView) itemView.findViewById(R.id.historyDateTextView);
            shortName = (TextView) itemView.findViewById(R.id.historyAvatarTextView);
            image = (ImageView) itemView.findViewById(R.id.historyImageView);
            transactionFlag = (TextView) itemView.findViewById(R.id.historyTransactionFlag);
        }
    }
}
