package com.carecloud.carepaylibray.payments.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by lmenendez on 2/13/18
 */

public class PaymentPlanListAdapter extends RecyclerView.Adapter<PaymentPlanListAdapter.ViewHolder> {

    private Context context;
    private List<PaymentPlanDTO> listItems = new ArrayList<>();
    private OnPaymentPlanSelectedListener listener;
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
    private PaymentsModel paymentsModel;

    public PaymentPlanListAdapter(Context context, List<PaymentPlanDTO> paymentPlans, OnPaymentPlanSelectedListener listener, PaymentsModel paymentsModel) {
        this.context = context;
        this.listItems = paymentPlans;
        this.listener = listener;
        this.paymentsModel = paymentsModel;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(
                R.layout.item_payment_plan, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final PaymentPlanDTO paymentPlanItem = (PaymentPlanDTO) listItems.get(position);

        String practiceName = getPracticeName(paymentPlanItem.getMetadata().getPracticeId());
        holder.shortName.setText(StringUtil.getShortName(practiceName));
        holder.locationName.setText(practiceName);

        double totalAmount = paymentPlanItem.getPayload().getAmount();
        double amountPaid = paymentPlanItem.getPayload().getAmountPaid();
        holder.amount.setText(currencyFormatter.format(totalAmount - amountPaid));

        String planDetails = currencyFormatter.format(
                paymentPlanItem.getPayload().getPaymentPlanDetails().getAmount()) +
                paymentPlanItem.getPayload().getPaymentPlanDetails().getFrequencyString();
        holder.planDetail.setText(planDetails);

        holder.planProgress.setMax(paymentPlanItem.getPayload().getPaymentPlanDetails().getInstallments());
        holder.planProgress.setProgress(paymentPlanItem.getPayload().getPaymentPlanDetails().getFilteredHistory().size());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onPaymentPlanItemSelected(paymentPlanItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView shortName;
        private TextView locationName;
        private TextView amount;
        private TextView planDetail;
        private ProgressBar planProgress;

        ViewHolder(View itemView) {
            super(itemView);
            locationName = (TextView) itemView.findViewById(R.id.balancesLocation);
            amount = (TextView) itemView.findViewById(R.id.balancesTotalAmount);
            shortName = (TextView) itemView.findViewById(R.id.balancesAvatarTextView);
            planDetail = (TextView) itemView.findViewById(R.id.planInstallmentDetail);
            planProgress = (ProgressBar) itemView.findViewById(R.id.paymentPlanProgress);
        }
    }

    public interface OnPaymentPlanSelectedListener {
        void onPaymentPlanItemSelected(PaymentPlanDTO paymentPlan);
    }

    private String getPracticeName(String practiceId){
        for(UserPracticeDTO userPracticeDTO : paymentsModel.getPaymentPayload().getUserPractices()){
            if(practiceId != null && practiceId.equals(userPracticeDTO.getPracticeId())){
                return userPracticeDTO.getPracticeName();
            }
        }
        return "";
    }

}
