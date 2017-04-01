package com.carecloud.carepay.practice.library.payments.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.utils.CircleImageTransform;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by pjohnson on 17/03/17.
 */
public class PaymentBalancesAdapter extends RecyclerView.Adapter<PaymentBalancesAdapter.ViewHolder> {

    private final List<PatientBalanceDTO> balances;
    private final UserPracticeDTO userPractice;
    private PaymentRecyclerViewCallback callback;
    private Context context;

    /**
     * Constructor
     * @param context context
     * @param patientBalances balances
     * @param userPracticeDTO dto
     */
    public PaymentBalancesAdapter(Context context, List<PatientBalanceDTO> patientBalances, UserPracticeDTO userPracticeDTO) {
        this.context = context;
        this.balances = patientBalances;
        this.userPractice = userPracticeDTO;
    }

    @Override
    public PaymentBalancesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View appointmentsListItemView = LayoutInflater.from(context).inflate(
                R.layout.cardview_payments_item, parent, false);
        return new PaymentBalancesAdapter.ViewHolder(appointmentsListItemView);
    }

    @Override
    public void onBindViewHolder(final PaymentBalancesAdapter.ViewHolder holder, int position) {
        final PatientBalanceDTO patientBalanceDTO = balances.get(position);
        Double responsibility = Double.valueOf(patientBalanceDTO.getPendingRepsonsibility());
        holder.paymentAmountTextView.setText(StringUtil.getFormattedBalanceAmount(responsibility));
        holder.placeNameTextView.setText(userPractice.getPracticeName());
        holder.payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onBalancePayButtonClicked(patientBalanceDTO);
            }
        });
        String photoUrl = userPractice.getPracticePhoto();
        if (TextUtils.isEmpty(photoUrl)) {
            holder.providerImageTextView.setText(StringUtil.getShortName(userPractice.getPracticeName()));
        } else {
            Picasso.with(context).load(photoUrl)
                    .transform(new CircleImageTransform())
                    .resize(58, 58)
                    .into(holder.providerImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.providerImageView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            holder.providerImageTextView.setText(StringUtil.getShortName(userPractice.getPracticeName()));
                        }
                    });


        }
    }

    @Override
    public int getItemCount() {
        return balances.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView placeNameTextView;
        TextView paymentAmountTextView;
        Button payButton;
        TextView providerImageTextView;
        ImageView providerImageView;

        /**
         * View holder constructor
         *
         * @param itemView view holder item
         */
        public ViewHolder(View itemView) {
            super(itemView);
            placeNameTextView = (TextView) itemView.findViewById(R.id.placeNameTextView);
            paymentAmountTextView = (TextView) itemView.findViewById(R.id.paymentAmountTextView);
            payButton = (Button) itemView.findViewById(R.id.payButton);
            providerImageTextView = (TextView) itemView.findViewById(R.id.providerImageTextView);
            providerImageView = (ImageView) itemView.findViewById(R.id.providerImageView);
        }
    }

    public interface PaymentRecyclerViewCallback {
        void onBalancePayButtonClicked(PatientBalanceDTO patientBalanceDTO);
    }

    /**
     * Sets the callback that will manage the events on the item views
     *
     * @param callback to manage item views events
     */
    public void setCallback(PaymentRecyclerViewCallback callback) {
        this.callback = callback;
    }
}
