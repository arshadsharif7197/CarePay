package com.carecloud.carepay.practice.library.payments.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;

import java.util.List;

/**
 * Created by pjohnson on 17/03/17.
 */
public class PaymentBalancesAdapter extends RecyclerView.Adapter<PaymentBalancesAdapter.ViewHolder> {

    private final List<PatientBalanceDTO> balances;

    public PaymentBalancesAdapter(List<PatientBalanceDTO> patientBalances) {
        this.balances = patientBalances;
    }

    @Override
    public PaymentBalancesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View appointmentsListItemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.cardview_payments_item, parent, false);
        return new PaymentBalancesAdapter.ViewHolder(appointmentsListItemView);
    }

    @Override
    public void onBindViewHolder(PaymentBalancesAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
