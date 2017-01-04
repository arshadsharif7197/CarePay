package com.carecloud.carepay.patient.payment.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.appointments.models.AppointmentChargeDTO;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientBalancessDTO;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorge on 31/12/16.
 */

public class PaymentBalancesAdapter extends RecyclerView.Adapter<PaymentBalancesAdapter.PaymentHistoryViewHolder> {
    private Context context;
    private List<PaymentsPatientBalancessDTO> paymentsPatientBalances;

    public PaymentBalancesAdapter(Context context, PaymentsModel paymentDTO) {

        this.context = context;
        //this.paymentsPatientBalances = paymentDTO.getPaymentPayload().getPatientBalances();
        paymentsPatientBalances=new ArrayList<>();
        paymentsPatientBalances.add(new PaymentsPatientBalancessDTO());
        paymentsPatientBalances.add(new PaymentsPatientBalancessDTO());
        paymentsPatientBalances.add(new PaymentsPatientBalancessDTO());
    }

    @Override
    public PaymentBalancesAdapter.PaymentHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View paymentHistoryListItemView = LayoutInflater.from(context).inflate(
                R.layout.balances_list_item, parent, false);
        return new PaymentBalancesAdapter.PaymentHistoryViewHolder(paymentHistoryListItemView);
    }

    @Override
    public void onBindViewHolder(final PaymentBalancesAdapter.PaymentHistoryViewHolder holder, int position) {
        final PaymentsPatientBalancessDTO charge = paymentsPatientBalances.get(position);
            String locationName= CarePayConstants.NOT_DEFINED;
            holder.shortName.setText(StringUtil.onShortDrName(locationName));
            holder.locationName.setText(locationName);
            holder.amount.setText(StringUtil.getFormattedBalanceAmount(position/*Double.parseDouble()*/));
            holder.payNow.setText("Pay Now");//no label yet
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

}
