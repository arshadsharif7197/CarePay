package com.carecloud.carepay.practice.library.payments.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentListItem;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDetailsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentModel;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by pjohnson on 17/03/17
 */
public class PaymentBalancesAdapter extends RecyclerView.Adapter<PaymentBalancesAdapter.ViewHolder> {
    private static final int VIEW_TYPE_BALANCE = 0;
    private static final int VIEW_TYPE_PLAN = 1;

    private List<? extends PaymentListItem> listItems;
    private final UserPracticeDTO userPractice;
    private PaymentRecyclerViewCallback callback;
    private Context context;
    private PaymentsModel paymentsModel;

    /**
     * Constructor
     *
     * @param context         context
     * @param paymentsModel   payments model
     * @param patientBalances listItems
     * @param userPracticeDTO dto
     * @param callback        callback
     */
    public PaymentBalancesAdapter(Context context, PaymentsModel paymentsModel,
                                  List<? extends PaymentListItem> patientBalances,
                                  UserPracticeDTO userPracticeDTO,
                                  PaymentRecyclerViewCallback callback) {
        this.context = context;
        this.listItems = patientBalances;
        this.userPractice = userPracticeDTO;
        this.callback = callback;
        this.paymentsModel = paymentsModel;
    }

    @Override
    public PaymentBalancesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View paymentListItemView;
        if (viewType == VIEW_TYPE_BALANCE) {
            paymentListItemView = LayoutInflater.from(context).inflate(
                    R.layout.cardview_payments_item, parent, false);
        } else {
            paymentListItemView = LayoutInflater.from(context).inflate(
                    R.layout.cardview_payment_plan_item, parent, false);
        }
        return new PaymentBalancesAdapter.ViewHolder(paymentListItemView);
    }

    @Override
    public void onBindViewHolder(final PaymentBalancesAdapter.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_BALANCE) {
            final PatientBalanceDTO patientBalanceDTO = (PatientBalanceDTO) listItems.get(position);
            double responsibility = 0D;
            try {
                responsibility = Double.valueOf(patientBalanceDTO.getPendingRepsonsibility());
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
            holder.paymentAmountTextView.setText(StringUtil.getFormattedBalanceAmount(responsibility));
            holder.payButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onBalancePayButtonClicked(patientBalanceDTO);
                }
            });

        } else {
            final PaymentPlanDTO paymentPlanDTO = (PaymentPlanDTO) listItems.get(position);
            PaymentPlanDetailsDTO detailsDTO = paymentPlanDTO.getPayload().getPaymentPlanDetails();
            holder.paymentAmountTextView.setText(StringUtil.getFormattedBalanceAmount(detailsDTO.getAmount()));
            holder.planInstallmentFrequency.setText(detailsDTO.getFrequencyString());

            holder.paymentPlanProgress.setProgress(paymentPlanDTO.getPayload().getPaymentPlanProgress());

            ScheduledPaymentModel scheduledPayment = paymentsModel.getPaymentPayload()
                    .findScheduledPayment(paymentPlanDTO);
            holder.scheduledIcon.setVisibility(scheduledPayment != null ? View.VISIBLE : View.GONE);

            holder.payButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onPaymentPlanButtonClicked(paymentPlanDTO);
                }
            });
        }

        holder.placeNameTextView.setText(userPractice.getPracticeName());
        String photoUrl = userPractice.getPracticePhoto();
        if (TextUtils.isEmpty(photoUrl)) {
            holder.providerImageTextView.setText(StringUtil.getShortName(userPractice.getPracticeName()));
            holder.providerImageView.setVisibility(View.GONE);
        } else {
            Picasso.with(context).load(photoUrl)
                    .transform(new CircleImageTransform())
                    .resize(100, 100)
                    .into(holder.providerImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.providerImageView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            holder.providerImageTextView.setText(StringUtil.getShortName(userPractice.getPracticeName()));
                            holder.providerImageView.setVisibility(View.GONE);
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (listItems.get(position) instanceof PatientBalanceDTO) {
            return VIEW_TYPE_BALANCE;
        }
        return VIEW_TYPE_PLAN;
    }

    public void setData(List<? extends PaymentListItem> listItems) {
        this.listItems = listItems;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView placeNameTextView;
        TextView paymentAmountTextView;
        Button payButton;
        TextView providerImageTextView;
        ImageView providerImageView;

        TextView planInstallmentFrequency;
        ProgressBar paymentPlanProgress;
        View scheduledIcon;

        /**
         * View holder constructor
         *
         * @param itemView view holder item
         */
        public ViewHolder(View itemView) {
            super(itemView);
            placeNameTextView = itemView.findViewById(R.id.placeNameTextView);
            paymentAmountTextView = itemView.findViewById(R.id.paymentAmountTextView);
            payButton = itemView.findViewById(R.id.payButton);
            providerImageTextView = itemView.findViewById(R.id.providerImageTextView);
            providerImageView = itemView.findViewById(R.id.providerImageView);
            planInstallmentFrequency = itemView.findViewById(R.id.planInstallmentFrequency);
            paymentPlanProgress = itemView.findViewById(R.id.paymentPlanProgress);
            scheduledIcon = itemView.findViewById(com.carecloud.carepaylibrary.R.id.scheduledPaymentIcon);
        }
    }

    public interface PaymentRecyclerViewCallback {
        void onBalancePayButtonClicked(PatientBalanceDTO patientBalanceDTO);

        void onPaymentPlanButtonClicked(PaymentPlanDTO paymentPlanDTO);
    }

}
