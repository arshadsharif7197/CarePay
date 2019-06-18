package com.carecloud.carepay.patient.payment.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.payments.models.PaymentListItem;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsBalancesItem;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentModel;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PaymentBalancesAdapter extends RecyclerView.Adapter<PaymentBalancesAdapter.ViewHolder> {
    private static final int VIEW_TYPE_BALANCE = 0;
    private static final int VIEW_TYPE_PLAN = 1;
    private final Map<String, UserPracticeDTO> practiceMap;

    private Context context;
    private List<? extends PaymentListItem> listItems = new ArrayList<>();
    private OnBalanceListItemClickListener listener;
    private PaymentsModel paymentsModel;
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

    /**
     * @param context         The context
     * @param pendingBalances The list of the pending listItems
     * @param listener        the listener
     * @param practiceMap
     */
    public PaymentBalancesAdapter(Context context, List<? extends PaymentListItem> pendingBalances,
                                  OnBalanceListItemClickListener listener,
                                  PaymentsModel paymentsModel,
                                  Map<String, UserPracticeDTO> practiceMap) {
        this.context = context;
        this.listItems = pendingBalances;
        this.listener = listener;
        this.paymentsModel = paymentsModel;
        this.practiceMap = practiceMap;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View paymentHistoryListItemView;

        if (viewType == VIEW_TYPE_BALANCE) {
            paymentHistoryListItemView = LayoutInflater.from(context).inflate(
                    R.layout.balances_list_item, parent, false);
        } else {
            paymentHistoryListItemView = LayoutInflater.from(context).inflate(
                    R.layout.item_payment_plan, parent, false);
        }
        return new ViewHolder(paymentHistoryListItemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String practiceId = null;
        if (getItemViewType(position) == VIEW_TYPE_BALANCE) {
            final PaymentsBalancesItem pendingBalance = (PaymentsBalancesItem) listItems.get(position);
            String locationName = pendingBalance.getMetadata().getPracticeName();
            holder.shortName.setText(StringUtil.getShortName(locationName));
            holder.locationName.setText(locationName);
            holder.amount.setText(currencyFormatter.format(pendingBalance.getBalance().getAmount()));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View listItem) {
                    listener.onBalanceListItemClickListener(pendingBalance);
                }
            });
            String pendingPracticeId = pendingBalance.getMetadata().getPracticeId();
            boolean canPay = paymentsModel.getPaymentPayload().canMakePayments(pendingPracticeId) &&
                    paymentsModel.getPaymentPayload().hasPaymentMethods(pendingPracticeId);
            holder.payLabel.setVisibility(canPay ? View.VISIBLE : View.GONE);
            practiceId = pendingBalance.getMetadata().getPracticeId();
        } else {
            final PaymentPlanDTO paymentPlanItem = (PaymentPlanDTO) listItems.get(position);

            String locationName = paymentPlanItem.getMetadata().getPracticeName();
            holder.shortName.setText(StringUtil.getShortName(locationName));
            holder.locationName.setText(locationName);
            holder.planName.setText(paymentPlanItem.getPayload().getDescription());

            double totalAmount = paymentPlanItem.getPayload().getAmount();
            double amountPaid = paymentPlanItem.getPayload().getAmountPaid();
            holder.amount.setText(currencyFormatter.format(totalAmount - amountPaid));

            String planDetails = currencyFormatter.format(
                    paymentPlanItem.getPayload().getPaymentPlanDetails().getAmount()) +
                    paymentPlanItem.getPayload().getPaymentPlanDetails().getFrequencyString();
            holder.planDetail.setText(planDetails);

            holder.planProgress.setProgress(paymentPlanItem.getPayload().getPaymentPlanProgress());

            ScheduledPaymentModel scheduledPayment = paymentsModel.getPaymentPayload()
                    .findScheduledPayment(paymentPlanItem);
            holder.scheduledIcon.setVisibility(scheduledPayment != null ? View.VISIBLE : View.GONE);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onPaymentPlanItemClickListener(paymentPlanItem);
                }
            });
            practiceId = paymentPlanItem.getMetadata().getPracticeId();
        }

        loadImage(holder, practiceMap.get(practiceId).getPracticePhoto());
    }

    private void loadImage(final PaymentBalancesAdapter.ViewHolder holder, String photoUrl) {
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
    public int getItemViewType(int position) {
        if (listItems.get(position) instanceof PaymentsBalancesItem) {
            return VIEW_TYPE_BALANCE;
        }
        return VIEW_TYPE_PLAN;
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView picImageView;
        private TextView shortName;
        private TextView locationName;
        private TextView amount;
        private View payLabel;
        private TextView planDetail;
        private ProgressBar planProgress;
        private View scheduledIcon;
        private TextView planName;

        ViewHolder(View itemView) {
            super(itemView);
            locationName = itemView.findViewById(R.id.balancesLocation);
            amount = itemView.findViewById(R.id.balancesTotalAmount);
            shortName = itemView.findViewById(R.id.balancesAvatarTextView);
            payLabel = itemView.findViewById(R.id.balancesPayNowTextView);
            planDetail = itemView.findViewById(R.id.planInstallmentDetail);
            planProgress = itemView.findViewById(R.id.paymentPlanProgress);
            scheduledIcon = itemView.findViewById(R.id.scheduledPaymentIcon);
            planName = itemView.findViewById(R.id.planName);
            picImageView = itemView.findViewById(R.id.picImageView);
        }
    }

    public interface OnBalanceListItemClickListener {
        void onBalanceListItemClickListener(PaymentsBalancesItem pendingBalance);

        void onPaymentPlanItemClickListener(PaymentPlanDTO paymentPlan);
    }
}
